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

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * Static utility methods pertaining to {@code byte} primitives, that are not
 * already found in either {@link Byte} or {@link Arrays}, <i>and interpret
 * bytes as neither signed nor unsigned</i>. The methods which specifically
 * treat bytes as signed.
 *
 * <p>See the Guava User Guide article on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained">
 * primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Bytes {
    private Bytes() {
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted array {@code array}.
     * Searching in an unsorted array has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param array the sorted array to search.
     * @param value the element to find.
     * @return the non-negative index of the element, or a negative index which
     * is {@code -index - 1} where the element would be inserted.
     */
    public static int binarySearch(byte[] array, byte value) {
        return Arrays.binarySearch(array, value);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted array {@code array},
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted array has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param array      the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive start index.
     * @param value      the element to find.
     * @return the non-negative index of the element, or a negative index which
     * is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException       if {@code startIndex > endIndex}
     * @throws ArrayIndexOutOfBoundsException if {@code startIndex < 0 || endIndex > array.length}
     * @since 1.6
     */
    public static int binarySearch(byte[] array, int startIndex, int endIndex, byte value) {
        return Arrays.binarySearch(array, startIndex, endIndex, value);
    }

    /**
     * Fills the specified array with the specified element.
     *
     * @param array the {@code byte} array to fill.
     * @param value the {@code byte} element.
     */
    public static void fill(byte[] array, byte value) {
        Arrays.fill(array, value);
    }

    /**
     * Fills the specified range in the array with the specified element.
     *
     * @param array the {@code byte} array to fill.
     * @param start the first index to fill.
     * @param end   the last + 1 index to fill.
     * @param value the {@code byte} element.
     * @throws IllegalArgumentException       if {@code start > end}.
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0} or {@code end > array.length}.
     */
    public static void fill(byte[] array, int start, int end, byte value) {
        Arrays.fill(array, start, end, value);
    }

    /**
     * Returns a hash code for {@code value}; equal to the result of invoking
     * {@code ((Byte) value).hashCode()}.
     *
     * @param value a primitive {@code byte} value
     * @return a hash code for the value
     */
    public static int hashCode(byte value) {
        return value;
    }

    /**
     * Returns a hash code based on the contents of the given array. For any two
     * {@code byte} arrays {@code a} and {@code b}, if
     * {@code Arrays.equals(a, b)} returns {@code true}, it means
     * that the return value of {@code Arrays.hashCode(a)} equals {@code Arrays.hashCode(b)}.
     * <p>
     * The value returned by this method is the same value as the
     * {@link List#hashCode()} method which is invoked on a {@link List}
     * containing a sequence of {@link Byte} instances representing the
     * elements of array in the same order. If the array is {@code null}, the return
     * value is 0.
     *
     * @param array the array whose hash code to compute.
     * @return the hash code for {@code array}.
     */
    public static int hashCode(byte[] array) {
        return Arrays.hashCode(array);
    }

    /**
     * Compares the two arrays.
     *
     * @param array1 the first {@code byte} array.
     * @param array2 the second {@code byte} array.
     * @return {@code true} if both arrays are {@code null} or if the arrays have the
     * same length and the elements at each index in the two arrays are
     * equal, {@code false} otherwise.
     */
    public static boolean equals(byte[] array1, byte[] array2) {
        return Arrays.equals(array1, array2);
    }

    /**
     * Sorts the specified array in ascending numerical order.
     *
     * @param array the {@code byte} array to be sorted.
     */
    public static void sort(byte[] array) {
        Arrays.sort(array);
    }

    /**
     * Sorts the specified range in the array in ascending numerical order.
     *
     * @param array the {@code byte} array to be sorted.
     * @param start the start index to sort.
     * @param end   the last + 1 index to sort.
     * @throws IllegalArgumentException       if {@code start > end}.
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0} or {@code end > array.length}.
     */
    public static void sort(byte[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    /**
     * Creates a {@code String} representation of the {@code byte[]} passed. The
     * result is surrounded by brackets ({@code "[]"}), each element
     * is converted to a {@code String} via the {@link String#valueOf(int)} and
     * separated by {@code ", "}. If the array is {@code null}, then
     * {@code "null"} is returned.
     *
     * @param array the {@code byte} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(byte[] array) {
        return Arrays.toString(array);
    }

    /**
     * Copies {@code newLength} elements from {@code original} into a new array.
     * If {@code newLength} is greater than {@code original.length}, the result is padded
     * with the value {@code (byte) 0}.
     *
     * @param original  the original array
     * @param newLength the length of the new array
     * @return the new array
     * @throws NegativeArraySizeException if {@code newLength < 0}
     * @throws NullPointerException       if {@code original == null}
     * @since 1.6
     */
    public static byte[] copyOf(byte[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    /**
     * Copies elements from {@code original} into a new array, from indexes start (inclusive) to
     * end (exclusive). The original order of elements is preserved.
     * If {@code end} is greater than {@code original.length}, the result is padded
     * with the value {@code (byte) 0}.
     *
     * @param original the original array
     * @param start    the start index, inclusive
     * @param end      the end index, exclusive
     * @return the new array
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0 || start > original.length}
     * @throws IllegalArgumentException       if {@code start > end}
     * @throws NullPointerException           if {@code original == null}
     * @since 1.6
     */
    public static byte[] copyOfRange(byte[] original, int start, int end) {
        return Arrays.copyOfRange(original, start, end);
    }

    /**
     * Returns {@code true} if {@code target} is present as an element anywhere in
     * {@code array}.
     *
     * @param array  an array of {@code byte} values, possibly empty
     * @param target a primitive {@code byte} value
     * @return {@code true} if {@code array[i] == target} for some value of {@code
     * i}
     */
    public static boolean contains(byte[] array, byte target) {
        for (byte value : array) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index of the first appearance of the value {@code target} in
     * {@code array}.
     *
     * @param array  an array of {@code byte} values, possibly empty
     * @param target a primitive {@code byte} value
     * @return the least index {@code i} for which {@code array[i] == target}, or
     * {@code -1} if no such index exists.
     */
    public static int indexOf(byte[] array, byte target) {
        return indexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int indexOf(byte[] array, byte target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the start position of the first occurrence of the specified {@code
     * target} within {@code array}, or {@code -1} if there is no such occurrence.
     *
     * <p>More formally, returns the lowest index {@code i} such that {@code
     * java.util.Arrays.copyOfRange(array, i, i + target.length)} contains exactly
     * the same elements as {@code target}.
     *
     * @param array  the array to search for the sequence {@code target}
     * @param target the array to search for as a sub-sequence of {@code array}
     */
    public static int indexOf(byte[] array, byte[] target) {
        Preconditions.checkNotNull(array, "array");
        Preconditions.checkNotNull(target, "target");
        if (target.length == 0) {
            return 0;
        }

        outer:
        for (int i = 0; i < array.length - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    /**
     * Returns the index of the last appearance of the value {@code target} in
     * {@code array}.
     *
     * @param array  an array of {@code byte} values, possibly empty
     * @param target a primitive {@code byte} value
     * @return the greatest index {@code i} for which {@code array[i] == target},
     * or {@code -1} if no such index exists.
     */
    public static int lastIndexOf(byte[] array, byte target) {
        return lastIndexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int lastIndexOf(byte[] array, byte target, int start, int end) {
        for (int i = end - 1; i >= start; i--) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the values from each provided array combined into a single array.
     * For example, {@code concat(new byte[] {a, b}, new byte[] {}, new
     * byte[] {c}} returns the array {@code {a, b, c}}.
     *
     * @param arrays zero or more {@code byte} arrays
     * @return a single array containing all the values from the source arrays, in
     * order
     */
    public static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }

    /**
     * Returns an array containing the same values as {@code array}, but
     * guaranteed to be of a specified minimum length. If {@code array} already
     * has a length of at least {@code minLength}, it is returned directly.
     * Otherwise, a new array of size {@code minLength + padding} is returned,
     * containing the values of {@code array}, and zeroes in the remaining places.
     *
     * @param array     the source array
     * @param minLength the minimum length the returned array must guarantee
     * @param padding   an extra amount to "grow" the array by if growth is
     *                  necessary
     * @return an array containing the values of {@code array}, with guaranteed
     * minimum length {@code minLength}
     * @throws IllegalArgumentException if {@code minLength} or {@code padding} is
     *                                  negative
     */
    public static byte[] ensureCapacity(byte[] array, int minLength, int padding) {
        Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", minLength);
        Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", padding);
        return (array.length < minLength)
                ? copyOf(array, minLength + padding)
                : array;
    }

    /**
     * Returns an array containing each value of {@code collection}, converted to
     * a {@code byte} value in the manner of {@link Number#byteValue}.
     *
     * <p>Elements are copied from the argument collection as if by {@code
     * collection.toArray()}.  Calling this method is as thread-safe as calling
     * that method.
     *
     * @param collection a collection of {@code Number} instances
     * @return an array containing the same values as {@code collection}, in the
     * same order, converted to primitives
     * @throws NullPointerException if {@code collection} or any of its elements
     *                              is null
     * @since 1.0 (parameter was {@code Collection<Byte>} before 12.0)
     */
    public static byte[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof ByteArrayAsList) {
            return ((ByteArrayAsList) collection).toByteArray();
        }

        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        byte[] array = new byte[len];
        for (int i = 0; i < len; i++) {
            // checkNotNull for GWT (do not optimize)
            array[i] = ((Number) Preconditions.checkNotNull(boxedArray[i])).byteValue();
        }
        return array;
    }

    /**
     * Returns a fixed-size list backed by the specified array, similar to {@link
     * Arrays#asList(Object[])}. The list supports {@link List#set(int, Object)},
     * but any attempt to set a value to {@code null} will result in a {@link
     * NullPointerException}.
     *
     * <p>The returned list maintains the values, but not the identities, of
     * {@code Byte} objects written to or read from it.  For example, whether
     * {@code list.get(0) == list.get(0)} is true for the returned list is
     * unspecified.
     *
     * @param backingArray the array to back the list
     * @return a list view of the array
     */
    public static List<Byte> asList(byte... backingArray) {
        if (backingArray.length == 0) {
            return Collections.emptyList();
        }
        return new ByteArrayAsList(backingArray);
    }

    private static class ByteArrayAsList extends AbstractList<Byte>
            implements RandomAccess, Serializable {
        final byte[] array;
        final int start;
        final int end;

        ByteArrayAsList(byte[] array) {
            this(array, 0, array.length);
        }

        ByteArrayAsList(byte[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        public int size() {
            return end - start;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Byte get(int index) {
            Preconditions.checkElementIndex(index, size());
            return array[start + index];
        }

        @Override
        public boolean contains(Object target) {
            // Overridden to prevent a ton of boxing
            return (target instanceof Byte)
                    && Bytes.indexOf(array, (Byte) target, start, end) != -1;
        }

        @Override
        public int indexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Byte) {
                int i = Bytes.indexOf(array, (Byte) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Byte) {
                int i = Bytes.lastIndexOf(array, (Byte) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public Byte set(int index, Byte element) {
            Preconditions.checkElementIndex(index, size());
            byte oldValue = array[start + index];
            // checkNotNull for GWT (do not optimize)
            array[start + index] = Preconditions.checkNotNull(element);
            return oldValue;
        }

        @Override
        public List<Byte> subList(int fromIndex, int toIndex) {
            int size = size();
            Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
            if (fromIndex == toIndex) {
                return Collections.emptyList();
            }
            return new ByteArrayAsList(array, start + fromIndex, start + toIndex);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof ByteArrayAsList) {
                ByteArrayAsList that = (ByteArrayAsList) object;
                int size = size();
                if (that.size() != size) {
                    return false;
                }
                for (int i = 0; i < size; i++) {
                    if (array[start + i] != that.array[that.start + i]) {
                        return false;
                    }
                }
                return true;
            }
            return super.equals(object);
        }

        @Override
        public int hashCode() {
            int result = 1;
            for (int i = start; i < end; i++) {
                result = 31 * result + Bytes.hashCode(array[i]);
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(size() * 5);
            builder.append('[').append(array[start]);
            for (int i = start + 1; i < end; i++) {
                builder.append(", ").append(array[i]);
            }
            return builder.append(']').toString();
        }

        byte[] toByteArray() {
            // Arrays.copyOfRange() is not available under GWT
            int size = size();
            byte[] result = new byte[size];
            System.arraycopy(array, start, result, 0, size);
            return result;
        }

        private static final long serialVersionUID = 0;
    }
}
