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

public final class RationalNumber implements Comparable<RationalNumber> {

    public static RationalNumber of(int numerator, int denominator) {
        if (denominator == 0) {
            return null;
        }
        return new RationalNumber(numerator, denominator);
    }

    private final int mNumerator;
    private final int mDenominator;
    private final int mGcd;

    private RationalNumber(int numerator, int denominator) {
        mNumerator = numerator;
        mDenominator = denominator;
        mGcd = gcd(numerator, denominator);
    }

    public int getNumerator() {
        return mNumerator;
    }

    public int getDenominator() {
        return mDenominator;
    }

    public int getScaledNumberator() {
        return mNumerator / mGcd;
    }

    public int getScaledDenominator() {
        return mDenominator / mGcd;
    }

    public int getGcd() {
        return mGcd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RationalNumber)) {
            return false;
        }
        final RationalNumber other = (RationalNumber) o;
        return getScaledNumberator() == other.getScaledNumberator()
                && getScaledDenominator() == other.getScaledDenominator();
    }

    @Override
    public String toString() {
        return "{" + mNumerator + ", " + mDenominator + ", " + mGcd + "}";
    }

    @Override
    public int hashCode() {
        return 37 * mNumerator + mDenominator;
    }

    @Override
    public int compareTo(RationalNumber o) {
        if (this == o) {
            return 0;
        }
        return getScaledNumberator() * o.getScaledDenominator()
                - o.getScaledNumberator() * getScaledDenominator();
    }

    private static int gcd(int a, int b) {
        if (a < 0) {
            a = -a;
        }
        if (b < 0) {
            b = -b;
        }
        while (a != 0 && b != 0) {
            if (a > b) {
                a %= b;
            } else {
                b %= a;
            }
        }
        return a + b;
    }
}
