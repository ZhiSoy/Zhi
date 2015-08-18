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
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Build;
import android.util.SparseArray;

/**
 * A cursor-backed type that can return an object for each row of the cursor. This class is most useful when:
 * <ol>
 * <li>The cursor is returned in conjunction with an AsyncTaskLoader and created off the UI thread.</li>
 * <li>A single row in the cursor specifies everything for an object.</li>
 * </ol>
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ObjectCursor<T> extends CursorWrapper {
    /**
     * The cache for objects in the underlying cursor.
     */
    private final SparseArray<T> mCache;

    /**
     * An object that knows how to construct T objects using cursor.
     */
    private final CursorCreator<T> mFactory;

    /**
     * Creates a new cursor-backed type object cursor.
     *
     * @param cursor The underlying wrapped cursor that contains T type object(s).
     * @param factor An object knows how to construct T objects using cursor.
     */
    public ObjectCursor(Cursor cursor, CursorCreator<T> factor) {
        super(cursor);
        if (cursor != null) {
            mCache = new SparseArray<>(cursor.getCount());
        } else {
            mCache = null;
        }
        mFactory = factor;
    }

    /**
     * Creates a concrete object at the current cursor position. This is no guarantee on object creation:
     * <ul>
     * <li>an object might has been previously created,</li>
     * <li>or the cache might be populated by calling {@link #fillCache()}.</li>
     * </ul>
     * In both these cases, the previously created object is returned.
     *
     * @return The object with type T, or null.
     */
    public final T getModel() {
        T model = null;
        final Cursor c = getWrappedCursor();
        if (c != null) {
            // If the cache contains this object, return it.
            final int curPos = c.getPosition();
            model = mCache.get(curPos);
            // Construct a new object and add it to the cache.
            if (model == null) {
                model = mFactory.createFromCursor(c);
                mCache.put(curPos, model);
            }
        }

        return model;
    }

    /**
     * Read the entire cursor to populate the objects in the cache. Subsequent calls to {@link #getModel()}
     * will return the cached objects as far as the underlying cursor does not cache.
     * <em>As a side effect, the models may be cached away.</em>
     */
    public final void fillCache() {
        final Cursor c = getWrappedCursor();
        if (c != null && c.moveToFirst()) {
            do {
                getModel();
            } while (c.moveToNext());
        }
    }

    @Override
    public void close() {
        super.close();
        mCache.clear();
    }
}
