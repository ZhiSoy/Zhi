package com.zhi.volley.image;
/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import com.zhi.common.log.LogUtils;
import com.zhi.common.util.Utils;
import com.zhi.volley.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import android.widget.Scroller;

import static android.view.GestureDetector.SimpleOnGestureListener;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import static com.zhi.volley.image.PhotoGestureDetector.OnGestureListener;

/**
 * Subclass of ImageView, it can be used to display a photo with the feature:
 * Photo can be zoom in or zoom out support.
 *
 * @attr R.attr.CachePhotoView_zoomable
 * @attr R.attr.CachePhotoView_minimumScale
 * @attr R.attr.CachePhotoView_mediumScale
 * @attr R.attr.CachePhotoView_maximumScale
 * @attr R.attr.CachePhotoView_allowParentInterceptOnEdge
 * <em> This class is changed from  PhotoView </em>
 */
public class CachePhotoView extends CacheImageView implements OnGestureListener {
    private static final String LOG_TAG = "CachePhotoView";
    private static final boolean DEBUG = true;

    private static final Interpolator sInterpolator = new AccelerateDecelerateInterpolator();

    private static final float DEFAULT_MAXIMUM_SCALE = 3.0f;
    private static final float DEFAULT_MEDIMUM_SCALE = 1.75f;
    private static final float DEFAULT_MINIMUM_SCALE = 1.0f;

    private static final int DEFAULT_ZOOM_DURATION = 200;

    private static final int EDGE_NONE = -1;
    private static final int EDGE_LEFT = 0;
    private static final int EDGE_RIGHT = 1;
    private static final int EDGE_BOTH = 2;

    private float mMinimumScale = DEFAULT_MINIMUM_SCALE;
    private float mMediumScale = DEFAULT_MEDIMUM_SCALE;
    private float mMaximumScale = DEFAULT_MAXIMUM_SCALE;

    private boolean mZoomable = true;
    private int mScrollEdge = EDGE_BOTH;
    private FlingRunnable mCurrentFlingRunnable;
    private boolean mAllowParentInterceptOnEdge = true;
    private ScaleType mScaleType = ScaleType.FIT_CENTER;

    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final RectF mDisplayRect = new RectF();
    private final float[] mMatrixValues = new float[9];

    private GestureDetector mGestureDetector;
    private PhotoGestureDetector mScaleDragDetector;

    private OnViewTapListener mViewTapListener;
    private OnPhotoTapListener mPhotoTapListener;
    private OnLongClickListener mLongClickListener;
    private OnMatrixChangedListener mMatrixChangeListener;

    public CachePhotoView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public CachePhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CachePhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CachePhotoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.CachePhotoView, defStyleAttr, defStyleRes);

            mZoomable = a.getBoolean(R.styleable.CachePhotoView_zoomable, true);
            mMinimumScale = a.getFloat(R.styleable.CachePhotoView_minimumScale, DEFAULT_MINIMUM_SCALE);
            mMediumScale = a.getFloat(R.styleable.CachePhotoView_medimumScale, DEFAULT_MEDIMUM_SCALE);
            mMaximumScale = a.getFloat(R.styleable.CachePhotoView_maximumScale, DEFAULT_MAXIMUM_SCALE);
            mAllowParentInterceptOnEdge = a.getBoolean(R.styleable.CachePhotoView_allowParentInterceptOnEdge, true);

            a.recycle();
        }
        // PhotoView sets it's own ScaleType to Matrix, then diverts all calls
        // setScaleType to this.setScaleType automatically.
        super.setScaleType(ScaleType.MATRIX);

        mScaleDragDetector = PhotoGestureDetector.newInstance(context, this);
        mGestureDetector = new GestureDetector(context, new PhotoGestureDectorListener());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed && mZoomable) {
            updateBaseMatrix(getDrawable());
        } else if (mDrawMatrix.isIdentity()) {
            updateBaseMatrix(getDrawable());
        } else {
            setImageMatrix(mDrawMatrix);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        if (mZoomable && getDrawable() != null) {
            switch (ev.getAction()) {
                case ACTION_DOWN:
                    // Disable the Parent from intercepting the touch event.
                    getParent().requestDisallowInterceptTouchEvent(true);
                    // Cancel on going fling.
                    cancelFling();
                    break;

                case ACTION_CANCEL:
                case ACTION_UP:
                    // Zoom back to min scale.
                    final float scaleValue = getScale();
                    if (scaleValue < mMinimumScale) {
                        final RectF rect = getDisplayRect();
                        if (null != rect) {
                            post(new ZoomRunnable(getScale(), mMinimumScale,
                                    rect.centerX(), rect.centerY()));
                            handled = true;
                        }
                    }
                    break;
            }

            handled |= mScaleDragDetector.onTouchEvent(ev);
            handled |= mGestureDetector.onTouchEvent(ev);
        }
        return handled || super.onTouchEvent(ev);
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (mScaleDragDetector.isScaling()) {
            // Do not drag if we are already scaling
            return;
        }

        if (DEBUG) {
            LogUtils.d(LOG_TAG, String.format("onDrag: dx: %.2f. dy: %.2f", dx, dy));
        }

        mSuppMatrix.postTranslate(dx, dy);
        checkAndDisplayMatrix();

        // Here we decide whether to let the ImageView's parent to start taking
        // over the touch event.
        // First we check whether this function is enabled. We never want the
        // parent to take over if we're scaling. We then check the edge we're
        // on, and the direction of the scroll (i.e. if we're pulling against
        // the edge, aka 'overscrolling', let the parent take over).
        final ViewParent parent = getParent();
        if (parent != null) {
            if (mAllowParentInterceptOnEdge && !mScaleDragDetector.isScaling()) {
                if (mScrollEdge == EDGE_BOTH
                        || (mScrollEdge == EDGE_LEFT && dx >= 1f)
                        || (mScrollEdge == EDGE_RIGHT && dx <= -1f)) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        if (DEBUG) {
            LogUtils.d(LOG_TAG, "onFling. sX: %.2f sY: %.2f Vx: %.2f Vy: %.2f",
                    startX, startY, velocityX, velocityY);
        }
        mCurrentFlingRunnable = new FlingRunnable(getContext());
        mCurrentFlingRunnable.fling(getContentWidth(),
                getContentHeight(), (int) velocityX, (int) velocityY);
        post(mCurrentFlingRunnable);
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        if (DEBUG) {
            LogUtils.d(LOG_TAG, "onScale: scale: %.2f. fX: %.2f. fY: %.2f",
                    scaleFactor, focusX, focusY);
        }

        if (getScale() < mMaximumScale || scaleFactor < 1f) {
            mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkAndDisplayMatrix();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelFling();
    }

    public int getContentWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    public int getContentHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    @Override
    public ScaleType getScaleType() {
        return mScaleType;
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);

        if (null != mMatrixChangeListener) {
            final RectF displayRect = getDisplayRect(matrix);
            if (null != displayRect) {
                mMatrixChangeListener.onMatrixChanged(displayRect);
            }
        }
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    private void cancelFling() {
        if (null != mCurrentFlingRunnable) {
            mCurrentFlingRunnable.cancel();
            mCurrentFlingRunnable = null;
        }
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageMatrix(getDrawMatrix());
        }
    }

    /**
     * Gets the Display Rectangle of the currently displayed Drawable. The Rectangle is relative to
     * this View and includes all scaling and translations.
     *
     * @return - RectF of Displayed Drawable
     */
    private RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        final Drawable d = getDrawable();
        if (null != d) {
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    private void updateBaseMatrix(Drawable d) {
        if (null == d) {
            return;
        }

        final float viewWidth = getContentWidth();
        final float viewHeight = getContentHeight();
        final int drawableWidth = d.getIntrinsicWidth();
        final int drawableHeight = d.getIntrinsicHeight();

        mBaseMatrix.reset();

        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;

        if (mScaleType == ScaleType.CENTER) {
            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                    (viewHeight - drawableHeight) / 2F);

        } else if (mScaleType == ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            final RectF src = new RectF(0, 0, drawableWidth, drawableHeight);
            final RectF dst = new RectF(0, 0, viewWidth, viewHeight);

            switch (mScaleType) {
                case FIT_CENTER:
                    mBaseMatrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
                    break;

                case FIT_START:
                    mBaseMatrix.setRectToRect(src, dst, Matrix.ScaleToFit.START);
                    break;

                case FIT_END:
                    mBaseMatrix.setRectToRect(src, dst, Matrix.ScaleToFit.END);
                    break;

                case FIT_XY:
                    mBaseMatrix.setRectToRect(src, dst, Matrix.ScaleToFit.FILL);
                    break;

                default:
                    break;
            }
        }

        resetMatrix();
    }

    private void resetMatrix() {
        mSuppMatrix.reset();
        setImageMatrix(getDrawMatrix());
        checkMatrixBounds();
    }


    private boolean checkMatrixBounds() {
        final RectF rect = getDisplayRect(getDrawMatrix());
        if (null == rect) {
            return false;
        }

        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getContentHeight();
        if (height <= viewHeight) {
            switch (mScaleType) {
                case FIT_START:
                    deltaY = -rect.top;
                    break;

                case FIT_END:
                    deltaY = viewHeight - height - rect.top;
                    break;

                default:
                    deltaY = (viewHeight - height) / 2 - rect.top;
                    break;
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getContentWidth();
        if (width <= viewWidth) {
            switch (mScaleType) {
                case FIT_START:
                    deltaX = -rect.left;
                    break;

                case FIT_END:
                    deltaX = viewWidth - width - rect.left;
                    break;

                default:
                    deltaX = (viewWidth - width) / 2 - rect.left;
                    break;
            }
            mScrollEdge = EDGE_BOTH;
        } else if (rect.left > 0) {
            mScrollEdge = EDGE_LEFT;
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            mScrollEdge = EDGE_RIGHT;
        } else {
            mScrollEdge = EDGE_NONE;
        }

        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }


    class PhotoGestureDectorListener extends SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            if (null != mLongClickListener) {
                mLongClickListener.onLongClick(CachePhotoView.this);
            }
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            boolean consumed = false;
            if (mPhotoTapListener != null) {
                final RectF rect = getDisplayRect();
                if (rect != null) {
                    final float x = e.getX(), y = e.getY();
                    // Check to see if the user tapped on the photo
                    if (rect.contains(x, y)) {
                        float xResult = (x - rect.left) / rect.width();
                        float yResult = (y - rect.top) / rect.height();

                        mPhotoTapListener.onPhotoTap(CachePhotoView.this, xResult, yResult);
                        consumed = true;
                    }
                }
            }
            if (mViewTapListener != null) {
                mViewTapListener.onViewTap(CachePhotoView.this, e.getX(), e.getY());
            }
            return consumed || super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent ev) {
            try {
                final float scale = getScale();
                final float x = ev.getX();
                final float y = ev.getY();

                if (scale < mMediumScale) {
                    setScale(mMediumScale, x, y, true);
                } else if (scale >= mMediumScale && scale < mMaximumScale) {
                    setScale(mMaximumScale, x, y, true);
                } else {
                    setScale(mMinimumScale, x, y, true);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // Can sometimes happen when getX() and getY() is called
            }
            return true;
        }
    }

    private class ZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mStart, mEnd;

        public ZoomRunnable(float start, float end, float focalX, float focalY) {
            mStart = start;
            mEnd = end;
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            float t = interpolate();
            float scale = mStart + t * (mEnd - mStart);
            float deltaScale = scale / getScale();

            mSuppMatrix.postScale(deltaScale, deltaScale, mFocalX, mFocalY);
            checkAndDisplayMatrix();

            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                ViewCompat.postOnAnimation(CachePhotoView.this, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / DEFAULT_ZOOM_DURATION;
            t = Math.min(1f, t);
            t = sInterpolator.getInterpolation(t);
            return t;
        }
    }

    class FlingRunnable implements Runnable {

        private final ScrollerProxy mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = ScrollerProxy.getScroller(context);
        }

        public void cancel() {
            mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY) {
            final RectF rect = getDisplayRect();
            if (null == rect) {
                return;
            }

            final int startX = Math.round(-rect.left);
            final int minX, maxX, minY, maxY;

            if (viewWidth < rect.width()) {
                minX = 0;
                maxX = Math.round(rect.width() - viewWidth);
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(-rect.top);
            if (viewHeight < rect.height()) {
                minY = 0;
                maxY = Math.round(rect.height() - viewHeight);
            } else {
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;

            if (DEBUG) {
                LogUtils.d(LOG_TAG, "fling. StartX:%d StartY:%d MaxX:%d MaxY:%d",
                        +startX, startY, maxY, maxY);
            }

            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY,
                        minX, maxX, minY, maxY, 0, 0);
            }
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {
                return; // remaining post that should not be handled
            }

            if (mScroller.computeScrollOffset()) {
                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();

                if (DEBUG) {
                    LogUtils.d(LOG_TAG, "fling run. CurrentX: %d  CurrentY: %d  NewX: %d  NewY: %d",
                            mCurrentX, mCurrentY, newX, newY);
                }

                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                setImageMatrix(getDrawMatrix());

                mCurrentX = newX;
                mCurrentY = newY;

                // Post On animation
                ViewCompat.postOnAnimation(CachePhotoView.this, this);
            }
        }
    }

    /**
     * Allows you to enable/disable the zoom functionality on the ImageView. When disable the
     * ImageView reverts to using the FIT_CENTER matrix.
     *
     * @param zoomable - Whether the zoom functionality is enabled.
     */
    public void setZoomable(boolean zoomable) {
        mZoomable = zoomable;
    }

    /**
     * Returns true if the PhotoView is set to allow zooming of Photos.
     *
     * @return true if the PhotoView allows zooming.
     */
    public boolean canZoom() {
        return mZoomable;
    }

    /**
     * Returns the current scale value
     *
     * @return float - current scale value
     */
    public float getScale() {
        mSuppMatrix.getValues(mMatrixValues);
        return (float) Math.hypot(mMatrixValues[Matrix.MSCALE_X], mMatrixValues[Matrix.MSKEW_Y]);
    }

    /**
     * Changes the current scale to the specified value.
     *
     * @param scale - Value to scale to
     */
    public void setScale(float scale) {
        setScale(scale, false);
    }

    /**
     * Changes the current scale to the specified value.
     *
     * @param scale   - Value to scale to
     * @param animate - Whether to animate the scale
     */
    public void setScale(float scale, boolean animate) {
        setScale(scale, getRight() / 2, getLeft() / 2, animate);
    }

    /**
     * Changes the current scale to the specified value, around the given focal point.
     *
     * @param scale   - Value to scale to
     * @param focalX  - X Focus Point
     * @param focalY  - Y Focus Point
     * @param animate - Whether to animate the scale
     */
    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        // Check to see if the scale is within bounds
        if (scale < mMinimumScale || scale > mMaximumScale) {
            LogUtils.i(LOG_TAG, "Scale must be within the range of minScale and maxScale");
            return;
        }

        if (animate) {
            post(new ZoomRunnable(getScale(), scale, focalX, focalY));
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY);
            checkAndDisplayMatrix();
        }
    }

    /**
     * Whether to allow the ImageView's parent to intercept the touch event when the photo is scroll
     * to it's horizontal edge.
     *
     * @param allow whether to allow intercepting by parent element or not
     */
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAllowParentInterceptOnEdge = allow;
    }

    /**
     * @return The current minimum scale level. What this value represents depends on the current
     * {@link android.widget.ImageView.ScaleType}.
     */
    public float getMinimumScale() {
        return mMinimumScale;
    }

    /**
     * Sets the minimum scale level. What this value represents depends on the current
     * {@link android.widget.ImageView.ScaleType}.
     *
     * @param minimumScale minimum allowed scale
     */
    public void setMinimumScale(float minimumScale) {
        mMinimumScale = minimumScale;
    }

    /**
     * @return The current medium scale level. What this value represents depends on the current
     * {@link android.widget.ImageView.ScaleType}.
     */
    public float getMediumScale() {
        return mMediumScale;
    }

    /**
     * Sets the medium scale level. What this value represents depends on the current
     * {@link android.widget.ImageView.ScaleType}.
     *
     * @param mediumScale medium scale preset
     */
    public void setMediumScale(float mediumScale) {
        mMediumScale = mediumScale;
    }

    /**
     * @return The current maximum scale level. What this value represents depends on the current
     * {@link android.widget.ImageView.ScaleType}.
     */
    public float getMaximumScale() {
        return mMaximumScale;
    }

    /**
     * Sets the maximum scale level. What this value represents depends on the current {@link
     * android.widget.ImageView.ScaleType}.
     *
     * @param maximumScale maximum allowed scale preset
     */
    public void setMaximumScale(float maximumScale) {
        mMaximumScale = maximumScale;
    }

    /**
     * Returns a callback listener to be invoked when the View is tapped with a single tap.
     *
     * @return PhotoViewAttacher.OnViewTapListener currently set, may be null
     */
    public OnViewTapListener getViewTapListener() {
        return mViewTapListener;
    }

    /**
     * Register a callback to be invoked when the View is tapped with a single tap.
     *
     * @param listener - Listener to be registered.
     */
    public void setViewTapListener(OnViewTapListener listener) {
        mViewTapListener = listener;
    }

    /**
     * Returns a listener to be invoked when the Photo displayed by this View is tapped with a
     * single tap.
     *
     * @return OnPhotoTapListener currently set, may be null
     */
    public OnPhotoTapListener getPhotoTapListener() {
        return mPhotoTapListener;
    }

    /**
     * Register a callback to be invoked when the Photo displayed by this View is tapped with a
     * single tap.
     *
     * @param listener - Listener to be registered.
     */
    public void setPhotoTapListener(OnPhotoTapListener listener) {
        mPhotoTapListener = listener;
    }

    public OnLongClickListener getLongClickListener() {
        return mLongClickListener;
    }

    /**
     * Register a callback to be invoked when the Photo displayed by this view is long-pressed.
     *
     * @param listener - Listener to be registered.
     */
    public void setLongClickListener(OnLongClickListener listener) {
        mLongClickListener = listener;
    }

    public OnMatrixChangedListener getMatrixChangeListener() {
        return mMatrixChangeListener;
    }

    /**
     * Register a callback to be invoked when the Matrix has changed for this View. An example would
     * be the user panning or scaling the Photo.
     *
     * @param listener - Listener to be registered.
     */
    public void setMatrixChangeListener(OnMatrixChangedListener listener) {
        mMatrixChangeListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when the internal Matrix has changed for
     * this View.
     *
     * @author Chris Banes
     */
    public static interface OnMatrixChangedListener {
        /**
         * Callback for when the Matrix displaying the Drawable has changed. This could be because
         * the View's bounds have changed, or the user has zoomed.
         *
         * @param rect - Rectangle displaying the Drawable's new bounds.
         */
        void onMatrixChanged(RectF rect);
    }

    /**
     * Interface definition for a callback to be invoked when the Photo is tapped with a single
     * tap.
     *
     * @author Chris Banes
     */
    public static interface OnPhotoTapListener {

        /**
         * A callback to receive where the user taps on a photo. You will only receive a callback if
         * the user taps on the actual photo, tapping on 'whitespace' will be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the of the Drawable, as percentage of the
         *             Drawable mWidth.
         * @param y    - where the user tapped from the top of the Drawable, as percentage of the
         *             Drawable mHeight.
         */
        void onPhotoTap(View view, float x, float y);
    }

    /**
     * Interface definition for a callback to be invoked when the ImageView is tapped with a single
     * tap.
     *
     * @author Chris Banes
     */
    public static interface OnViewTapListener {

        /**
         * A callback to receive where the user taps on a ImageView. You will receive a callback if
         * the user taps anywhere on the view, tapping on 'whitespace' will not be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the left of the View.
         * @param y    - where the user tapped from the top of the View.
         */
        void onViewTap(View view, float x, float y);
    }
}


abstract class ScrollerProxy {

    public abstract boolean computeScrollOffset();

    public abstract void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY,
            int maxY, int overX, int overY);

    public abstract void forceFinished(boolean finished);

    public abstract boolean isFinished();

    public abstract int getCurrX();

    public abstract int getCurrY();

    public static ScrollerProxy getScroller(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            return new PreGingerScroller(context);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return new GingerScroller(context);
        } else {
            return new IcsScroller(context);
        }
    }


    public static class PreGingerScroller extends ScrollerProxy {

        private final Scroller mScroller;

        public PreGingerScroller(Context context) {
            mScroller = new Scroller(context);
        }

        @Override
        public boolean computeScrollOffset() {
            return mScroller.computeScrollOffset();
        }

        @Override
        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY,
                int overX, int overY) {
            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
        }

        @Override
        public void forceFinished(boolean finished) {
            mScroller.forceFinished(finished);
        }

        public boolean isFinished() {
            return mScroller.isFinished();
        }

        @Override
        public int getCurrX() {
            return mScroller.getCurrX();
        }

        @Override
        public int getCurrY() {
            return mScroller.getCurrY();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static class GingerScroller extends ScrollerProxy {

        protected final OverScroller mScroller;
        private boolean mFirstScroll = false;

        public GingerScroller(Context context) {
            mScroller = new OverScroller(context);
        }

        @Override
        public boolean computeScrollOffset() {
            // Workaround for first scroll returning 0 for the direction of the edge it hits.
            // Simply recompute values.
            if (mFirstScroll) {
                mScroller.computeScrollOffset();
                mFirstScroll = false;
            }
            return mScroller.computeScrollOffset();
        }

        @Override
        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY,
                int overX, int overY) {
            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY);
        }

        @Override
        public void forceFinished(boolean finished) {
            mScroller.forceFinished(finished);
        }

        @Override
        public boolean isFinished() {
            return mScroller.isFinished();
        }

        @Override
        public int getCurrX() {
            return mScroller.getCurrX();
        }

        @Override
        public int getCurrY() {
            return mScroller.getCurrY();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static class IcsScroller extends GingerScroller {

        public IcsScroller(Context context) {
            super(context);
        }

        @Override
        public boolean computeScrollOffset() {
            return mScroller.computeScrollOffset();
        }
    }
}


abstract class PhotoGestureDetector {
    /**
     * Analyzes the given motion event and if applicable triggers the
     * appropriate callbacks on the {@link OnGestureListener} supplied.
     *
     * @param ev The current motion event.
     * @return true if the {@link OnGestureListener} consumed the event, else false.
     */
    public abstract boolean onTouchEvent(MotionEvent ev);

    /**
     * Whether the current photo view is being scaled.
     *
     * @return true if this photo view is scaling.
     */
    public abstract boolean isScaling();

    /**
     * Set the gesture listener.
     *
     * @param listener A {@link OnGestureListener}
     */
    public abstract void setOnGestureListener(OnGestureListener listener);

    /**
     * The listener that is used to notify when gestures occur.
     */
    public interface OnGestureListener {

        public void onDrag(float dx, float dy);

        public void onFling(float startX, float startY, float velocityX, float velocityY);

        public void onScale(float scaleFactor, float focusX, float focusY);
    }

    public static PhotoGestureDetector newInstance(Context context, OnGestureListener listener) {
        final PhotoGestureDetector detector;

        if (Utils.hasFroyo()) {
            detector = new FroyoGestureDetector(context);
        } else if (Utils.hasEclair()) {
            detector = new EclairGestureDetector(context);
        } else {
            detector = new CupcakeGestureDetector(context);
        }

        detector.setOnGestureListener(listener);
        return detector;
    }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    static class CupcakeGestureDetector extends PhotoGestureDetector {
        private static final String LOG_TAG = "CupcakeGestureDetector";

        float mLastTouchX;
        float mLastTouchY;
        final float mTouchSlop;
        final float mMinimumVelocity;
        protected OnGestureListener mListener;
        private VelocityTracker mVelocityTracker;

        private boolean mIsDragging;


        public CupcakeGestureDetector(Context context) {
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
            mTouchSlop = configuration.getScaledTouchSlop();
        }

        float getActiveX(MotionEvent ev) {
            return ev.getX();
        }

        float getActiveY(MotionEvent ev) {
            return ev.getY();
        }

        @Override
        public boolean isScaling() {
            return false;
        }

        @Override
        public void setOnGestureListener(OnGestureListener listener) {
            mListener = listener;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mVelocityTracker = VelocityTracker.obtain();
                    if (null != mVelocityTracker) {
                        mVelocityTracker.addMovement(ev);
                    } else {
                        Log.i(LOG_TAG, "Velocity tracker is null");
                    }

                    mLastTouchX = getActiveX(ev);
                    mLastTouchY = getActiveY(ev);
                    mIsDragging = false;
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    final float x = getActiveX(ev);
                    final float y = getActiveY(ev);
                    final float dx = x - mLastTouchX, dy = y - mLastTouchY;

                    if (!mIsDragging) {
                        // Use Pythagoras to see if drag length is larger than touch slop
                        mIsDragging = FloatMath.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
                    }

                    if (mIsDragging) {
                        mListener.onDrag(dx, dy);
                        mLastTouchX = x;
                        mLastTouchY = y;

                        if (null != mVelocityTracker) {
                            mVelocityTracker.addMovement(ev);
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    // Recycle Velocity Tracker
                    if (null != mVelocityTracker) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    if (mIsDragging) {
                        if (null != mVelocityTracker) {
                            mLastTouchX = getActiveX(ev);
                            mLastTouchY = getActiveY(ev);

                            // Compute velocity within the last 1000ms
                            mVelocityTracker.addMovement(ev);
                            mVelocityTracker.computeCurrentVelocity(1000);

                            final float vX = mVelocityTracker.getXVelocity();
                            final float vY = mVelocityTracker.getYVelocity();

                            // If the velocity is greater than minVelocity, call listener
                            if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                                mListener.onFling(mLastTouchX, mLastTouchY, -vX, -vY);
                            }
                        }
                    }

                    // Recycle Velocity Tracker
                    if (null != mVelocityTracker) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    break;
                }
            }

            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    static class EclairGestureDetector extends CupcakeGestureDetector {
        private static final int INVALID_POINTER_ID = -1;

        private int mActivePointerId = INVALID_POINTER_ID;
        private int mActivePointerIndex = 0;

        public EclairGestureDetector(Context context) {
            super(context);
        }

        @Override
        float getActiveX(MotionEvent ev) {
            try {
                return ev.getX(mActivePointerIndex);
            } catch (Exception e) {
                return ev.getX();
            }
        }

        @Override
        float getActiveY(MotionEvent ev) {
            try {
                return ev.getY(mActivePointerIndex);
            } catch (Exception e) {
                return ev.getY();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            final int action = ev.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mActivePointerId = ev.getPointerId(0);
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mActivePointerId = INVALID_POINTER_ID;
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    // Ignore deprecation, ACTION_POINTER_ID_MASK and
                    // ACTION_POINTER_ID_SHIFT has same value and are deprecated
                    // You can have either deprecation or lint target api warning
                    final int pointerIndex = getPointerIndex(ev.getAction());
                    final int pointerId = ev.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mActivePointerId = ev.getPointerId(newPointerIndex);
                        mLastTouchX = ev.getX(newPointerIndex);
                        mLastTouchY = ev.getY(newPointerIndex);
                    }
                    break;
            }

            mActivePointerIndex = ev.findPointerIndex(
                    mActivePointerId != INVALID_POINTER_ID ? mActivePointerId : 0);

            return super.onTouchEvent(ev);
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    static class FroyoGestureDetector extends EclairGestureDetector {

        protected final ScaleGestureDetector mDetector;

        public FroyoGestureDetector(Context context) {
            super(context);
            mDetector = new ScaleGestureDetector(context, new SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    final float scaleFactor = detector.getScaleFactor();
                    if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                        return false;
                    }

                    mListener.onScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
                    return true;
                }
            });
        }

        @Override
        public boolean isScaling() {
            return mDetector.isInProgress();
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            mDetector.onTouchEvent(ev);
            return super.onTouchEvent(ev);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static int getPointerIndex(int action) {
        if (Utils.hasHoneycomb()) {
            return getPointerIndexHoneyComb(action);
        } else {
            return getPointerIndexEclair(action);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    static int getPointerIndexEclair(int action) {
        return (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static int getPointerIndexHoneyComb(int action) {
        return (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }
}
