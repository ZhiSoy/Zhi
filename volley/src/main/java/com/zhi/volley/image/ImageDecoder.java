package com.zhi.volley.image;

import com.zhi.common.content.UiHandler;
import com.zhi.common.util.Objects;
import com.zhi.common.util.Queues;
import com.zhi.volley.VolleyError;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public abstract class ImageDecoder extends Thread {
    /**
     * Used for telling us to die.
     */
    private volatile boolean mQuit = false;

    private final UiHandler mUiHandler;
    private final BlockingQueue<DecodeRequest> mQueue = Queues.newLinkedBlockingDeque();

    public ImageDecoder(UiHandler uiHandler) {
        mUiHandler = uiHandler;
        mUiHandler.setEnabled(true);
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    /**
     * Add the Decode request to the Working Queue.
     *
     * @param request The decode request to be added.
     */
    public void add(DecodeRequest request) {
        if (request == null || request.listener == null) {
            return;
        }
        mQueue.add(request);
    }

    /**
     * Remove the Decode request from the Working Queue.
     *
     * @param request The decode request to be removed.
     */
    public void remove(DecodeRequest request) {
        mQueue.remove(request);
    }

    @Override
    public void run() {
        while (true) {
            DecodeRequest request = null;
            try {
                request = mQueue.take();

                if (request.canceled) {
                    postCanceled(request);
                    continue;
                }

                final File file = new File(request.fileName);
                if (!file.exists()) {
                    postError(request, DecodeListener.ERROR_FILE_NOT_EXISTS, null);
                    continue;
                }

                decode(request, request.fileName);

            } catch (IOException e) {
                postError(request, DecodeListener.ERROR_IO_EXCEPTION, null);

            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
            } catch (Exception e) {
                postError(request, DecodeListener.ERROR_UNKNOWN, new VolleyError(e));
            }
        }
    }

    /**
     * Decode the input image file.
     *
     * @param fileName The input file absolute path.
     */
    protected abstract void decode(DecodeRequest request, String fileName) throws IOException;

    /**
     * Get current decoded frame index.
     */
    protected abstract int getFramePointer() throws IOException;

    /**
     * Get this image frame count.
     */
    protected abstract int getFrameCount() throws IOException;

    /**
     * Post a cancel callback to Decode Request Listener.
     *
     * @param request Current being executed request.
     */
    protected void postCanceled(final DecodeRequest request) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                request.listener.onCanceled();
            }
        });
    }

    /**
     * Post the decoded result to Decode Request Listener.
     *
     * @param request Current being executed request.
     * @param count   Total frame count, maybe -1. if it's first time to decode frame.
     * @param frame   Currently be decoded image frame.
     * @param bitmap  The result bitmap. Outside should not do any change on it.
     */
    protected void postResponse(final DecodeRequest request,
            final int count, final int frame, final Bitmap bitmap) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!request.canceled) {
                    request.listener.onResponse(count, frame, bitmap);
                }
            }
        });
    }

    /**
     * Post the error response to Decode Request Listener.
     *
     * @param request Currently being executed request.
     * @param error   Decode error.
     */
    protected void postError(final DecodeRequest request, final int code, final VolleyError error) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!request.canceled) {
                    request.listener.onError(code, error);
                }
            }
        });
    }

    public interface DecodeListener {
        int ERROR_FILE_NOT_EXISTS = 0x01;
        int ERROR_IO_EXCEPTION = 0x02;
        int ERROR_UNKNOWN = 0x03;

        /**
         * Callback to be invoked when a decode request is called.
         * This callback is invoked in UI thread.
         */
        void onCanceled();

        /**
         * Callback to be invoked when a image frame is decoded.
         * Thi callback is invoked in UI thread.
         *
         * @param frameCount   image frame total count.
         * @param framePointer current decoded frame index.
         * @param bitmap       current frame relative bitmap.
         */
        void onResponse(int frameCount, int framePointer, @Nullable Bitmap bitmap);

        /**
         * Callback to be invoked when a error happened.
         *
         * @param code  error code defined in {@link DecodeListener}
         * @param error error detail info.
         */
        void onError(int code, @Nullable VolleyError error);
    }

    public static class DecodeRequest {
        final Object tag;
        final int maxWidth;
        final int maxHeight;
        final String fileName;
        final DecodeListener listener;
        boolean canceled;

        public DecodeRequest(String fileName, Object tag, int maxWidth, int maxHeight, DecodeListener listener) {
            this.fileName = fileName;
            this.tag = tag;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.listener = listener;
        }

        /**
         * Cancel potential on-going decode request.
         */
        public DecodeRequest cancel() {
            Image.getInstance().getGifImageDecoder().remove(this);
            canceled = true;
            return this;
        }

        /**
         * Check whether on-going request is decoding fileName specified image.
         *
         * @param fileName To checked image.
         * @return true if this decode request is decoding fileName specified image, otherwise false.
         */
        public boolean isDecoding(String fileName) {
            return Objects.equals(this.fileName, fileName);
        }
    }
}
