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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Static utility methods pertaining to {@link Queue} and {@link Deque} instances.
 * Also see this class's counterparts {@link Lists}, {@link Sets}, and {@link Maps}.
 */
public class Queues extends Collections {
    private Queues() {
    }

    // CUSTOM
    public static final class Builder<E> {
        private final Queue<E> queue;

        public Builder(Queue<E> queue) {
            this.queue = queue;
        }

        /**
         * Add this {@code element} to the {@code queue}.
         */
        public Builder add(E element) {
            queue.add(element);
            return this;
        }

        /**
         * Adds each element of {@code elements} to the {@code Queue}.
         */
        public Builder<E> addAll(Iterable<? extends E> elements) {
            Lists.addAll(queue, elements);
            return this;
        }

        /**
         * Adds each element of {@code elements} to the {@code Queue}.
         */
        public Builder<E> add(E... elements) {
            Lists.addAll(queue, elements);
            return this;
        }

        /**
         * Adds each element of {@code elements} to the {@code List}.
         */
        public Builder<E> addAll(Iterator<? extends E> elements) {
            Lists.addAll(queue, elements);
            return this;
        }

        /**
         * Returns a newly-created {@code Queue} based on the contents of the {@code Builder}.
         */
        public Queue<E> build() {
            return queue;
        }
    }
    // GUAVA
    // ArrayBlockingQueue

    /**
     * Creates an empty {@code ArrayBlockingQueue} with the given (fixed) capacity
     * and nonfair access policy.
     */
    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity) {
        return new ArrayBlockingQueue<>(capacity);
    }

    // ArrayDeque

    /**
     * Creates an empty {@code ArrayDeque}.
     *
     * @since 12.0
     */
    public static <E> ArrayDeque<E> newArrayDeque() {
        return new ArrayDeque<>();
    }

    /**
     * Creates an {@code ArrayDeque} containing the elements of the specified iterable,
     * in the order they are returned by the iterable's iterator.
     *
     * @since 12.0
     */
    public static <E> ArrayDeque<E> newArrayDeque(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new ArrayDeque<>(cast(elements));
        }
        ArrayDeque<E> deque = newArrayDeque();
        addAll(deque, elements);
        return deque;
    }

    // ConcurrentLinkedQueue

    /**
     * Creates an empty {@code ConcurrentLinkedQueue}.
     */
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<>();
    }

    /**
     * Creates a {@code ConcurrentLinkedQueue} containing the elements of the specified iterable,
     * in the order they are returned by the iterable's iterator.
     */
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(
            Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new ConcurrentLinkedQueue<>(cast(elements));
        }
        ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<>();
        addAll(queue, elements);
        return queue;
    }

    // LinkedBlockingDeque

    /**
     * Creates an empty {@code LinkedBlockingDeque} with a capacity of {@link Integer#MAX_VALUE}.
     *
     * @since 12.0
     */
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque() {
        return new LinkedBlockingDeque<>();
    }

    /**
     * Creates an empty {@code LinkedBlockingDeque} with the given (fixed) capacity.
     *
     * @throws IllegalArgumentException if {@code capacity} is less than 1
     * @since 12.0
     */
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity) {
        return new LinkedBlockingDeque<>(capacity);
    }

    /**
     * Creates a {@code LinkedBlockingDeque} with a capacity of {@link Integer#MAX_VALUE},
     * containing the elements of the specified iterable,
     * in the order they are returned by the iterable's iterator.
     *
     * @since 12.0
     */
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new LinkedBlockingDeque<>(cast(elements));
        }
        LinkedBlockingDeque<E> deque = new LinkedBlockingDeque<>();
        addAll(deque, elements);
        return deque;
    }

    // LinkedBlockingQueue

    /**
     * Creates an empty {@code LinkedBlockingQueue} with a capacity of {@link Integer#MAX_VALUE}.
     */
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue() {
        return new LinkedBlockingQueue<>();
    }

    /**
     * Creates an empty {@code LinkedBlockingQueue} with the given (fixed) capacity.
     *
     * @throws IllegalArgumentException if {@code capacity} is less than 1
     */
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(int capacity) {
        return new LinkedBlockingQueue<>(capacity);
    }

    /**
     * Creates a {@code LinkedBlockingQueue} with a capacity of {@link Integer#MAX_VALUE},
     * containing the elements of the specified iterable,
     * in the order they are returned by the iterable's iterator.
     *
     * @param elements the elements that the queue should contain, in order
     * @return a new {@code LinkedBlockingQueue} containing those elements
     */
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new LinkedBlockingQueue<>(cast(elements));
        }
        LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<>();
        addAll(queue, elements);
        return queue;
    }

    // LinkedList: see {@link com.google.common.collect.Lists}

    // PriorityBlockingQueue

    /**
     * Creates an empty {@code PriorityBlockingQueue} with the ordering given by its
     * elements' natural ordering.
     *
     * @since 11.0 (requires that {@code E} be {@code Comparable} since 15.0).
     */
    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue() {
        return new PriorityBlockingQueue<>();
    }

    /**
     * Creates a {@code PriorityBlockingQueue} containing the given elements.
     *
     * <b>Note:</b> If the specified iterable is a {@code SortedSet} or a {@code PriorityQueue},
     * this priority queue will be ordered according to the same ordering.
     *
     * @since 11.0 (requires that {@code E} be {@code Comparable} since 15.0).
     */
    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new PriorityBlockingQueue<>(cast(elements));
        }
        PriorityBlockingQueue<E> queue = new PriorityBlockingQueue<>();
        addAll(queue, elements);
        return queue;
    }

    // PriorityQueue

    /**
     * Creates an empty {@code PriorityQueue} with the ordering given by its
     * elements' natural ordering.
     *
     * @since 11.0 (requires that {@code E} be {@code Comparable} since 15.0).
     */
    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue() {
        return new PriorityQueue<>();
    }

    /**
     * Creates a {@code PriorityQueue} containing the given elements.
     *
     * <b>Note:</b> If the specified iterable is a {@code SortedSet} or a {@code PriorityQueue},
     * this priority queue will be ordered according to the same ordering.
     *
     * @since 11.0 (requires that {@code E} be {@code Comparable} since 15.0).
     */
    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return new PriorityQueue<>(cast(elements));
        }
        PriorityQueue<E> queue = new PriorityQueue<>();
        addAll(queue, elements);
        return queue;
    }

    // SynchronousQueue

    /**
     * Creates an empty {@code SynchronousQueue} with nonfair access policy.
     */
    public static <E> SynchronousQueue<E> newSynchronousQueue() {
        return new SynchronousQueue<>();
    }

    /**
     * Drains the queue as {@link BlockingQueue#drainTo(Collection, int)}, but if the requested
     * {@code numElements} elements are not available, it will wait for them up to the specified
     * timeout.
     *
     * @param q           the blocking queue to be drained
     * @param buffer      where to add the transferred elements
     * @param numElements the number of elements to be waited for
     * @param timeout     how long to wait before giving up, in units of {@code unit}
     * @param unit        a {@code TimeUnit} determining how to interpret the timeout parameter
     * @return the number of elements transferred
     * @throws InterruptedException if interrupted while waiting
     */
    public static <E> int drain(BlockingQueue<E> q, Collection<? super E> buffer, int numElements,
            long timeout, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(buffer);
    /*
     * This code performs one System.nanoTime() more than necessary, and in return, the time to
     * execute Queue#drainTo is not added *on top* of waiting for the timeout (which could make
     * the timeout arbitrarily inaccurate, given a queue that is slow to drain).
     */
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        int added = 0;
        while (added < numElements) {
            // we could rely solely on #poll, but #drainTo might be more efficient when there are multiple
            // elements already available (e.g. LinkedBlockingQueue#drainTo locks only once)
            added += q.drainTo(buffer, numElements - added);
            if (added < numElements) { // not enough elements immediately available; will have to poll
                E e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
                if (e == null) {
                    break; // we already waited enough, and there are no more elements in sight
                }
                buffer.add(e);
                added++;
            }
        }
        return added;
    }

    /**
     * Drains the queue as {@linkplain #drain(BlockingQueue, Collection, int, long, TimeUnit)},
     * but with a different behavior in case it is interrupted while waiting. In that case, the
     * operation will continue as usual, and in the end the thread's interruption status will be set
     * (no {@code InterruptedException} is thrown).
     *
     * @param q           the blocking queue to be drained
     * @param buffer      where to add the transferred elements
     * @param numElements the number of elements to be waited for
     * @param timeout     how long to wait before giving up, in units of {@code unit}
     * @param unit        a {@code TimeUnit} determining how to interpret the timeout parameter
     * @return the number of elements transferred
     */
    public static <E> int drainUninterruptibly(BlockingQueue<E> q, Collection<? super E> buffer,
            int numElements, long timeout, TimeUnit unit) {
        Preconditions.checkNotNull(buffer);
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        int added = 0;
        boolean interrupted = false;
        try {
            while (added < numElements) {
                // we could rely solely on #poll, but #drainTo might be more efficient when there are
                // multiple elements already available (e.g. LinkedBlockingQueue#drainTo locks only once)
                added += q.drainTo(buffer, numElements - added);
                if (added < numElements) { // not enough elements immediately available; will have to poll
                    E e; // written exactly once, by a successful (uninterrupted) invocation of #poll
                    while (true) {
                        try {
                            e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
                            break;
                        } catch (InterruptedException ex) {
                            interrupted = true; // note interruption and retry
                        }
                    }
                    if (e == null) {
                        break; // we already waited enough, and there are no more elements in sight
                    }
                    buffer.add(e);
                    added++;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        return added;
    }

    // JDK

    /**
     * Returns a last-in, first-out queue as a view of {@code deque}.
     *
     * @since 1.6
     */
    public static <T> Queue<T> asLifoQueue(Deque<T> deque) {
        return java.util.Collections.asLifoQueue(deque);
    }
}
