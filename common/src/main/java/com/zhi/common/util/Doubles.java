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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import java.util.regex.Pattern;

import static java.lang.Double.MAX_EXPONENT;
import static java.lang.Double.MIN_EXPONENT;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.isNaN;
import static java.lang.Double.longBitsToDouble;
import static java.lang.Math.abs;
import static java.lang.Math.copySign;
import static java.lang.Math.getExponent;
import static java.lang.Math.log;
import static java.lang.Math.rint;

/**
 * Static utility methods pertaining to {@code double} primitives, that are not
 * already found in either {@link Double} or {@link Arrays}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained">
 * primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Doubles {
    private Doubles() {
    }

    // Custom

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
    public static int binarySearch(double[] array, double value) {
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
    public static int binarySearch(double[] array, int startIndex, int endIndex, double value) {
        return Arrays.binarySearch(array, startIndex, endIndex, value);
    }

    /**
     * Fills the specified array with the specified element.
     *
     * @param array the {@code double} array to fill.
     * @param value the {@code double} element.
     */
    public static void fill(double[] array, double value) {
        Arrays.fill(array, value);
    }

    /**
     * Fills the specified range in the array with the specified element.
     *
     * @param array the {@code double} array to fill.
     * @param start the first index to fill.
     * @param end   the last + 1 index to fill.
     * @param value the {@code double} element.
     * @throws IllegalArgumentException       if {@code start > end}.
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0} or {@code end > array.length}.
     */
    public static void fill(double[] array, int start, int end, double value) {
        Arrays.fill(array, start, end, value);

    }

    /**
     * The number of bytes required to represent a primitive {@code double}
     * value.
     *
     * @since 10.0
     */
    public static final int BYTES = Double.SIZE / Byte.SIZE;

    /**
     * Returns a hash code for {@code value}; equal to the result of invoking
     * {@code ((Double) value).hashCode()}.
     *
     * @param value a primitive {@code double} value
     * @return a hash code for the value
     */
    public static int hashCode(double value) {
        return ((Double) value).hashCode();
        // TODO(kevinb): do it this way when we can (GWT problem):
        // long bits = Double.doubleToLongBits(value);
        // return (int) (bits ^ (bits >>> 32));
    }

    /**
     * Returns a hash code based on the contents of the given array. For any two
     * {@code double} arrays {@code a} and {@code b}, if
     * {@code Arrays.equals(a, b)} returns {@code true}, it means
     * that the return value of {@code Arrays.hashCode(a)} equals {@code Arrays.hashCode(b)}.
     * <p>
     * The value returned by this method is the same value as the
     * {@link List#hashCode()} method which is invoked on a {@link List}
     * containing a sequence of {@link Double} instances representing the
     * elements of array in the same order. If the array is {@code null}, the return
     * value is 0.
     *
     * @param array the array whose hash code to compute.
     * @return the hash code for {@code array}.
     */
    public static int hashCode(double[] array) {
        return Arrays.hashCode(array);
    }

    /**
     * Compares the two arrays. The values are compared in the same manner as
     * {@code Double.equals()}.
     *
     * @param array1 the first {@code double} array.
     * @param array2 the second {@code double} array.
     * @return {@code true} if both arrays are {@code null} or if the arrays have the
     * same length and the elements at each index in the two arrays are
     * equal, {@code false} otherwise.
     * @see Double#equals(Object)
     */
    public static boolean equals(double[] array1, double[] array2) {
        return Arrays.equals(array1, array2);
    }

    /**
     * Sorts the specified array in ascending numerical order.
     *
     * @param array the {@code double} array to be sorted.
     * @see #sort(double[], int, int)
     */
    public static void sort(double[] array) {
        Arrays.sort(array);
    }

    /**
     * Sorts the specified range in the array in ascending numerical order. The
     * values are sorted according to the order imposed by {@code Double.compareTo()}.
     *
     * @param array the {@code double} array to be sorted.
     * @param start the start index to sort.
     * @param end   the last + 1 index to sort.
     * @throws IllegalArgumentException       if {@code start > end}.
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0} or {@code end > array.length}.
     * @see Double#compareTo(Double)
     */
    public static void sort(double[] array, int start, int end) {
        Arrays.sort(array, start, end);
    }

    /**
     * Creates a {@code String} representation of the {@code double[]} passed.
     * The result is surrounded by brackets ({@code "[]"}), each
     * element is converted to a {@code String} via the
     * {@link String#valueOf(double)} and separated by {@code ", "}.
     * If the array is {@code null}, then {@code "null"} is returned.
     *
     * @param array the {@code double} array to convert.
     * @return the {@code String} representation of {@code array}.
     * @since 1.5
     */
    public static String toString(double[] array) {
        return Arrays.toString(array);
    }

    /**
     * Copies {@code newLength} elements from {@code original} into a new array.
     * If {@code newLength} is greater than {@code original.length}, the result is padded
     * with the value {@code 0.0d}.
     *
     * @param original  the original array
     * @param newLength the length of the new array
     * @return the new array
     * @throws NegativeArraySizeException if {@code newLength < 0}
     * @throws NullPointerException       if {@code original == null}
     * @since 1.6
     */
    public static double[] copyOf(double[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    /**
     * Copies elements from {@code original} into a new array, from indexes start (inclusive) to
     * end (exclusive). The original order of elements is preserved.
     * If {@code end} is greater than {@code original.length}, the result is padded
     * with the value {@code 0.0d}.
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
    public static double[] copyOfRange(double[] original, int start, int end) {
        return Arrays.copyOfRange(original, start, end);
    }

    /**
     * Compares the two specified {@code double} values. The sign of the value
     * returned is the same as that of <code>((Double) a).{@linkplain
     * Double#compareTo compareTo}(b)</code>. As with that method, {@code NaN} is
     * treated as greater than all other values, and {@code 0.0 > -0.0}.
     *
     * <p><b>Note:</b> this method simply delegates to the JDK method {@link
     * Double#compare}. It is provided for consistency with the other primitive
     * types, whose compare methods were not added to the JDK until JDK 7.
     *
     * @param a the first {@code double} to compare
     * @param b the second {@code double} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive
     * value if {@code a} is greater than {@code b}; or zero if they are equal
     */
    public static int compare(double a, double b) {
        return Double.compare(a, b);
    }

    /**
     * Returns {@code true} if {@code value} represents a real number. This is
     * equivalent to, but not necessarily implemented as,
     * {@code !(Double.isInfinite(value) || Double.isNaN(value))}.
     *
     * @since 10.0
     */
    public static boolean isFinite(double value) {
        return NEGATIVE_INFINITY < value & value < POSITIVE_INFINITY;
    }

    /**
     * Returns {@code true} if {@code target} is present as an element anywhere in
     * {@code array}. Note that this always returns {@code false} when {@code
     * target} is {@code NaN}.
     *
     * @param array  an array of {@code double} values, possibly empty
     * @param target a primitive {@code double} value
     * @return {@code true} if {@code array[i] == target} for some value of {@code
     * i}
     */
    public static boolean contains(double[] array, double target) {
        for (double value : array) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index of the first appearance of the value {@code target} in
     * {@code array}. Note that this always returns {@code -1} when {@code target}
     * is {@code NaN}.
     *
     * @param array  an array of {@code double} values, possibly empty
     * @param target a primitive {@code double} value
     * @return the least index {@code i} for which {@code array[i] == target}, or
     * {@code -1} if no such index exists.
     */
    public static int indexOf(double[] array, double target) {
        return indexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int indexOf(
            double[] array, double target, int start, int end) {
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
     * <p>Note that this always returns {@code -1} when {@code target} contains
     * {@code NaN}.
     *
     * @param array  the array to search for the sequence {@code target}
     * @param target the array to search for as a sub-sequence of {@code array}
     */
    public static int indexOf(double[] array, double[] target) {
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
     * {@code array}. Note that this always returns {@code -1} when {@code target}
     * is {@code NaN}.
     *
     * @param array  an array of {@code double} values, possibly empty
     * @param target a primitive {@code double} value
     * @return the greatest index {@code i} for which {@code array[i] == target},
     * or {@code -1} if no such index exists.
     */
    public static int lastIndexOf(double[] array, double target) {
        return lastIndexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int lastIndexOf(
            double[] array, double target, int start, int end) {
        for (int i = end - 1; i >= start; i--) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the least value present in {@code array}, using the same rules of
     * comparison as {@link Math#min(double, double)}.
     *
     * @param array a <i>nonempty</i> array of {@code double} values
     * @return the value present in {@code array} that is less than or equal to
     * every other value in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static double min(double... array) {
        Preconditions.checkArgument(array.length > 0);
        double min = array[0];
        for (int i = 1; i < array.length; i++) {
            min = Math.min(min, array[i]);
        }
        return min;
    }

    /**
     * Returns the greatest value present in {@code array}, using the same rules
     * of comparison as {@link Math#max(double, double)}.
     *
     * @param array a <i>nonempty</i> array of {@code double} values
     * @return the value present in {@code array} that is greater than or equal to
     * every other value in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static double max(double... array) {
        Preconditions.checkArgument(array.length > 0);
        double max = array[0];
        for (int i = 1; i < array.length; i++) {
            max = Math.max(max, array[i]);
        }
        return max;
    }

    /**
     * Returns the values from each provided array combined into a single array.
     * For example, {@code concat(new double[] {a, b}, new double[] {}, new
     * double[] {c}} returns the array {@code {a, b, c}}.
     *
     * @param arrays zero or more {@code double} arrays
     * @return a single array containing all the values from the source arrays, in
     * order
     */
    public static double[] concat(double[]... arrays) {
        int length = 0;
        for (double[] array : arrays) {
            length += array.length;
        }
        double[] result = new double[length];
        int pos = 0;
        for (double[] array : arrays) {
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
    public static double[] ensureCapacity(double[] array, int minLength, int padding) {
        Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", minLength);
        Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", padding);
        return (array.length < minLength)
                ? copyOf(array, minLength + padding)
                : array;
    }

    /**
     * Returns a string containing the supplied {@code double} values, converted
     * to strings as specified by {@link Double#toString(double)}, and separated
     * by {@code separator}. For example, {@code join("-", 1.0, 2.0, 3.0)} returns
     * the string {@code "1.0-2.0-3.0"}.
     *
     * <p>Note that {@link Double#toString(double)} formats {@code double}
     * differently in GWT sometimes.  In the previous example, it returns the
     * string {@code "1-2-3"}.
     *
     * @param separator the text that should appear between consecutive values in
     *                  the resulting string (but not at the start or end)
     * @param array     an array of {@code double} values, possibly empty
     */
    public static String join(String separator, double... array) {
        Preconditions.checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 12);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    /**
     * Returns a comparator that compares two {@code double} arrays
     * lexicographically. That is, it compares, using {@link
     * #compare(double, double)}), the first pair of values that follow any
     * common prefix, or when one array is a prefix of the other, treats the
     * shorter array as the lesser. For example,
     * {@code [] < [1.0] < [1.0, 2.0] < [2.0]}.
     *
     * <p>The returned comparator is inconsistent with {@link
     * Object#equals(Object)} (since arrays support only identity equality), but
     * it is consistent with {@link Arrays#equals(double[], double[])}.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Lexicographical_order">
     * Lexicographical order article at Wikipedia</a>
     * @since 2.0
     */
    public static Comparator<double[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    private enum LexicographicalComparator implements Comparator<double[]> {
        INSTANCE;

        @Override
        public int compare(double[] left, double[] right) {
            int minLength = Math.min(left.length, right.length);
            for (int i = 0; i < minLength; i++) {
                int result = Double.compare(left[i], right[i]);
                if (result != 0) {
                    return result;
                }
            }
            return left.length - right.length;
        }
    }

    /**
     * Returns an array containing each value of {@code collection}, converted to
     * a {@code double} value in the manner of {@link Number#doubleValue}.
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
     * @since 1.0 (parameter was {@code Collection<Double>} before 12.0)
     */
    public static double[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof DoubleArrayAsList) {
            return ((DoubleArrayAsList) collection).toDoubleArray();
        }

        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        double[] array = new double[len];
        for (int i = 0; i < len; i++) {
            // checkNotNull for GWT (do not optimize)
            array[i] = ((Number) Preconditions.checkNotNull(boxedArray[i])).doubleValue();
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
     * {@code Double} objects written to or read from it.  For example, whether
     * {@code list.get(0) == list.get(0)} is true for the returned list is
     * unspecified.
     *
     * <p>The returned list may have unexpected behavior if it contains {@code
     * NaN}, or if {@code NaN} is used as a parameter to any of its methods.
     *
     * @param backingArray the array to back the list
     * @return a list view of the array
     */
    public static List<Double> asList(double... backingArray) {
        if (backingArray.length == 0) {
            return Collections.emptyList();
        }
        return new DoubleArrayAsList(backingArray);
    }

    private static class DoubleArrayAsList extends AbstractList<Double>
            implements RandomAccess, Serializable {
        final double[] array;
        final int start;
        final int end;

        DoubleArrayAsList(double[] array) {
            this(array, 0, array.length);
        }

        DoubleArrayAsList(double[] array, int start, int end) {
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
        public Double get(int index) {
            Preconditions.checkElementIndex(index, size());
            return array[start + index];
        }

        @Override
        public boolean contains(Object target) {
            // Overridden to prevent a ton of boxing
            return (target instanceof Double)
                    && Doubles.indexOf(array, (Double) target, start, end) != -1;
        }

        @Override
        public int indexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Double) {
                int i = Doubles.indexOf(array, (Double) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Double) {
                int i = Doubles.lastIndexOf(array, (Double) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public Double set(int index, Double element) {
            Preconditions.checkElementIndex(index, size());
            double oldValue = array[start + index];
            // checkNotNull for GWT (do not optimize)
            array[start + index] = Preconditions.checkNotNull(element);
            return oldValue;
        }

        @Override
        public List<Double> subList(int fromIndex, int toIndex) {
            int size = size();
            Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
            if (fromIndex == toIndex) {
                return Collections.emptyList();
            }
            return new DoubleArrayAsList(array, start + fromIndex, start + toIndex);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof DoubleArrayAsList) {
                DoubleArrayAsList that = (DoubleArrayAsList) object;
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
                result = 31 * result + Doubles.hashCode(array[i]);
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(size() * 12);
            builder.append('[').append(array[start]);
            for (int i = start + 1; i < end; i++) {
                builder.append(", ").append(array[i]);
            }
            return builder.append(']').toString();
        }

        double[] toDoubleArray() {
            // Arrays.copyOfRange() is not available under GWT
            int size = size();
            double[] result = new double[size];
            System.arraycopy(array, start, result, 0, size);
            return result;
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * This is adapted from the regex suggested by {@link Double#valueOf(String)}
     * for prevalidating inputs.  All valid inputs must pass this regex, but it's
     * semantically fine if not all inputs that pass this regex are valid --
     * only a performance hit is incurred, not a semantics bug.
     */
    static final Pattern FLOATING_POINT_PATTERN = fpPattern();

    private static Pattern fpPattern() {
        String decimal = "(?:\\d++(?:\\.\\d*+)?|\\.\\d++)";
        String completeDec = decimal + "(?:[eE][+-]?\\d++)?[fFdD]?";
        String hex = "(?:\\p{XDigit}++(?:\\.\\p{XDigit}*+)?|\\.\\p{XDigit}++)";
        String completeHex = "0[xX]" + hex + "[pP][+-]?\\d++[fFdD]?";
        String fpPattern = "[+-]?(?:NaN|Infinity|" + completeDec + "|" + completeHex + ")";
        return Pattern.compile(fpPattern);
    }

    /**
     * Parses the specified string as a double-precision floating point value.
     * The ASCII character {@code '-'} (<code>'&#92;u002D'</code>) is recognized
     * as the minus sign.
     *
     * <p>Unlike {@link Double#parseDouble(String)}, this method returns
     * {@code null} instead of throwing an exception if parsing fails.
     * Valid inputs are exactly those accepted by {@link Double#valueOf(String)},
     * except that leading and trailing whitespace is not permitted.
     *
     * <p>This implementation is likely to be faster than {@code
     * Double.parseDouble} if many failures are expected.
     *
     * @param string the string representation of a {@code double} value
     * @return the floating point value represented by {@code string}, or
     * {@code null} if {@code string} has a length of zero or cannot be
     * parsed as a {@code double} value
     * @since 14.0
     */
    @Nullable
    public static Double tryParse(String string) {
        if (FLOATING_POINT_PATTERN.matcher(string).matches()) {
            // TODO(user): could be potentially optimized, but only with
            // extensive testing
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                // Double.parseDouble has changed specs several times, so fall through
                // gracefully
            }
        }
        return null;
    }

    // MATH

    /**
     * This method returns a value y such that rounding y DOWN (towards zero) gives the same result
     * as rounding x according to the specified mode.
     */
    static double roundIntermediate(double x, RoundingMode mode) {
        if (!isFinite(x)) {
            throw new ArithmeticException("input is infinite or NaN");
        }
        switch (mode) {
            case UNNECESSARY:
                Preconditions.Math.checkRoundingUnnecessary(isMathematicalInteger(x));
                return x;

            case FLOOR:
                if (x >= 0.0 || isMathematicalInteger(x)) {
                    return x;
                } else {
                    return x - 1.0;
                }

            case CEILING:
                if (x <= 0.0 || isMathematicalInteger(x)) {
                    return x;
                } else {
                    return x + 1.0;
                }

            case DOWN:
                return x;

            case UP:
                if (isMathematicalInteger(x)) {
                    return x;
                } else {
                    return x + Math.copySign(1.0, x);
                }

            case HALF_EVEN:
                return rint(x);

            case HALF_UP: {
                double z = rint(x);
                if (abs(x - z) == 0.5) {
                    return x + copySign(0.5, x);
                } else {
                    return z;
                }
            }

            case HALF_DOWN: {
                double z = rint(x);
                if (abs(x - z) == 0.5) {
                    return x;
                } else {
                    return z;
                }
            }

            default:
                throw new AssertionError();
        }
    }

    /**
     * Returns the {@code int} value that is equal to {@code x} rounded with the specified rounding
     * mode, if possible.
     *
     * @throws ArithmeticException if
     *                             <ul>
     *                             <li>{@code x} is infinite or NaN
     *                             <li>{@code x}, after being rounded to a mathematical integer using the specified
     *                             rounding mode, is either less than {@code Integer.MIN_VALUE} or greater than {@code
     *                             Integer.MAX_VALUE}
     *                             <li>{@code x} is not a mathematical integer and {@code mode} is
     *                             {@link RoundingMode#UNNECESSARY}
     *                             </ul>
     */
    public static int roundToInt(double x, RoundingMode mode) {
        double z = roundIntermediate(x, mode);
        Preconditions.Math.checkInRange(z > MIN_INT_AS_DOUBLE - 1.0 & z < MAX_INT_AS_DOUBLE + 1.0);
        return (int) z;
    }

    private static final double MIN_INT_AS_DOUBLE = -0x1p31;
    private static final double MAX_INT_AS_DOUBLE = 0x1p31 - 1.0;

    /**
     * Returns the {@code long} value that is equal to {@code x} rounded with the specified rounding
     * mode, if possible.
     *
     * @throws ArithmeticException if
     *                             <ul>
     *                             <li>{@code x} is infinite or NaN
     *                             <li>{@code x}, after being rounded to a mathematical integer using the specified
     *                             rounding mode, is either less than {@code Long.MIN_VALUE} or greater than {@code
     *                             Long.MAX_VALUE}
     *                             <li>{@code x} is not a mathematical integer and {@code mode} is
     *                             {@link RoundingMode#UNNECESSARY}
     *                             </ul>
     */
    public static long roundToLong(double x, RoundingMode mode) {
        double z = roundIntermediate(x, mode);
        Preconditions.Math.checkInRange(MIN_LONG_AS_DOUBLE - z < 1.0 & z < MAX_LONG_AS_DOUBLE_PLUS_ONE);
        return (long) z;
    }

    private static final double MIN_LONG_AS_DOUBLE = -0x1p63;
    /*
     * We cannot store Long.MAX_VALUE as a double without losing precision.  Instead, we store
     * Long.MAX_VALUE + 1 == -Long.MIN_VALUE, and then offset all comparisons by 1.
     */
    private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 0x1p63;

    /**
     * Returns the {@code BigInteger} value that is equal to {@code x} rounded with the specified
     * rounding mode, if possible.
     *
     * @throws ArithmeticException if
     *                             <ul>
     *                             <li>{@code x} is infinite or NaN
     *                             <li>{@code x} is not a mathematical integer and {@code mode} is
     *                             {@link RoundingMode#UNNECESSARY}
     *                             </ul>
     */
    public static BigInteger roundToBigInteger(double x, RoundingMode mode) {
        x = roundIntermediate(x, mode);
        if (MIN_LONG_AS_DOUBLE - x < 1.0 & x < MAX_LONG_AS_DOUBLE_PLUS_ONE) {
            return BigInteger.valueOf((long) x);
        }
        int exponent = getExponent(x);
        long significand = DoubleUtils.getSignificand(x);
        BigInteger result = BigInteger.valueOf(significand).shiftLeft(exponent - DoubleUtils.SIGNIFICAND_BITS);
        return (x < 0) ? result.negate() : result;
    }

    /**
     * Returns {@code true} if {@code x} is exactly equal to {@code 2^k} for some finite integer
     * {@code k}.
     */
    public static boolean isPowerOfTwo(double x) {
        return x > 0.0 && isFinite(x) && Longs.isPowerOfTwo(DoubleUtils.getSignificand(x));
    }

    /**
     * Returns the base 2 logarithm of a double value.
     *
     * <p>Special cases:
     * <ul>
     * <li>If {@code x} is NaN or less than zero, the result is NaN.
     * <li>If {@code x} is positive infinity, the result is positive infinity.
     * <li>If {@code x} is positive or negative zero, the result is negative infinity.
     * </ul>
     *
     * <p>The computed result is within 1 ulp of the exact result.
     *
     * <p>If the result of this method will be immediately rounded to an {@code int},
     * {@link #log2(double, RoundingMode)} is faster.
     */
    public static double log2(double x) {
        return log(x) / LN_2; // surprisingly within 1 ulp according to tests
    }

    private static final double LN_2 = log(2);

    /**
     * Returns the base 2 logarithm of a double value, rounded with the specified rounding mode to an
     * {@code int}.
     *
     * <p>Regardless of the rounding mode, this is faster than {@code (int) log2(x)}.
     *
     * @throws IllegalArgumentException if {@code x <= 0.0}, {@code x} is NaN, or {@code x} is
     *                                  infinite
     */
    @SuppressWarnings("fallthrough")
    public static int log2(double x, RoundingMode mode) {
        Preconditions.checkArgument(x > 0.0 && isFinite(x), "x must be positive and finite");
        int exponent = getExponent(x);
        if (!DoubleUtils.isNormal(x)) {
            return log2(x * DoubleUtils.IMPLICIT_BIT, mode) - DoubleUtils.SIGNIFICAND_BITS;
            // Do the calculation on a normal value.
        }
        // x is positive, finite, and normal
        boolean increment;
        switch (mode) {
            case UNNECESSARY:
                Preconditions.Math.checkRoundingUnnecessary(isPowerOfTwo(x));
                // fall through
            case FLOOR:
                increment = false;
                break;
            case CEILING:
                increment = !isPowerOfTwo(x);
                break;
            case DOWN:
                increment = exponent < 0 & !isPowerOfTwo(x);
                break;
            case UP:
                increment = exponent >= 0 & !isPowerOfTwo(x);
                break;
            case HALF_DOWN:
            case HALF_EVEN:
            case HALF_UP:
                double xScaled = DoubleUtils.scaleNormalize(x);
                // sqrt(2) is irrational, and the spec is relative to the "exact numerical result,"
                // so log2(x) is never exactly exponent + 0.5.
                increment = (xScaled * xScaled) > 2.0;
                break;
            default:
                throw new AssertionError();
        }
        return increment ? exponent + 1 : exponent;
    }

    /**
     * Returns {@code true} if {@code x} represents a mathematical integer.
     *
     * <p>This is equivalent to, but not necessarily implemented as, the expression {@code
     * !Double.isNaN(x) && !Double.isInfinite(x) && x == Math.rint(x)}.
     */
    public static boolean isMathematicalInteger(double x) {
        return isFinite(x)
                && (x == 0.0 || DoubleUtils.SIGNIFICAND_BITS - Long.numberOfTrailingZeros(DoubleUtils.getSignificand(x)) <= getExponent(x));
    }

    /**
     * Returns {@code n!}, that is, the product of the first {@code n} positive
     * integers, {@code 1} if {@code n == 0}, or {@code n!}, or
     * {@link Double#POSITIVE_INFINITY} if {@code n! > Double.MAX_VALUE}.
     *
     * <p>The result is within 1 ulp of the true value.
     *
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public static double factorial(int n) {
        Preconditions.Math.checkNonNegative("n", n);
        if (n > MAX_FACTORIAL) {
            return Double.POSITIVE_INFINITY;
        } else {
            // Multiplying the last (n & 0xf) values into their own accumulator gives a more accurate
            // result than multiplying by everySixteenthFactorial[n >> 4] directly.
            double accum = 1.0;
            for (int i = 1 + (n & ~0xf); i <= n; i++) {
                accum *= i;
            }
            return accum * everySixteenthFactorial[n >> 4];
        }
    }

    static final int MAX_FACTORIAL = 170;

    static final double[] everySixteenthFactorial = {
            0x1.0p0,
            0x1.30777758p44,
            0x1.956ad0aae33a4p117,
            0x1.ee69a78d72cb6p202,
            0x1.fe478ee34844ap295,
            0x1.c619094edabffp394,
            0x1.3638dd7bd6347p498,
            0x1.7cac197cfe503p605,
            0x1.1e5dfc140e1e5p716,
            0x1.8ce85fadb707ep829,
            0x1.95d5f3d928edep945};

    /**
     * Returns {@code true} if {@code a} and {@code b} are within {@code tolerance} of each other.
     *
     * <p>Technically speaking, this is equivalent to
     * {@code Math.abs(a - b) <= tolerance || Double.valueOf(a).equals(Double.valueOf(b))}.
     *
     * <p>Notable special cases include:
     * <ul>
     * <li>All NaNs are fuzzily equal.
     * <li>If {@code a == b}, then {@code a} and {@code b} are always fuzzily equal.
     * <li>Positive and negative zero are always fuzzily equal.
     * <li>If {@code tolerance} is zero, and neither {@code a} nor {@code b} is NaN, then
     * {@code a} and {@code b} are fuzzily equal if and only if {@code a == b}.
     * <li>With {@link Double#POSITIVE_INFINITY} tolerance, all non-NaN values are fuzzily equal.
     * <li>With finite tolerance, {@code Double.POSITIVE_INFINITY} and {@code
     * Double.NEGATIVE_INFINITY} are fuzzily equal only to themselves.
     * </li>
     *
     * <p>This is reflexive and symmetric, but <em>not</em> transitive, so it is <em>not</em> an
     * equivalence relation and <em>not</em> suitable for use in {@link Object#equals}
     * implementations.
     *
     * @throws IllegalArgumentException if {@code tolerance} is {@code < 0} or NaN
     * @since 13.0
     */
    public static boolean fuzzyEquals(double a, double b, double tolerance) {
        Preconditions.Math.checkNonNegative("tolerance", tolerance);
        return Math.copySign(a - b, 1.0) <= tolerance
                // copySign(x, 1.0) is a branch-free version of abs(x), but with different NaN semantics
                || (a == b) // needed to ensure that infinities equal themselves
                || (Double.isNaN(a) && Double.isNaN(b));
    }

    /**
     * Compares {@code a} and {@code b} "fuzzily," with a tolerance for nearly-equal values.
     *
     * <p>This method is equivalent to
     * {@code fuzzyEquals(a, b, tolerance) ? 0 : Double.compare(a, b)}. In particular, like
     * {@link Double#compare(double, double)}, it treats all NaN values as equal and greater than all
     * other values (including {@link Double#POSITIVE_INFINITY}).
     *
     * <p>This is <em>not</em> a total ordering and is <em>not</em> suitable for use in
     * {@link Comparable#compareTo} implementations.  In particular, it is not transitive.
     *
     * @throws IllegalArgumentException if {@code tolerance} is {@code < 0} or NaN
     * @since 13.0
     */
    public static int fuzzyCompare(double a, double b, double tolerance) {
        if (fuzzyEquals(a, b, tolerance)) {
            return 0;
        } else if (a < b) {
            return -1;
        } else if (a > b) {
            return 1;
        } else {
            return Booleans.compare(Double.isNaN(a), Double.isNaN(b));
        }
    }

    private static final class MeanAccumulator {
        private long count = 0;
        private double mean = 0.0;

        void add(double value) {
            Preconditions.checkArgument(isFinite(value));
            ++count;
            // Art of Computer Programming vol. 2, Knuth, 4.2.2, (15)
            mean += (value - mean) / count;
        }

        double mean() {
            Preconditions.checkArgument(count > 0, "Cannot take mean of 0 values");
            return mean;
        }
    }

    /**
     * Returns the arithmetic mean of the values. There must be at least one value, and they must all
     * be finite.
     */
    public static double mean(double... values) {
        MeanAccumulator accumulator = new MeanAccumulator();
        for (double value : values) {
            accumulator.add(value);
        }
        return accumulator.mean();
    }

    /**
     * Returns the arithmetic mean of the values. There must be at least one value. The values will
     * be converted to doubles, which does not cause any loss of precision for ints.
     */
    public static double mean(int... values) {
        MeanAccumulator accumulator = new MeanAccumulator();
        for (int value : values) {
            accumulator.add(value);
        }
        return accumulator.mean();
    }

    /**
     * Returns the arithmetic mean of the values. There must be at least one value. The values will
     * be converted to doubles, which causes loss of precision for longs of magnitude over 2^53
     * (slightly over 9e15).
     */
    public static double mean(long... values) {
        MeanAccumulator accumulator = new MeanAccumulator();
        for (long value : values) {
            accumulator.add(value);
        }
        return accumulator.mean();
    }

    /**
     * Returns the arithmetic mean of the values. There must be at least one value, and they must all
     * be finite. The values will be converted to doubles, which may cause loss of precision for some
     * numeric types.
     */
    public static double mean(Iterable<? extends Number> values) {
        MeanAccumulator accumulator = new MeanAccumulator();
        for (Number value : values) {
            accumulator.add(value.doubleValue());
        }
        return accumulator.mean();
    }

    /**
     * Returns the arithmetic mean of the values. There must be at least one value, and they must all
     * be finite. The values will be converted to doubles, which may cause loss of precision for some
     * numeric types.
     */
    public static double mean(Iterator<? extends Number> values) {
        MeanAccumulator accumulator = new MeanAccumulator();
        while (values.hasNext()) {
            accumulator.add(values.next().doubleValue());
        }
        return accumulator.mean();
    }
}

final class DoubleUtils {
    private DoubleUtils() {
    }

    static double nextDown(double d) {
        return -Math.nextUp(-d);
    }

    // The mask for the significand, according to the {@link
    // Double#doubleToRawLongBits(double)} spec.
    static final long SIGNIFICAND_MASK = 0x000fffffffffffffL;

    // The mask for the exponent, according to the {@link
    // Double#doubleToRawLongBits(double)} spec.
    static final long EXPONENT_MASK = 0x7ff0000000000000L;

    // The mask for the sign, according to the {@link
    // Double#doubleToRawLongBits(double)} spec.
    static final long SIGN_MASK = 0x8000000000000000L;

    static final int SIGNIFICAND_BITS = 52;

    static final int EXPONENT_BIAS = 1023;

    /**
     * The implicit 1 bit that is omitted in significands of normal doubles.
     */
    static final long IMPLICIT_BIT = SIGNIFICAND_MASK + 1;

    static long getSignificand(double d) {
        Preconditions.checkArgument(isFinite(d), "not a normal value");
        int exponent = getExponent(d);
        long bits = doubleToRawLongBits(d);
        bits &= SIGNIFICAND_MASK;
        return (exponent == MIN_EXPONENT - 1)
                ? bits << 1
                : bits | IMPLICIT_BIT;
    }

    static boolean isFinite(double d) {
        return getExponent(d) <= MAX_EXPONENT;
    }

    static boolean isNormal(double d) {
        return getExponent(d) >= MIN_EXPONENT;
    }

    /*
     * Returns x scaled by a power of 2 such that it is in the range [1, 2). Assumes x is positive,
     * normal, and finite.
     */
    static double scaleNormalize(double x) {
        long significand = doubleToRawLongBits(x) & SIGNIFICAND_MASK;
        return longBitsToDouble(significand | ONE_BITS);
    }

    static double bigToDouble(BigInteger x) {
        // This is an extremely fast implementation of BigInteger.doubleValue().  JDK patch pending.
        BigInteger absX = x.abs();
        int exponent = absX.bitLength() - 1;
        // exponent == floor(log2(abs(x)))
        if (exponent < Long.SIZE - 1) {
            return x.longValue();
        } else if (exponent > MAX_EXPONENT) {
            return x.signum() * POSITIVE_INFINITY;
        }

    /*
     * We need the top SIGNIFICAND_BITS + 1 bits, including the "implicit" one bit. To make
     * rounding easier, we pick out the top SIGNIFICAND_BITS + 2 bits, so we have one to help us
     * round up or down. twiceSignifFloor will contain the top SIGNIFICAND_BITS + 2 bits, and
     * signifFloor the top SIGNIFICAND_BITS + 1.
     *
     * It helps to consider the real number signif = absX * 2^(SIGNIFICAND_BITS - exponent).
     */
        int shift = exponent - SIGNIFICAND_BITS - 1;
        long twiceSignifFloor = absX.shiftRight(shift).longValue();
        long signifFloor = twiceSignifFloor >> 1;
        signifFloor &= SIGNIFICAND_MASK; // remove the implied bit

    /*
     * We round up if either the fractional part of signif is strictly greater than 0.5 (which is
     * true if the 0.5 bit is set and any lower bit is set), or if the fractional part of signif is
     * >= 0.5 and signifFloor is odd (which is true if both the 0.5 bit and the 1 bit are set).
     */
        boolean increment = (twiceSignifFloor & 1) != 0
                && ((signifFloor & 1) != 0 || absX.getLowestSetBit() < shift);
        long signifRounded = increment ? signifFloor + 1 : signifFloor;
        long bits = (long) ((exponent + EXPONENT_BIAS)) << SIGNIFICAND_BITS;
        bits += signifRounded;
    /*
     * If signifRounded == 2^53, we'd need to set all of the significand bits to zero and add 1 to
     * the exponent. This is exactly the behavior we get from just adding signifRounded to bits
     * directly.  If the exponent is MAX_DOUBLE_EXPONENT, we round up (correctly) to
     * Double.POSITIVE_INFINITY.
     */
        bits |= x.signum() & SIGN_MASK;
        return longBitsToDouble(bits);
    }

    /**
     * Returns its argument if it is non-negative, zero if it is negative.
     */
    static double ensureNonNegative(double value) {
        Preconditions.checkArgument(!isNaN(value));
        if (value > 0.0) {
            return value;
        } else {
            return 0.0;
        }
    }

    private static final long ONE_BITS = doubleToRawLongBits(1.0);
}
