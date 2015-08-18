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

package com.zhi.common.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.util.SparseArrayCompat;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;

/**
 * Static utility methods pertaining Sparse Array instances.
 */
public final class SparseArrays {

    private SparseArrays() {
    }

    /**
     * Create an empty {@code SparseArray} instance.
     */
    public static <E> SparseArray<E> newSparseArray() {
        return new SparseArray<>();
    }

    /**
     * Creates an {@code SparseArray} instance backed by an array with the specified
     * initial size.
     */
    public static <E> SparseArray<E> newSparseArray(int capacity) {
        return new SparseArray<>(capacity);
    }

    /**
     * Create an empty {@code SparseBooleanArray} instance.
     */
    public static SparseBooleanArray newSparseBooleanArray() {
        return new SparseBooleanArray();
    }

    /**
     * Creates an {@code SparseBooleanArray} instance backed by an array with the specified
     * initial size.
     */
    public static SparseBooleanArray newSparseBooleanArray(int capacity) {
        return new SparseBooleanArray(capacity);
    }

    /**
     * Create an empty {@code SparseIntArray} instance.
     */
    public static SparseIntArray newSparseIntArray() {
        return new SparseIntArray();
    }

    /**
     * Creates an {@code SparseIntArray} instance backed by an array with the specified
     * initial size.
     */
    public static SparseIntArray newSparseIntArray(int capacity) {
        return new SparseIntArray(capacity);
    }

    /**
     * Create an empty {@code SparseLongArray} instance.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static SparseLongArray newSparseLongArray() {
        return new SparseLongArray();
    }

    /**
     * Creates an {@code SparseLongArray} instance backed by an array with the specified
     * initial size.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static SparseLongArray newSparseLongArray(int capacity) {
        return new SparseLongArray(capacity);
    }

    /**
     * Create an empty {@code SparseArrayCompat} instance.
     */
    public static <E> SparseArrayCompat<E> newSparseArrayCompat() {
        return new SparseArrayCompat<>();
    }

    /**
     * Creates an {@code SparseArrayCompat} instance backed by an array with the specified
     * initial size.
     */
    public static <E> SparseArrayCompat<E> newSparseArrayCompat(int capacity) {
        return new SparseArrayCompat<>(capacity);
    }

    /**
     * Create an empty {@code LongSparseArray} instance.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static <E> LongSparseArray<E> newLongSparseArray() {
        return new LongSparseArray<>();
    }

    /**
     * Creates an {@code LongSparseArray} instance backed by an array with the specified
     * initial size.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static <E> LongSparseArray<E> newLongSparseArray(int capacity) {
        return new LongSparseArray<>(capacity);
    }

    /**
     * Create an empty {@code android.support.v4.util.LongSparseArray} instance.
     */
    public static <E> android.support.v4.util.LongSparseArray<E> newLongSparseArrayCmpat() {
        return new android.support.v4.util.LongSparseArray<>();
    }

    /**
     * Creates an {@code android.support.v4.util.LongSparseArray} instance backed by an array with the specified
     * initial size.
     */
    public static <E> android.support.v4.util.LongSparseArray<E> newLongSparseArrayCmpat(int capacity) {
        return new android.support.v4.util.LongSparseArray<>(capacity);
    }

    /**
     * Returns whether this {@code SparseArray} contains no elements.
     *
     * @return {@code true} if this {@code SparseArray} has no elements, {@code false} otherwise.
     */
    public static <E> boolean isEmpty(SparseArray<E> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    /**
     * Returns whether this {@code SparseArray} contains no elements.
     *
     * @return {@code true} if this {@code SparseArray} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(SparseBooleanArray sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    /**
     * Returns whether this {@code SparseArray} contains no elements.
     *
     * @return {@code true} if this {@code SparseArray} has no elements, {@code false} otherwise.
     */
    public static boolean isEmpty(SparseIntArray sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    /**
     * Returns whether this {@code SparseArray} contains no elements.
     *
     * @return {@code true} if this {@code SparseArray} has no elements, {@code false} otherwise.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isEmpty(SparseLongArray sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    /**
     * Returns whether this {@code SparseArray} contains no elements.
     *
     * @return {@code true} if this {@code SparseArray} has no elements, {@code false} otherwise.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static <E> boolean isEmpty(LongSparseArray<E> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    /**
     * Returns whether this {@code SparseArray} contains no elements.
     *
     * @return {@code true} if this {@code SparseArray} has no elements, {@code false} otherwise.
     */
    public static <E> boolean isEmpty(android.support.v4.util.LongSparseArray<E> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }
}
