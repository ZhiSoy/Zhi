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

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Static utility methods pertaining to {@link Set} instances. Also see this
 * class's counterparts {@link Lists}, {@link Maps} and {@link Queues}.
 */
public final class Sets extends Collections {
    private Sets() {
    }

    // CUSTOM

    /**
     * A builder for creating set instance. It's not thread-safe.
     */
    public static class Builder<E> {
        private final Set<E> set;

        public Builder(Set<E> set) {
            this.set = set;
        }

        /**
         * Adds {@code element} to the {@code Set}.  If the {@code Set} already
         * contains {@code element}, then {@code add} has no
         * effect (only the previously added element is retained).
         */
        public Builder<E> add(E element) {
            set.add(element);
            return this;
        }

        /**
         * Adds each element of {@code elements} to the {@code Set},
         * ignoring duplicate elements (only the first duplicate element is added).
         */
        public Builder<E> add(E... elements) {
            Sets.addAll(set, elements);
            return this;
        }

        /**
         * Adds each element of {@code elements} to the {@code Set},
         * ignoring duplicate elements (only the first duplicate element is added).
         */
        public Builder<E> addAll(Iterable<? extends E> elements) {
            Sets.addAll(set, elements);
            return this;
        }

        /**
         * Adds each element of {@code elements} to the {@code Set},
         * ignoring duplicate elements (only the first duplicate element is added).
         */
        public Builder<E> addAll(Iterator<? extends E> elements) {
            Sets.addAll(set, elements);
            return this;
        }

        /**
         * Returns a newly-created {@code Set} based on the contents of the {@code Builder}.
         */
        public Set<E> build() {
            return set;
        }
    }

    /**
     * Create a new {@code HashSet} builder.
     */
    public static <E> Builder<E> hashSetBuilder() {
        return new Builder<>(Sets.<E>newHashSet(DEFAULT_INITIAL_CAPACITY));
    }

    /**
     * Create a new {@code LinkedHashSet} builder.
     */
    public static <E> Builder<E> linkedHashSeetBuilder() {
        return new Builder<>(Sets.<E>newLinkedHashSet(DEFAULT_INITIAL_CAPACITY));
    }
    // GUAVA

    /**
     * Returns a new {@code EnumSet} instance containing the given elements.
     * Unlike {@link EnumSet#copyOf(Collection)}, this method does not produce an
     * exception on an empty collection, and it may be called on any iterable, not
     * just a {@code Collection}.
     */
    public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable,
            Class<E> elementType) {
        EnumSet<E> set = EnumSet.noneOf(elementType);
        addAll(set, iterable);
        return set;
    }

    // HashSet

    /**
     * Creates a <i>mutable</i>, empty {@code HashSet} instance.
     *
     * <p><b>Note:</b> if {@code E} is an {@link Enum} type, use {@link
     * EnumSet#noneOf} instead.
     *
     * @return a new, empty {@code HashSet}
     */
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<>();
    }

    /**
     * Constructs a new instance of {@code HashSet} with the specified capacity.
     *
     * @param capacity the initial capacity of this {@code HashSet}.
     */
    public static <E> HashSet<E> newHashSet(int capacity) {
        return new HashSet<>(capacity);
    }

    /**
     * Creates a <i>mutable</i> {@code HashSet} instance containing the given
     * elements in unspecified order.
     *
     * <p><b>Note:</b> if {@code E} is an {@link Enum} type, use {@link
     * EnumSet#of(Enum, Enum[])} instead.
     *
     * @param elements the elements that the set should contain
     * @return a new {@code HashSet} containing those elements (minus duplicates)
     */
    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = newHashSetWithExpectedSize(elements.length);
        addAll(set, elements);
        return set;
    }

    /**
     * Creates a {@code HashSet} instance, with a high enough "initial capacity"
     * that it <i>should</i> hold {@code expectedSize} elements without growth.
     * This behavior cannot be broadly guaranteed, but it is observed to be true
     * for OpenJDK 1.6. It also can't be guaranteed that the method isn't
     * inadvertently <i>oversizing</i> the returned set.
     *
     * @param expectedSize the number of elements you expect to add to the
     *                     returned set
     * @return a new, empty {@code HashSet} with enough capacity to hold {@code
     * expectedSize} elements without resizing
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     */
    public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
        return new HashSet<>(Maps.capacity(expectedSize));
    }

    /**
     * Creates a <i>mutable</i> {@code HashSet} instance containing the given
     * elements in unspecified order.
     *
     * <p><b>Note:</b> if {@code E} is an {@link Enum} type, use
     * {@link #newEnumSet(Iterable, Class)} instead.
     *
     * @param elements the elements that the set should contain
     * @return a new {@code HashSet} containing those elements (minus duplicates)
     */
    public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements) {
        return (elements instanceof Collection)
                ? new HashSet<>(cast(elements))
                : newHashSet(elements.iterator());
    }

    /**
     * Creates a <i>mutable</i> {@code HashSet} instance containing the given
     * elements in unspecified order.
     *
     * <p><b>Note:</b> if {@code E} is an {@link Enum} type, you should create an
     * {@link EnumSet} instead.
     *
     * @param elements the elements that the set should contain
     * @return a new {@code HashSet} containing those elements (minus duplicates)
     */
    public static <E> HashSet<E> newHashSet(Iterator<? extends E> elements) {
        HashSet<E> set = newHashSet();
        addAll(set, elements);
        return set;
    }

    /**
     * Creates a thread-safe set backed by a hash map. The set is backed by a
     * {@link ConcurrentHashMap} instance, and thus carries the same concurrency
     * guarantees.
     *
     * <p>Unlike {@code HashSet}, this class does NOT allow {@code null} to be
     * used as an element. The set is serializable.
     *
     * @return a new, empty thread-safe {@code Set}
     * @since 15.0
     */
    public static <E> Set<E> newConcurrentHashSet() {
        return Maps.newSetFromMap(new ConcurrentHashMap<E, Boolean>());
    }

    /**
     * Creates a thread-safe set backed by a hash map and containing the given
     * elements. The set is backed by a {@link ConcurrentHashMap} instance, and
     * thus carries the same concurrency guarantees.
     *
     * <p>Unlike {@code HashSet}, this class does NOT allow {@code null} to be
     * used as an element. The set is serializable.
     *
     * @param elements the elements that the set should contain
     * @return a new thread-safe set containing those elements (minus duplicates)
     * @throws NullPointerException if {@code elements} or any of its contents is null
     * @since 15.0
     */
    public static <E> Set<E> newConcurrentHashSet(Iterable<? extends E> elements) {
        Set<E> set = newConcurrentHashSet();
        addAll(set, elements);
        return set;
    }

    // LinkedHashSet

    /**
     * Creates a <i>mutable</i>, empty {@code LinkedHashSet} instance.
     *
     * @return a new, empty {@code LinkedHashSet}
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet<>();
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(int capacity) {
        return new LinkedHashSet<>(capacity);
    }

    /**
     * Creates a {@code LinkedHashSet} instance, with a high enough "initial
     * capacity" that it <i>should</i> hold {@code expectedSize} elements without
     * growth. This behavior cannot be broadly guaranteed, but it is observed to
     * be true for OpenJDK 1.6. It also can't be guaranteed that the method isn't
     * inadvertently <i>oversizing</i> the returned set.
     *
     * @param expectedSize the number of elements you expect to add to the
     *                     returned set
     * @return a new, empty {@code LinkedHashSet} with enough capacity to hold
     * {@code expectedSize} elements without resizing
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     * @since 11.0
     */
    public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int expectedSize) {
        return new LinkedHashSet<>(Maps.capacity(expectedSize));
    }

    /**
     * Creates a <i>mutable</i> {@code LinkedHashSet} instance containing the
     * given elements in order.
     *
     * @param elements the elements that the set should contain, in order
     * @return a new {@code LinkedHashSet} containing those elements (minus
     * duplicates)
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new LinkedHashSet<>(cast(elements));
        }
        LinkedHashSet<E> set = newLinkedHashSet();
        addAll(set, elements);
        return set;
    }

    // TreeSet

    /**
     * Creates a <i>mutable</i>, empty {@code TreeSet} instance sorted by the
     * natural sort ordering of its elements.
     *
     * @return a new, empty {@code TreeSet}
     */
    public static <E extends Comparable> TreeSet<E> newTreeSet() {
        return new TreeSet<>();
    }

    /**
     * Creates a <i>mutable</i> {@code TreeSet} instance containing the given
     * elements sorted by their natural ordering.
     *
     * <p><b>Note:</b> If {@code elements} is a {@code SortedSet} with an explicit
     * comparator, this method has different behavior than
     * {@link TreeSet#TreeSet(SortedSet)}, which returns a {@code TreeSet} with
     * that comparator.
     *
     * @param elements the elements that the set should contain
     * @return a new {@code TreeSet} containing those elements (minus duplicates)
     */
    public static <E extends Comparable> TreeSet<E> newTreeSet(Iterable<? extends E> elements) {
        TreeSet<E> set = newTreeSet();
        addAll(set, elements);
        return set;
    }

    /**
     * Creates a <i>mutable</i>, empty {@code TreeSet} instance with the given
     * comparator.
     *
     * <p><b>Note:</b> if mutability is not required, use {@code
     * ImmutableSortedSet.orderedBy(comparator).build()} instead.
     *
     * @param comparator the comparator to use to sort the set
     * @return a new, empty {@code TreeSet}
     * @throws NullPointerException if {@code comparator} is null
     */
    public static <E> TreeSet<E> newTreeSet(Comparator<? super E> comparator) {
        return new TreeSet<>(comparator);
    }

    /**
     * Creates an empty {@code Set} that uses identity to determine equality. It
     * compares object references, instead of calling {@code equals}, to
     * determine whether a provided object matches an element in the set. For
     * example, {@code contains} returns {@code false} when passed an object that
     * equals a set member, but isn't the same instance. This behavior is similar
     * to the way {@code IdentityHashMap} handles key lookups.
     *
     * @since 8.0
     */
    public static <E> Set<E> newIdentityHashSet() {
        return Sets.newSetFromMap(Maps.<E, Boolean>newIdentityHashMap());
    }

    /**
     * Creates an empty {@code CopyOnWriteArraySet} instance.
     *
     * <p><b>Note:</b> if you need an immutable empty {@link Set}, use
     * {@link java.util.Collections#emptySet} instead.
     *
     * @return a new, empty {@code CopyOnWriteArraySet}
     * @since 12.0
     */
    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
        return new CopyOnWriteArraySet<>();
    }

    /**
     * Creates a {@code CopyOnWriteArraySet} instance containing the given elements.
     *
     * @param elements the elements that the set should contain, in order
     * @return a new {@code CopyOnWriteArraySet} containing those elements
     * @since 12.0
     */
    public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<? extends E> elements) {
        // We copy elements to an ArrayList first, rather than incurring the
        // quadratic cost of adding them to the COWAS directly.
        Collection<? extends E> elementsCollection = (elements instanceof Collection)
                ? cast(elements)
                : Lists.newArrayList(elements);
        return new CopyOnWriteArraySet<>(elementsCollection);
    }

    /**
     * Creates an {@code EnumSet} consisting of all enum values that are not in
     * the specified collection. If the collection is an {@link EnumSet}, this
     * method has the same behavior as {@link EnumSet#complementOf}. Otherwise,
     * the specified collection must contain at least one element, in order to
     * determine the element type. If the collection could be empty, use
     * {@link #complementOf(Collection, Class)} instead of this method.
     *
     * @param collection the collection whose complement should be stored in the
     *                   enum set
     * @return a new, modifiable {@code EnumSet} containing all values of the enum
     * that aren't present in the given collection
     * @throws IllegalArgumentException if {@code collection} is not an
     *                                  {@code EnumSet} instance and contains no elements
     */
    public static <E extends Enum<E>> EnumSet<E> complementOf(
            Collection<E> collection) {
        if (collection instanceof EnumSet) {
            return EnumSet.complementOf((EnumSet<E>) collection);
        }
        Class<E> type = collection.iterator().next().getDeclaringClass();
        return makeComplementByHand(collection, type);
    }

    /**
     * Creates an {@code EnumSet} consisting of all enum values that are not in
     * the specified collection. This is equivalent to
     * {@link EnumSet#complementOf}, but can act on any input collection, as long
     * as the elements are of enum type.
     *
     * @param collection the collection whose complement should be stored in the
     *                   {@code EnumSet}
     * @param type       the type of the elements in the set
     * @return a new, modifiable {@code EnumSet} initially containing all the
     * values of the enum not present in the given collection
     */
    public static <E extends Enum<E>> EnumSet<E> complementOf(
            Collection<E> collection, Class<E> type) {
        return (collection instanceof EnumSet)
                ? EnumSet.complementOf((EnumSet<E>) collection)
                : makeComplementByHand(collection, type);
    }

    private static <E extends Enum<E>> EnumSet<E> makeComplementByHand(
            Collection<E> collection, Class<E> type) {
        EnumSet<E> result = EnumSet.allOf(type);
        result.removeAll(collection);
        return result;
    }

    /**
     * Returns a set backed by the specified map. The resulting set displays
     * the same ordering, concurrency, and performance characteristics as the
     * backing map. In essence, this factory method provides a {@link Set}
     * implementation corresponding to any {@link Map} implementation. There is no
     * need to use this method on a {@link Map} implementation that already has a
     * corresponding {@link Set} implementation (such as {@link java.util.HashMap}
     * or {@link java.util.TreeMap}).
     *
     * <p>Each method invocation on the set returned by this method results in
     * exactly one method invocation on the backing map or its {@code keySet}
     * view, with one exception. The {@code addAll} method is implemented as a
     * sequence of {@code put} invocations on the backing map.
     *
     * <p>The specified map must be empty at the time this method is invoked,
     * and should not be accessed directly after this method returns. These
     * conditions are ensured if the map is created empty, passed directly
     * to this method, and no reference to the map is retained, as illustrated
     * in the following code fragment: <pre>  {@code
     *
     *   Set<Object> identityHashSet = Sets.newSetFromMap(
     *       new IdentityHashMap<Object, Boolean>());}</pre>
     *
     * <p>This method has the same behavior as the JDK 6 method
     * {@code Collections.newSetFromMap()}. The returned set is serializable if
     * the backing map is.
     *
     * @param map the backing map
     * @return the set backed by the map
     * @throws IllegalArgumentException if {@code map} is not empty
     */
    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return Maps.newSetFromMap(map);
    }

    // JDK

    /**
     * Returns a set containing the specified element. The set cannot be
     * modified. The set is serializable.
     *
     * @param object the element.
     * @return a set containing the element.
     */
    public static <E> Set<E> singleton(E object) {
        return java.util.Collections.singleton(object);
    }

    /**
     * Returns a wrapper on the specified set which synchronizes all access to
     * the set.
     *
     * @param set the set to wrap in a synchronized set.
     * @return a synchronized set.
     */
    public static <E> Set<E> synchronizedSet(Set<E> set) {
        return java.util.Collections.synchronizedSet(set);
    }

    /**
     * Returns a wrapper on the specified set which throws an
     * {@code UnsupportedOperationException} whenever an attempt is made to
     * modify the set.
     *
     * @param set the set to wrap in an unmodifiable set.
     * @return a unmodifiable set
     */
    @SuppressWarnings("unchecked")
    public static <E> Set<E> unmodifiableSet(Set<? extends E> set) {
        return java.util.Collections.unmodifiableSet(set);
    }

    /**
     * Returns a wrapper on the specified sorted set which throws an
     * {@code UnsupportedOperationException} whenever an attempt is made to
     * modify the sorted set.
     *
     * @param set the sorted set to wrap in an unmodifiable sorted set.
     * @return a unmodifiable sorted set.
     */
    public static <E> SortedSet<E> unmodifiableSortedSet(SortedSet<E> set) {
        return java.util.Collections.unmodifiableSortedSet(set);
    }


    /**
     * Returns a type-safe empty, immutable {@link Set}.
     *
     * @return an empty {@link Set}.
     * @since 1.5
     */
    @SuppressWarnings("unchecked")
    public static final <T> Set<T> emptySet() {
        return java.util.Collections.emptySet();
    }

    /**
     * Returns a dynamically typesafe view of the specified set. Trying to
     * insert an element of the wrong type into this set throws a
     * {@code ClassCastException}. At creation time the types in {@code s} are
     * not checked for correct type.
     *
     * @param s    the set to be wrapped in a typesafe set.
     * @param type the type of the elements permitted to insert.
     * @return a typesafe set.
     */
    public static <E> Set<E> checkedSet(Set<E> s, Class<E> type) {
        return java.util.Collections.checkedSet(s, type);
    }

    /**
     * Returns a dynamically typesafe view of the specified sorted set. Trying
     * to insert an element of the wrong type into this sorted set throws a
     * {@code ClassCastException}. At creation time the types in {@code s} are
     * not checked for correct type.
     *
     * @param s    the sorted set to be wrapped in a typesafe sorted set.
     * @param type the type of the elements permitted to insert.
     * @return a typesafe sorted set.
     */
    public static <E> SortedSet<E> checkedSortedSet(SortedSet<E> s, Class<E> type) {
        return java.util.Collections.checkedSortedSet(s, type);
    }
}
