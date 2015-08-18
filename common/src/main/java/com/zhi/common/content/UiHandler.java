/*
 * Copyright [2015] [zhi]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhi.common.content;

import android.os.Handler;
import android.os.Message;

/**
 * A thin wrapper of {@link Handler} to run a callback in UI thread. Any callback to this handler
 * is guarantee to run inside {@link android.app.Activity} lifecycle. However, it can be dropped if the
 * {@link android.app.Activity} has been stopped. This handler is safe to use with {@link android.app.FragmentTransaction}.
 */
public final class UiHandler extends Handler {
    private final UiCallback mUiCallback;
    private boolean mEnabled = true;

    public UiHandler(UiCallback uiCallback) {
        mUiCallback = uiCallback;
    }

    @Override
    public void dispatchMessage(Message msg) {
        if (mEnabled) {
            return;
        }
        super.dispatchMessage(msg);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mUiCallback != null) {
            if (mUiCallback.handleUiMessage(msg, msg.what, mEnabled)) {
                return;
            }
        }
        super.handleMessage(msg);
    }

    /**
     * To check whether the UiHandler is enabled or not.
     * <I> It's safe to edit UI if the UiHandler is enabled.</I>
     *
     * @return True if UiHandler is enabled, otherwise false.
     */
    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * To enable the Handler or not. <I> It's safe to edit UI if the UiHandler is enabled.</I>
     * When disabled, any pending runnable will be removed.
     *
     * @param enabled Whether to enabled the UiHandler.
     */
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            removeCallbacksAndMessages(null);
        }
        mEnabled = enabled;
    }

    /**
     * Callback to be invoked by UiHandler in UI thread.
     */
    public interface UiCallback {
        /**
         * Handle the message that handled by UiHandler.
         *
         * @param msg     The message received in UI thread.
         * @param what    Them message what member.
         * @param enabled Whether current ui is enabled or not.
         * @return true if this message is handled successfully otherwise false.
         */
        boolean handleUiMessage(Message msg, int what, boolean enabled);
    }
}
