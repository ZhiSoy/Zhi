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

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

import static java.lang.Math.abs;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;

/**
 * Static utility methods pertaining to {@code int} primitives, that are not
 * already found in either {@link Integer} or {@link Arrays}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained">
 * primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Ints {
    private Ints() {
    }

    // CUSTOM

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
    public static int binarySearch(int[] array, int value) {
        return binarySearch(array, 0, array.length, value);
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
    public static int binarySearch(int[] array, int startIndex, int endIndex, int value) {
        return Arrays.binarySearch(array, startIndex, endIndex, value);
    }

    /**
     * Fills the specified array with the specified element.
     *
     * @param array the {@code int} array to fill.
     * @param value the {@code int} element.
     */
    public static void fill(int[] array, int value) {
        Arrays.fill(array, value);
    }

    /**
     * Fills the specified range in the array with the specified element.
     *
     * @param array the {@code int} array to fill.
     * @param start the first index to fill.
     * @param end   the last + 1 index to fill.
     * @param value the {@code int} element.
     * @throws IllegalArgumentException       if {@code start > end}.
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0} or {@code end > array.length}.
     */
    public static void fill(int[] array, int start, int end, int value) {
        Arrays.fill(array, start, end, value);
    }

    /**
     * The number of bytes required to represent a primitive {@code int}
     * value.
     */
    public static final int BYTES = Integer.SIZE / Byte.SIZE;

    /**
     * The largest power of two that can be represented as an {@code int}.
     *
     * @since 10.0
     */
    public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    /**
     * Returns a hash code for {@code value}; equal to the result of invoking
     * {@code ((Integer) value).hashCode()}.
     *
     * @param value a primitive {@code int} value
     * @return a hash code for the value
     */
    public static int hashCode(int value) {
        return value;
    }

    /**
     * Returns a hash code based on the contents of the given array. For any two
     * not-null {@code int} arrays {@code a} and {@code b}, if
     * {@code Arrays.equals(a, b)} returns {@code true}, it means
     * that the return value of {@code Arrays.hashCode(a)} equals {@code Arrays.hashCode(b)}.
     * <p>
     * The value returned by this method is the same value as the
     * {@link List#hashCode()} method which is invoked on a {@link List}
     * containing a sequence of {@link Integer} instances representing the
     * elements of array in the same order. If the array is {@code null}, the return
     * value is 0.
     *
     * @param array the array whose hash code to compute.
     * @return the hash code for {@code array}.
     */
    public static int hashCode(int[] array) {
        return Arrays.hashCode(array);
    }

    /**
     * Compares the two arrays.
     *
     * @param array1 the first {@code int} array.
     * @param array2 the second {@code int} array.
     * @return {@code true} if both arrays are {@code null} or if the arrays have the
     * same length and the elements at each index in the two arrays are
     * equal, {@code false} otherwise.
     */
    public static boolean equals(int[] array1, int[] array2) {
        return Arrays.equals(array1, array2);
    }

    /**
     * Sorts the specified array in ascending numerical order.
     *
     * @param array the {@code int} array to be sorted.
     */
    public static void sort(int[] array) {
        Arrays.sort(array);
    }

    /**
     * Sorts the specified range in the array in ascending numerical order.
     *
     * @param array the {@code int} array to be sorted.
     * @param start the start index to sort.
     * @param end   the last + 1 index to sort.
     * @throws IllegalArgumentException       if {@code start > end}.
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0} or {@code end > array.length}.
     */
    public static void sort(int[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    /**
     * Creates a {@code String} representation of the {@code int[]} passed. The
     * result is surrounded by brackets ({@code "[]"}), each element
     * is converted to a {@code String} via the {@link String#valueOf(int)} and
     * separated by {@code ", "}. If the array is {@code null}, then
     * {@code "null"} is returned.
     *
     * @param array the {@code int} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(int[] array) {
        return Arrays.toString(array);
    }

    /**
     * Copies {@code newLength} elements from {@code original} into a new array.
     * If {@code newLength} is greater than {@code original.length}, the result is padded
     * with the value {@code 0}.
     *
     * @param original  the original array
     * @param newLength the length of the new array
     * @return the new array
     * @throws NegativeArraySizeException if {@code newLength < 0}
     * @throws NullPointerException       if {@code original == null}
     * @since 1.6
     */
    public static int[] copyOf(int[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    /**
     * Copies elements from {@code original} into a new array, from indexes start (inclusive) to
     * end (exclusive). The original order of elements is preserved.
     * If {@code end} is greater than {@code original.length}, the result is padded
     * with the value {@code 0}.
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
    public static int[] copyOfRange(int[] original, int start, int end) {
        return Arrays.copyOfRange(original, start, end);
    }

    /**
     * Returns the {@code int} value that is equal to {@code value}, if possible.
     *
     * @param value any value in the range of the {@code int} type
     * @return the {@code int} value that equals {@code value}
     * @throws IllegalArgumentException if {@code value} is greater than {@link
     *                                  Integer#MAX_VALUE} or less than {@link Integer#MIN_VALUE}
     */
    public static int checkedCast(long value) {
        int result = (int) value;
        if (result != value) {
            // don't use checkArgument here, to avoid boxing
            throw new IllegalArgumentException("Out of range: " + value);
        }
        return result;
    }

    /**
     * Returns the {@code int} nearest in value to {@code value}.
     *
     * @param value any {@code long} value
     * @return the same value cast to {@code int} if it is in the range of the
     * {@code int} type, {@link Integer#MAX_VALUE} if it is too large,
     * or {@link Integer#MIN_VALUE} if it is too small
     */
    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    /**
     * Compares the two specified {@code int} values. The sign of the value
     * returned is the same as that of {@code ((Integer) a).compareTo(b)}.
     *
     * <p><b>Note for Java 7 and later:</b> this method should be treated as
     * deprecated; use the equivalent {@link Integer#compare} method instead.
     *
     * @param a the first {@code int} to compare
     * @param b the second {@code int} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive
     * value if {@code a} is greater than {@code b}; or zero if they are equal
     */
    public static int compare(int a, int b) {
        return (a < b) ? -1 : ((a > b) ? 1 : 0);
    }

    /**
     * Returns {@code true} if {@code target} is present as an element anywhere in
     * {@code array}.
     *
     * @param array  an array of {@code int} values, possibly empty
     * @param target a primitive {@code int} value
     * @return {@code true} if {@code array[i] == target} for some value of {@code
     * i}
     */
    public static boolean contains(int[] array, int target) {
        for (int value : array) {
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
     * @param array  an array of {@code int} values, possibly empty
     * @param target a primitive {@code int} value
     * @return the least index {@code i} for which {@code array[i] == target}, or
     * {@code -1} if no such index exists.
     */
    public static int indexOf(int[] array, int target) {
        return indexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int indexOf(
            int[] array, int target, int start, int end) {
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
    public static int indexOf(int[] array, int[] target) {
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
     * @param array  an array of {@code int} values, possibly empty
     * @param target a primitive {@code int} value
     * @return the greatest index {@code i} for which {@code array[i] == target},
     * or {@code -1} if no such index exists.
     */
    public static int lastIndexOf(int[] array, int target) {
        return lastIndexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int lastIndexOf(
            int[] array, int target, int start, int end) {
        for (int i = end - 1; i >= start; i--) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the least value present in {@code array}.
     *
     * @param array a <i>nonempty</i> array of {@code int} values
     * @return the value present in {@code array} that is less than or equal to
     * every other value in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static int min(int... array) {
        Preconditions.checkArgument(array.length > 0);
        int min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * Returns the greatest value present in {@code array}.
     *
     * @param array a <i>nonempty</i> array of {@code int} values
     * @return the value present in {@code array} that is greater than or equal to
     * every other value in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static int max(int... array) {
        Preconditions.checkArgument(array.length > 0);
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * Returns the values from each provided array combined into a single array.
     * For example, {@code concat(new int[] {a, b}, new int[] {}, new
     * int[] {c}} returns the array {@code {a, b, c}}.
     *
     * @param arrays zero or more {@code int} arrays
     * @return a single array containing all the values from the source arrays, in
     * order
     */
    public static int[] concat(int[]... arrays) {
        int length = 0;
        for (int[] array : arrays) {
            length += array.length;
        }
        int[] result = new int[length];
        int pos = 0;
        for (int[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }

    /**
     * Returns a big-endian representation of {@code value} in a 4-element byte
     * array; equivalent to {@code ByteBuffer.allocate(4).putInt(value).array()}.
     * For example, the input value {@code 0x12131415} would yield the byte array
     * {@code {0x12, 0x13, 0x14, 0x15}}.
     *
     * <p>If you need to convert and concatenate several values (possibly even of
     * different types), use a shared {@link java.nio.ByteBuffer} instance, or use
     * com.google.common.io.ByteStreams#newDataOutput() to get a growable
     * buffer.
     */
    public static byte[] toByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value};
    }

    /**
     * Returns the {@code int} value whose big-endian representation is stored in
     * the first 4 bytes of {@code bytes}; equivalent to {@code
     * ByteBuffer.wrap(bytes).getInt()}. For example, the input byte array {@code
     * {0x12, 0x13, 0x14, 0x15, 0x33}} would yield the {@code int} value {@code
     * 0x12131415}.
     *
     * <p>Arguably, it's preferable to use {@link java.nio.ByteBuffer}; that
     * library exposes much more flexibility at little cost in readability.
     *
     * @throws IllegalArgumentException if {@code bytes} has fewer than 4 elements
     */
    public static int fromByteArray(byte[] bytes) {
        Preconditions.checkArgument(bytes.length >= BYTES,
                "array too small: %s < %s", bytes.length, BYTES);
        return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3]);
    }

    /**
     * Returns the {@code int} value whose byte representation is the given 4
     * bytes, in big-endian order; equivalent to {@code Ints.fromByteArray(new
     * byte[] {b1, b2, b3, b4})}.
     *
     * @since 7.0
     */
    public static int fromBytes(byte b1, byte b2, byte b3, byte b4) {
        return b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);
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
    public static int[] ensureCapacity(int[] array, int minLength, int padding) {
        Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", minLength);
        Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", padding);
        return (array.length < minLength)
                ? copyOf(array, minLength + padding)
                : array;
    }

    /**
     * Returns a string containing the supplied {@code int} values separated
     * by {@code separator}. For example, {@code join("-", 1, 2, 3)} returns
     * the string {@code "1-2-3"}.
     *
     * @param separator the text that should appear between consecutive values in
     *                  the resulting string (but not at the start or end)
     * @param array     an array of {@code int} values, possibly empty
     */
    public static String join(String separator, int... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 5);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    /**
     * Returns a comparator that compares two {@code int} arrays
     * lexicographically. That is, it compares, using {@link
     * #compare(int, int)}), the first pair of values that follow any
     * common prefix, or when one array is a prefix of the other, treats the
     * shorter array as the lesser. For example, {@code [] < [1] < [1, 2] < [2]}.
     *
     * <p>The returned comparator is inconsistent with {@link
     * Object#equals(Object)} (since arrays support only identity equality), but
     * it is consistent with {@link Arrays#equals(int[], int[])}.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Lexicographical_order">
     * Lexicographical order article at Wikipedia</a>
     * @since 2.0
     */
    public static Comparator<int[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    private enum LexicographicalComparator implements Comparator<int[]> {
        INSTANCE;

        @Override
        public int compare(int[] left, int[] right) {
            int minLength = Math.min(left.length, right.length);
            for (int i = 0; i < minLength; i++) {
                int result = Ints.compare(left[i], right[i]);
                if (result != 0) {
                    return result;
                }
            }
            return left.length - right.length;
        }
    }

    /**
     * Returns an array containing each value of {@code collection}, converted to
     * a {@code int} value in the manner of {@link Number#intValue}.
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
     * @since 1.0 (parameter was {@code Collection<Integer>} before 12.0)
     */
    public static int[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof IntArrayAsList) {
            return ((IntArrayAsList) collection).toIntArray();
        }

        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            // checkNotNull for GWT (do not optimize)
            array[i] = ((Number) Preconditions.checkNotNull(boxedArray[i])).intValue();
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
     * {@code Integer} objects written to or read from it.  For example, whether
     * {@code list.get(0) == list.get(0)} is true for the returned list is
     * unspecified.
     *
     * @param backingArray the array to back the list
     * @return a list view of the array
     */
    public static List<Integer> asList(int... backingArray) {
        if (backingArray.length == 0) {
            return Collections.emptyList();
        }
        return new IntArrayAsList(backingArray);
    }

    private static class IntArrayAsList extends AbstractList<Integer>
            implements RandomAccess, Serializable {
        final int[] array;
        final int start;
        final int end;

        IntArrayAsList(int[] array) {
            this(array, 0, array.length);
        }

        IntArrayAsList(int[] array, int start, int end) {
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
        public Integer get(int index) {
            Preconditions.checkElementIndex(index, size());
            return array[start + index];
        }

        @Override
        public boolean contains(Object target) {
            // Overridden to prevent a ton of boxing
            return (target instanceof Integer)
                    && Ints.indexOf(array, (Integer) target, start, end) != -1;
        }

        @Override
        public int indexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Integer) {
                int i = Ints.indexOf(array, (Integer) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Integer) {
                int i = Ints.lastIndexOf(array, (Integer) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public Integer set(int index, Integer element) {
            Preconditions.checkElementIndex(index, size());
            int oldValue = array[start + index];
            // checkNotNull for GWT (do not optimize)
            array[start + index] = Preconditions.checkNotNull(element);
            return oldValue;
        }

        @Override
        public List<Integer> subList(int fromIndex, int toIndex) {
            int size = size();
            Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
            if (fromIndex == toIndex) {
                return Collections.emptyList();
            }
            return new IntArrayAsList(array, start + fromIndex, start + toIndex);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof IntArrayAsList) {
                IntArrayAsList that = (IntArrayAsList) object;
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
                result = 31 * result + Ints.hashCode(array[i]);
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

        int[] toIntArray() {
            // Arrays.copyOfRange() is not available under GWT
            int size = size();
            int[] result = new int[size];
            System.arraycopy(array, start, result, 0, size);
            return result;
        }

        private static final long serialVersionUID = 0;
    }

    private static final byte[] asciiDigits = new byte[128];

    static {
        Arrays.fill(asciiDigits, (byte) -1);
        for (int i = 0; i <= 9; i++) {
            asciiDigits['0' + i] = (byte) i;
        }
        for (int i = 0; i <= 26; i++) {
            asciiDigits['A' + i] = (byte) (10 + i);
            asciiDigits['a' + i] = (byte) (10 + i);
        }
    }

    private static int digit(char c) {
        return (c < 128) ? asciiDigits[c] : -1;
    }

    /**
     * Parses the specified string as a signed decimal integer value. The ASCII
     * character {@code '-'} (<code>'&#92;u002D'</code>) is recognized as the
     * minus sign.
     *
     * <p>Unlike {@link Integer#parseInt(String)}, this method returns
     * {@code null} instead of throwing an exception if parsing fails.
     * Additionally, this method only accepts ASCII digits, and returns
     * {@code null} if non-ASCII digits are present in the string.
     *
     * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even
     * under JDK 7, despite the change to {@link Integer#parseInt(String)} for
     * that version.
     *
     * @param string the string representation of an integer value
     * @return the integer value represented by {@code string}, or {@code null} if
     * {@code string} has a length of zero or cannot be parsed as an integer
     * value
     * @since 11.0
     */
    public static Integer tryParse(String string) {
        return tryParse(string, 10);
    }

    /**
     * Parses the specified string as a signed integer value using the specified
     * radix. The ASCII character {@code '-'} (<code>'&#92;u002D'</code>) is
     * recognized as the minus sign.
     *
     * <p>Unlike {@link Integer#parseInt(String, int)}, this method returns
     * {@code null} instead of throwing an exception if parsing fails.
     * Additionally, this method only accepts ASCII digits, and returns
     * {@code null} if non-ASCII digits are present in the string.
     *
     * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even
     * under JDK 7, despite the change to {@link Integer#parseInt(String, int)}
     * for that version.
     *
     * @param string the string representation of an integer value
     * @param radix  the radix to use when parsing
     * @return the integer value represented by {@code string} using
     * {@code radix}, or {@code null} if {@code string} has a length of zero
     * or cannot be parsed as an integer value
     * @throws IllegalArgumentException if {@code radix < Character.MIN_RADIX} or
     *                                  {@code radix > Character.MAX_RADIX}
     */
    static Integer tryParse(String string, int radix) {
        if (Preconditions.checkNotNull(string).isEmpty()) {
            return null;
        }
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new IllegalArgumentException(
                    "radix must be between MIN_RADIX and MAX_RADIX but was " + radix);
        }
        boolean negative = string.charAt(0) == '-';
        int index = negative ? 1 : 0;
        if (index == string.length()) {
            return null;
        }
        int digit = digit(string.charAt(index++));
        if (digit < 0 || digit >= radix) {
            return null;
        }
        int accum = -digit;

        int cap = Integer.MIN_VALUE / radix;

        while (index < string.length()) {
            digit = digit(string.charAt(index++));
            if (digit < 0 || digit >= radix || accum < cap) {
                return null;
            }
            accum *= radix;
            if (accum < Integer.MIN_VALUE + digit) {
                return null;
            }
            accum -= digit;
        }

        if (negative) {
            return accum;
        } else if (accum == Integer.MIN_VALUE) {
            return null;
        } else {
            return -accum;
        }
    }

    // MATH
    // NOTE: Whenever both tests are cheap and functional, it's faster to use &, | instead of &&, ||

    /**
     * Returns {@code true} if {@code x} represents a power of two.
     *
     * <p>This differs from {@code Integer.bitCount(x) == 1}, because
     * {@code Integer.bitCount(Integer.MIN_VALUE) == 1}, but {@link Integer#MIN_VALUE} is not a power
     * of two.
     */
    public static boolean isPowerOfTwo(int x) {
        return x > 0 & (x & (x - 1)) == 0;
    }

    /**
     * Returns 1 if {@code x < y} as unsigned integers, and 0 otherwise. Assumes that x - y fits into
     * a signed int. The implementation is branch-free, and benchmarks suggest it is measurably (if
     * narrowly) faster than the straightforward ternary expression.
     */
    static int lessThanBranchFree(int x, int y) {
        // The double negation is optimized away by normal Java, but is necessary for GWT
        // to make sure bit twiddling works as expected.
        return ~~(x - y) >>> (Integer.SIZE - 1);
    }

    /**
     * Returns the base-2 logarithm of {@code x}, rounded according to the specified rounding mode.
     *
     * @throws IllegalArgumentException if {@code x <= 0}
     * @throws ArithmeticException      if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code x}
     *                                  is not a power of two
     */
    @SuppressWarnings("fallthrough")
    // TODO(kevinb): remove after this warning is disabled globally
    public static int log2(int x, RoundingMode mode) {
        Preconditions.Math.checkPositive("x", x);
        switch (mode) {
            case UNNECESSARY:
                Preconditions.Math.checkRoundingUnnecessary(isPowerOfTwo(x));
                // fall through
            case DOWN:
            case FLOOR:
                return (Integer.SIZE - 1) - Integer.numberOfLeadingZeros(x);

            case UP:
            case CEILING:
                return Integer.SIZE - Integer.numberOfLeadingZeros(x - 1);

            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                // Since sqrt(2) is irrational, log2(x) - logFloor cannot be exactly 0.5
                int leadingZeros = Integer.numberOfLeadingZeros(x);
                int cmp = MAX_POWER_OF_SQRT2_UNSIGNED >>> leadingZeros;
                // floor(2^(logFloor + 0.5))
                int logFloor = (Integer.SIZE - 1) - leadingZeros;
                return logFloor + lessThanBranchFree(cmp, x);

            default:
                throw new AssertionError();
        }
    }

    /** The biggest half power of two that can fit in an unsigned int. */
    static final int MAX_POWER_OF_SQRT2_UNSIGNED = 0xB504F333;

    /**
     * Returns the base-10 logarithm of {@code x}, rounded according to the specified rounding mode.
     *
     * @throws IllegalArgumentException if {@code x <= 0}
     * @throws ArithmeticException      if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code x}
     *                                  is not a power of ten
     */
    @SuppressWarnings("fallthrough")
    public static int log10(int x, RoundingMode mode) {
        Preconditions.Math.checkPositive("x", x);
        int logFloor = log10Floor(x);
        int floorPow = powersOf10[logFloor];
        switch (mode) {
            case UNNECESSARY:
                Preconditions.Math.checkRoundingUnnecessary(x == floorPow);
                // fall through
            case FLOOR:
            case DOWN:
                return logFloor;
            case CEILING:
            case UP:
                return logFloor + lessThanBranchFree(floorPow, x);
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                // sqrt(10) is irrational, so log10(x) - logFloor is never exactly 0.5
                return logFloor + lessThanBranchFree(halfPowersOf10[logFloor], x);
            default:
                throw new AssertionError();
        }
    }

    private static int log10Floor(int x) {
    /*
     * Based on Hacker's Delight Fig. 11-5, the two-table-lookup, branch-free implementation.
     *
     * The key idea is that based on the number of leading zeros (equivalently, floor(log2(x))),
     * we can narrow the possible floor(log10(x)) values to two.  For example, if floor(log2(x))
     * is 6, then 64 <= x < 128, so floor(log10(x)) is either 1 or 2.
     */
        int y = maxLog10ForLeadingZeros[Integer.numberOfLeadingZeros(x)];
    /*
     * y is the higher of the two possible values of floor(log10(x)). If x < 10^y, then we want the
     * lower of the two possible values, or y - 1, otherwise, we want y.
     */
        return y - lessThanBranchFree(x, powersOf10[y]);
    }

    // maxLog10ForLeadingZeros[i] == floor(log10(2^(Long.SIZE - i)))
    static final byte[] maxLog10ForLeadingZeros = {9, 9, 9, 8, 8, 8,
            7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0};

    static final int[] powersOf10 = {1, 10, 100, 1000, 10000,
            100000, 1000000, 10000000, 100000000, 1000000000};

    // halfPowersOf10[i] = largest int less than 10^(i + 0.5)
    static final int[] halfPowersOf10 =
            {3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE};

    /**
     * Returns {@code b} to the {@code k}th power. Even if the result overflows, it will be equal to
     * {@code BigInteger.valueOf(b).pow(k).intValue()}. This implementation runs in {@code O(log k)}
     * time.
     *
     * <p>Compare {@link #checkedPow}, which throws an {@link ArithmeticException} upon overflow.
     *
     * @throws IllegalArgumentException if {@code k < 0}
     */
    public static int pow(int b, int k) {
        Preconditions.Math.checkNonNegative("exponent", k);
        switch (b) {
            case 0:
                return (k == 0) ? 1 : 0;
            case 1:
                return 1;
            case (-1):
                return ((k & 1) == 0) ? 1 : -1;
            case 2:
                return (k < Integer.SIZE) ? (1 << k) : 0;
            case (-2):
                if (k < Integer.SIZE) {
                    return ((k & 1) == 0) ? (1 << k) : -(1 << k);
                } else {
                    return 0;
                }
            default:
                // continue below to handle the general case
        }
        for (int accum = 1; ; k >>= 1) {
            switch (k) {
                case 0:
                    return accum;
                case 1:
                    return b * accum;
                default:
                    accum *= ((k & 1) == 0) ? 1 : b;
                    b *= b;
            }
        }
    }

    /**
     * Returns the square root of {@code x}, rounded with the specified rounding mode.
     *
     * @throws IllegalArgumentException if {@code x < 0}
     * @throws ArithmeticException      if {@code mode} is {@link RoundingMode#UNNECESSARY} and
     *                                  {@code sqrt(x)} is not an integer
     */
    @SuppressWarnings("fallthrough")
    public static int sqrt(int x, RoundingMode mode) {
        Preconditions.Math.checkNonNegative("x", x);
        int sqrtFloor = sqrtFloor(x);
        switch (mode) {
            case UNNECESSARY:
                Preconditions.Math.checkRoundingUnnecessary(sqrtFloor * sqrtFloor == x); // fall through
            case FLOOR:
            case DOWN:
                return sqrtFloor;
            case CEILING:
            case UP:
                return sqrtFloor + lessThanBranchFree(sqrtFloor * sqrtFloor, x);
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                int halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
        /*
         * We wish to test whether or not x <= (sqrtFloor + 0.5)^2 = halfSquare + 0.25. Since both
         * x and halfSquare are integers, this is equivalent to testing whether or not x <=
         * halfSquare. (We have to deal with overflow, though.)
         *
         * If we treat halfSquare as an unsigned int, we know that
         *            sqrtFloor^2 <= x < (sqrtFloor + 1)^2
         * halfSquare - sqrtFloor <= x < halfSquare + sqrtFloor + 1
         * so |x - halfSquare| <= sqrtFloor.  Therefore, it's safe to treat x - halfSquare as a
         * signed int, so lessThanBranchFree is safe for use.
         */
                return sqrtFloor + lessThanBranchFree(halfSquare, x);
            default:
                throw new AssertionError();
        }
    }

    private static int sqrtFloor(int x) {
        // There is no loss of precision in converting an int to a double, according to
        // http://java.sun.com/docs/books/jls/third_edition/html/conversions.html#5.1.2
        return (int) Math.sqrt(x);
    }

    /**
     * Returns the result of dividing {@code p} by {@code q}, rounding using the specified
     * {@code RoundingMode}.
     *
     * @throws ArithmeticException if {@code q == 0}, or if {@code mode == UNNECESSARY} and {@code a}
     *                             is not an integer multiple of {@code b}
     */
    @SuppressWarnings("fallthrough")
    public static int divide(int p, int q, RoundingMode mode) {
        Preconditions.checkNotNull(mode);
        if (q == 0) {
            throw new ArithmeticException("/ by zero"); // for GWT
        }
        int div = p / q;
        int rem = p - q * div; // equal to p % q

        if (rem == 0) {
            return div;
        }

    /*
     * Normal Java division rounds towards 0, consistently with RoundingMode.DOWN. We just have to
     * deal with the cases where rounding towards 0 is wrong, which typically depends on the sign of
     * p / q.
     *
     * signum is 1 if p and q are both nonnegative or both negative, and -1 otherwise.
     */
        int signum = 1 | ((p ^ q) >> (Integer.SIZE - 1));
        boolean increment;
        switch (mode) {
            case UNNECESSARY:
                Preconditions.Math.checkRoundingUnnecessary(rem == 0);
                // fall through
            case DOWN:
                increment = false;
                break;
            case UP:
                increment = true;
                break;
            case CEILING:
                increment = signum > 0;
                break;
            case FLOOR:
                increment = signum < 0;
                break;
            case HALF_EVEN:
            case HALF_DOWN:
            case HALF_UP:
                int absRem = abs(rem);
                int cmpRemToHalfDivisor = absRem - (abs(q) - absRem);
                // subtracting two nonnegative ints can't overflow
                // cmpRemToHalfDivisor has the same sign as compare(abs(rem), abs(q) / 2).
                if (cmpRemToHalfDivisor == 0) { // exactly on the half mark
                    increment = (mode == HALF_UP || (mode == HALF_EVEN & (div & 1) != 0));
                } else {
                    increment = cmpRemToHalfDivisor > 0; // closer to the UP value
                }
                break;
            default:
                throw new AssertionError();
        }
        return increment ? div + signum : div;
    }

    /**
     * Returns {@code x mod m}, a non-negative value less than {@code m}.
     * This differs from {@code x % m}, which might be negative.
     *
     * <p>For example:<pre> {@code
     *
     * mod(7, 4) == 3
     * mod(-7, 4) == 1
     * mod(-1, 4) == 3
     * mod(-8, 4) == 0
     * mod(8, 4) == 0}</pre>
     *
     * @throws ArithmeticException if {@code m <= 0}
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.17.3">
     * Remainder Operator</a>
     */
    public static int mod(int x, int m) {
        if (m <= 0) {
            throw new ArithmeticException("Modulus " + m + " must be > 0");
        }
        int result = x % m;
        return (result >= 0) ? result : result + m;
    }

    /**
     * Returns the greatest common divisor of {@code a, b}. Returns {@code 0} if
     * {@code a == 0 && b == 0}.
     *
     * @throws IllegalArgumentException if {@code a < 0} or {@code b < 0}
     */
    public static int gcd(int a, int b) {
    /*
     * The reason we require both arguments to be >= 0 is because otherwise, what do you return on
     * gcd(0, Integer.MIN_VALUE)? BigInteger.gcd would return positive 2^31, but positive 2^31
     * isn't an int.
     */
        Preconditions.Math.checkNonNegative("a", a);
        Preconditions.Math.checkNonNegative("b", b);
        if (a == 0) {
            // 0 % b == 0, so b divides a, but the converse doesn't hold.
            // BigInteger.gcd is consistent with this decision.
            return b;
        } else if (b == 0) {
            return a; // similar logic
        }
    /*
     * Uses the binary GCD algorithm; see http://en.wikipedia.org/wiki/Binary_GCD_algorithm.
     * This is >40% faster than the Euclidean algorithm in benchmarks.
     */
        int aTwos = Integer.numberOfTrailingZeros(a);
        a >>= aTwos; // divide out all 2s
        int bTwos = Integer.numberOfTrailingZeros(b);
        b >>= bTwos; // divide out all 2s
        while (a != b) { // both a, b are odd
            // The key to the binary GCD algorithm is as follows:
            // Both a and b are odd.  Assume a > b; then gcd(a - b, b) = gcd(a, b).
            // But in gcd(a - b, b), a - b is even and b is odd, so we can divide out powers of two.

            // We bend over backwards to avoid branching, adapting a technique from
            // http://graphics.stanford.edu/~seander/bithacks.html#IntegerMinOrMax

            int delta = a - b; // can't overflow, since a and b are nonnegative

            int minDeltaOrZero = delta & (delta >> (Integer.SIZE - 1));
            // equivalent to Math.min(delta, 0)

            a = delta - minDeltaOrZero - minDeltaOrZero; // sets a to Math.abs(a - b)
            // a is now nonnegative and even

            b += minDeltaOrZero; // sets b to min(old a, b)
            a >>= Integer.numberOfTrailingZeros(a); // divide out all 2s, since 2 doesn't divide b
        }
        return a << min(aTwos, bTwos);
    }

    /**
     * Returns the sum of {@code a} and {@code b}, provided it does not overflow.
     *
     * @throws ArithmeticException if {@code a + b} overflows in signed {@code int} arithmetic
     */
    public static int checkedAdd(int a, int b) {
        long result = (long) a + b;
        Preconditions.Math.checkNoOverflow(result == (int) result);
        return (int) result;
    }

    /**
     * Returns the difference of {@code a} and {@code b}, provided it does not overflow.
     *
     * @throws ArithmeticException if {@code a - b} overflows in signed {@code int} arithmetic
     */
    public static int checkedSubtract(int a, int b) {
        long result = (long) a - b;
        Preconditions.Math.checkNoOverflow(result == (int) result);
        return (int) result;
    }

    /**
     * Returns the product of {@code a} and {@code b}, provided it does not overflow.
     *
     * @throws ArithmeticException if {@code a * b} overflows in signed {@code int} arithmetic
     */
    public static int checkedMultiply(int a, int b) {
        long result = (long) a * b;
        Preconditions.Math.checkNoOverflow(result == (int) result);
        return (int) result;
    }

    /**
     * Returns the {@code b} to the {@code k}th power, provided it does not overflow.
     *
     * <p>{@link #pow} may be faster, but does not check for overflow.
     *
     * @throws ArithmeticException if {@code b} to the {@code k}th power overflows in signed
     *                             {@code int} arithmetic
     */
    public static int checkedPow(int b, int k) {
        Preconditions.Math.checkNonNegative("exponent", k);
        switch (b) {
            case 0:
                return (k == 0) ? 1 : 0;
            case 1:
                return 1;
            case (-1):
                return ((k & 1) == 0) ? 1 : -1;
            case 2:
                Preconditions.Math.checkNoOverflow(k < Integer.SIZE - 1);
                return 1 << k;
            case (-2):
                Preconditions.Math.checkNoOverflow(k < Integer.SIZE);
                return ((k & 1) == 0) ? 1 << k : -1 << k;
            default:
                // continue below to handle the general case
        }
        int accum = 1;
        while (true) {
            switch (k) {
                case 0:
                    return accum;
                case 1:
                    return checkedMultiply(accum, b);
                default:
                    if ((k & 1) != 0) {
                        accum = checkedMultiply(accum, b);
                    }
                    k >>= 1;
                    if (k > 0) {
                        Preconditions.Math.checkNoOverflow(-FLOOR_SQRT_MAX_INT <= b & b <= FLOOR_SQRT_MAX_INT);
                        b *= b;
                    }
            }
        }
    }

    static final int FLOOR_SQRT_MAX_INT = 46340;

    /**
     * Returns {@code n!}, that is, the product of the first {@code n} positive
     * integers, {@code 1} if {@code n == 0}, or {@link Integer#MAX_VALUE} if the
     * result does not fit in a {@code int}.
     *
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public static int factorial(int n) {
        Preconditions.Math.checkNonNegative("n", n);
        return (n < factorials.length) ? factorials[n] : Integer.MAX_VALUE;
    }

    private static final int[] factorials = {
            1,
            1,
            1 * 2,
            1 * 2 * 3,
            1 * 2 * 3 * 4,
            1 * 2 * 3 * 4 * 5,
            1 * 2 * 3 * 4 * 5 * 6,
            1 * 2 * 3 * 4 * 5 * 6 * 7,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12};

    /**
     * Returns {@code n} choose {@code k}, also known as the binomial coefficient of {@code n} and
     * {@code k}, or {@link Integer#MAX_VALUE} if the result does not fit in an {@code int}.
     *
     * @throws IllegalArgumentException if {@code n < 0}, {@code k < 0} or {@code k > n}
     */
    public static int binomial(int n, int k) {
        Preconditions.Math.checkNonNegative("n", n);
        Preconditions.Math.checkNonNegative("k", k);
        Preconditions.checkArgument(k <= n, "k (%s) > n (%s)", k, n);
        if (k > (n >> 1)) {
            k = n - k;
        }
        if (k >= biggestBinomials.length || n > biggestBinomials[k]) {
            return Integer.MAX_VALUE;
        }
        switch (k) {
            case 0:
                return 1;
            case 1:
                return n;
            default:
                long result = 1;
                for (int i = 0; i < k; i++) {
                    result *= n - i;
                    result /= i + 1;
                }
                return (int) result;
        }
    }

    // binomial(biggestBinomials[k], k) fits in an int, but not binomial(biggestBinomials[k]+1,k).
    static int[] biggestBinomials = {
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            65536,
            2345,
            477,
            193,
            110,
            75,
            58,
            49,
            43,
            39,
            37,
            35,
            34,
            34,
            33
    };

    /**
     * Returns the arithmetic mean of {@code x} and {@code y}, rounded towards
     * negative infinity. This method is overflow resilient.
     *
     * @since 14.0
     */
    public static int mean(int x, int y) {
        // Efficient method for computing the arithmetic mean.
        // The alternative (x + y) / 2 fails for large values.
        // The alternative (x + y) >>> 1 fails for negative values.
        return (x & y) + ((x ^ y) >> 1);
    }
}
