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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

/**
 * This class main work is to parser the cursor result into an object that stored in {@link ObjectCursor}.
 * Overriding the {@link #loadInBackground()} is that, the parser work may be a time consuming work. <br/>
 * Begin with JellyBean or api Level 16, Android add Cancel Signal and database query cancel function.
 * In other words, if the user cancel some CursorLoader's work, the read or write database work
 * maybe canceled at the same time.
 *
 * @param <T> the data type to be loaded.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ObjectCursorLoader<T> extends CursorLoader {

    /**
     * The factory that knows how to create T objects.
     */
    private final CursorCreator<T> mFactory;

    public ObjectCursorLoader(Context context, CursorCreator<T> factory) {
        super(context);
        mFactory = factory;
    }

    public ObjectCursorLoader(Context context, CursorCreator<T> factory, Uri uri,
            String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        mFactory = factory;
    }

    /**
     * Override this class to switch the result's cursor type to ObjectCursor in Worker Thread.
     */
    @Override
    public final Cursor loadInBackground() {
        final Cursor cursor = super.loadInBackground();
        if (cursor instanceof ObjectCursor<?>) {
            if (mFactory != null) {
                final ObjectCursor<T> objectCursor = new ObjectCursor<>(cursor, mFactory);
                objectCursor.fillCache();
                return objectCursor;
            }
        }
        return cursor;
    }
}
