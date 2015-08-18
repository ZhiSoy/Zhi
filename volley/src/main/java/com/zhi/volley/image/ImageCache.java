package com.zhi.volley.image;

import com.zhi.common.BaseApplication;
import com.zhi.common.log.LogUtils;
import com.zhi.common.util.Utils;
import com.zhi.volley.Cache;
import com.zhi.volley.toolbox.DiskBasedCache;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.LruCache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static android.graphics.Bitmap.CompressFormat;

/**
 * This image cache is used both for memory cache and disk cache.
 * Current, the disk cache is implemented with {@link DiskBasedCache};
 * the memory cache is implemented with {@link android.util.LruCache}.`
 */
public final class ImageCache extends DiskBasedCache implements Cache {
    public static final String TAG = "ImageCache";

    private final LruCache<String, CacheDrawable> mMemCache;
    private final Set<SoftReference<Bitmap>> mReusableBitmaps;

    public ImageCache(@NonNull ImageCacheParams params) {
        super(params.diskCacheDir, params.extraCacheDir, params.diskCacheSize);

        if (Utils.hasHoneycomb()) {
            mReusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        } else {
            mReusableBitmaps = null;
        }

        mMemCache = new LruCache<String, CacheDrawable>(params.memCacheSize) {
            @Override
            protected int sizeOf(String key, CacheDrawable value) {
                final int itemSize = value == null ? 0 :
                        Image.getBitmapSize(value.getBitmap()) / 1024;
                return itemSize == 0 ? 1 : itemSize;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key,
                    CacheDrawable oldValue, CacheDrawable newValue) {
                // Removed from cache, onResponse its cache state.
                oldValue.setCacheState(false);
            }
        };
    }

    @Override
    public void clear() {
        super.clear();
        mMemCache.evictAll();
        LogUtils.d(TAG, "Memory cache cleared");
    }

    /**
     * Create the cache drawable from a specific bitmap.
     */
    public static CacheDrawable newCacheDrawable(Resources res, Bitmap bitmap, Image.Format format) {
        CacheDrawable drawable = null;
        if (bitmap != null) {
            if (res == null) {
                res = BaseApplication.getInstance().getResources();
            }
            // https://code.google.com/p/android-apktool/wiki/9PatchImages
            drawable = new CacheBitmapDrawable(res, bitmap);
            ((CacheBitmapDrawable) drawable).setFormate(format);
        }
        return drawable;
    }

    /**
     * Add the cache drawable to memory cache.
     *
     * @param cacheKey Unique identifier for the cache drawable to store
     * @param drawable The bitmap to store
     */
    public void addToMemCache(String cacheKey, CacheDrawable drawable) {
        if (cacheKey == null || drawable == null || !drawable.hasValidBitmap()) {
            return;
        }

        // Add to memory cache.
        mMemCache.put(cacheKey, drawable);
        drawable.setCacheState(true);
    }

    /**
     * Retrieves an cache drawable from memory cache.
     *
     * @param cacheKey Unique identifier for which item to get
     * @return The cache drawable if found in cache, null otherwise
     */
    public CacheDrawable getFromMemCache(String cacheKey) {
        return mMemCache.get(cacheKey);
    }

    /**
     * Add the bitmap to the reusable sets that will be populated into
     * the inBitmap field of BitmapFactory.Options.
     *
     * @param bitmap The bitmap to be added.
     */
    public void addBitmapToReusableSet(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            if (mReusableBitmaps != null) {
                LogUtils.d(TAG, "Add to reusable sets");
                mReusableBitmaps.add(new SoftReference<>(bitmap));
            } else if (!Utils.hasHoneycomb()) {
                // Recycle the bitmap for api below Honeycomb.
                LogUtils.d(TAG, "Recycled");
                bitmap.recycle();
            }
        }
    }

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;
        if (mReusableBitmaps != null && mReusableBitmaps.isEmpty()) {
            synchronized (mReusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
                while (iterator.hasNext()) {
                    final Bitmap item = iterator.next().get();
                    if (null != item && item.isMutable()) {
                        if (Image.canUseForInBitmap(item, options)) {
                            bitmap = item;
                            iterator.remove();
                            break;
                        }
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }

    /**
     * A holder class that contains cache parameters.
     */
    public static class ImageCacheParams {
        /**
         * Default memory cache size in kilobytes
         */
        private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 5;

        /**
         * Default disk cache size in bytes
         */
        private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10;

        private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
        private static final int DEFAULT_COMPRESS_QUALITY = 70;

        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
        public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
        public File diskCacheDir;
        public File extraCacheDir;

        public int compressQuality = DEFAULT_COMPRESS_QUALITY;
        public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;

        /**
         * Create a set of image cache parameters.
         *
         * @param context   A context to use.
         * @param cacheName A unique subdirectory name that will be appended to the application
         *                  cache directory. Usually "cache" or "images" is sufficient.
         */
        public ImageCacheParams(Context context, String cacheName, String extraCacheName) {
            diskCacheDir = Image.getDiskCacheDir(context, cacheName);
            extraCacheDir = Image.getDiskCacheDir(context, extraCacheName);
        }

        /**
         * Sets the memory cache size based on a percentage of the max available VM memory.
         * Eg. setting percent to 0.2 would set the memory cache to one fifth of the available
         * memory. Throws {@link IllegalArgumentException} if percent is < 0.01 or > .8.
         * memCacheSize is stored in kilobytes instead of bytes as this will eventually be passed
         * to construct a LruCache which takes an int in its constructor.
         * <p/>
         * This value should be chosen carefully based on a number of factors
         * Refer to the corresponding Android Training class for more discussion:
         * http://developer.android.com/training/displaying-bitmaps/
         *
         * @param percent Percent of available app memory to use to size memory cache
         */
        public ImageCacheParams setMemCacheSizePercent(float percent) {
            if (percent < 0.01f || percent > 0.8f) {
                throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                        + "between 0.01 and 0.8 (inclusive)");
            }
            memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);
            return this;
        }
    }
}