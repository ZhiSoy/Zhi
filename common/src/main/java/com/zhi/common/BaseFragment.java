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

package com.zhi.common;

import com.zhi.common.content.UiHandler;
import com.zhi.common.log.LogUtils;
import com.zhi.common.util.References;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.view.View;

import java.lang.ref.WeakReference;

public class BaseFragment extends Fragment implements ActivityContext, ApplicationContext, UiHandler.UiCallback {

    protected final UiHandler mUiHandler = new UiHandler(this);

    @Override
    public Context getApplicationContext() {
        return BaseApplication.getInstance().getApplicationContext();
    }

    @Override
    public final Context getActivityContext() {
        return getActivity();
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public SharedPreferences getPreferences() {
        return BaseApplication.getInstance().getPreferences();
    }

    @Override
    public boolean handleUiMessage(Message msg, int what, boolean enabled) {
        return false;
    }

    /**
     * Return the LoaderManager for this fragment, creating it if needed.
     */
    public LoaderManager getSupportLoaderManager() {
        return getBaseActivity().getSupportLoaderManager();
    }

    /**
     * Return the FragmentManager for this fragment, creating if if needed.
     */
    public FragmentManager getSupportFragmentManager() {
        return getBaseActivity().getSupportFragmentManager();
    }

    /**
     * If this fragment does not have retain state, and have already set a view with
     * {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     * this method is used to retrieve a specified child view.
     *
     * @return The view if found or null otherwise.
     * @see #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     * @see #onViewCreated(android.view.View, android.os.Bundle)
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int id) {
        if (getView() != null) {
            return (T) getView().findViewById(id);
        }
        return null;
    }

    public BaseFragment() {
        // EMPTY CONSTRUCTOR
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUiHandler.setEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mUiHandler.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUiHandler.setEnabled(true);
        onFragmentResume();
    }

    /**
     * Callback to be invoked when the fragment needs to be resume without depends on activity's lifecycle.
     * With this callback, UI update can be done.
     */
    @CallSuper
    public void onFragmentResume() {
        mUiHandler.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiHandler.setEnabled(false);
    }

    /**
     * Callback to be invoked when this fragment needs to be paused independent on activity's lifecycle.
     */
    @CallSuper
    public void onFragmentPause() {
    }

    /**
     * Callback to be invoked when the fragment monitored the System back button pressed.
     * With this callback, exits prompt dialog can be shown.
     *
     * @return true if this fragment consumed this action, otherwise false.
     */
    public boolean onFragmentBackPressed() {
        return false;
    }

    /**
     * Callback to be invoked when the activity monitored the system back button pressed.
     * This is an equivalence of {@link Activity#onBackPressed()}, but this will work with fragment.
     */
    public void onActivityBackPressed() {
        getBaseActivity().onActivityBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiHandler.setEnabled(false);
    }

    /**
     * Small Runnable-like wrapper that first checks that the Fragment is in a good state before
     * doing any work. Ideal for use with a {@link android.os.Handler}.
     */
    public static abstract class FragmentRunnable implements Runnable {
        public static final String TAG = "FragmentRunnable";
        private final String mOpName;
        private final WeakReference<Fragment> mData;

        public FragmentRunnable(String opName, Fragment fragment) {
            mOpName = opName;
            mData = References.newWeakReference(fragment);
        }

        @Override
        public final void run() {
            final Fragment fragment = mData.get();
            if (fragment == null) {
                LogUtils.i(TAG, "Unable to run op='%s' b/c fragment has been recycled.", mOpName);
                return;
            }
            if (!fragment.isAdded()) {
                LogUtils.i(TAG, "Unable to run op='%s' b/c fragment is not attached: %s", mOpName, fragment);
                return;
            }
            go();
        }

        public abstract void go();
    }
}
