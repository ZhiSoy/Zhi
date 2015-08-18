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

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Static utility methods pertaining to {@link List} instances. Also see this
 * class's counterparts {@link Sets}, {@link Maps} and {@link Queues}.
 */
public final class Lists extends Collections {
    private Lists() {
    }

    // CUSTOM

    /**
     * A builder for creating list instances. It's not thread safe.
     */
    public static final class Builder<E> {
        private final List<E> list;

        public Builder(List<E> list) {
            this.list = list;
        }

        /**
         * Adds this {@code element} to the {@code List}.
         */
        public Builder<E> add(E element) {
            list.add(element);
            return this;
        }

        /**
         * Adds each element of {@code elements} to the {@code List}.
         */
        public Builder<E> addAll(Iterable<? extends E> elements) {
            Lists.addAll(list, elements);
            return this;
        }

        /**
         * Adds each element of {@code elements} to the {@code List}.
         */
        public Builder<E> add(E... elements) {
            Lists.addAll(list, elements);
            return this;
        }

        /**
         * Adds each element of {@code elements} to the {@code List}.
         */
        public Builder<E> addAll(Iterator<? extends E> elements) {
            Lists.addAll(list, elements);
            return this;
        }

        /**
         * Returns a newly-created {@code List} based on the contents of the {@code Builder}.
         */
        public List<E> build() {
            return list;
        }
    }

    /**
     * Returns a new {@code ArrayList} builder.
     */
    public static <E> Builder<E> newArrayListBuilder() {
        return new Builder<>(Lists.<E>newArrayList(DEFAULT_INITIAL_CAPACITY));
    }

    /**
     * Returns a new {@code LinkedList} builder.
     */
    public static <E> Builder<E> newLinkedListBuilder() {
        return new Builder<>(Lists.<E>newLinkedList());
    }

    // GUAVA
    // ArrayList

    /**
     * Creates a <i>mutable</i>, empty {@code ArrayList} instance,
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    /**
     * Creates a <i>mutable</i> {@code ArrayList} instance containing the given
     * elements.
     */
    public static <E> ArrayList<E> newArrayList(E... elements) {
        // Avoid integer overflow when a large array is passed in
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<>(capacity);
        java.util.Collections.addAll(list, elements);
        return list;
    }

    static int computeArrayListCapacity(int arraySize) {
        return Ints.saturatedCast(5L + arraySize + (arraySize / 10));
    }

    /**
     * Creates a <i>mutable</i> {@code ArrayList} instance containing the given
     * elements; a very thin shortcut for creating an empty list then calling
     * {@link Collections#addAll}.
     */
    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        // Let ArrayList's sizing logic work, if possible
        return (elements instanceof Collection)
                ? new ArrayList<>(cast(elements))
                : newArrayList(elements.iterator());
    }

    /**
     * Creates a <i>mutable</i> {@code ArrayList} instance containing the given
     * elements; a very thin shortcut for creating an empty list and then calling
     * {@link Collections#addAll}.
     */
    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = new ArrayList<>();
        addAll(list, elements);
        return list;
    }

    /**
     * Creates an {@code ArrayList} instance backed by an array with the specified
     * initial size; simply delegates to {@link ArrayList#ArrayList(int)}.
     *
     * @param capacity the exact size of the initial backing array for
     *                 the returned array list ({@code ArrayList} documentation calls this
     *                 value the "capacity")
     * @return a new, empty {@code ArrayList} which is guaranteed not to resize
     * itself unless its size reaches {@code initialArraySize + 1}
     * @throws IllegalArgumentException if {@code initialArraySize} is negative
     */
    public static <E> ArrayList<E> newArrayList(int capacity) {
        return new ArrayList<>(capacity);
    }

    /**
     * Creates an {@code ArrayList} instance to hold {@code estimatedSize}
     * elements, <i>plus</i> an unspecified amount of padding; you almost
     * certainly mean to call {@link #newArrayList} (see that method
     * for further advice on usage).
     *
     * @param estimatedSize an estimate of the eventual {@link List#size()} of
     *                      the new list
     * @return a new, empty {@code ArrayList}, sized appropriately to hold the
     * estimated number of elements
     * @throws IllegalArgumentException if {@code estimatedSize} is negative
     */
    public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimatedSize) {
        return new ArrayList<>(computeArrayListCapacity(estimatedSize));
    }

    // LinkedList

    /**
     * Creates a <i>mutable</i>, empty {@code LinkedList} instance.
     */
    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<>();
    }

    /**
     * Creates a <i>mutable</i> {@code LinkedList} instance containing the given
     * elements; a very thin shortcut for creating an empty list then calling
     * {@link Collections#addAll}.
     */
    public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> elements) {
        LinkedList<E> list = newLinkedList();
        Lists.addAll(list, elements);
        return list;
    }

    /**
     * Creates an empty {@code CopyOnWriteArrayList} instance.
     *
     * <p><b>Note:</b> if you need an immutable empty {@link List}, use
     * {@link java.util.Collections#emptyList} instead.
     *
     * @return a new, empty {@code CopyOnWriteArrayList}
     * @since 12.0
     */
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<>();
    }

    /**
     * Creates a {@code CopyOnWriteArrayList} instance containing the given elements.
     *
     * @param elements the elements that the list should contain, in order
     * @return a new {@code CopyOnWriteArrayList} containing those elements
     * @since 12.0
     */
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> elements) {
        // We copy elements to an ArrayList first, rather than incurring the
        // quadratic cost of adding them to the COWAL directly.
        Collection<? extends E> elementsCollection = (elements instanceof Collection)
                ? cast(elements)
                : newArrayList(elements);
        return new CopyOnWriteArrayList<>(elementsCollection);
    }

    /**
     * Returns an unmodifiable list containing the specified first element and
     * backed by the specified array of additional elements. Changes to the {@code
     * rest} array will be reflected in the returned list. Unlike {@link
     * Arrays#asList}, the returned list is unmodifiable.
     *
     * <p>This is useful when a varargs method needs to use a signature such as
     * {@code (Foo firstFoo, Foo... moreFoos)}, in order to avoid overload
     * ambiguity or to enforce a minimum argument count.
     *
     * <p>The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param first the first element
     * @param rest  an array of additional elements, possibly empty
     * @return an unmodifiable list containing the specified elements
     */
    public static <E> List<E> asList(@Nullable E first, E[] rest) {
        return new OnePlusArrayList<>(first, rest);
    }

    /** @see Lists#asList(Object, Object[]) */
    private static class OnePlusArrayList<E> extends AbstractList<E>
            implements Serializable, RandomAccess {
        final E first;
        final E[] rest;

        OnePlusArrayList(@Nullable E first, E[] rest) {
            this.first = first;
            this.rest = rest;
        }

        @Override
        public int size() {
            return rest.length + 1;
        }

        @Override
        public E get(int index) {
            return (index == 0) ? first : rest[index - 1];
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * Returns an unmodifiable list containing the specified first and second
     * element, and backed by the specified array of additional elements. Changes
     * to the {@code rest} array will be reflected in the returned list. Unlike
     * {@link Arrays#asList}, the returned list is unmodifiable.
     *
     * <p>This is useful when a varargs method needs to use a signature such as
     * {@code (Foo firstFoo, Foo secondFoo, Foo... moreFoos)}, in order to avoid
     * overload ambiguity or to enforce a minimum argument count.
     *
     * <p>The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param first  the first element
     * @param second the second element
     * @param rest   an array of additional elements, possibly empty
     * @return an unmodifiable list containing the specified elements
     */
    public static <E> List<E> asList(@Nullable E first, @Nullable E second, E[] rest) {
        return new TwoPlusArrayList<>(first, second, rest);
    }

    /** @see Lists#asList(Object, Object, Object[]) */
    private static class TwoPlusArrayList<E> extends AbstractList<E>
            implements Serializable, RandomAccess {
        final E first;
        final E second;
        final E[] rest;

        TwoPlusArrayList(@Nullable E first, @Nullable E second, E[] rest) {
            this.first = first;
            this.second = second;
            this.rest = rest;
        }

        @Override
        public int size() {
            return rest.length + 2;
        }

        @Override
        public E get(int index) {
            switch (index) {
                case 0:
                    return first;
                case 1:
                    return second;
                default:
                    return rest[index - 2];
            }
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * Returns consecutive {@linkplain List#subList(int, int) sublists} of a list,
     * each of the same size (the final list may be smaller). For example,
     * partitioning a list containing {@code [a, b, c, d, e]} with a partition
     * size of 3 yields {@code [[a, b, c], [d, e]]} -- an outer list containing
     * two inner lists of three and two elements, all in the original order.
     *
     * <p>The outer list is unmodifiable, but reflects the latest state of the
     * source list. The inner lists are sublist views of the original list,
     * produced on demand using {@link List#subList(int, int)}, and are subject
     * to all the usual caveats about modification as explained in that API.
     *
     * @param list the list to return consecutive sublists of
     * @param size the desired size of each sublist (the last may be
     *             smaller)
     * @return a list of consecutive sublists
     * @throws IllegalArgumentException if {@code partitionSize} is nonpositive
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        return (list instanceof RandomAccess)
                ? new RandomAccessPartition<>(list, size)
                : new Partition<>(list, size);
    }

    private static class Partition<T> extends AbstractList<List<T>> {
        final List<T> list;
        final int size;

        Partition(List<T> list, int size) {
            this.list = list;
            this.size = size;
        }

        @Override
        public List<T> get(int index) {
            int start = index * size;
            int end = Math.min(start + size, list.size());
            return list.subList(start, end);
        }

        @Override
        public int size() {
            return Ints.divide(list.size(), size, RoundingMode.CEILING);
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }
    }

    private static class RandomAccessPartition<T> extends Partition<T>
            implements RandomAccess {
        RandomAccessPartition(List<T> list, int size) {
            super(list, size);
        }
    }

    /**
     * Returns a view of the specified {@code CharSequence} as a {@code
     * List<Character>}, viewing {@code sequence} as a sequence of Unicode code
     * units. The view does not support any modification operations, but reflects
     * any changes to the underlying character sequence.
     *
     * @param sequence the character sequence to view as a {@code List} of
     *                 characters
     * @return an {@code List<Character>} view of the character sequence
     * @since 7.0
     */
    public static List<Character> charactersOf(CharSequence sequence) {
        return new CharSequenceAsList(sequence);
    }

    private static final class CharSequenceAsList extends AbstractList<Character> {
        private final CharSequence sequence;

        CharSequenceAsList(CharSequence sequence) {
            this.sequence = sequence;
        }

        @Override
        public Character get(int index) {
            return sequence.charAt(index);
        }

        @Override
        public int size() {
            return sequence.length();
        }
    }

    /**
     * Returns a reversed view of the specified list. For example, {@code
     * Lists.reverse(Arrays.asList(1, 2, 3))} returns a list containing {@code 3,
     * 2, 1}. The returned list is backed by this list, so changes in the returned
     * list are reflected in this list, and vice-versa. The returned list supports
     * all of the optional list operations supported by this list.
     *
     * <p>The returned list is random-access if the specified list is random
     * access.
     *
     * @since 7.0
     */
    public static <T> List<T> reverse(List<T> list) {
        if (list instanceof ReverseList) {
            return ((ReverseList<T>) list).getForwardList();
        } else if (list instanceof RandomAccess) {
            return new RandomAccessReverseList<T>(list);
        } else {
            return new ReverseList<T>(list);
        }
    }

    private static class ReverseList<T> extends AbstractList<T> {
        private final List<T> forwardList;

        ReverseList(List<T> forwardList) {
            this.forwardList = forwardList;
        }

        List<T> getForwardList() {
            return forwardList;
        }

        private int reverseIndex(int index) {
            int size = size();
            return (size - 1) - index;
        }

        private int reversePosition(int index) {
            int size = size();
            return size - index;
        }

        @Override
        public void add(int index, @Nullable T element) {
            forwardList.add(reversePosition(index), element);
        }

        @Override
        public void clear() {
            forwardList.clear();
        }

        @Override
        public T remove(int index) {
            return forwardList.remove(reverseIndex(index));
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            subList(fromIndex, toIndex).clear();
        }

        @Override
        public T set(int index, @Nullable T element) {
            return forwardList.set(reverseIndex(index), element);
        }

        @Override
        public T get(int index) {
            return forwardList.get(reverseIndex(index));
        }

        @Override
        public int size() {
            return forwardList.size();
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            return reverse(forwardList.subList(
                    reversePosition(toIndex), reversePosition(fromIndex)));
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            int start = reversePosition(index);
            final ListIterator<T> forwardIterator = forwardList.listIterator(start);
            return new ListIterator<T>() {

                boolean canRemoveOrSet;

                @Override
                public void add(T e) {
                    forwardIterator.add(e);
                    forwardIterator.previous();
                    canRemoveOrSet = false;
                }

                @Override
                public boolean hasNext() {
                    return forwardIterator.hasPrevious();
                }

                @Override
                public boolean hasPrevious() {
                    return forwardIterator.hasNext();
                }

                @Override
                public T next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    canRemoveOrSet = true;
                    return forwardIterator.previous();
                }

                @Override
                public int nextIndex() {
                    return reversePosition(forwardIterator.nextIndex());
                }

                @Override
                public T previous() {
                    if (!hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    canRemoveOrSet = true;
                    return forwardIterator.next();
                }

                @Override
                public int previousIndex() {
                    return nextIndex() - 1;
                }

                @Override
                public void remove() {
                    forwardIterator.remove();
                    canRemoveOrSet = false;
                }

                @Override
                public void set(T e) {
                    forwardIterator.set(e);
                }
            };
        }
    }

    private static class RandomAccessReverseList<T> extends ReverseList<T>
            implements RandomAccess {
        RandomAccessReverseList(List<T> forwardList) {
            super(forwardList);
        }
    }

    // JDK

    /**
     * Performs a binary search for the specified element in the specified
     * sorted list. The list needs to be already sorted in natural sorting
     * order. Searching in an unsorted array has an undefined result. It's also
     * undefined which element is found if there are multiple occurrences of the
     * same element.
     *
     * @param list   the sorted list to search.
     * @param object the element to find.
     * @return the non-negative index of the element, or a negative index which
     * is the {@code -index - 1} where the element would be inserted
     * @throws ClassCastException if an element in the List or the search element does not
     *                            implement Comparable, or cannot be compared to each other.
     */
    @SuppressWarnings("unchecked")
    public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T object) {
        return java.util.Collections.binarySearch(list, object);
    }

    /**
     * Performs a binary search for the specified element in the specified
     * sorted list using the specified comparator. The list needs to be already
     * sorted according to the comparator passed. Searching in an unsorted array
     * has an undefined result. It's also undefined which element is found if
     * there are multiple occurrences of the same element.
     *
     * @param list       the sorted List to search.
     * @param object     the element to find.
     * @param comparator the comparator. If the comparator is {@code null} then the
     *                   search uses the objects' natural ordering.
     * @return the non-negative index of the element, or a negative index which
     * is the {@code -index - 1} where the element would be inserted.
     * @throws ClassCastException when an element in the list and the searched element cannot
     *                            be compared to each other using the comparator.
     */
    @SuppressWarnings("unchecked")
    public static <T> int binarySearch(List<? extends T> list, T object, Comparator<? super T> comparator) {
        return java.util.Collections.binarySearch(list, object, comparator);
    }

    /**
     * Copies the elements from the source list to the destination list. At the
     * end both lists will have the same objects at the same index. If the
     * destination array is larger than the source list, the elements in the
     * destination list with {@code index >= source.size()} will be unchanged.
     *
     * @param destination the list whose elements are set from the source list.
     * @param source      the list with the elements to be copied into the destination.
     * @throws IndexOutOfBoundsException     when the destination list is smaller than the source list.
     * @throws UnsupportedOperationException when replacing an element in the destination list is not
     *                                       supported.
     */
    public static <T> void copy(List<? super T> destination, List<? extends T> source) {
        java.util.Collections.copy(destination, source);
    }

    /**
     * Fills the specified list with the specified element.
     *
     * @param list   the list to fill.
     * @param object the element to fill the list with.
     * @throws UnsupportedOperationException when replacing an element in the List is not supported.
     */
    public static <T> void fill(List<? super T> list, T object) {
        java.util.Collections.fill(list, object);
    }

    /**
     * Returns a list containing the specified number of the specified element.
     * The list cannot be modified. The list is serializable.
     *
     * @param length the size of the returned list.
     * @param object the element to be added {@code length} times to a list.
     * @return a list containing {@code length} copies of the element.
     * @throws IllegalArgumentException when {@code length < 0}.
     */
    public static <T> List<T> nCopies(final int length, T object) {
        return java.util.Collections.nCopies(length, object);
    }

    /**
     * Modifies the specified {@code List} by reversing the order of the elements.
     *
     * @param list the list to reverse.
     * @throws UnsupportedOperationException when replacing an element in the List is not supported.
     * @see java.util.Collections#reverse(List)
     */
    public static void reverseList(List<?> list) {
        java.util.Collections.reverse(list);
    }

    /**
     * Moves every element of the list to a random new position in the list.
     *
     * @param list the List to shuffle.
     * @throws UnsupportedOperationException when replacing an element in the List is not supported.
     */
    public static void shuffle(List<?> list) {
        java.util.Collections.shuffle(list, new Random());
    }

    /**
     * Moves every element of the list to a random new position in the list
     * using the specified random number generator.
     *
     * @param list   the list to shuffle.
     * @param random the random number generator.
     * @throws UnsupportedOperationException when replacing an element in the list is not supported.
     */
    public static void shuffle(List<?> list, Random random) {
        java.util.Collections.shuffle(list, random);
    }

    /**
     * Returns an immutable list containing only the specified object.
     * The returned list is serializable.
     *
     * @param o the sole object to be stored in the returned list.
     * @return an immutable list containing only the specified object.
     * @since 1.3
     */
    public static <T> List<T> singletonList(T o) {
        return java.util.Collections.singletonList(o);
    }

    /**
     * Sorts the given list in ascending natural order. The algorithm is
     * stable which means equal elements don't get reordered.
     *
     * @throws ClassCastException if any element does not implement {@code Comparable},
     *                            or if {@code compareTo} throws for any pair of elements.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        java.util.Collections.sort(list);
    }

    /**
     * Sorts the given list using the given comparator. The algorithm is
     * stable which means equal elements don't get reordered.
     *
     * @throws ClassCastException if any element does not implement {@code Comparable},
     *                            or if {@code compareTo} throws for any pair of elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        java.util.Collections.sort(list, comparator);
    }

    /**
     * Swaps the elements of list {@code list} at indices {@code index1} and
     * {@code index2}.
     *
     * @param list   the list to manipulate.
     * @param index1 position of the first element to swap with the element in
     *               index2.
     * @param index2 position of the other element.
     * @throws IndexOutOfBoundsException if index1 or index2 is out of range of this list.
     * @since 1.4
     */
    @SuppressWarnings("unchecked")
    public static void swap(List<?> list, int index1, int index2) {
        java.util.Collections.swap(list, index1, index2);
    }

    /**
     * Replaces all occurrences of Object {@code obj} in {@code list} with
     * {@code newObj}. If the {@code obj} is {@code null}, then all
     * occurrences of {@code null} are replaced with {@code newObj}.
     *
     * @param list the list to modify.
     * @param obj  the object to find and replace occurrences of.
     * @param obj2 the object to replace all occurrences of {@code obj} in
     *             {@code list}.
     * @return true, if at least one occurrence of {@code obj} has been found in
     * {@code list}.
     * @throws UnsupportedOperationException if the list does not support setting elements.
     */
    public static <T> boolean replaceAll(List<T> list, T obj, T obj2) {
        return java.util.Collections.replaceAll(list, obj, obj2);
    }

    /**
     * Rotates the elements in {@code list} by the distance {@code dist}
     * <p>
     * e.g. for a given list with elements [1, 2, 3, 4, 5, 6, 7, 8, 9, 0],
     * calling rotate(list, 3) or rotate(list, -7) would modify the list to look
     * like this: [8, 9, 0, 1, 2, 3, 4, 5, 6, 7]
     *
     * @param lst  the list whose elements are to be rotated.
     * @param dist is the distance the list is rotated. This can be any valid
     *             integer. Negative values rotate the list backwards.
     */
    @SuppressWarnings("unchecked")
    public static void rotate(List<?> lst, int dist) {
        java.util.Collections.rotate(lst, dist);
    }

    /**
     * Searches the {@code list} for {@code sublist} and returns the beginning
     * index of the first occurrence.
     * <p>
     * -1 is returned if the {@code sublist} does not exist in {@code list}.
     *
     * @param list    the List to search {@code sublist} in.
     * @param sublist the List to search in {@code list}.
     * @return the beginning index of the first occurrence of {@code sublist} in
     * {@code list}, or -1.
     */
    public static int indexOfSubList(List<?> list, List<?> sublist) {
        return java.util.Collections.indexOfSubList(list, sublist);
    }

    /**
     * Returns an {@code ArrayList} with all the elements in the {@code
     * enumeration}. The elements in the returned {@code ArrayList} are in the
     * same order as in the {@code enumeration}.
     *
     * @param enumeration the source {@link Enumeration}.
     * @return an {@code ArrayList} from {@code enumeration}.
     */
    public static <T> ArrayList<T> list(Enumeration<T> enumeration) {
        return java.util.Collections.list(enumeration);
    }

    /**
     * Returns a synchronized (thread-safe) list backed by the specified
     * list.  In order to guarantee serial access, it is critical that
     * <strong>all</strong> access to the backing list is accomplished
     * through the returned list.<p>
     *
     * It is imperative that the user manually synchronize on the returned
     * list when iterating over it:
     * <pre>
     *  List list = Collections.synchronizedList(new ArrayList());
     *      ...
     *  synchronized (list) {
     *      Iterator i = list.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * Failure to follow this advice may result in non-deterministic behavior.
     *
     * <p>The returned list will be serializable if the specified list is
     * serializable.
     *
     * @param list the list to be "wrapped" in a synchronized list.
     * @return a synchronized view of the specified list.
     */
    public static <T> List<T> synchronizedList(List<T> list) {
        return java.util.Collections.synchronizedList(list);
    }

    /**
     * Returns a wrapper on the specified list which throws an
     * {@code UnsupportedOperationException} whenever an attempt is made to
     * modify the list.
     *
     * @param list the list to wrap in an unmodifiable list.
     * @return an unmodifiable List.
     */
    @SuppressWarnings("unchecked")
    public static <E> List<E> unmodifiableList(List<? extends E> list) {
        return java.util.Collections.unmodifiableList(list);
    }

    /**
     * Returns the empty list (immutable).  This list is serializable.
     *
     * <p>This example illustrates the type-safe way to obtain an empty list:
     * <pre>
     *     List&lt;String&gt; s = Collections.emptyList();
     * </pre>
     * Implementation note:  Implementations of this method need not
     * create a separate <tt>List</tt> object for each call.   Using this
     * method is likely to have comparable cost to using the like-named
     * field.  (Unlike this method, the field does not provide type safety.)
     *
     * @see java.util.Collections#EMPTY_LIST
     * @since 1.5
     */
    @SuppressWarnings("unchecked")
    public static final <T> List<T> emptyList() {
        return java.util.Collections.emptyList();
    }

    /**
     * Returns a dynamically typesafe view of the specified list. Trying to
     * insert an element of the wrong type into this list throws a
     * {@code ClassCastException}. At creation time the types in {@code list}
     * are not checked for correct type.
     *
     * @param list the list to be wrapped in a typesafe list.
     * @param type the type of the elements permitted to insert.
     * @return a typesafe list.
     */
    public static <E> List<E> checkedList(List<E> list, Class<E> type) {
        return java.util.Collections.checkedList(list, type);
    }
}
