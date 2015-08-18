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

import com.zhi.common.content.SQLiteContentProvider;
import com.zhi.cupid.sync.SyncHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Provider that stores {@link CupidContract} data. Data is usually inserted
 * by {@link SyncHelper}, and queried by various {@link Activity} instances.
 */
public class CupidProvider extends SQLiteContentProvider {

    private static final UriMatcher sUriMatcher;

    /**
     * A block that instantiates and sets static objects
     */
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    }

    @Override
    protected SQLiteOpenHelper getDatabaseHelper(Context context) {
        return new CupidDatabase(context);
    }

    @Override
    public boolean onCreate() {
        return super.onCreate();
    }

    @Override
    protected Uri insertInTransaction(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    protected int updateInTransaction(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    protected int deleteInTransaction(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    protected void notifyChange() {

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
