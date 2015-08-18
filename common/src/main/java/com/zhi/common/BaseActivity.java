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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BaseActivity extends AppCompatActivity implements ApplicationContext, ActivityContext, UiHandler.UiCallback {

    protected final UiHandler mUiHandler = new UiHandler(this);

    @Override
    public Context getActivityContext() {
        return this;
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
     * Finds a view that was identified by the id attribute from the XML that
     * was processed in {@link #onCreate}.
     *
     * @return The view if found or null otherwise.
     * @see #findViewById(int)
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    /**
     * Finds a fragment that was identified by the given id either when inflated
     * from XML or as the container ID when added in a transaction.  This first
     * searches through fragments that are currently added to the manager's
     * activity; if no such fragment is found, then all fragments currently
     * on the back stack associated with this ID are searched.
     *
     * @return The fragment if found or null otherwise.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T extends BaseFragment> T findFragment(int id) {
        return (T) getSupportFragmentManager().findFragmentById(id);
    }

    /**
     * Finds a fragment that was identified by the given tag either when inflated
     * from XML or as supplied when added in a transaction.  This first
     * searches through fragments that are currently added to the manager's
     * activity; if no such fragment is found, then all fragments currently
     * on the back stack are searched.
     *
     * @return The fragment if found or null otherwise.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T extends BaseFragment> T findFragment(String tag) {
        return (T) getSupportFragmentManager().findFragmentByTag(tag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUiHandler.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUiHandler.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUiHandler.setEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiHandler.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUiHandler.setEnabled(false);
    }

    public boolean onActivityBackPressed() {
        super.onBackPressed();
        return true;
    }
}
