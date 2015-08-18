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
 * Static utility methods pertaining to {@code long} primitives, that are not
 * already found in either {@link Long} or {@link Arrays}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained">
 * primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Longs {
    private Longs() {
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
    public static int binarySearch(long[] array, long value) {
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
    public static int binarySearch(long[] array, int startIndex, int endIndex, long value) {
        return Arrays.binarySearch(array, startIndex, endIndex, value);
    }

    /**
     * Fills the specified array with the specified element.
     *
     * @param array the {@code long} array to fill.
     * @param value the {@code long} element.
     */
    public static void fill(long[] array, long value) {
        Arrays.fill(array, value);
    }

    /**
     * Fills the specified range in the array with the specified element.
     *
     * @param array the {@code long} array to fill.
     * @param start the first index to fill.
     * @param end   the last + 1 index to fill.
     * @param value the {@code long} element.
     * @throws IllegalArgumentException       if {@code start > end}.
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0} or {@code end > array.length}.
     */
    public static void fill(long[] array, int start, int end, long value) {
        Arrays.fill(array, start, end, value);
    }

    /**
     * The number of bytes required to represent a primitive {@code long}
     * value.
     */
    public static final int BYTES = Long.SIZE / Byte.SIZE;

    /**
     * The largest power of two that can be represented as a {@code long}.
     *
     * @since 10.0
     */
    public static final long MAX_POWER_OF_TWO = 1L << (Long.SIZE - 2);

    /**
     * Returns a hash code for {@code value}; equal to the result of invoking
     * {@code ((Long) value).hashCode()}.
     *
     * <p>This method always return the value specified by {@link
     * Long#hashCode()} in java, which might be different from
     * {@code ((Long) value).hashCode()} in GWT because {@link Long#hashCode()}
     * in GWT does not obey the JRE contract.
     *
     * @param value a primitive {@code long} value
     * @return a hash code for the value
     */
    public static int hashCode(long value) {
        return (int) (value ^ (value >>> 32));
    }

    /**
     * Returns a hash code based on the contents of the given array. For any two
     * {@code long} arrays {@code a} and {@code b}, if
     * {@code Arrays.equals(a, b)} returns {@code true}, it means
     * that the return value of {@code Arrays.hashCode(a)} equals {@code Arrays.hashCode(b)}.
     * <p>
     * The value returned by this method is the same value as the
     * {@link List#hashCode()} method which is invoked on a {@link List}
     * containing a sequence of {@link Long} instances representing the
     * elements of array in the same order. If the array is {@code null}, the return
     * value is 0.
     *
     * @param array the array whose hash code to compute.
     * @return the hash code for {@code array}.
     */
    public static int hashCode(long[] array) {
        return Arrays.hashCode(array);
    }

    /**
     * Compares the two arrays.
     *
     * @param array1 the first {@code long} array.
     * @param array2 the second {@code long} array.
     * @return {@code true} if both arrays are {@code null} or if the arrays have the
     * same length and the elements at each index in the two arrays are
     * equal, {@code false} otherwise.
     */
    public static boolean equals(long[] array1, long[] array2) {
        return Arrays.equals(array1, array2);
    }

    /**
     * Sorts the specified array in ascending numerical order.
     *
     * @param array the {@code long} array to be sorted.
     */
    public static void sort(long[] array) {
        Arrays.sort(array);
    }

    /**
     * Sorts the specified range in the array in ascending numerical order.
     *
     * @param array the {@code long} array to be sorted.
     * @param start the start index to sort.
     * @param end   the last + 1 index to sort.
     * @throws IllegalArgumentException       if {@code start > end}.
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0} or {@code end > array.length}.
     */
    public static void sort(long[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    /**
     * Creates a {@code String} representation of the {@code long[]} passed. The
     * result is surrounded by brackets ({@code "[]"}), each element
     * is converted to a {@code String} via the {@link String#valueOf(long)} and
     * separated by {@code ", "}. If the array is {@code null}, then
     * {@code "null"} is returned.
     *
     * @param array the {@code long} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(long[] array) {
        return Arrays.toString(array);
    }

    /**
     * Copies {@code newLength} elements from {@code original} into a new array.
     * If {@code newLength} is greater than {@code original.length}, the result is padded
     * with the value {@code 0L}.
     *
     * @param original  the original array
     * @param newLength the length of the new array
     * @return the new array
     * @throws NegativeArraySizeException if {@code newLength < 0}
     * @throws NullPointerException       if {@code original == null}
     * @since 1.6
     */
    public static long[] copyOf(long[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    /**
     * Copies elements from {@code original} into a new array, from indexes start (inclusive) to
     * end (exclusive). The original order of elements is preserved.
     * If {@code end} is greater than {@code original.length}, the result is padded
     * with the value {@code 0L}.
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
    public static long[] copyOfRange(long[] original, int start, int end) {
        return Arrays.copyOfRange(original, start, end);
    }

    /**
     * Compares the two specified {@code long} values. The sign of the value
     * returned is the same as that of {@code ((Long) a).compareTo(b)}.
     *
     * <p><b>Note for Java 7 and later:</b> this method should be treated as
     * deprecated; use the equivalent {@link Long#compare} method instead.
     *
     * @param a the first {@code long} to compare
     * @param b the second {@code long} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive
     * value if {@code a} is greater than {@code b}; or zero if they are equal
     */
    public static int compare(long a, long b) {
        return (a < b) ? -1 : ((a > b) ? 1 : 0);
    }

    /**
     * Returns {@code true} if {@code target} is present as an element anywhere in
     * {@code array}.
     *
     * @param array  an array of {@code long} values, possibly empty
     * @param target a primitive {@code long} value
     * @return {@code true} if {@code array[i] == target} for some value of {@code
     * i}
     */
    public static boolean contains(long[] array, long target) {
        for (long value : array) {
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
     * @param array  an array of {@code long} values, possibly empty
     * @param target a primitive {@code long} value
     * @return the least index {@code i} for which {@code array[i] == target}, or
     * {@code -1} if no such index exists.
     */
    public static int indexOf(long[] array, long target) {
        return indexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int indexOf(
            long[] array, long target, int start, int end) {
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
    public static int indexOf(long[] array, long[] target) {
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
     * @param array  an array of {@code long} values, possibly empty
     * @param target a primitive {@code long} value
     * @return the greatest index {@code i} for which {@code array[i] == target},
     * or {@code -1} if no such index exists.
     */
    public static int lastIndexOf(long[] array, long target) {
        return lastIndexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int lastIndexOf(
            long[] array, long target, int start, int end) {
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
     * @param array a <i>nonempty</i> array of {@code long} values
     * @return the value present in {@code array} that is less than or equal to
     * every other value in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static long min(long... array) {
        Preconditions.checkArgument(array.length > 0);
        long min = array[0];
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
     * @param array a <i>nonempty</i> array of {@code long} values
     * @return the value present in {@code array} that is greater than or equal to
     * every other value in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static long max(long... array) {
        Preconditions.checkArgument(array.length > 0);
        long max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * Returns the values from each provided array combined into a single array.
     * For example, {@code concat(new long[] {a, b}, new long[] {}, new
     * long[] {c}} returns the array {@code {a, b, c}}.
     *
     * @param arrays zero or more {@code long} arrays
     * @return a single array containing all the values from the source arrays, in
     * order
     */
    public static long[] concat(long[]... arrays) {
        int length = 0;
        for (long[] array : arrays) {
            length += array.length;
        }
        long[] result = new long[length];
        int pos = 0;
        for (long[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }

    /**
     * Returns a big-endian representation of {@code value} in an 8-element byte
     * array; equivalent to {@code ByteBuffer.allocate(8).putLong(value).array()}.
     * For example, the input value {@code 0x1213141516171819L} would yield the
     * byte array {@code {0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19}}.
     *
     * <p>If you need to convert and concatenate several values (possibly even of
     * different types), use a shared {@link java.nio.ByteBuffer} instance, or use
     * com.google.common.io.ByteStreams#newDataOutput() to get a growable
     * buffer.
     */
    public static byte[] toByteArray(long value) {
        // Note that this code needs to stay compatible with GWT, which has known
        // bugs when narrowing byte casts of long values occur.
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xffL);
            value >>= 8;
        }
        return result;
    }

    /**
     * Returns the {@code long} value whose big-endian representation is
     * stored in the first 8 bytes of {@code bytes}; equivalent to {@code
     * ByteBuffer.wrap(bytes).getLong()}. For example, the input byte array
     * {@code {0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19}} would yield the
     * {@code long} value {@code 0x1213141516171819L}.
     *
     * <p>Arguably, it's preferable to use {@link java.nio.ByteBuffer}; that
     * library exposes much more flexibility at little cost in readability.
     *
     * @throws IllegalArgumentException if {@code bytes} has fewer than 8
     *                                  elements
     */
    public static long fromByteArray(byte[] bytes) {
        Preconditions.checkArgument(bytes.length >= BYTES,
                "array too small: %s < %s", bytes.length, BYTES);
        return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3],
                bytes[4], bytes[5], bytes[6], bytes[7]);
    }

    /**
     * Returns the {@code long} value whose byte representation is the given 8
     * bytes, in big-endian order; equivalent to {@code Longs.fromByteArray(new
     * byte[] {b1, b2, b3, b4, b5, b6, b7, b8})}.
     *
     * @since 7.0
     */
    public static long fromBytes(byte b1, byte b2, byte b3, byte b4,
            byte b5, byte b6, byte b7, byte b8) {
        return (b1 & 0xFFL) << 56
                | (b2 & 0xFFL) << 48
                | (b3 & 0xFFL) << 40
                | (b4 & 0xFFL) << 32
                | (b5 & 0xFFL) << 24
                | (b6 & 0xFFL) << 16
                | (b7 & 0xFFL) << 8
                | (b8 & 0xFFL);
    }

    /**
     * Parses the specified string as a signed decimal long value. The ASCII
     * character {@code '-'} (<code>'&#92;u002D'</code>) is recognized as the
     * minus sign.
     *
     * <p>Unlike {@link Long#parseLong(String)}, this method returns
     * {@code null} instead of throwing an exception if parsing fails.
     * Additionally, this method only accepts ASCII digits, and returns
     * {@code null} if non-ASCII digits are present in the string.
     *
     * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even
     * under JDK 7, despite the change to {@link Long#parseLong(String)} for
     * that version.
     *
     * @param string the string representation of a long value
     * @return the long value represented by {@code string}, or {@code null} if
     * {@code string} has a length of zero or cannot be parsed as a long
     * value
     * @since 14.0
     */
    public static Long tryParse(String string) {
        if (Preconditions.checkNotNull(string).isEmpty()) {
            return null;
        }
        boolean negative = string.charAt(0) == '-';
        int index = negative ? 1 : 0;
        if (index == string.length()) {
            return null;
        }
        int digit = string.charAt(index++) - '0';
        if (digit < 0 || digit > 9) {
            return null;
        }
        long accum = -digit;
        while (index < string.length()) {
            digit = string.charAt(index++) - '0';
            if (digit < 0 || digit > 9 || accum < Long.MIN_VALUE / 10) {
                return null;
            }
            accum *= 10;
            if (accum < Long.MIN_VALUE + digit) {
                return null;
            }
            accum -= digit;
        }

        if (negative) {
            return accum;
        } else if (accum == Long.MIN_VALUE) {
            return null;
        } else {
            return -accum;
        }
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
    public static long[] ensureCapacity(long[] array, int minLength, int padding) {
        Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", minLength);
        Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", padding);
        return (array.length < minLength)
                ? copyOf(array, minLength + padding)
                : array;
    }

    /**
     * Returns a string containing the supplied {@code long} values separated
     * by {@code separator}. For example, {@code join("-", 1L, 2L, 3L)} returns
     * the string {@code "1-2-3"}.
     *
     * @param separator the text that should appear between consecutive values in
     *                  the resulting string (but not at the start or end)
     * @param array     an array of {@code long} values, possibly empty
     */
    public static String join(String separator, long... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 10);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    /**
     * Returns a comparator that compares two {@code long} arrays
     * lexicographically. That is, it compares, using {@link
     * #compare(long, long)}), the first pair of values that follow any
     * common prefix, or when one array is a prefix of the other, treats the
     * shorter array as the lesser. For example,
     * {@code [] < [1L] < [1L, 2L] < [2L]}.
     *
     * <p>The returned comparator is inconsistent with {@link
     * Object#equals(Object)} (since arrays support only identity equality), but
     * it is consistent with {@link Arrays#equals(long[], long[])}.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Lexicographical_order">
     * Lexicographical order article at Wikipedia</a>
     * @since 2.0
     */
    public static Comparator<long[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    private enum LexicographicalComparator implements Comparator<long[]> {
        INSTANCE;

        @Override
        public int compare(long[] left, long[] right) {
            int minLength = Math.min(left.length, right.length);
            for (int i = 0; i < minLength; i++) {
                int result = Longs.compare(left[i], right[i]);
                if (result != 0) {
                    return result;
                }
            }
            return left.length - right.length;
        }
    }

    /**
     * Returns an array containing each value of {@code collection}, converted to
     * a {@code long} value in the manner of {@link Number#longValue}.
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
     * @since 1.0 (parameter was {@code Collection<Long>} before 12.0)
     */
    public static long[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof LongArrayAsList) {
            return ((LongArrayAsList) collection).toLongArray();
        }

        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        long[] array = new long[len];
        for (int i = 0; i < len; i++) {
            // checkNotNull for GWT (do not optimize)
            array[i] = ((Number) Preconditions.checkNotNull(boxedArray[i])).longValue();
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
     * {@code Long} objects written to or read from it.  For example, whether
     * {@code list.get(0) == list.get(0)} is true for the returned list is
     * unspecified.
     *
     * @param backingArray the array to back the list
     * @return a list view of the array
     */
    public static List<Long> asList(long... backingArray) {
        if (backingArray.length == 0) {
            return Collections.emptyList();
        }
        return new LongArrayAsList(backingArray);
    }

    private static class LongArrayAsList extends AbstractList<Long>
            implements RandomAccess, Serializable {
        final long[] array;
        final int start;
        final int end;

        LongArrayAsList(long[] array) {
            this(array, 0, array.length);
        }

        LongArrayAsList(long[] array, int start, int end) {
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
        public Long get(int index) {
            Preconditions.checkElementIndex(index, size());
            return array[start + index];
        }

        @Override
        public boolean contains(Object target) {
            // Overridden to prevent a ton of boxing
            return (target instanceof Long)
                    && Longs.indexOf(array, (Long) target, start, end) != -1;
        }

        @Override
        public int indexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Long) {
                int i = Longs.indexOf(array, (Long) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Long) {
                int i = Longs.lastIndexOf(array, (Long) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public Long set(int index, Long element) {
            Preconditions.checkElementIndex(index, size());
            long oldValue = array[start + index];
            // checkNotNull for GWT (do not optimize)
            array[start + index] = Preconditions.checkNotNull(element);
            return oldValue;
        }

        @Override
        public List<Long> subList(int fromIndex, int toIndex) {
            int size = size();
            Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
            if (fromIndex == toIndex) {
                return Collections.emptyList();
            }
            return new LongArrayAsList(array, start + fromIndex, start + toIndex);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof LongArrayAsList) {
                LongArrayAsList that = (LongArrayAsList) object;
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
                result = 31 * result + Longs.hashCode(array[i]);
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(size() * 10);
            builder.append('[').append(array[start]);
            for (int i = start + 1; i < end; i++) {
                builder.append(", ").append(array[i]);
            }
            return builder.append(']').toString();
        }

        long[] toLongArray() {
            // Arrays.copyOfRange() is not available under GWT
            int size = size();
            long[] result = new long[size];
            System.arraycopy(array, start, result, 0, size);
            return result;
        }

        private static final long serialVersionUID = 0;
    }

    // NOTE: Whenever both tests are cheap and functional, it's faster to use &, | instead of &&, ||

    /**
     * Returns {@code true} if {@code x} represents a power of two.
     *
     * <p>This differs from {@code Long.bitCount(x) == 1}, because
     * {@code Long.bitCount(Long.MIN_VALUE) == 1}, but {@link Long#MIN_VALUE} is not a power of two.
     */
    public static boolean isPowerOfTwo(long x) {
        return x > 0 & (x & (x - 1)) == 0;
    }

    /**
     * Returns 1 if {@code x < y} as unsigned longs, and 0 otherwise.  Assumes that x - y fits into a
     * signed long.  The implementation is branch-free, and benchmarks suggest it is measurably
     * faster than the straightforward ternary expression.
     */
    static int lessThanBranchFree(long x, long y) {
        // Returns the sign bit of x - y.
        return (int) (~~(x - y) >>> (Long.SIZE - 1));
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
    public static int log2(long x, RoundingMode mode) {
        Preconditions.Math.checkPositive("x", x);
        switch (mode) {
            case UNNECESSARY:
                Preconditions.Math.checkRoundingUnnecessary(isPowerOfTwo(x));
                // fall through
            case DOWN:
            case FLOOR:
                return (Long.SIZE - 1) - Long.numberOfLeadingZeros(x);

            case UP:
            case CEILING:
                return Long.SIZE - Long.numberOfLeadingZeros(x - 1);

            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                // Since sqrt(2) is irrational, log2(x) - logFloor cannot be exactly 0.5
                int leadingZeros = Long.numberOfLeadingZeros(x);
                long cmp = MAX_POWER_OF_SQRT2_UNSIGNED >>> leadingZeros;
                // floor(2^(logFloor + 0.5))
                int logFloor = (Long.SIZE - 1) - leadingZeros;
                return logFloor + lessThanBranchFree(cmp, x);

            default:
                throw new AssertionError("impossible");
        }
    }

    /** The biggest half power of two that fits into an unsigned long */
    static final long MAX_POWER_OF_SQRT2_UNSIGNED = 0xB504F333F9DE6484L;

    /**
     * Returns the base-10 logarithm of {@code x}, rounded according to the specified rounding mode.
     *
     * @throws IllegalArgumentException if {@code x <= 0}
     * @throws ArithmeticException      if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code x}
     *                                  is not a power of ten
     */
    @SuppressWarnings("fallthrough")
    // TODO(kevinb): remove after this warning is disabled globally
    public static int log10(long x, RoundingMode mode) {
        Preconditions.Math.checkPositive("x", x);
        int logFloor = log10Floor(x);
        long floorPow = powersOf10[logFloor];
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
                // sqrt(10) is irrational, so log10(x)-logFloor is never exactly 0.5
                return logFloor + lessThanBranchFree(halfPowersOf10[logFloor], x);
            default:
                throw new AssertionError();
        }
    }

    static int log10Floor(long x) {
    /*
     * Based on Hacker's Delight Fig. 11-5, the two-table-lookup, branch-free implementation.
     *
     * The key idea is that based on the number of leading zeros (equivalently, floor(log2(x))),
     * we can narrow the possible floor(log10(x)) values to two.  For example, if floor(log2(x))
     * is 6, then 64 <= x < 128, so floor(log10(x)) is either 1 or 2.
     */
        int y = maxLog10ForLeadingZeros[Long.numberOfLeadingZeros(x)];
    /*
     * y is the higher of the two possible values of floor(log10(x)). If x < 10^y, then we want the
     * lower of the two possible values, or y - 1, otherwise, we want y.
     */
        return y - lessThanBranchFree(x, powersOf10[y]);
    }

    // maxLog10ForLeadingZeros[i] == floor(log10(2^(Long.SIZE - i)))
    static final byte[] maxLog10ForLeadingZeros = {
            19, 18, 18, 18, 18, 17, 17, 17, 16, 16, 16, 15, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12,
            12, 12, 11, 11, 11, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4,
            3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0};

    static final long[] powersOf10 = {
            1L,
            10L,
            100L,
            1000L,
            10000L,
            100000L,
            1000000L,
            10000000L,
            100000000L,
            1000000000L,
            10000000000L,
            100000000000L,
            1000000000000L,
            10000000000000L,
            100000000000000L,
            1000000000000000L,
            10000000000000000L,
            100000000000000000L,
            1000000000000000000L
    };

    // halfPowersOf10[i] = largest long less than 10^(i + 0.5)
    static final long[] halfPowersOf10 = {
            3L,
            31L,
            316L,
            3162L,
            31622L,
            316227L,
            3162277L,
            31622776L,
            316227766L,
            3162277660L,
            31622776601L,
            316227766016L,
            3162277660168L,
            31622776601683L,
            316227766016837L,
            3162277660168379L,
            31622776601683793L,
            316227766016837933L,
            3162277660168379331L
    };

    /**
     * Returns {@code b} to the {@code k}th power. Even if the result overflows, it will be equal to
     * {@code BigInteger.valueOf(b).pow(k).longValue()}. This implementation runs in {@code O(log k)}
     * time.
     *
     * @throws IllegalArgumentException if {@code k < 0}
     */
    public static long pow(long b, int k) {
        Preconditions.Math.checkNonNegative("exponent", k);
        if (-2 <= b && b <= 2) {
            switch ((int) b) {
                case 0:
                    return (k == 0) ? 1 : 0;
                case 1:
                    return 1;
                case (-1):
                    return ((k & 1) == 0) ? 1 : -1;
                case 2:
                    return (k < Long.SIZE) ? 1L << k : 0;
                case (-2):
                    if (k < Long.SIZE) {
                        return ((k & 1) == 0) ? 1L << k : -(1L << k);
                    } else {
                        return 0;
                    }
                default:
                    throw new AssertionError();
            }
        }
        for (long accum = 1; ; k >>= 1) {
            switch (k) {
                case 0:
                    return accum;
                case 1:
                    return accum * b;
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
    public static long sqrt(long x, RoundingMode mode) {
        Preconditions.Math.checkNonNegative("x", x);
        if (fitsInInt(x)) {
            return Ints.sqrt((int) x, mode);
        }
    /*
     * Let k be the true value of floor(sqrt(x)), so that
     *
     *            k * k <= x          <  (k + 1) * (k + 1)
     * (double) (k * k) <= (double) x <= (double) ((k + 1) * (k + 1))
     *          since casting to double is nondecreasing.
     *          Note that the right-hand inequality is no longer strict.
     * Math.sqrt(k * k) <= Math.sqrt(x) <= Math.sqrt((k + 1) * (k + 1))
     *          since Math.sqrt is monotonic.
     * (long) Math.sqrt(k * k) <= (long) Math.sqrt(x) <= (long) Math.sqrt((k + 1) * (k + 1))
     *          since casting to long is monotonic
     * k <= (long) Math.sqrt(x) <= k + 1
     *          since (long) Math.sqrt(k * k) == k, as checked exhaustively in
     *          {@link LongMathTest#testSqrtOfPerfectSquareAsDoubleIsPerfect}
     */
        long guess = (long) Math.sqrt(x);
        // Note: guess is always <= FLOOR_SQRT_MAX_LONG.
        long guessSquared = guess * guess;
        // Note (2013-2-26): benchmarks indicate that, inscrutably enough, using if statements is
        // faster here than using lessThanBranchFree.
        switch (mode) {
            case UNNECESSARY:
                Preconditions.Math.checkRoundingUnnecessary(guessSquared == x);
                return guess;
            case FLOOR:
            case DOWN:
                if (x < guessSquared) {
                    return guess - 1;
                }
                return guess;
            case CEILING:
            case UP:
                if (x > guessSquared) {
                    return guess + 1;
                }
                return guess;
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                long sqrtFloor = guess - ((x < guessSquared) ? 1 : 0);
                long halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
        /*
         * We wish to test whether or not x <= (sqrtFloor + 0.5)^2 = halfSquare + 0.25. Since both
         * x and halfSquare are integers, this is equivalent to testing whether or not x <=
         * halfSquare. (We have to deal with overflow, though.)
         *
         * If we treat halfSquare as an unsigned long, we know that
         *            sqrtFloor^2 <= x < (sqrtFloor + 1)^2
         * halfSquare - sqrtFloor <= x < halfSquare + sqrtFloor + 1
         * so |x - halfSquare| <= sqrtFloor.  Therefore, it's safe to treat x - halfSquare as a
         * signed long, so lessThanBranchFree is safe for use.
         */
                return sqrtFloor + lessThanBranchFree(halfSquare, x);
            default:
                throw new AssertionError();
        }
    }

    /**
     * Returns the result of dividing {@code p} by {@code q}, rounding using the specified
     * {@code RoundingMode}.
     *
     * @throws ArithmeticException if {@code q == 0}, or if {@code mode == UNNECESSARY} and {@code a}
     *                             is not an integer multiple of {@code b}
     */
    @SuppressWarnings("fallthrough")
    public static long divide(long p, long q, RoundingMode mode) {
        Preconditions.checkNotNull(mode);
        long div = p / q; // throws if q == 0
        long rem = p - q * div; // equals p % q

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
        int signum = 1 | (int) ((p ^ q) >> (Long.SIZE - 1));
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
                long absRem = abs(rem);
                long cmpRemToHalfDivisor = absRem - (abs(q) - absRem);
                // subtracting two nonnegative longs can't overflow
                // cmpRemToHalfDivisor has the same sign as compare(abs(rem), abs(q) / 2).
                if (cmpRemToHalfDivisor == 0) { // exactly on the half mark
                    increment = (mode == HALF_UP | (mode == HALF_EVEN & (div & 1) != 0));
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
     * <p>For example:
     *
     * <pre> {@code
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
    public static int mod(long x, int m) {
        // Cast is safe because the result is guaranteed in the range [0, m)
        return (int) mod(x, (long) m);
    }

    /**
     * Returns {@code x mod m}, a non-negative value less than {@code m}.
     * This differs from {@code x % m}, which might be negative.
     *
     * <p>For example:
     *
     * <pre> {@code
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
    public static long mod(long x, long m) {
        if (m <= 0) {
            throw new ArithmeticException("Modulus must be positive");
        }
        long result = x % m;
        return (result >= 0) ? result : result + m;
    }

    /**
     * Returns the greatest common divisor of {@code a, b}. Returns {@code 0} if
     * {@code a == 0 && b == 0}.
     *
     * @throws IllegalArgumentException if {@code a < 0} or {@code b < 0}
     */
    public static long gcd(long a, long b) {
    /*
     * The reason we require both arguments to be >= 0 is because otherwise, what do you return on
     * gcd(0, Long.MIN_VALUE)? BigInteger.gcd would return positive 2^63, but positive 2^63 isn't
     * an int.
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
     * This is >60% faster than the Euclidean algorithm in benchmarks.
     */
        int aTwos = Long.numberOfTrailingZeros(a);
        a >>= aTwos; // divide out all 2s
        int bTwos = Long.numberOfTrailingZeros(b);
        b >>= bTwos; // divide out all 2s
        while (a != b) { // both a, b are odd
            // The key to the binary GCD algorithm is as follows:
            // Both a and b are odd.  Assume a > b; then gcd(a - b, b) = gcd(a, b).
            // But in gcd(a - b, b), a - b is even and b is odd, so we can divide out powers of two.

            // We bend over backwards to avoid branching, adapting a technique from
            // http://graphics.stanford.edu/~seander/bithacks.html#IntegerMinOrMax

            long delta = a - b; // can't overflow, since a and b are nonnegative

            long minDeltaOrZero = delta & (delta >> (Long.SIZE - 1));
            // equivalent to Math.min(delta, 0)

            a = delta - minDeltaOrZero - minDeltaOrZero; // sets a to Math.abs(a - b)
            // a is now nonnegative and even

            b += minDeltaOrZero; // sets b to min(old a, b)
            a >>= Long.numberOfTrailingZeros(a); // divide out all 2s, since 2 doesn't divide b
        }
        return a << min(aTwos, bTwos);
    }

    /**
     * Returns the sum of {@code a} and {@code b}, provided it does not overflow.
     *
     * @throws ArithmeticException if {@code a + b} overflows in signed {@code long} arithmetic
     */
    public static long checkedAdd(long a, long b) {
        long result = a + b;
        Preconditions.Math.checkNoOverflow((a ^ b) < 0 | (a ^ result) >= 0);
        return result;
    }

    /**
     * Returns the difference of {@code a} and {@code b}, provided it does not overflow.
     *
     * @throws ArithmeticException if {@code a - b} overflows in signed {@code long} arithmetic
     */
    public static long checkedSubtract(long a, long b) {
        long result = a - b;
        Preconditions.Math.checkNoOverflow((a ^ b) >= 0 | (a ^ result) >= 0);
        return result;
    }

    /**
     * Returns the product of {@code a} and {@code b}, provided it does not overflow.
     *
     * @throws ArithmeticException if {@code a * b} overflows in signed {@code long} arithmetic
     */
    public static long checkedMultiply(long a, long b) {
        // Hacker's Delight, Section 2-12
        int leadingZeros = Long.numberOfLeadingZeros(a) + Long.numberOfLeadingZeros(~a)
                + Long.numberOfLeadingZeros(b) + Long.numberOfLeadingZeros(~b);
    /*
     * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's < Long.SIZE it's definitely
     * bad. We do the leadingZeros check to avoid the division below if at all possible.
     *
     * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a are 0 and 1. We take
     * care of all a < 0 with their own check, because in particular, the case a == -1 will
     * incorrectly pass the division check below.
     *
     * In all other cases, we check that either a is 0 or the result is consistent with division.
     */
        if (leadingZeros > Long.SIZE + 1) {
            return a * b;
        }
        Preconditions.Math.checkNoOverflow(leadingZeros >= Long.SIZE);
        Preconditions.Math.checkNoOverflow(a >= 0 | b != Long.MIN_VALUE);
        long result = a * b;
        Preconditions.Math.checkNoOverflow(a == 0 || result / a == b);
        return result;
    }

    /**
     * Returns the {@code b} to the {@code k}th power, provided it does not overflow.
     *
     * @throws ArithmeticException if {@code b} to the {@code k}th power overflows in signed
     *                             {@code long} arithmetic
     */
    public static long checkedPow(long b, int k) {
        Preconditions.Math.checkNonNegative("exponent", k);
        if (b >= -2 & b <= 2) {
            switch ((int) b) {
                case 0:
                    return (k == 0) ? 1 : 0;
                case 1:
                    return 1;
                case (-1):
                    return ((k & 1) == 0) ? 1 : -1;
                case 2:
                    Preconditions.Math.checkNoOverflow(k < Long.SIZE - 1);
                    return 1L << k;
                case (-2):
                    Preconditions.Math.checkNoOverflow(k < Long.SIZE);
                    return ((k & 1) == 0) ? (1L << k) : (-1L << k);
                default:
                    throw new AssertionError();
            }
        }
        long accum = 1;
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
                        Preconditions.Math.checkNoOverflow(b <= FLOOR_SQRT_MAX_LONG);
                        b *= b;
                    }
            }
        }
    }

    static final long FLOOR_SQRT_MAX_LONG = 3037000499L;

    /**
     * Returns {@code n!}, that is, the product of the first {@code n} positive
     * integers, {@code 1} if {@code n == 0}, or {@link Long#MAX_VALUE} if the
     * result does not fit in a {@code long}.
     *
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public static long factorial(int n) {
        Preconditions.Math.checkNonNegative("n", n);
        return (n < factorials.length) ? factorials[n] : Long.MAX_VALUE;
    }

    static final long[] factorials = {
            1L,
            1L,
            1L * 2,
            1L * 2 * 3,
            1L * 2 * 3 * 4,
            1L * 2 * 3 * 4 * 5,
            1L * 2 * 3 * 4 * 5 * 6,
            1L * 2 * 3 * 4 * 5 * 6 * 7,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16 * 17,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16 * 17 * 18,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16 * 17 * 18 * 19,
            1L * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16 * 17 * 18 * 19 * 20
    };

    /**
     * Returns {@code n} choose {@code k}, also known as the binomial coefficient of {@code n} and
     * {@code k}, or {@link Long#MAX_VALUE} if the result does not fit in a {@code long}.
     *
     * @throws IllegalArgumentException if {@code n < 0}, {@code k < 0}, or {@code k > n}
     */
    public static long binomial(int n, int k) {
        Preconditions.Math.checkNonNegative("n", n);
        Preconditions.Math.checkNonNegative("k", k);
        Preconditions.checkArgument(k <= n, "k (%s) > n (%s)", k, n);
        if (k > (n >> 1)) {
            k = n - k;
        }
        switch (k) {
            case 0:
                return 1;
            case 1:
                return n;
            default:
                if (n < factorials.length) {
                    return factorials[n] / (factorials[k] * factorials[n - k]);
                } else if (k >= biggestBinomials.length || n > biggestBinomials[k]) {
                    return Long.MAX_VALUE;
                } else if (k < biggestSimpleBinomials.length && n <= biggestSimpleBinomials[k]) {
                    // guaranteed not to overflow
                    long result = n--;
                    for (int i = 2; i <= k; n--, i++) {
                        result *= n;
                        result /= i;
                    }
                    return result;
                } else {
                    int nBits = Longs.log2(n, RoundingMode.CEILING);

                    long result = 1;
                    long numerator = n--;
                    long denominator = 1;

                    int numeratorBits = nBits;
                    // This is an upper bound on log2(numerator, ceiling).

          /*
           * We want to do this in long math for speed, but want to avoid overflow. We adapt the
           * technique previously used by BigIntegerMath: maintain separate numerator and
           * denominator accumulators, multiplying the fraction into result when near overflow.
           */
                    for (int i = 2; i <= k; i++, n--) {
                        if (numeratorBits + nBits < Long.SIZE - 1) {
                            // It's definitely safe to multiply into numerator and denominator.
                            numerator *= n;
                            denominator *= i;
                            numeratorBits += nBits;
                        } else {
                            // It might not be safe to multiply into numerator and denominator,
                            // so multiply (numerator / denominator) into result.
                            result = multiplyFraction(result, numerator, denominator);
                            numerator = n;
                            denominator = i;
                            numeratorBits = nBits;
                        }
                    }
                    return multiplyFraction(result, numerator, denominator);
                }
        }
    }

    /**
     * Returns (x * numerator / denominator), which is assumed to come out to an integral value.
     */
    static long multiplyFraction(long x, long numerator, long denominator) {
        if (x == 1) {
            return numerator / denominator;
        }
        long commonDivisor = gcd(x, denominator);
        x /= commonDivisor;
        denominator /= commonDivisor;
        // We know gcd(x, denominator) = 1, and x * numerator / denominator is exact,
        // so denominator must be a divisor of numerator.
        return x * (numerator / denominator);
    }

    /*
     * binomial(biggestBinomials[k], k) fits in a long, but not
     * binomial(biggestBinomials[k] + 1, k).
     */
    static final int[] biggestBinomials =
            {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3810779, 121977, 16175, 4337, 1733,
                    887, 534, 361, 265, 206, 169, 143, 125, 111, 101, 94, 88, 83, 79, 76, 74, 72, 70, 69, 68,
                    67, 67, 66, 66, 66, 66};

    /*
     * binomial(biggestSimpleBinomials[k], k) doesn't need to use the slower GCD-based impl,
     * but binomial(biggestSimpleBinomials[k] + 1, k) does.
     */
    static final int[] biggestSimpleBinomials =
            {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2642246, 86251, 11724, 3218, 1313,
                    684, 419, 287, 214, 169, 139, 119, 105, 95, 87, 81, 76, 73, 70, 68, 66, 64, 63, 62, 62,
                    61, 61, 61};
    // These values were generated by using checkedMultiply to see when the simple multiply/divide
    // algorithm would lead to an overflow.

    static boolean fitsInInt(long x) {
        return (int) x == x;
    }

    /**
     * Returns the arithmetic mean of {@code x} and {@code y}, rounded toward
     * negative infinity. This method is resilient to overflow.
     *
     * @since 14.0
     */
    public static long mean(long x, long y) {
        // Efficient method for computing the arithmetic mean.
        // The alternative (x + y) / 2 fails for large values.
        // The alternative (x + y) >>> 1 fails for negative values.
        return (x & y) + ((x ^ y) >> 1);
    }
}
