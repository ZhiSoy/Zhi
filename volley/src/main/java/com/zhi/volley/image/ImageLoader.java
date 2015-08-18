/**
 * Copyright (C) 2013 The Android Open Source Project
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
 */
package com.zhi.volley.image;

import com.zhi.common.util.Lists;
import com.zhi.common.util.Maps;
import com.zhi.volley.Request;
import com.zhi.volley.RequestQueue;
import com.zhi.volley.Response;
import com.zhi.volley.VolleyError;

import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Looper;

import java.util.LinkedList;
import java.util.Map;

/**
 * Helper that handles loading and caching images from remote URLs or local Data.
 */
public final class ImageLoader {
    /**
     * RequestQueue for dispatching Image Request onto.
     */
    private final RequestQueue mImageQueue;

    /**
     * Amount of time to wait after first response arrives before delivering all responses.
     */
    private int mBatchResponseDelayMs = 100;

    /**
     * The cache implementation to be used as an L1 cache before calling into volley.
     */
    private final ImageCache mCache;

    /**
     * HashMap of Cache keys -> BatchedImageRequest used to track in-flight requests so
     * that we can coalesce multiple requests to the same Data into a single request.
     */
    private final Map<String, BatchedImageRequest> mInFlightRequests = Maps.newHashMap();

    /**
     * HashMap of the currently pending responses (waiting to be delivered).
     */
    private final Map<String, BatchedImageRequest> mBatchedResponses = Maps.newHashMap();

    /**
     * Handler to the main thread.
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Runnable for in-flight response delivery.
     */
    private Runnable mRunnable;

    /**
     * Constructs a new ImageLoader.
     *
     * @param queue      The ImageQueue to use for making image requests.
     * @param imageCache The cache to use as an L1 cache.
     */
    public ImageLoader(RequestQueue queue, ImageCache imageCache) {
        mImageQueue = queue;
        mCache = imageCache;
    }

    /**
     * Returns an ImageContainer for the requested URL.
     * <p/>
     * The ImageContainer will contain either the specified default bitmap or the loaded bitmap.
     * If the default was returned, the {@link ImageLoader} will be invoked when the request is fulfilled.
     *
     * @param url      The URL of the image to be loaded.
     * @param tag      The tag used to cancel the image request.
     * @param listener The listener to call when the remote image is loaded
     * @return A container object that contains all of the properties of the request, as well as
     * the currently available image (default if remote is not loaded).
     */
    public ImageContainer request(String url, Object tag, ImageListener listener) {
        return request(url, tag, 0, 0, listener);
    }

    /**
     * Issues a bitmap request with the given URL if that image is not available in the cache,
     * and returns a bitmap container that contains all of the data relating to the request
     * (as well as the default image if the requested image is not available).
     *
     * @param url       The url of the remote image
     * @param tag       The tag used to cancel the image request.
     * @param maxWidth  The maximum mWidth of the returned image.
     * @param maxHeight The maximum mHeight of the returned image.
     * @param listener  The listener to call when the remote image is loaded
     * @return A container object that contains all of the properties of the request, as well as
     * the currently available image (default if remote is not loaded).
     */
    public ImageContainer request(String url, Object tag, int maxWidth, int maxHeight, ImageListener listener) {
        return get(ImageRequest.REQUEST_NETWORK_IMAGE, url, tag, maxWidth, maxHeight, listener);
    }

    /**
     * Returns an ImageContainer for the decoded resource.
     * <p/>
     * The ImageContainer will contain either the specified default bitmap or the loaded bitmap. If
     * the default was returned, the {@link ImageLoader} will be invoked when the request is fulfilled.
     *
     * @param resId    The resource id of the image.
     * @param listener The listener to call when the remote image is loaded
     * @return A container object that contains all of the properties of the decode, as well as
     * the currently available image (default if remote is not loaded).
     */
    public ImageContainer decodeResource(int resId, Object tag, ImageListener listener) {
        return decodeResource(resId, tag, 0, 0, listener);
    }

    /**
     * Issues a bitmap decode with the given resource if that image is not available in the cache,
     * and returns a bitmap container that contains image cache key and the decode listener
     * relating to the decode.
     *
     * @param resId     The resource id of the image.
     * @param maxWidth  The maximum mWidth of the returned image.
     * @param maxHeight The maximum mHeight of the returned image.
     * @param listener  The listener to call when the remote image is loaded
     * @return A container object that contains all of the properties of the decode, as well as
     * the currently available image (default if remote is not loaded).
     */
    public ImageContainer decodeResource(int resId, Object tag, int maxWidth, int maxHeight, ImageListener listener) {
        return get(ImageRequest.REQUEST_RESOURCE_IMAGE, resId, tag, maxWidth, maxHeight, listener);
    }

    /**
     * Returns an ImageContainer for the decoded file.
     * <p/>
     * The ImageContainer will contain either the specified default bitmap or the loaded bitmap. If
     * the default was returned, the {@link ImageLoader} will be invoked when the request is fulfilled.
     *
     * @param file     The file name of the image.
     * @param listener The listener to call when the remote image is loaded
     * @return A container object that contains all of the properties of the decode, as well as
     * the currently available image (default if remote is not loaded).
     */
    public ImageContainer decodeFile(String file, Object tag, ImageListener listener) {
        return decodeFile(file, tag, 0, 0, listener);
    }

    /**
     * Issues a bitmap decode with the given file if that image is not available in the cache,
     * and returns a bitmap container that contains image cache key and the decode listener
     * relating to the decode.
     *
     * @param file      The file name of the image.
     * @param maxWidth  The maximum mWidth of the returned image.
     * @param maxHeight The maximum mHeight of the returned image.
     * @param listener  The listener to call when the remote image is loaded
     * @return A container object that contains all of the properties of the decode, as well as
     * the currently available image (default if remote is not loaded).
     */
    public ImageContainer decodeFile(String file, Object tag, int maxWidth, int maxHeight, ImageListener listener) {
        return get(ImageRequest.REQUEST_FILE_IMAGE, file, tag, maxWidth, maxHeight, listener);
    }

    /**
     * Issues a image request with the given Data if that image is not available
     * in the cache, and returns a drawable container that contains all of the data
     * relating to the request (as well as the default image if the requested
     * image is not available).
     *
     * @param type     The image request type.  Request image type. One of the following value:
     *                 {@link ImageRequest#REQUEST_NETWORK_IMAGE},
     *                 {@link ImageRequest#REQUEST_RESOURCE_IMAGE},
     *                 {@link ImageRequest#REQUEST_FILE_IMAGE}.
     * @param data     The request data that was specified.
     * @param tag      The tag used to cancel the image request.
     * @param listener The listener to call when the remote image is loaded
     * @return A container object that contains all of the properties of the request, as well as
     * the currently available image (default if remote is not loaded).
     */
    private ImageContainer get(int type, Object data, Object tag, int maxWidth, int maxHeight,
            ImageListener listener) {
        // only fulfill requests that were initiated from the main thread.
        throwIfNotOnMainThread();

        final String cacheKey = Image.getCacheKey(data, maxWidth, maxHeight);
        final ImageContainer container = new ImageContainer(data, cacheKey, listener, null);

        // Try to look up the request in the cache of remote images.
        final CacheDrawable drawable = mCache.getFromMemCache(cacheKey);
        if (drawable != null && drawable.hasValidBitmap()) {
            // Return the cached bitmap.
            container.mDrawable = drawable;
            listener.onResponse(container, true);
            return container;
        }

        // The bitmap did not exist in the cache, fetch it!
        // Update the caller to let them know that they should use the default bitmap.
        listener.onResponse(container, true);

        // Check to see if a request is already in-flight.
        final BatchedImageRequest request = mInFlightRequests.get(cacheKey);
        if (request != null) {
            // If it is, add this request to the list of containers.
            request.addContainer(container);
            return container;
        }

        // Send the new request to the network and track it.
        final ImageRequest newRequest = new ImageRequest(type,
                data, maxWidth, maxHeight,
                Config.RGB_565, new Response.Listener<CacheDrawable>() {
            @Override
            public void onResponse(CacheDrawable response) {
                onImageRequestSuccess(cacheKey, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onImageRequestError(cacheKey, error);
            }
        });

        // Set the request tag.
        newRequest.setTag(tag);

        mImageQueue.add(newRequest);
        mInFlightRequests.put(cacheKey, new BatchedImageRequest(newRequest, container));
        return container;
    }

    /**
     * Sets the amount of time to wait after the first response arrives before delivering all
     * responses. Batching can be disabled entirely by passing in 0.
     *
     * @param newBatchedResponseDelayMs The time in milliseconds to wait.
     * @return The image loader itself.
     */
    public ImageLoader setBatchedResponseDelay(int newBatchedResponseDelayMs) {
        mBatchResponseDelayMs = newBatchedResponseDelayMs;
        return this;
    }

    /**
     * Handler for when an image was successfully loaded.
     *
     * @param cacheKey The cache key that is associated with the image request.
     * @param response The bitmap that was returned from the network.
     */
    private void onImageRequestSuccess(String cacheKey, CacheDrawable response) {
        // cache the image that was fetched.
        mCache.addToMemCache(cacheKey, response);

        // remove the request from the list of in-flight requests.
        final BatchedImageRequest request = mInFlightRequests.remove(cacheKey);
        if (request != null) {
            // Update the response bitmap.
            request.drawable = response;

            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }

    /**
     * Handler for when an image failed to load.
     *
     * @param cacheKey The cache key that is associated with the image request.
     */
    private void onImageRequestError(String cacheKey, VolleyError error) {
        // Notify the requests that something failed via a null result.
        // Remove this request from the list of in-flight requests.
        final BatchedImageRequest request = mInFlightRequests.remove(cacheKey);
        if (request != null) {
            // Set the error for this request
            request.error = error;

            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }


    /**
     * Starts the runnable for batched delivery of responses if it is not already started.
     *
     * @param cacheKey The cacheKey of the response being delivered.
     * @param request  The BatchedImageRequest to be delivered.
     */
    private void batchResponse(String cacheKey, BatchedImageRequest request) {
        mBatchedResponses.put(cacheKey, request);
        // If we don't already have a batch delivery runnable in flight, make a new one.
        // Note that this will be used to deliver responses to all callers in mBatchedResponses.
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    for (BatchedImageRequest bir : mBatchedResponses.values()) {
                        for (ImageContainer container : bir.containers) {
                            // If one of the callers in the batched request canceled the request
                            // after the response was received but before it was delivered,
                            // skip them.
                            if (container.mListener == null) {
                                continue;
                            }
                            if (bir.error == null) {
                                container.mDrawable = bir.drawable;
                                container.mListener.onResponse(container, false);
                            } else {
                                container.mListener.onErrorResponse(bir.error);
                            }
                        }
                    }
                    mBatchedResponses.clear();
                    mRunnable = null;
                }
            };
            // Post the runnable.
            mHandler.postDelayed(mRunnable, mBatchResponseDelayMs);
        }
    }

    /**
     * Container object for all of the data surrounding an image request.
     */
    public class ImageContainer {
        /**
         * The request data that was specified
         */
        private final Object mData;

        /**
         * The listener used to do callback.
         */
        private final ImageListener mListener;

        /**
         * The cache key that was associated with the request
         */
        private final String mCacheKey;

        /**
         * The most relevant bitmap for the container. If the image was in cache, the
         * Holder to use for the final bitmap (the one that pairs to the requested URL).
         */
        private CacheDrawable mDrawable;

        /**
         * Constructs a ImageContainer object.
         *
         * @param data     The request data for this container.
         * @param cacheKey The cache key that identifies the request data for this container.
         * @param listener The listener used to do callback.
         * @param drawable The final drawable (if it exists).
         */
        private ImageContainer(Object data, String cacheKey, ImageListener listener,
                CacheDrawable drawable) {
            mData = data;
            mCacheKey = cacheKey;
            mListener = listener;
            mDrawable = drawable;
        }

        /**
         * Releases interest in the in-flight request (and cancels it if no one else is listening).
         */
        public void cancel() {
            if (mListener == null) {
                return;
            }

            BatchedImageRequest request = mInFlightRequests.get(mCacheKey);
            if (request != null) {
                boolean canceled = request.removeContainerAndCancelIfNecessary(this);
                if (canceled) {
                    mInFlightRequests.remove(mCacheKey);
                }
            } else {
                // check to see if it is already batched for delivery.
                request = mBatchedResponses.get(mCacheKey);
                if (request != null) {
                    boolean canceled = request.removeContainerAndCancelIfNecessary(this);
                    if (canceled) {
                        mBatchedResponses.remove(mCacheKey);
                    }
                }
            }
        }

        /**
         * Returns the request Data for this container.
         */
        public Object getData() {
            return mData;
        }

        /**
         * Return the cache key that was associated with the request.
         */
        public String getCacheKey() {
            return mCacheKey;
        }

        /**
         * Returns the CacheDrawable associated with the request Data if it has been loaded,
         * null otherwise.
         */
        public CacheDrawable getDrawable() {
            return mDrawable;
        }

    }

    /**
     * Wrapper class used to map a Image Request to the set of active ImageContainer objects that are
     * interested in its results.
     */
    private class BatchedImageRequest {

        /**
         * The request being tracked
         */
        final Request<?> request;

        /**
         * The result of the request being tracked by this item
         */
        CacheDrawable drawable;

        /**
         * Error if one occurred for this response
         */
        VolleyError error;

        /**
         * List of all of the active ImageContainers that are interested in the request
         */
        final LinkedList<ImageContainer> containers = Lists.newLinkedList();

        /**
         * Constructs a new {@link BatchedImageRequest} object
         *
         * @param request   The request being tracked
         * @param container The ImageContainer of the person who initiated the request.
         */
        BatchedImageRequest(Request<?> request, ImageContainer container) {
            this.request = request;
            containers.add(container);
        }

        /**
         * Adds another ImageContainer to the list of those interested in the results of the request.
         */
        void addContainer(ImageContainer container) {
            containers.add(container);
        }

        /**
         * Detaches the bitmap container from the request and cancels the request
         * if no one is left listening.
         *
         * @param container The container to remove from the list
         * @return True if the request was canceled, false otherwise.
         */
        boolean removeContainerAndCancelIfNecessary(ImageContainer container) {
            containers.remove(container);
            if (containers.size() == 0) {
                request.cancel();
                return true;
            }
            return false;
        }

    }

    private static void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
        }
    }

    /**
     * Interface for the response handlers on image requests.
     * <p/>
     * The call flow is this:
     * 1. Upon being  attached to a request, onResponse(response, true) will
     * be invoked to reflect any cached data that was already available. If the
     * data was available, response.getDrawable() will be non-null.
     * <p/>
     * 2. After a network response returns, only one of the following cases will happen:
     * - onResponse(response, false) will be called if the image was loaded.
     * or
     * - onErrorResponse will be called if there was an error loading the image.
     */
    public interface ImageListener extends Response.ErrorListener {

        /**
         * Listens for non-error changes to the loading of the image request.
         *
         * @param response    Holds all information pertaining to the request, as well
         *                    as the bitmap (if it is loaded).
         * @param isImmediate True if this was called during ImageLoader.get() variants.
         *                    This can be used to differentiate between a cached image loading and a network
         *                    image loading in order to, for example, run an animation to fade in network loaded
         *                    images.
         */
        void onResponse(ImageContainer response, boolean isImmediate);
    }
}
