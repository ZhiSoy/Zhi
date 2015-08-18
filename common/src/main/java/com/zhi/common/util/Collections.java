/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhi.common.util;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Provides static methods for working with {@code Collection} instances.
 * It's has subclass {@link Lists}, {@link Sets}, {@link Maps} and {@link Queues}
 *
 * <em>This class combines the {@link java.util.Collections} and GUAVA Collections.</em>
 */
public class Collections {
    protected Collections() {
    }

    // CUSTOM
    protected static final int DEFAULT_INITIAL_CAPACITY = 4;


    /**
     * Returns whether this {@link Collection} contains no elements.
     *
     * @return {@code true} if this {@code List} has no elements, {@code false} otherwise.
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Returns a count of how many objects this {@code Collection} contains.
     *
     * @return how many objects this {@code Collection} contains, or Integer.MAX_VALUE
     * if there are more than Integer.MAX_VALUE elements in this
     * {@code Collection}, or 0 if this collection is null or empty.
     */
    public static <T> int size(Collection<T> collection) {
        return collection == null ? 0 : collection.size();
    }
    // GUAVA

    /**
     * Delegates to {@link Collection#contains}. Returns {@code false} if the
     * {@code contains} method throws a {@code ClassCastException} or
     * {@code NullPointerException}.
     */
    public static boolean safeContains(Collection<?> collection, @Nullable Object object) {
        try {
            return collection.contains(object);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Delegates to {@link Collection#remove}. Returns {@code false} if the
     * {@code remove} method throws a {@code ClassCastException} or
     * {@code NullPointerException}.
     */
    public static boolean safeRemove(Collection<?> collection, @Nullable Object object) {
        try {
            return collection.remove(object);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Returns best-effort-sized StringBuilder based on the given collection size.
     */
    static StringBuilder newStringBuilderForCollection(int size) {
        return new StringBuilder((int) Math.min(size * 8L, Ints.MAX_POWER_OF_TWO));
    }

    /**
     * Used to avoid http://bugs.sun.com/view_bug.do?bug_id=6558557
     */
    protected static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection<T>) iterable;
    }

    // GUAVA ITERATORS

    /**
     * Adds all elements in {@code iterator} to {@code collection}. The iterator
     * will be left exhausted: its {@code hasNext()} method will return
     * {@code false}.
     *
     * @return {@code true} if {@code collection} was modified as a result of this
     * operation
     */
    public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= addTo.add(iterator.next());
        }
        return wasModified;
    }

    /**
     * Adds all elements in {@code iterable} to {@code collection}.
     *
     * @return {@code true} if {@code collection} was modified as a result of this
     * operation.
     */
    public static <T> boolean addAll(Collection<T> addTo, Iterable<? extends T> elementsToAdd) {
        if (elementsToAdd instanceof Collection) {
            Collection<? extends T> c = Collections.cast(elementsToAdd);
            return addTo.addAll(c);
        }
        return Collections.addAll(addTo, Preconditions.checkNotNull(elementsToAdd).iterator());
    }

    // JDK

    /**
     * Returns an {@code Enumeration} on the specified collection.
     *
     * @param collection the collection to enumerate.
     * @return an Enumeration.
     */
    public static <T> Enumeration<T> enumeration(Collection<T> collection) {
        return java.util.Collections.enumeration(collection);
    }

    /**
     * Searches the specified collection for the maximum element.
     *
     * @param collection the collection to search.
     * @return the maximum element in the Collection.
     * @throws ClassCastException when an element in the collection does not implement
     *                            {@code Comparable} or elements cannot be compared to each
     *                            other.
     */
    public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> collection) {
        return java.util.Collections.max(collection);
    }

    /**
     * Searches the specified collection for the maximum element using the
     * specified comparator.
     *
     * @param collection the collection to search.
     * @param comparator the comparator.
     * @return the maximum element in the Collection.
     * @throws ClassCastException when elements in the collection cannot be compared to each
     *                            other using the {@code Comparator}.
     */
    public static <T> T max(Collection<? extends T> collection, Comparator<? super T> comparator) {
        return java.util.Collections.max(collection, comparator);
    }

    /**
     * Searches the specified collection for the minimum element.
     *
     * @param collection the collection to search.
     * @return the minimum element in the collection.
     * @throws ClassCastException when an element in the collection does not implement
     *                            {@code Comparable} or elements cannot be compared to each
     *                            other.
     */
    public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> collection) {
        return java.util.Collections.min(collection);
    }

    /**
     * Searches the specified collection for the minimum element using the
     * specified comparator.
     *
     * @param collection the collection to search.
     * @param comparator the comparator.
     * @return the minimum element in the collection.
     * @throws ClassCastException when elements in the collection cannot be compared to each
     *                            other using the {@code Comparator}.
     */
    public static <T> T min(Collection<? extends T> collection, Comparator<? super T> comparator) {
        return java.util.Collections.min(collection, comparator);
    }

    /**
     * A comparator which reverses the natural order of the elements. The
     * {@code Comparator} that's returned is {@link Serializable}.
     *
     * @return a {@code Comparator} instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> reverseOrder() {
        return java.util.Collections.reverseOrder();
    }

    /**
     * Returns a {@link Comparator} that reverses the order of the
     * {@code Comparator} passed. If the {@code Comparator} passed is
     * {@code null}, then this method is equivalent to {@link #reverseOrder()}.
     * <p>
     * The {@code Comparator} that's returned is {@link Serializable} if the
     * {@code Comparator} passed is serializable or {@code null}.
     *
     * @param c the {@code Comparator} to reverse or {@code null}.
     * @return a {@code Comparator} instance.
     * @since 1.5
     */
    public static <T> Comparator<T> reverseOrder(Comparator<T> c) {
        return java.util.Collections.reverseOrder(c);
    }

    /**
     * Returns a wrapper on the specified collection which throws an
     * {@code UnsupportedOperationException} whenever an attempt is made to
     * modify the collection.
     *
     * @param collection the collection to wrap in an unmodifiable collection.
     * @return an unmodifiable collection.
     */
    @SuppressWarnings("unchecked")
    public static <E> Collection<E> unmodifiableCollection(Collection<? extends E> collection) {
        return java.util.Collections.unmodifiableCollection(collection);
    }

    /**
     * Returns the number of elements in the {@code Collection} that match the
     * {@code Object} passed. If the {@code Object} is {@code null}, then the
     * number of {@code null} elements is returned.
     *
     * @param c the {@code Collection} to search.
     * @param o the {@code Object} to search for.
     * @return the number of matching elements.
     * @throws NullPointerException if the {@code Collection} parameter is {@code null}.
     * @since 1.5
     */
    public static int frequency(Collection<?> c, Object o) {
        return java.util.Collections.frequency(c, o);
    }

    /**
     * Returns a dynamically typesafe view of the specified collection. Trying
     * to insert an element of the wrong type into this collection throws a
     * {@code ClassCastException}. At creation time the types in {@code c} are
     * not checked for correct type.
     *
     * @param c    the collection to be wrapped in a typesafe collection.
     * @param type the type of the elements permitted to insert.
     * @return a typesafe collection.
     */
    public static <E> Collection<E> checkedCollection(Collection<E> c, Class<E> type) {
        return java.util.Collections.checkedCollection(c, type);
    }

    /**
     * Adds all the specified elements to the specified collection.
     *
     * @param c the collection the elements are to be inserted into.
     * @param a the elements to insert.
     * @return true if the collection changed during insertion.
     * @throws UnsupportedOperationException when the method is not supported.
     * @throws NullPointerException          when {@code c} or {@code a} is {@code null}, or {@code a}
     *                                       contains one or more {@code null} elements and {@code c}
     *                                       doesn't support {@code null} elements.
     * @throws IllegalArgumentException      if at least one of the elements can't be inserted into the
     *                                       collection.
     */
    @SafeVarargs
    public static <T> boolean addAll(Collection<? super T> c, T... a) {
        return java.util.Collections.addAll(c, a);
    }

    /**
     * Returns whether the specified collections have no elements in common.
     *
     * @param c1 the first collection.
     * @param c2 the second collection.
     * @return {@code true} if the collections have no elements in common,
     * {@code false} otherwise.
     * @throws NullPointerException if one of the collections is {@code null}.
     */
    public static boolean disjoint(Collection<?> c1, Collection<?> c2) {
        return java.util.Collections.disjoint(c1, c2);
    }
}
