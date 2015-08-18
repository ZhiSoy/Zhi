package com.zhi.volley.image;

import android.graphics.Bitmap;

/**
 * A interface that keeps track of whether the bitmap is being displayed or cached.
 * When the bitmap is no longer being displayed and cached,
 * {@link android.graphics.Bitmap#recycle() recycle()} may be called on this
 * drawable's bitmap if version below HoneyComb, otherwise it would be added into re-used sets.
 */
public interface CacheDrawable {
    /**
     * Notify the bitmap that the displayed state has changed. Internally a count is kept
     * so that the drawable knows when it is no longer being displayed.
     *
     * @param isDisplayed - Whether the drawable is being displayed or not
     * @return This drawable itself.
     */
    CacheDrawable setDisplayState(boolean isDisplayed);

    /**
     * Notify the bitmap that the cache state has changed. Internally a count
     * is kept so that the drawable knows when it is no longer being cached.
     *
     * @param isCached - Whether the drawable is being cached or not
     */
    CacheDrawable setCacheState(boolean isCached);

    /**
     * Check whether the bitmap is recycled or not.
     *
     * @return True, if the bitmap has not been recycled, otherwise false.
     */
    boolean hasValidBitmap();

    /**
     * Returns the bitmap used by this drawable to render. May be null.
     */
    Bitmap getBitmap();
}
