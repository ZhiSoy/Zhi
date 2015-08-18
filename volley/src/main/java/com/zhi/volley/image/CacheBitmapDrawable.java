package com.zhi.volley.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import static com.zhi.volley.image.Image.Format;

/**
 * A class that keeps track of whether the bitmap is being displayed or cached.
 * When the bitmap is no longer being displayed and cached,
 * {@link android.graphics.Bitmap#recycle() recycle()} may be called on this
 * drawable's bitmap if version below HoneyComb, otherwise it would be added into re-used sets.
 */
public class CacheBitmapDrawable extends BitmapDrawable implements CacheDrawable {

    /**
     * This stores image type. Like png. gif. jpeg or other.
     */
    private Format mFormat = Format.NONE;

    /**
     * The count of this bitmap is displayed.
     */
    private int mReferenceCount;

    /**
     * Indicates whether this bitmap is in the cache or not.
     */
    private boolean mCacheState;

    /**
     * For a bitmap, it should be displayed once before being reused or recycled.
     */
    private boolean mHasBeenDisplayed;

    /**
     * Create drawable from a bitmap, setting initial target density based on
     * the display metrics of the resources.
     */
    public CacheBitmapDrawable(Resources resources, Bitmap bitmap) {
        super(resources, bitmap);
    }

    /**
     * Set the format of this image.
     *
     * @param format One of the predefined value in {@link Image.Format}
     * @return This CacheBitmapDrawable Object.
     */
    public CacheBitmapDrawable setFormate(Format format) {
        mFormat = format;
        return this;
    }

    /**
     * Get image format.
     *
     * @return The format of this image. Default is Format.NONE.
     */
    public Format getFormat() {
        return mFormat;
    }

    @Override
    public synchronized CacheBitmapDrawable setDisplayState(boolean isDisplayed) {
        // Internal lock is ok.
        if (isDisplayed) {
            mReferenceCount++;
            mHasBeenDisplayed = true;
        } else {
            mReferenceCount--;
        }

        // Check if it is valid to recycle or reuse it.
        checkState();
        return this;
    }

    @Override
    public synchronized CacheBitmapDrawable setCacheState(boolean isCached) {
        // Internal lock is ok.
        mCacheState = isCached;

        // Check if it is valid to recycle or reuse it.
        checkState();
        return this;
    }

    /**
     * If the bitmap is valid, and not being cached or displayed, this bitmap should be recycled
     * or put into re-used queue.
     */
    private synchronized void checkState() {
        if (!mCacheState && mReferenceCount <= 0 && mHasBeenDisplayed && hasValidBitmap()) {
            Image.getInstance().getImageCache().addBitmapToReusableSet(getBitmap());
        }
    }

    @Override
    public synchronized boolean hasValidBitmap() {
        final Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }
}
