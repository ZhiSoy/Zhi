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

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

public class BaseApplication extends Application implements ApplicationContext {

    private static BaseApplication sInstance;

    private int mVersionCode;
    private String mVersionName;
    private SharedPreferences mPreferences;

    public static BaseApplication getInstance() {
        return sInstance;
    }

    @Override
    public SharedPreferences getPreferences() {
        if (mPreferences == null) {
            mPreferences = getSharedPreferences(getPreferencesName(), MODE_PRIVATE);
        }
        return mPreferences;
    }

    /**
     * @return app shared preference name.
     */
    protected String getPreferencesName() {
        return "Teddy";
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    /**
     * @return the application's version code set in the manifest.
     */
    public int getVersionCode() {
        checkVersionInfo();
        return mVersionCode;
    }

    /**
     * @return the application's version name set in the manifest.
     */
    public String getVersionName() {
        checkVersionInfo();
        return mVersionName;
    }

    /**
     * The version info with the format that the pot separated version name and version code,
     * as "zhi.0001".
     */
    public String getVersionInfo() {
        checkVersionInfo();
        return mVersionName + "." + mVersionCode;
    }

    private void checkVersionInfo() {
        if (mVersionName == null || mVersionCode == 0) {
            try {
                final PackageInfo packageInfo = getPackageManager()
                        .getPackageInfo(getApplicationInfo().packageName, 0);
                if (packageInfo != null) {
                    mVersionName = packageInfo.versionName;
                    mVersionCode = packageInfo.versionCode;
                }
            } catch (Exception ignored) {
            }
        }
    }
}
