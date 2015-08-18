package com.zhi.volley.image;

import com.zhi.common.BaseApplication;
import com.zhi.common.content.UiHandler;
import com.zhi.common.util.Preconditions;
import com.zhi.common.util.Utils;
import com.zhi.volley.RequestQueue;
import com.zhi.volley.toolbox.ClearCacheRequest;
import com.zhi.volley.toolbox.Volley;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.graphics.Bitmap.CompressFormat;

public final class Image {
    public static final String IMAGE_CACHE_DIR = "images";
    public static final String IMAGE_NETWORK_CACHE_DIR = "images/network";

    private final RequestQueue mImageQueue;

    private final ImageCache mImageCache;
    private final ImageCache.ImageCacheParams mCacheParams;

    private final ImageLoader mImageLoader;
    private final ImageDecoder mGifImageDecoder;

    public static Image getInstance() {
        return ImageHolder.INSTANCE;
    }

    private static class ImageHolder {
        private static final Image INSTANCE = new Image();
    }

    private Image() {
        final Context context = BaseApplication.getInstance();
        mCacheParams = new ImageCache.ImageCacheParams(context, IMAGE_CACHE_DIR,
                IMAGE_NETWORK_CACHE_DIR).setMemCacheSizePercent(0.25f);
        mImageCache = new ImageCache(mCacheParams);

        mImageQueue = Volley.newRequestQueue(context, null, mImageCache);
        mImageLoader = new ImageLoader(mImageQueue, mImageCache);
        mGifImageDecoder = new GifImageDecoder(new UiHandler(null));
        mGifImageDecoder.start();
    }

    /**
     * @return the image catch use to cache the image resource.
     */
    public ImageCache getImageCache() {
        return mImageCache;
    }

    /**
     * @return the request dispatch queue with a thread pool of dispatchers.
     */
    public RequestQueue getRequestQueue() {
        return mImageQueue;
    }

    /**
     * @return the helper that handles loading and caching images from remote URLs.
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * @return the gif decoder.
     */
    public ImageDecoder getGifImageDecoder() {
        return mGifImageDecoder;
    }

    /**
     * @return the bitmap compress format.
     */
    public CompressFormat getCompressFormat() {
        return mCacheParams.compressFormat;
    }

    /**
     * @return the bitmap compress quality.
     */
    public int getCompressQuality() {
        return mCacheParams.compressQuality;
    }

    /**
     * Clears both the memory and disk cache.
     */
    public void clearCache() {
        mImageQueue.add(new ClearCacheRequest(mImageCache, null));
    }

    public enum Format {
        NONE("none"),
        GIF("gif");

        Format(String format) {
        }
    }

    /**
     * Decode the image format from data.
     * The format is defined in {@link Image.Format}.
     *
     * @param data The image decoded byte. The image format is at he beginning.
     *             So the length of data may be less than 8-bytes.
     * @return the image format.
     */
    public static Format decodeFormat(@NonNull byte[] data) {
        Preconditions.checkNotNull(data);
        if (data.length < 3) {
            return Format.NONE;
        }
        // Check whether it's gif image.
        if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F'
                || (data[0] == 'g' && data[1] == 't' && data[2] == 'f')) {
            return Format.GIF;
        }
        return Format.NONE;
    }

    /**
     * Decode the image format from image file.
     * The format is defined in {@link Image.Format}.
     *
     * @param file The image file.
     */
    public static Format decodeFormat(File file) {
        if (file == null || !file.exists()) {
            return Format.NONE;
        }
        final byte[] data = new byte[8];
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file), 8);
            bis.read(data);
            return decodeFormat(data);
        } catch (IOException ignored) {
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ignored) {
                }
            }
        }
        return Format.NONE;
    }

    /**
     * Decode and sample down a bitmap from resources to the requested width and height.
     *
     * @param data       The image data
     * @param config     Default unlimited bitmap Config.
     * @param maxWidth   The maximum width of the resulting bitmap
     * @param maxHeight  The maximum height of the resulting bitmap
     * @param imageCache The bitmap cache.
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * that are equal to or greater than the requested width and height
     */
    public static Bitmap decode(byte[] data, Config config, int maxWidth,
            int maxHeight, ImageCache imageCache) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        final Bitmap bitmap;
        if (maxWidth <= 0 && maxHeight <= 0) {
            options.inPreferredConfig = config;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } else {
            // If we have to resize this image, first get the natural bounds.
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            final int actualWidth = options.outWidth;
            final int actualHeight = options.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            final int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                    actualWidth, actualHeight);
            final int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                    actualHeight, actualWidth);

            // Decode to the nearest power of two scaling factor.
            options.inJustDecodeBounds = false;
            options.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                    desiredWidth, desiredHeight);

            // If we're running on Honeycomb or newer, try to use inBitmap
            if (Utils.hasHoneycomb() && imageCache != null) {
                addInBitmapOptions(imageCache, options);
            }

            final Bitmap tempBitmap =
                    BitmapFactory.decodeByteArray(data, 0, data.length, options);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        return bitmap;
    }

    /**
     * Decode and sample down a bitmap from resources to the requested width and height.
     *
     * @param res        The resources object containing the image data
     * @param resId      The resource id of the image data
     * @param config     Default unlimited bitmap Config.
     * @param maxWidth   The maximum width of the resulting bitmap
     * @param maxHeight  The maximum height of the resulting bitmap
     * @param imageCache The bitmap cache.
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * that are equal to or greater than the requested width and height
     */
    public static Bitmap decode(Resources res, int resId, Config config,
            int maxWidth, int maxHeight, ImageCache imageCache) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        final Bitmap bitmap;
        if (maxWidth <= 0 && maxHeight <= 0) {
            options.inPreferredConfig = config;
            bitmap = BitmapFactory.decodeResource(res, resId, options);
        } else {
            // If we have to resize this image, first get the natural bounds.
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);
            final int actualWidth = options.outWidth;
            final int actualHeight = options.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            final int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                    actualWidth, actualHeight);
            final int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                    actualHeight, actualWidth);

            // Decode to the nearest power of two scaling factor.
            options.inJustDecodeBounds = false;
            options.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                    desiredWidth, desiredHeight);

            // If we're running on Honeycomb or newer, try to use inBitmap
            if (Utils.hasHoneycomb() && imageCache != null) {
                addInBitmapOptions(imageCache, options);
            }

            final Bitmap tempBitmap =
                    BitmapFactory.decodeResource(res, resId, options);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        return bitmap;
    }

    /**
     * Decode and sample down a bitmap from a file to the requested width and height.
     *
     * @param filename   The full path of the file to decode
     * @param config     Default unlimited bitmap Config.
     * @param maxWidth   The requested width of the resulting bitmap
     * @param maxHeight  The requested height of the resulting bitmap
     * @param imageCache The bitmap cache.
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * that are equal to or greater than the requested width and height
     */
    public static Bitmap decode(String filename, Config config,
            int maxWidth, int maxHeight, ImageCache imageCache) {
        final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        final Bitmap bitmap;
        if (maxWidth <= 0 && maxHeight <= 0) {
            decodeOptions.inPreferredConfig = config;
            bitmap = BitmapFactory.decodeFile(filename, decodeOptions);
        } else {
            // If we have to resize this image, first get the natural bounds.
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, decodeOptions);
            final int actualWidth = decodeOptions.outWidth;
            final int actualHeight = decodeOptions.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            final int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                    actualWidth, actualHeight);
            final int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                    actualHeight, actualWidth);

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                    desiredWidth, desiredHeight);

            // If we're running on Honeycomb or newer, try to use inBitmap
            if (Utils.hasHoneycomb() && imageCache != null) {
                addInBitmapOptions(imageCache, decodeOptions);
            }

            final Bitmap tempBitmap =
                    BitmapFactory.decodeFile(filename, decodeOptions);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        return bitmap;
    }

    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.
     *
     * @param descriptor The file descriptor to read from
     * @param config     Default unlimited bitmap Config.
     * @param maxWidth   The requested width of the resulting bitmap
     * @param maxHeight  The requested height of the resulting bitmap
     * @param imageCache The bitmap cache.
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * that are equal to or greater than the requested width and height
     */
    public static Bitmap decode(FileDescriptor descriptor, Config config,
            int maxWidth, int maxHeight, ImageCache imageCache) {
        final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        final Bitmap bitmap;
        if (maxWidth <= 0 && maxHeight <= 0) {
            decodeOptions.inPreferredConfig = config;
            bitmap = BitmapFactory.decodeFileDescriptor(descriptor, null, decodeOptions);
        } else {
            // If we have to resize this image, first get the natural bounds.
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(descriptor, null, decodeOptions);
            final int actualWidth = decodeOptions.outWidth;
            final int actualHeight = decodeOptions.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            final int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                    actualWidth, actualHeight);
            final int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                    actualHeight, actualWidth);

            // Decode to tqhe nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                    desiredWidth, desiredHeight);

            // If we're running on Honeycomb or newer, try to use inBitmap
            if (Utils.hasHoneycomb() && imageCache != null) {
                addInBitmapOptions(imageCache, decodeOptions);
            }

            final Bitmap tempBitmap =
                    BitmapFactory.decodeFileDescriptor(descriptor, null, decodeOptions);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        return bitmap;
    }

    /**
     * Decode and sample down a bitmap from a file to the requested width and height.
     *
     * @param is         The input stream that holds the raw data to be decoded into a bitmap.
     * @param config     Default unlimited bitmap Config.
     * @param maxWidth   The requested width of the resulting bitmap
     * @param maxHeight  The requested height of the resulting bitmap
     * @param imageCache The bitmap cache.
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * that are equal to or greater than the requested width and height
     */
    public static Bitmap decode(InputStream is, Config config,
            int maxWidth, int maxHeight, ImageCache imageCache) {
        final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        final Bitmap bitmap;
        if (maxWidth <= 0 && maxHeight <= 0) {
            decodeOptions.inPreferredConfig = config;
            bitmap = BitmapFactory.decodeStream(is, null, decodeOptions);
        } else {
            // If we have to resize this image, first get the natural bounds.
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, decodeOptions);
            final int actualWidth = decodeOptions.outWidth;
            final int actualHeight = decodeOptions.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            final int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                    actualWidth, actualHeight);
            final int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                    actualHeight, actualWidth);

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                    desiredWidth, desiredHeight);

            // If we're running on Honeycomb or newer, try to use inBitmap
            if (Utils.hasHoneycomb() && imageCache != null) {
                addInBitmapOptions(imageCache, decodeOptions);
            }

            final Bitmap tempBitmap = BitmapFactory.decodeStream(is, null, decodeOptions);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        return bitmap;
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     *
     * @param maxPrimary      Maximum size of the primary dimension (i.e. width for
     *                        max width), or zero to maintain aspect ratio with secondary
     *                        dimension
     * @param maxSecondary    Maximum size of the secondary dimension, or zero to
     *                        maintain aspect ratio with primary dimension
     * @param actualPrimary   Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
            int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    private static int findBestSampleSize(int actualWidth, int actualHeight,
            int desiredWidth, int desiredHeight) {
        final double wr = (double) actualWidth / desiredWidth;
        final double hr = (double) actualHeight / desiredHeight;
        final double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void addInBitmapOptions(ImageCache imageCache, BitmapFactory.Options options) {
        // Try and find a bitmap to use for inBitmap
        final Bitmap inBitmap = imageCache.getBitmapFromReusableSet(options);

        if (inBitmap != null) {
            // inBitmap only works with mutable bitmaps so force the decoder to
            // return mutable bitmaps.
            options.inMutable = true;
            options.inBitmap = inBitmap;
        }
    }

    /**
     * Get the size in bytes of a bitmap.
     * <em>Note that form Android 4.4 (KitKat) onward, this returns the allocated memory
     * size of the bitmap which can be larger than the actual bitmap data byte count
     * (in the case it was re-used).</em>
     *
     * @param bitmap Get the bitmap's size in bytes.
     * @return size in bytes.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapSize(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return 0;
        }

        // Form KitKat onward, getAllocationByteCount() returns the size of the allocated memory
        // used to store this bitmap's colors. This can be larger than the result of getByteCount()
        // if a bitmap is reused to decode other bitmaps of smaller size, or by manual reconfiguration.
        if (Utils.hasKitKat()) {
            return bitmap.getAllocationByteCount();
        }

        if (Utils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }


    /**
     * Check to see it a mutable item can be used for inBitmap
     *
     * @param candidate     - Bitmap to check
     * @param targetOptions - Options that have the out* value populated
     * @return true if <code>candidate</code> can be used for inBitmap re-use with
     * <code>targetOptions</code>
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
        if (!Utils.hasKitKat()) {
            // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
            return candidate.getWidth() == targetOptions.outWidth
                    && candidate.getHeight() == targetOptions.outHeight
                    && targetOptions.inSampleSize == 1;
        }

        // From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
        // is smaller than the reusable bitmap candidate allocation byte count.
        final int width = targetOptions.outWidth / targetOptions.inSampleSize;
        final int height = targetOptions.outHeight / targetOptions.inSampleSize;
        final int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
        return byteCount <= candidate.getAllocationByteCount();
    }

    /**
     * Return the byte usage per pixel of a bitmap based on its configuration.
     *
     * @param config The bitmap configuration.
     * @return The byte usage per pixel.
     */
    public static int getBytesPerPixel(Bitmap.Config config) {
        switch (config) {
            case ARGB_8888:
                return 4;
            case RGB_565:
                return 2;
            case ARGB_4444:
                return 2;
            case ALPHA_8:
                return 1;
            default:
                return 1;
        }
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context   The context to use
     * @param cacheName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String cacheName) {
        // Check to see if media is mounted or storage is built-in. If so, use external cache dir,
        // otherwise, use internal cache dir.
        final String cachePath = (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || isExternalStorageRemovable()) ? getExternalCacheDir(context).getPath()
                : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + cacheName);
    }

    /**
     * Check to see if the external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false otherwise.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        return !Utils.hasGingerbread() || Environment.isExternalStorageRemovable();
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (Utils.hasFroyo()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "Android/data/" + context.getPackageName() + "cache";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static long getUsableSpace(File path) {
        if (Utils.hasGingerbread()) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Creates a cache key.
     *
     * @param data   The Data of the request.
     * @param width  The width of the output.
     * @param height The height of the output.
     */
    public static String getCacheKey(Object data, int width, int height) {
        return new StringBuilder(String.valueOf(data).length() + 12).append("#W").append(width)
                .append("#H").append(height).append(data).toString();
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException ignored) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    public static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Compress a bitmap into a byte[].
     */
    public static byte[] bitmapToBytes(Bitmap bitmap, CompressFormat format, int quality) {
        final ByteArrayOutputStream bytes =
                new ByteArrayOutputStream(Image.getBitmapSize(bitmap));
        bitmap.compress(format, quality, bytes);
        return bytes.toByteArray();
    }

    /**
     * Compress a bitmap into a file.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void bitmapToFile(Bitmap bitmap, File file, CompressFormat format, int quality) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(format, quality, fos);
        } catch (IOException e) {
        }
    }
}
