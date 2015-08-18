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

package com.zhi.cupid.provider;

import com.zhi.common.log.LogTag;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper for managing {@link SQLiteDatabase} that stores data for
 * {@link CupidProvider}.
 */
public class CupidDatabase extends SQLiteOpenHelper {
    private static final String TAG = LogTag.makeLogTag("CupidDatabase");

    // Any changes to the database format *must* include update-in-place code.
    // Version 1: Add Cloud Database code.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cupid.db";

    public CupidDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
