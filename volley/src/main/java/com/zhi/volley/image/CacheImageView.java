package com.zhi.volley.image;

import com.zhi.common.util.Objects;
import com.zhi.common.util.Utils;
import com.zhi.volley.R;
import com.zhi.volley.VolleyError;
import com.zhi.volley.image.ImageLoader.ImageContainer;
import com.zhi.volley.image.ImageLoader.ImageListener;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.io.File;

import static com.zhi.volley.image.Image.Format;
import static com.zhi.volley.image.ImageDecoder.DecodeRequest;
import static com.zhi.volley.image.ImageRequest.REQUEST_FILE_IMAGE;
import static com.zhi.volley.image.ImageRequest.REQUEST_NETWORK_IMAGE;
import static com.zhi.volley.image.ImageRequest.REQUEST_RESOURCE_IMAGE;

/**
 * This class is an subclass of {@link android.widget.ImageView}, but with extra characters:
 * 1. Support load network or local image asynchronously.
 * {@link #setImageUrl(String, Object)}, load a network image;
 * {@link #setImageResource(int, boolean, Object)} load resource imagel;
 * {@link #setImageFile(String, Object)} load local image with absolute path.
 * <p/>
 * 2. Support display gif image. The gif decoder will decode the gif frames in memory. But it'll use
 * as leas memory as possible.
 * 3. Support the gif displaying alpha in or out animation.
 *
 * @attr R.attr.CacheImageView_shouldAnimate.
 * @attr R.attr.CacheImageView_animateDuration.
 * @attr R.attr.CacheImageView_defaultImage.
 * @attr R.attr.CacheImageView_errorImage.
 * @attr R.attr.CacheImageView_gifEnabled.
 */
public class CacheImageView extends ImageView implements ImageDecoder.DecodeListener {
    private static final int DEFAULT_ANIMATION_DURATION_MS = 300;

    private Object mData;
    private Object mImageTag;
    private int mRequestType = ImageRequest.REQUEST_NONE_IMAGE;

    private int mErrorImageId;
    private int mDefaultImageId;
    private boolean mShouldAnimate;
    private long mAnimateDuration;
    private ImageContainer mImageContainer;

    private boolean mGifEnabled;
    private boolean mGifPlaying;
    private DecodeRequest mRequest;

    public CacheImageView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public CacheImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CacheImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CacheImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null) {
            mShouldAnimate = false;
            mGifEnabled = false;
        } else {
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.CacheImageView, defStyleAttr, defStyleRes);
            mShouldAnimate = a.getBoolean(R.styleable.CacheImageView_shouldAnimate, false);
            mAnimateDuration = a.getInteger(R.styleable.CacheImageView_animateDuration,
                    DEFAULT_ANIMATION_DURATION_MS);
            mGifEnabled = a.getBoolean(R.styleable.CacheImageView_gifEnabled, true);
            mErrorImageId = a.getResourceId(R.styleable.CacheImageView_errorImage, 0);
            mDefaultImageId = a.getResourceId(R.styleable.CacheImageView_defaultImage, 0);
            a.recycle();
        }
    }

    /**
     * Sets URL of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link #setDefaultImageResId(int)} on the view.
     * <p/>
     * NOTE: If applicable, {@link #setDefaultImageResId(int)} and
     * {@link #setErrorImageResId(int)} should be called prior to calling this function.
     *
     * @param url The URL that should be loaded into this ImageView.
     * @param tag This tag will be used to cancel the ongoing request, maybe null.
     *            But it'd be identify equality. Maybe Activity.lass, Fragment.class or View.class.
     * @return This image view.
     */
    public CacheImageView setImageUrl(String url, @Nullable Object tag) {
        mRequestType = REQUEST_NETWORK_IMAGE;
        mData = url;
        mImageTag = tag;
        loadImageIfNecessary(false);
        return this;
    }

    /**
     * Sets Resource of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link #setDefaultImageResId(int)} on the view.
     * NOTE: If applicable, {@link #setDefaultImageResId(int)} and
     * {@link #setErrorImageResId(int)} should be called prior to calling this function.
     *
     * @param resId  The resource id of the image.
     * @param decode True the image will be loaded asynchronously, otherwise is synchronized.
     * @param tag    This tag will be used to cancel the ongoing request, maybe null.
     *               But it'd be identify equality. Maybe Activity.lass, Fragment.class or View.class.
     * @return This image view.
     */
    public CacheImageView setImageResource(@DrawableRes int resId, boolean decode, @Nullable Object tag) {
        if (decode) {
            mRequestType = REQUEST_RESOURCE_IMAGE;
            mData = resId;
            mImageTag = tag;
            loadImageIfNecessary(false);
        } else {
            mRequestType = ImageRequest.REQUEST_NONE_IMAGE;
            setImageResource(resId);
        }
        return this;
    }

    /**
     * Sets file of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link #setDefaultImageResId(int)} on the view.
     * NOTE: If applicable, {@link #setDefaultImageResId(int)} and
     * {@link #setErrorImageResId(int)} should be called prior to calling this function.
     *
     * @param file The file name of the image.
     * @param tag  This tag will be used to cancel the ongoing request, maybe null.
     *             But it'd be identify equality. Maybe Activity.lass, Fragment.class or View.class.
     * @return This image view.
     */
    public CacheImageView setImageFile(String file, @Nullable Object tag) {
        mRequestType = REQUEST_FILE_IMAGE;
        mData = file;
        mImageTag = tag;
        loadImageIfNecessary(false);
        return this;
    }

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     *
     * @param defaultImage The default image resource ID.
     * @return This image view.
     */
    public CacheImageView setDefaultImageResId(@DrawableRes int defaultImage) {
        mDefaultImageId = defaultImage;
        return this;
    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     *
     * @param errorImage The error image resource ID.
     * @return This image view.
     */
    public CacheImageView setErrorImageResId(@DrawableRes int errorImage) {
        mErrorImageId = errorImage;
        return this;
    }

    /**
     * Sets whether the image should be added with fade in animation.
     *
     * @param shouldAnimate true the image will be load with fade in animation, otherwise not.
     * @return This image view.
     */
    public CacheImageView setShouldAnimate(boolean shouldAnimate) {
        mShouldAnimate = shouldAnimate;
        return this;
    }

    /**
     * Sets the animate duration.
     *
     * @param animateDuration the fade in duration in time milliseconds.
     * @return This image view.
     */
    public CacheImageView setAnimateDuration(long animateDuration) {
        mAnimateDuration = animateDuration;
        return this;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    /**
     * Loads the image for the view if it isn't already loaded.
     *
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     *                       Long time consuming work should not be done in onLayout.
     */
    void loadImageIfNecessary(final boolean isInLayoutPass) {
        final int width = getWidth();
        final int height = getHeight();

        final LayoutParams layoutParams = getLayoutParams();
        final boolean wrapWidth = layoutParams != null && layoutParams.width == LayoutParams.WRAP_CONTENT;
        final boolean wrapHeight = layoutParams != null && layoutParams.width == LayoutParams.WRAP_CONTENT;

        // If the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        final boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        // Calculate the max image mWidth / mHeight to use while ignoring WRAP_CONTENT dimens.
        final int maxWidth = wrapWidth ? 0 : width;
        final int maxHeight = wrapHeight ? 0 : height;

        // If the data to be loaded in this view is empty, cancel any old load
        // and clear the currently loaded image.
        if (mData == null) {
            onLoadImageCanceled();
            setDefaultImage(isInLayoutPass);
            return;
        }

        // If there was an old loading in this view, check if it needs to be canceled.
        if (mImageContainer != null) {
            if (Objects.equals(mImageContainer.getData(), mData)) {
                // if the loading is from the same Data, and previous is also the same loading.
                return;
            } else {
                // if there is a pre-existing loading, cancel it if it's fetching a different Data.
                mImageContainer.cancel();
            }
        }

        setCacheDrawable(null, isInLayoutPass);

        // The pre-existing content of this view didn't match the current Data. Load the new image.
        final ImageLoader imageLoader = Image.getInstance().getImageLoader();
        final ImageListener imageListener = new ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setErrorImage(false);
            }

            @Override
            public void onResponse(final ImageContainer response, boolean isImmediate) {
                // If this was an immediate response that was delivered inside of a layout
                // pass do not set the image immediately as it will trigger a requestLayout
                // inside of a layout. Instead, defer setting the image by posting back to
                // the main thread.
                if (isImmediate && isInLayoutPass) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            onResponse(response, false);
                        }
                    });
                    return;
                }

                final CacheDrawable drawable = response.getDrawable();
                setCacheDrawable(drawable, !isImmediate);
            }
        };

        ImageContainer newContainer = null;
        switch (mRequestType) {
            case REQUEST_NETWORK_IMAGE:
                newContainer = imageLoader.request((String) mData, mImageTag,
                        maxWidth, maxHeight, imageListener);
                break;

            case REQUEST_FILE_IMAGE:
                newContainer = imageLoader.decodeFile((String) mData, mImageTag,
                        maxWidth, maxHeight, imageListener);
                break;

            case REQUEST_RESOURCE_IMAGE:
                newContainer = imageLoader.decodeResource((Integer) mData, mImageTag,
                        maxWidth, maxHeight, imageListener);
                break;
        }

        mImageContainer = newContainer;
    }

    /**
     * Set this view to display default image specified by {@link #setDefaultImageResId(int)}.
     *
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     *                       Long time consuming work should not be done in onLayout.
     */
    private void setDefaultImage(boolean isInLayoutPass) {
        if (isInLayoutPass) {
            post(new Runnable() {
                @Override
                public void run() {
                    setDefaultImage(false);
                }
            });
            return;
        }

        if (mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        } else {
            // Maybe a default image is better.
            setImageBitmap(null);
        }
    }

    /**
     * Set this view to display error image specified by {@link #setErrorImageResId(int)}.
     *
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     *                       Long time consuming work should not be done in onLayout.
     */
    private void setErrorImage(boolean isInLayoutPass) {
        if (isInLayoutPass) {
            post(new Runnable() {
                @Override
                public void run() {
                    setErrorImage(false);
                }
            });
            return;
        }

        if (mErrorImageId != 0) {
            setImageResource(mErrorImageId);
        } else {
            setImageBitmap(null);
        }
    }

    /**
     * Cancel the previous loaded image.
     */
    private void onLoadImageCanceled() {
        mData = null;
        if (mImageContainer != null) {
            mImageContainer.cancel();
            mImageContainer = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        onLoadImageCanceled();
        // Clear the drawable.
        setImageDrawable(null);
        stopPlaying();
        super.onDetachedFromWindow();
    }

    /**
     * Callback to be invoked when a image drawable is loaded.
     *
     * @param drawable the cache drawable.
     */
    protected void setCacheDrawable(CacheDrawable drawable, boolean isImmediate) {
        if (drawable == null) {
            if (mGifEnabled && mGifPlaying && getDrawable() != null) {
                // ignore the default image set.
            } else {
                setDefaultImage(isImmediate);
            }
        } else if (drawable.hasValidBitmap()) {
            setImageDrawable((Drawable) drawable, isImmediate);
            if (mGifEnabled && mGifPlaying) {
                startPlayingInternal();
            }
        }
    }

    private boolean startPlayingInternal() {
        if (!(getDrawable() instanceof CacheBitmapDrawable)) {
            return false;
        }
        final CacheBitmapDrawable drawable = (CacheBitmapDrawable) getDrawable();
        if (drawable.getFormat() != Format.GIF) {
            return false;
        }

        final String cacheKey = String.valueOf(mData);
        final File file = Image.getInstance().getImageCache().getExtraFileForKey(cacheKey);
        if (file != null) {
            if (mRequest != null) {
                if (mRequest.isDecoding(file.getAbsolutePath())) {
                    return true;
                }
                mRequest.cancel();
            }
            mRequest = new DecodeRequest(file.getAbsolutePath(), mImageTag, 0, 0, this);
            Image.getInstance().getGifImageDecoder().add(mRequest);
            return true;
        }
        return false;
    }

    private boolean stopPlayingInternal() {
        if (mRequest != null) {
            mRequest.cancel();
            mRequest = null;
        }
        return true;
    }

    /**
     * Start the gif playing animation if this image view displaying a Gif image.
     *
     * @return This CacheImageView Object.
     */
    public CacheImageView startPlaying() {
        if (mGifEnabled) {
            mGifPlaying = true;
            startPlayingInternal();
        }
        return this;
    }

    /**
     * Stop the gif playing animation if this image  view displaying a Gif image.
     *
     * @return This CacheImageView Object.
     */
    public CacheImageView stopPlaying() {
        if (mGifEnabled) {
            mGifPlaying = false;
            stopPlayingInternal();
        }
        return this;
    }

    @Override
    public void onCanceled() {
        if (mImageContainer != null) {
            setImageDrawable((Drawable) mImageContainer.getDrawable());
        } else {
            loadImageIfNecessary(false);
        }
    }

    @Override
    public void onError(int code, VolleyError error) {
        stopPlaying();
    }

    @Override
    public void onResponse(int frameCount, int framePointer, Bitmap bitmap) {
        setImageBitmap(bitmap);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void setImageDrawable(Drawable drawable, boolean isImmediate) {
        final Drawable oldDrawable = getDrawable();
        final boolean animate = mShouldAnimate && isImmediate;
        if (animate) {
            if (Utils.hasHoneycomb()) {
                setAlpha(0f);
                super.setImageDrawable(drawable);
                animate().alpha(1f).setDuration(mAnimateDuration);
            } else {
                final TransitionDrawable transitionDrawable = new TransitionDrawable(
                        new Drawable[]{new ColorDrawable(getResources().getColor(android.R.color.transparent)), drawable});
                super.setImageDrawable(transitionDrawable);
                transitionDrawable.startTransition((int) mAnimateDuration);
                drawable = transitionDrawable;
            }
        } else {
            super.setImageDrawable(drawable);
        }

        // Maybe the drawable is the same as oldDrawable.
        notifyDrawable(drawable, true);
        notifyDrawable(oldDrawable, false);
    }


    @Override
    public void setImageDrawable(Drawable drawable) {
        setImageDrawable(drawable, false);
    }

    @Override
    public void setBackground(Drawable background) {
        final Drawable oldBackground = getBackground();
        super.setBackground(background);

        // Maybe the background is the same as oldBackground.
        notifyDrawable(background, true);
        notifyDrawable(oldBackground, false);
    }

    /**
     * Notify the drawable display state changed, from being displayed to hidden.
     *
     * @param drawable    The drawable.
     * @param isDisplayed True if the drawable is displayed, otherwise false.
     */
    private static void notifyDrawable(Drawable drawable, final boolean isDisplayed) {
        if (drawable instanceof CacheDrawable) {
            ((CacheDrawable) drawable).setDisplayState(isDisplayed);
        }
        // Layer drawable notify every layer drawable.
        else if (drawable instanceof LayerDrawable) {
            final LayerDrawable layerDrawable = (LayerDrawable) drawable;
            final int layerCount = layerDrawable.getNumberOfLayers();
            for (int i = 0; i < layerCount; i++) {
                notifyDrawable(layerDrawable.getDrawable(i), isDisplayed);
            }
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }
}
