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
 *
 */
/*
 * This project method some are copied from Google Guava project.
 * If you use, you should contains its copy right.
 */
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

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Static utility methods pertaining to {@link Map} instances (including instances of
 * {@link SortedMap},, etc.). Also see this class's counterparts
 * {@link Lists}, {@link Sets} and {@link Queues}.
 */
public final class Maps extends Collections {
    private Maps() {
    }

    // CUSTOM

    /**
     * A builder for creating map instances. It's not thread safe.
     */
    public static final class Builder<K, V> {
        private final Map<K, V> map;

        public Builder(Map<K, V> map) {
            this.map = map;
        }

        /**
         * Associates {@code key} with {@code value} in the built map.
         */
        public Builder<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        /**
         * Associates all of the given map's keys and values in the built map.*
         */
        public Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
            for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
            return this;
        }

        /**
         * Return new created Map instance.
         */
        public Map<K, V> build() {
            return map;
        }
    }

    /**
     * Returns a new {@code HashMap} builder.
     */
    public static <K, V> Builder<K, V> newHashMapBuilder() {
        return new Builder<>(Maps.<K, V>newHashMap(DEFAULT_INITIAL_CAPACITY));
    }

    /**
     * Returns a new {@code LinkedHashMap} builder.
     */
    public static <K, V> Builder<K, V> linkedHashMapBuilder() {
        return new Builder<>(Maps.<K, V>newLinkedHashMap(DEFAULT_INITIAL_CAPACITY));
    }
    // GUAVA

    /**
     * Creates a <i>mutable</i>, empty {@code HashMap} instance.
     *
     * <p><b>Note:</b> if {@code K} is an {@code enum} type, use {@link
     * #newEnumMap} instead.
     *
     * @return a new, empty {@code HashMap}
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * Constructs a new {@code HashMap} instance with the specified capacity.
     *
     * @param capacity the initial capacity of this hash map.
     * @throws IllegalArgumentException when the capacity is less than zero.
     */
    public static <K, V> HashMap<K, V> newHashMap(int capacity) {
        return new HashMap<>(capacity);
    }

    /**
     * Creates a {@code HashMap} instance, with a high enough "initial capacity"
     * that it <i>should</i> hold {@code expectedSize} elements without growth.
     * This behavior cannot be broadly guaranteed, but it is observed to be true
     * for OpenJDK 1.6. It also can't be guaranteed that the method isn't
     * inadvertently <i>oversizing</i> the returned map.
     *
     * @param expectedSize the number of elements you expect to add to the
     *                     returned map
     * @return a new, empty {@code HashMap} with enough capacity to hold {@code
     * expectedSize} elements without resizing
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     */
    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

    /**
     * Returns a capacity that is sufficient to keep the map from being resized as
     * long as it grows no larger than expectedSize and the load factor is >= its
     * default (0.75).
     */
    static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            return expectedSize + 1;
        }
        if (expectedSize < Ints.MAX_POWER_OF_TWO) {
            return expectedSize + expectedSize / 3;
        }
        return Integer.MAX_VALUE; // any large value
    }

    /**
     * Creates a <i>mutable</i> {@code HashMap} instance with the same mappings as
     * the specified map.
     *
     * <p><b>Note:</b> if {@code K} is an {@link Enum} type, use {@link
     * #newEnumMap} instead.
     *
     * @param map the mappings to be placed in the new map
     * @return a new {@code HashMap} initialized with the mappings from {@code
     * map}
     */
    public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        return new HashMap<>(map);
    }

    /**
     * Creates a <i>mutable</i>, empty, insertion-ordered {@code LinkedHashMap}
     * instance.
     *
     * @return a new, empty {@code LinkedHashMap}
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    /**
     * Constructs a new {@code LinkedHashMap} instance with the specified
     * capacity.
     *
     * @param capacity the initial capacity of this map.
     * @throws IllegalArgumentException when the capacity is less than zero.
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int capacity) {
        return new LinkedHashMap<>(capacity);
    }

    /**
     * Creates a <i>mutable</i>, insertion-ordered {@code LinkedHashMap} instance
     * with the same mappings as the specified map.
     *
     * @param map the mappings to be placed in the new map
     * @return a new, {@code LinkedHashMap} initialized with the mappings from
     * {@code map}
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
        return new LinkedHashMap<>(map);
    }

    /**
     * Creates a <i>mutable</i>, empty {@code TreeMap} instance using the natural
     * ordering of its elements.
     *
     * @return a new, empty {@code TreeMap}
     */
    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<>();
    }

    /**
     * Creates a <i>mutable</i> {@code TreeMap} instance with the same mappings as
     * the specified map and using the same ordering as the specified map.
     *
     * @param map the sorted map whose mappings are to be placed in the new map
     *            and whose comparator is to be used to sort the new map
     * @return a new {@code TreeMap} initialized with the mappings from {@code map}
     * and using the comparator of {@code map}
     */
    public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> map) {
        return new TreeMap<>(map);
    }

    /**
     * Creates a <i>mutable</i>, empty {@code TreeMap} instance using the given
     * comparator.
     *
     * <p><b>Note:</b> if mutability is not required, use {@code
     * ImmutableSortedMap.orderedBy(comparator).build()} instead.
     *
     * @param comparator the comparator to sort the keys with
     * @return a new, empty {@code TreeMap}
     */
    public static <C, K extends C, V> TreeMap<K, V> newTreeMap(@Nullable Comparator<C> comparator) {
        // Ideally, the extra type parameter "C" shouldn't be necessary. It is a
        // work-around of a compiler type inference quirk that prevents the
        // following code from being compiled:
        // Comparator<Class<?>> comparator = null;
        // Map<Class<? extends Throwable>, String> map = newTreeMap(comparator);
        return new TreeMap<>(comparator);
    }

    /**
     * Creates an {@code EnumMap} instance.
     *
     * @param type the key type for this map
     * @return a new, empty {@code EnumMap}
     */
    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
        return new EnumMap<>(Preconditions.checkNotNull(type));
    }

    /**
     * Creates an {@code EnumMap} with the same mappings as the specified map.
     *
     * @param map the map from which to initialize this {@code EnumMap}
     * @return a new {@code EnumMap} initialized with the mappings from {@code
     * map}
     * @throws IllegalArgumentException if {@code m} is not an {@code EnumMap}
     *                                  instance and contains no mappings
     */
    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
        return new EnumMap<>(map);
    }

    /**
     * Creates an {@code IdentityHashMap} instance.
     *
     * @return a new, empty {@code IdentityHashMap}
     */
    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<>();
    }

    // JDK

    /**
     * Returns a Map containing the specified key and value. The map cannot be
     * modified. The map is serializable.
     *
     * @param key   the key.
     * @param value the value.
     * @return a Map containing the key and value.
     */
    public static <K, V> Map<K, V> singletonMap(K key, V value) {
        return java.util.Collections.singletonMap(key, value);
    }

    /**
     * Returns a wrapper on the specified map which synchronizes all access to
     * the map.
     *
     * @param map the map to wrap in a synchronized map.
     * @return a synchronized Map.
     */
    public static <K, V> Map<K, V> synchronizedMap(Map<K, V> map) {
        return java.util.Collections.synchronizedMap(map);
    }

    /**
     * Returns a wrapper on the specified sorted map which synchronizes all
     * access to the sorted map.
     *
     * @param map the sorted map to wrap in a synchronized sorted map.
     * @return a synchronized sorted map.
     */
    public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> map) {
        return java.util.Collections.synchronizedSortedMap(map);
    }

    /**
     * Returns a wrapper on the specified map which throws an
     * {@code UnsupportedOperationException} whenever an attempt is made to
     * modify the map.
     *
     * @param map the map to wrap in an unmodifiable map.
     * @return a unmodifiable map.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> map) {
        return java.util.Collections.unmodifiableMap(map);
    }

    /**
     * Returns a wrapper on the specified sorted map which throws an
     * {@code UnsupportedOperationException} whenever an attempt is made to
     * modify the sorted map.
     *
     * @param map the sorted map to wrap in an unmodifiable sorted map.
     * @return a unmodifiable sorted map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> map) {
        return java.util.Collections.unmodifiableSortedMap(map);
    }

    /**
     * Returns a type-safe empty, immutable {@link Map}.
     *
     * @return an empty {@link Map}.
     * @since 1.5
     */
    @SuppressWarnings("unchecked")
    public static final <K, V> Map<K, V> emptyMap() {
        return java.util.Collections.emptyMap();
    }

    /**
     * Returns a list iterator containing no elements.
     *
     * @since 1.7
     */
    public static <T> ListIterator<T> emptyListIterator() {
        return java.util.Collections.<T>emptyList().listIterator();
    }

    /**
     * Returns a dynamically typesafe view of the specified map. Trying to
     * insert an element of the wrong type into this map throws a
     * {@code ClassCastException}. At creation time the types in {@code m} are
     * not checked for correct type.
     *
     * @param m         the map to be wrapped in a typesafe map.
     * @param keyType   the type of the keys permitted to insert.
     * @param valueType the type of the values permitted to insert.
     * @return a typesafe map.
     */
    public static <K, V> Map<K, V> checkedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
        return java.util.Collections.checkedMap(m, keyType, valueType);
    }

    /**
     * Returns a dynamically typesafe view of the specified sorted map. Trying
     * to insert an element of the wrong type into this sorted map throws a
     * {@code ClassCastException}. At creation time the types in {@code m} are
     * not checked for correct type.
     *
     * @param m         the sorted map to be wrapped in a typesafe sorted map.
     * @param keyType   the type of the keys permitted to insert.
     * @param valueType the type of the values permitted to insert.
     * @return a typesafe sorted map.
     */
    public static <K, V> SortedMap<K, V> checkedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
        return java.util.Collections.checkedSortedMap(m, keyType, valueType);
    }

    /**
     * Returns a set backed by {@code map}.
     *
     * @throws IllegalArgumentException if the map is not empty
     * @since 1.6
     */
    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return java.util.Collections.newSetFromMap(map);
    }
}
