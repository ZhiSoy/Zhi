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

package com.zhi.common.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

/**
 * A {PoolingBufferedRandomAccessFileReader is a READ-ONLY {@link java.io.RandomAccessFile}
 * with a cache buffer,
 */
public class PoolingRandomAccessFileReader extends RandomAccessFile {
    /**
     * The object used to synchronize access to the reader.
     */
    protected final Object lock;

    /**
     * This is the default size to which the underlying byte array is initialized.
     */
    private static final int DEFAULT_SIZE = 8192;

    /**
     * The byte array containing the bytes written.
     */
    private byte[] buf;
    private byte[] scratch;
    /**
     * Buffers Low and end bounds[start, end) and current position.
     */
    private long start, end, pos;
    protected final ByteArrayPool mPool;

    public PoolingRandomAccessFileReader(String fileName, ByteArrayPool pool)
            throws IOException {
        this(fileName, pool, DEFAULT_SIZE);
    }

    public PoolingRandomAccessFileReader(String fileName, ByteArrayPool pool, int size)
            throws IOException {
        super(fileName, "r");
        lock = this;
        mPool = pool;
        buf = mPool.getBuf(Math.max(size, DEFAULT_SIZE));
        scratch = mPool.getBuf(8);
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (!isClosed()) {
                mPool.returnBuf(buf);
                buf = null;
                mPool.returnBuf(scratch);
                scratch = null;
                super.close();
            }
        }
    }

    /**
     * Indicates whether or not this reader is closed.
     *
     * @return {@code true} if this reader is closed, {@code false}
     * otherwise.
     */
    public boolean isClosed() {
        return buf == null;
    }

    private void checkNotClosed() throws IOException {
        if (isClosed()) {
            throw new IOException("BufferedReader is closed");
        }
    }

    /**
     * Gets the current position within this file. All reads and
     * writes take place at the current file pointer position.
     *
     * @return the current offset in bytes from the beginning of the file.
     */
    public long getFilePosition() throws IOException {
        return super.getFilePointer();
    }

    @Override
    public long getFilePointer() throws IOException {
        return pos;
    }

    /**
     * Read at most buf.length bytes into this buf, returning the number of bytes read.
     * If the return result is less than this buf.length, EOF was reached.
     */
    private int fillBuf() throws IOException {
        final int res = super.read(buf, 0, buf.length);
        if (res > 0) {
            end = getFilePosition();
            start = end - res;
            pos = start;
        }
        return res;
    }

    @Override
    public void seek(long offset) throws IOException {
        synchronized (lock) {
            if (offset < start || offset >= end) {
                // seeking is not in the buffered segment.
                super.seek(offset);
                fillBuf();
            }
            pos = offset;
        }
    }

    @Override
    public int read() throws IOException {
        synchronized (lock) {
            checkNotClosed();
            if (pos < end || fillBuf() != -1) {
                return buf[(int) (pos++ - start)] & 0xff;
            }
            return -1;
        }
    }

    /**
     * Reads a little-endian 16-bit character from the current position in this file. Blocks until
     * two bytes have been read, the end of the file is reached or an exception is thrown.
     *
     * @return the next char value from this file.
     * @throws java.io.EOFException if the end of this file is detected.
     * @throws java.io.IOException  if this file is closed or another I/O error occurs.
     * @see #writeChar(int)
     */
    public int readLEShort() throws IOException {
        readFully(scratch, 0, SizeOf.SHORT);
        return Memory.peekShort(scratch, 0, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Reads an unsigned little-endian 16-bit short from the current position in this file and
     * returns it as an integer. Blocks until two bytes have been read, the end of
     * the file is reached or an exception is thrown.
     *
     * @return the next unsigned short value from this file as an int.
     * @throws java.io.EOFException if the end of this file is detected.
     * @throws java.io.IOException  if this file is closed or another I/O error occurs.
     * @see #writeShort(int)
     */
    public final int readUnsignedLEShort() throws IOException {
        return ((int) readLEShort()) & 0xffff;
    }

    /**
     * Reads a little-endian 16-bit character from the current position in this file. Blocks until
     * two bytes have been read, the end of the file is reached or an exception is thrown.
     *
     * @return the next char value from this file.
     * @throws java.io.EOFException if the end of this file is detected.
     * @throws java.io.IOException  if this file is closed or another I/O error occurs.
     * @see #writeChar(int)
     */
    public final char readLEChar() throws IOException {
        return (char) readLEShort();
    }

    /**
     * Reads a little-endian 32-bit integer from the current position in this file. Blocks
     * until four bytes have been read, the end of the file is reached or an
     * exception is thrown.
     *
     * @return the next int value from this file.
     * @throws java.io.EOFException if the end of this file is detected.
     * @throws java.io.IOException  if this file is closed or another I/O error occurs.
     * @see #writeInt(int)
     */
    public final int readLEInt() throws IOException {
        readFully(scratch, 0, SizeOf.INT);
        return Memory.peekInt(scratch, 0, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Reads a little-endian 64-bit long from the current position in this file. Blocks until
     * eight bytes have been read, the end of the file is reached or an
     * exception is thrown.
     *
     * @return the next long value from this file.
     * @throws java.io.EOFException if the end of this file is detected.
     * @throws java.io.IOException  if this file is closed or another I/O error occurs.
     * @see #writeLong(long)
     */
    public final long readLELong() throws IOException {
        readFully(scratch, 0, SizeOf.LONG);
        return Memory.peekLong(scratch, 0, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Reads a little-endian 64-bit double from the current position in this file. Blocks
     * until eight bytes have been read, the end of the file is reached or an
     * exception is thrown.
     *
     * @return the next double value from this file.
     * @throws java.io.EOFException if the end of this file is detected.
     * @throws java.io.IOException  if this file is closed or another I/O error occurs.
     * @see #writeDouble(double)
     */
    public final double readLEDouble() throws IOException {
        return Double.longBitsToDouble(readLELong());
    }

    /**
     * Reads a little-endian 32-bit float from the current position in this file. Blocks
     * until four bytes have been read, the end of the file is reached or an
     * exception is thrown.
     *
     * @return the next float value from this file.
     * @throws java.io.EOFException if the end of this file is detected.
     * @throws java.io.IOException  if this file is closed or another I/O error occurs.
     * @see #writeFloat(float)
     */
    public final float readLEFloat() throws IOException {
        return Float.intBitsToFloat(readLEInt());
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        synchronized (lock) {
            checkNotClosed();
            if (length == 0) {
                return 0;
            }

            int outstanding = length;
            while (outstanding > 0) {
                // If there are chars in the buffer, grab those first.
                final int available = (int) (end - pos);
                if (available > 0) {
                    final int count = available >= outstanding ? outstanding : available;
                    System.arraycopy(buf, (int) (pos - start), buffer, offset, count);
                    pos += count;
                    offset += count;
                    outstanding -= count;
                }

                if (outstanding == 0) {
                    break;
                }

                if (outstanding >= buf.length) {
                    int count = super.read(buffer, offset, outstanding);
                    if (count > 0) {
                        outstanding -= count;
                    }
                    break;
                }

                if (fillBuf() == -1) {
                    break; // source is exhausted
                }
            }

            final int count = length - outstanding;
            if (count > 0) {
                return count;
            }
            return -1;
        }
    }
}