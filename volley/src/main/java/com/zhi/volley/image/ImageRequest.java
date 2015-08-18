/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhi.volley.image;

import com.zhi.volley.DefaultRetryPolicy;
import com.zhi.volley.NetworkResponse;
import com.zhi.volley.ParseError;
import com.zhi.volley.Request;
import com.zhi.volley.Response;
import com.zhi.volley.Response.ErrorListener;
import com.zhi.volley.Response.Listener;
import com.zhi.volley.VolleyLog;
import com.zhi.volley.toolbox.HttpHeaderParser;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import java.io.File;

import static com.zhi.volley.Cache.Entry;
import static com.zhi.volley.image.Image.Format;

/**
 * A canned request for getting an image at a given Data and calling back with a decoded Bitmap.
 * For a gif image, this request only response image bytes data.
 */
public class ImageRequest extends Request<CacheDrawable> {
    /**
     * Socket timeout in milliseconds for image requests
     */
    private static final int IMAGE_TIMEOUT_MS = 5000;

    /**
     * Default number of retries for image requests
     */
    private static final int IMAGE_MAX_RETRIES = 2;

    /**
     * Default backoff multiplier for image requests
     */
    private static final float IMAGE_BACKOFF_MULT = 2f;

    /**
     * Request the network image.
     */
    public static final int REQUEST_NETWORK_IMAGE = 0x00;

    /**
     * Request the none image.
     */
    public static final int REQUEST_NONE_IMAGE = 0x0f;

    /**
     * Request the local image with a resource id.
     */
    public static final int REQUEST_RESOURCE_IMAGE = 0x01;

    /**
     * Request the local image with a file name.
     */
    public static final int REQUEST_FILE_IMAGE = 0x02;

    /**
     * Decoding lock so that we don't decode more than one image at a time (to avoid OOM's)
     */
    private static final Object sDecodeLock = new Object();

    private final int mType;
    private final Object mData;
    private final int mMaxWidth;
    private final int mMaxHeight;
    private final Config mDecodeConfig;
    private final Listener<CacheDrawable> mListener;

    /**
     * Creates a new image request, decoding to a maximum specified mWidth and
     * mHeight. If both mWidth and mHeight are zero, the image will be decoded to
     * its natural size. If one of the two is nonzero, that dimension will be
     * clamped and the other one will be set to preserve the image's aspect
     * ratio. If both mWidth and mHeight are nonzero, the image will be decoded to
     * be fit in the rectangle of dimensions mWidth x mHeight while keeping its
     * aspect ratio.
     *
     * @param type          Request image type. One of the following value:
     *                      {@link #REQUEST_NETWORK_IMAGE},
     *                      {@link #REQUEST_RESOURCE_IMAGE},
     *                      {@link #REQUEST_FILE_IMAGE},
     * @param data          The request image data.
     * @param maxWidth      Maximum mWidth to decode this bitmap to, or zero for none
     * @param maxHeight     Maximum mHeight to decode this bitmap to, or zero for none
     * @param decodeConfig  Format to decode the bitmap to
     * @param listener      Listener to receive the decoded bitmap
     * @param errorListener Error listener, or null to ignore errors
     */
    public ImageRequest(int type, Object data, int maxWidth, int maxHeight, Config decodeConfig,
            Listener<CacheDrawable> listener, ErrorListener errorListener) {
        super(Method.GET, data.toString(), errorListener);

        setRetryPolicy(new DefaultRetryPolicy(IMAGE_TIMEOUT_MS, IMAGE_MAX_RETRIES, IMAGE_BACKOFF_MULT));
        setToNetwork(type == REQUEST_NETWORK_IMAGE);
        setInMemory(false);

        mType = type;
        mData = data;
        mListener = listener;
        mDecodeConfig = decodeConfig;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    protected Response<CacheDrawable> parseNetworkResponse(NetworkResponse response) {
        // Serialize all decode on a global lock to reduce concurrent heap usage.
        synchronized (sDecodeLock) {
            try {
                // The real guts of parseNetworkResponse. Broken out for readability.
                final ImageCache imageCache = Image.getInstance().getImageCache();
                Bitmap bitmap = null;
                Format format = Format.NONE;
                if (mType == REQUEST_NETWORK_IMAGE) {
                    if (response.data != null && response.data.length > 0) {
                        bitmap = Image.decode(response.data,
                                mDecodeConfig, mMaxWidth, mMaxHeight, imageCache);
                        format = Image.decodeFormat(response.data);
                    } else {
                        final File file = imageCache.getExtraFileForKey(getCacheKey());
                        if (file != null && file.exists()) {
                            bitmap = Image.decode(file.getAbsolutePath(),
                                    mDecodeConfig, mMaxWidth, mMaxHeight, imageCache);
                            format = Image.decodeFormat(file);
                        }
                    }
                }
                // resolve local image from a specific file.
                else if (mType == REQUEST_FILE_IMAGE) {
                    final File file = new File((String) mData);
                    if (file.exists()) {
                        bitmap = Image.decode(file.getAbsolutePath(),
                                mDecodeConfig, mMaxWidth, mMaxHeight, imageCache);
                        format = Image.decodeFormat(file);
                    }
                }
                // resolve local image from resources.
                else if (mType == REQUEST_RESOURCE_IMAGE) {
                    final Resources resources = null;// BaseApplication.getInstance().getResources();
                    bitmap = Image.decode(resources, (Integer) mData,
                            mDecodeConfig, mMaxWidth, mMaxHeight, imageCache);
                }

                if (bitmap == null) {
                    return Response.error(new ParseError(response));
                }

                final CacheDrawable cacheDrawable = ImageCache.newCacheDrawable(null, bitmap, format);
                Entry entry = null;
                if (isToNetwork()) {
                    entry = HttpHeaderParser.parseCacheHeaders(response);
                }
                return Response.success(cacheDrawable, entry);

            } catch (OutOfMemoryError e) {
                VolleyLog.e("Caught OOM for %d byte image, url=%s",
                        (response.data == null) ? 0 : response.data.length, getUrl());
                return Response.error(new ParseError(e));
            }
        }
    }

    @Override
    protected void deliverResponse(CacheDrawable response) {
        mListener.onResponse(response);
    }
}
