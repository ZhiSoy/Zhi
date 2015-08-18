/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhi.volley;

import java.util.Collections;
import java.util.Map;

/**
 * An interface for a cache keyed by a String with a byte array as data.
 */
public interface Cache {
    /**
     * Retrieves an entry from the cache.
     *
     * @param key Cache key
     * @return An {@link Cache.Entry} or null in the event of a cache miss
     */
    Entry get(String key);

    /**
     * Adds or replaces an entry to the cache.
     *
     * @param key   Cache key
     * @param entry Data to store and metadata for cache coherency, TTL, etc.
     */
    void put(String key, Entry entry);

    /**
     * Performs any potentially long-running actions needed to initialize the cache;
     * will be called from a worker thread.
     */
    void initialize();

    /**
     * Invalidates an entry in the cache.
     *
     * @param key        Cache key
     * @param fullExpire True to fully expire the entry, false to soft expire
     */
    void invalidate(String key, boolean fullExpire);

    /**
     * Removes an entry from the cache.
     *
     * @param key Cache key
     */
    void remove(String key);

    /**
     * Empties the cache.
     */
    void clear();

    /**
     * Data and metadata for an entry returned by the cache.
     */
    class Entry {
        public static final Entry PERSISTENT_IN_MEMORY = new Entry();
        public static final Entry PERSISTENT_OUT_MEMORY = new Entry();

        static {
            PERSISTENT_IN_MEMORY.inMemory = true;
            PERSISTENT_OUT_MEMORY.inMemory = false;
            PERSISTENT_IN_MEMORY.data = PERSISTENT_OUT_MEMORY.data = new byte[0];
            PERSISTENT_IN_MEMORY.etag = PERSISTENT_OUT_MEMORY.etag = "";
            PERSISTENT_IN_MEMORY.serverDate = PERSISTENT_OUT_MEMORY.serverDate = System.currentTimeMillis();
            PERSISTENT_IN_MEMORY.ttl = PERSISTENT_OUT_MEMORY.ttl = Long.MAX_VALUE;
            PERSISTENT_IN_MEMORY.softTtl = PERSISTENT_OUT_MEMORY.softTtl = Long.MAX_VALUE;
            PERSISTENT_IN_MEMORY.lastModified = PERSISTENT_OUT_MEMORY.lastModified = Long.MAX_VALUE;
        }

        /** Whether the entry should be list in memory. */
        public boolean inMemory;
        /** The data returned from cache. */
        public byte[] data;
        /** ETag for cache coherency. */
        public String etag;
        /** Date of this response as reported by the server. */
        public long serverDate;
        /** The last modified date for the requested object. */
        public long lastModified;
        /** TTL for this record. */
        public long ttl;
        /** Soft TTL for this record. */
        public long softTtl;
        /** Immutable response headers as received from server; must be non-null. */
        public Map<String, String> responseHeaders = Collections.emptyMap();

        /** True if the entry is expired. */
        public boolean isExpired() {
            return this.ttl < System.currentTimeMillis();
        }

        /** True if a refresh is needed from the original data source. */
        public boolean refreshNeeded() {
            return this.softTtl < System.currentTimeMillis();
        }
    }
}
