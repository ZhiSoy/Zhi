package com.zhi.volley.image;

import com.zhi.common.content.UiHandler;
import com.zhi.common.io.ByteArrayPool;
import com.zhi.common.io.IntArrayPool;
import com.zhi.common.io.PoolingRandomAccessFileReader;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * The Decoder is the program used to process a GIF Data Stream. It processes the Data Stream
 * sequentially, parsing the various blocks and sub-blocks, using the control information to
 * set hardware and process parameters and interpreting the data to render the graphics. <p/>
 * The decoder has the following primary responsibilities.
 * <ul>
 * <li>- Process each graphic in the Data Stream in sequence, without </li>
 * delays other than those specified in the control information.
 * <li>- Set its hardware parameters to fit, as closely as possible, the
 * control information contained in the Data Stream. </li>
 * </ul>
 */
public class GifImageDecoder extends ImageDecoder {
    private static final int DISPOSAL_METHOD_NOT_SPECIFIED = 0;
    private static final int DISPOSAL_METHOD_DO_NOT_DISPOSE = 1;
    private static final int DISPOSAL_METHOD_RESTORE_TO_BACKGROUND_COLOR = 2;
    private static final int DISPOSAL_METHOD_RESTORE_TO_PREVIOUS = 3;

    private static final int IMAGE_SEPARATOR = 0x2C;
    private static final int EXTENSION_BLOCK = 0x21;
    private static final int TERMINATOR = 0x3b;
    private static final int GRAPHIC_CONTROL_LABEL = 0xf9;
    private static final int APPLICATION_LABEL = 0xff;
    private static final int COMMENT_LABEL = 0xfe;
    private static final int PLAIN_TEXT_EXTENSION = 0x01;
    private static final int DEFAULT_POOL_SIZE = 8012;
    private static final int MAX_STACK_SIZE = 4096;
    private static final int MAX_COLOR_TABLE_SIZE = 256;
    private static final long FRAME_MIN_DELAY_MS = 10;

    private final IntArrayPool mIPool = new IntArrayPool(DEFAULT_POOL_SIZE);
    private final ByteArrayPool mBPool = new ByteArrayPool(DEFAULT_POOL_SIZE);

    private final byte[] stack = new byte[MAX_STACK_SIZE + 1];
    private final byte[] suffix = new byte[MAX_STACK_SIZE];
    private final short[] prefix = new short[MAX_STACK_SIZE];

    private final int[] gct = new int[MAX_COLOR_TABLE_SIZE];
    private final int[] lct = new int[MAX_COLOR_TABLE_SIZE];
    private final GifFrame mFrame = new GifFrame();

    private int[] act;
    private int[] mPixels;
    private int[] mSaved;
    private byte[] mScratch;
    private Bitmap mBitmap;
    private GifReader mReader;
    private DecodeRequest mRequest;

    private int mWidth;
    private int mHeight;
    private int mScreenPacked;
    private int mBgColorIndex;

    private int mFrameCount;
    private int mFramePointer;
    private int pass, lines, inc;

    public GifImageDecoder(UiHandler uiHandler) {
        super(uiHandler);
    }

    /**
     * This method should be called when the gif decode finished.
     */
    void finishDecode() throws IOException {
        if (mRequest != null) {
            mRequest.cancel();
            mRequest = null;
        }
        if (mReader != null) {
            mReader.close();
            mReader = null;
        }
        if (mPixels != null) {
            mIPool.returnBuf(mPixels);
            mPixels = null;
        }
        if (mSaved != null) {
            mIPool.returnBuf(mSaved);
            mSaved = null;
        }
        if (mScratch != null) {
            mBPool.returnBuf(mScratch);
        }
    }

    @Override
    protected void decode(DecodeRequest request, String fileName) throws IOException {
        try {
            mRequest = request;
            mReader = new GifReader(fileName, mBPool);

            if (!mReader.checkSignature()) {
                throw new IOException("Not a gif file.");
            }
            mReader.skipBytes(3);

            mWidth = mReader.readLEShort();
            mHeight = mReader.readLEShort();
            mScreenPacked = mReader.read();
            mBgColorIndex = mReader.read();
            // ignored the aspect radio.
            mReader.read();

            // Read global color table.
            if (!hasColorTable(mScreenPacked)) {
                throw new IOException("No Global Color Table");
            }
            mReader.readColorTable(gct, getColorTableSize(mScreenPacked));

            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
            final int totals = mWidth * mHeight;
            mSaved = mIPool.getBuf(totals);
            mPixels = mIPool.getBuf(totals);
            mScratch = mBPool.getBuf(totals);

            mFrameCount = -1;
            mFramePointer = 0;

            final long offset = mReader.getFilePointer();

            int loops = 0;
            whileLoop:
            while (!mRequest.canceled) {
                switch (mReader.read()) {
                    case EXTENSION_BLOCK: {
                        switch (mReader.read()) {
                            case GRAPHIC_CONTROL_LABEL:
                                readGraphicControlExtension();
                                break;

                            case APPLICATION_LABEL:
                                loops = readApplicationExtension();
                                break;

                            case COMMENT_LABEL:
                                // Loop through.
                            case PLAIN_TEXT_EXTENSION:
                                // Loop through.
                            default:
                                // uninteresting extension
                                mReader.skipBlocks();
                        }
                        break;
                    }

                    case IMAGE_SEPARATOR:
                        readImage();
                        mFramePointer++;
                        break;

                    case -1:
                        // loop through.
                    default:
                        // bad byte. if there's ast least two frame, ignored the rest.
                        if (mFramePointer <= 1) {
                            break whileLoop;
                        }
                        // loop through.
                    case TERMINATOR:
                        if (mFrameCount == -1) {
                            mFrameCount = mFramePointer;
                        }
                        if (loops == 0 || loops-- > 0) {
                            mFramePointer = 0;
                            mReader.seek(offset);
                            break;
                        }
                        // else the end of decoding.
                        break whileLoop;
                }
            }
        } finally {
            if (mRequest.canceled) {
                postCanceled(request);
            }
            finishDecode();
        }
    }


    /**
     * Decode the mFrame image data into the pixels with start position.
     */
    void decodeFrameData() throws IOException {
        pass = 1;
        inc = 8;
        lines = 0;

        final int tIndex = mFrame.isTransparency() ? mFrame.transpIndex : -1;

        final int size = mReader.read();
        final int clear = 1 << size;
        final int endOfInformation = clear + 1;

        for (int code = 0; code < clear; code++) {
            prefix[code] = 0;
            suffix[code] = (byte) code;
        }

        int datum = 0;
        int code, oldCode = -1, inCode;
        int codes = size + 1;
        int mask = (1 << codes) - 1;
        int available = clear + 2;
        int top = 0, count = 0, bits = 0;
        int first = 0, pi = 0;

        // The number of pixels that's need to read most.
        final int npixels = mFrame.width * mFrame.height;
        int cols = 0, rows = 0;
        final byte[] pixels = mScratch;

        // Pop a pixel off the pixel stack.
        while (pi < npixels) {
            if (top == 0) {
                if (bits < codes) {
                    if (count == 0) {
                        count = mReader.read();
                        if (count <= 0) {
                            count = -1;
                            break;
                        }
                    }
                    datum += mReader.read() << bits;
                    bits += 8;
                    count--;
                    continue;
                }
                // Get the next code.
                code = datum & mask;
                datum >>= codes;
                bits -= codes;

                // Interpret the code
                if (code > available || code == endOfInformation) {
                    break;
                }

                if (code == clear) {
                    codes = size + 1;
                    mask = (1 << codes) - 1;
                    available = clear + 2;
                    oldCode = -1;
                    continue;
                }

                if (oldCode == -1) {
                    stack[top++] = suffix[code];
                    oldCode = code;
                    first = code;
                    continue;
                }

                inCode = code;
                if (code == available) {
                    stack[top++] = (byte) first;
                    code = oldCode;
                }
                while (code > clear) {
                    stack[top++] = suffix[code];
                    code = prefix[code];
                }
                first = suffix[code] & 0xff;
                if (available >= MAX_STACK_SIZE) {
                    break;
                }

                stack[top++] = (byte) first;
                prefix[available] = (short) oldCode;
                suffix[available] = (byte) first;
                available++;

                if ((available & mask) == 0 && available < MAX_STACK_SIZE) {
                    codes++;
                    mask += available;
                }
                oldCode = inCode;
            }
            // Pop a pixel off the pixel stack, and add to row buffer.
            pixels[cols++] = stack[--top];
            if (cols == mFrame.width) {
                // finish a read row. save to bitmap.
                saveToBitmap(tIndex, pixels, rows, cols);
                // continue to read next row.
                rows++;
                cols = 0;
            }
            pi++;
        }

        if (cols != 0) {
            while (cols < mFrame.width) {
                pixels[cols++] = 0;
            }
            // finish a read row. save to bitmap.
            saveToBitmap(tIndex, pixels, rows, cols);
        }

        mReader.skipBytes(count);
        if (count != -1) {
            mReader.skipBlocks();
        }
    }

    void saveToBitmap(int tIndex, byte[] pixels, int rows, int cols) {
        int line = rows;
        if (mFrame.isInterlaced()) {
            if (lines >= mFrame.height) {
                pass++;
                switch (pass) {
                    case 2:
                        lines = 4;
                        break;
                    case 3:
                        lines = 2;
                        inc = 4;
                        break;
                    case 4:
                        lines = 1;
                        inc = 2;
                        break;
                }
            }
            line = lines;
            lines += inc;
        }

        final int x = mFrame.x;
        final int y = line + mFrame.y;
        if (y >= mHeight) {
            // out of screen bounds.
            return;
        }

        final int offset = getArrayIndex(x, y);
        for (int i = 0; i < cols; i++) {
            final int index = pixels[i] & 0xff;
            // if indexed color is transparency, keep old color.
            if (index != tIndex && act[index] != 0) {
                mPixels[offset + i] = act[index];
            }
        }
    }

    /**
     * Check whether packed fields has a Color Table.
     *
     * @return True if this gif has a global color table otherwise false.
     */
    static boolean hasColorTable(int packed) {
        return (packed & 0x80) != 0;
    }

    /**
     * Get the global color table size.
     *
     * @return global color table size.
     */
    static int getColorTableSize(int packed) {
        return packed & 0x07;
    }

    /**
     * Get the background color.
     */
    int getBackgroundColor() {
        if (hasColorTable(mScreenPacked)) {
            if (0 <= mBgColorIndex && mBgColorIndex < gct.length) {
                return gct[mBgColorIndex];
            }
        }
        return 0;
    }

    /**
     * Reads Graphics Control Extension values
     */
    void readGraphicControlExtension() throws IOException {
        final int size = mReader.read();
        if (size != 4) {
            throw new IOException("The graphic control size should be 4.");
        }
        mFrame.graphicPacked = mReader.read();
        mFrame.delayTimeMs = mReader.readLEShort() * 10;
        if (mFrame.delayTimeMs < FRAME_MIN_DELAY_MS) {
            mFrame.delayTimeMs = FRAME_MIN_DELAY_MS;
        }
        mFrame.transpIndex = mReader.read();
        mReader.read(); // terminal guard.
    }

    /**
     * Reads Application Extension values
     *
     * @return The animation loops count, default is zero.
     */
    int readApplicationExtension() throws IOException {
        final int loops;
        if (mReader.readAndEquals(mReader.read(), "NETSCAPE2.0")) {
            final int size = mReader.read();
            mReader.read(); // data sub-block index (always 1)
            loops = mReader.readLEShort();
            // ignored extra data.
            if (size - 3 > 0) {
                mReader.skipBytes(size - 3);
            }
            mReader.read(); // terminal guard.
        } else {
            loops = 0; // default is unlimited.
            mReader.skipBlocks();
        }
        return loops;
    }

    /**
     * Get the array index in this Gif file (width X height).
     *
     * @param x coordinate position in gif.
     * @param y coordinate position in gif.
     * @return The array index. arr[0,...,width*height]
     */
    int getArrayIndex(int x, int y) {
        return x + y * mWidth;
    }

    /**
     * Reads image descriptor.
     */
    void readImage() throws IOException {
        mFrame.x = mReader.readLEShort();
        mFrame.y = mReader.readLEShort();
        mFrame.width = mReader.readLEShort();
        mFrame.height = mReader.readLEShort();
        mFrame.imagePacked = mReader.read();

        act = null;
        if (hasColorTable(mFrame.imagePacked)) {
            final int size = getColorTableSize(mFrame.imagePacked);
            mReader.readColorTable(lct, size);
            if (size > 0) {
                act = lct;
            }
        }
        if (act == null) {
            act = gct;
        }

        // decode frame image data.
        decodeFrameData();
        mBitmap.setPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);

        postResponse(mRequest, mFrameCount, mFramePointer, mBitmap);

        // dispose this frame, apply dispose method.
        mFrame.dispose();
    }

    @Override
    protected int getFramePointer() throws IOException {
        return mFramePointer;
    }

    @Override
    protected int getFrameCount() throws IOException {
        return mFrameCount;
    }

    final class GifFrame {

        int x, y;
        int width;
        int height;
        int imagePacked;
        int graphicPacked;
        long delayTimeMs;
        int transpIndex;

        /**
         * Get this frame dispose method which indicates the way in which the graphic is
         * to be treated after being displayed.
         *
         * @return this frame's dispose method or one of the predefined disposal methods:
         * {@link #DISPOSAL_METHOD_NOT_SPECIFIED},
         * {@link #DISPOSAL_METHOD_DO_NOT_DISPOSE},
         * {@link #DISPOSAL_METHOD_RESTORE_TO_BACKGROUND_COLOR},
         * {@link #DISPOSAL_METHOD_RESTORE_TO_PREVIOUS}
         */
        int getDisposalMethod() {
            return (graphicPacked & 0x1c) >> 2;
        }

        /**
         * Check whether this frame is interlaced.
         *
         * @return true if this frame is interlaced, otherwise false.
         */
        boolean isInterlaced() {
            return (imagePacked & 0x40) != 0;
        }

        /**
         * Indicates whether a transparency index is given in the Transparent Index field.
         *
         * @return true if this frame is transparency, otherwise false.
         */
        boolean isTransparency() {
            return (graphicPacked & 0x01) != 0;
        }

        void dispose() {
            if (delayTimeMs > 0) {
                try {
                    sleep(delayTimeMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                yield();
            }

            final int start = getArrayIndex(x, y);
            final int end = Math.min(start + width * height, mSaved.length);

            switch (getDisposalMethod()) {
                case DISPOSAL_METHOD_RESTORE_TO_BACKGROUND_COLOR:
                    final int bgColor = getBackgroundColor();
                    if (bgColor != 0) {
                        for (int i = start; i < end; i++) {
                            mPixels[i] = mSaved[i] = bgColor;
                        }
                    } else {
                        // copy current displaying to saved.
                        System.arraycopy(mPixels, start, mSaved, start, end - start);
                    }
                    break;

                case DISPOSAL_METHOD_RESTORE_TO_PREVIOUS:
                    System.arraycopy(mSaved, start, mPixels, start, end - start);
                    break;

                case DISPOSAL_METHOD_DO_NOT_DISPOSE:
                    // Loop through.
                case DISPOSAL_METHOD_NOT_SPECIFIED:
                    // Loop through.
                default:
                    System.arraycopy(mPixels, start, mSaved, start, end - start);
            }
        }
    }
}

class GifReader extends PoolingRandomAccessFileReader {

    public GifReader(String fileName, ByteArrayPool pool) throws IOException {
        super(fileName, pool);
    }

    /**
     * Public check the gif file signature.
     *
     * @return true if the signature is start with "GIF", otherwise false.
     */
    boolean checkSignature() throws IOException {
        return readAndEquals(3, "GIF");
    }

    /**
     * Reads color table as 256 RGB integer values
     *
     * @param tab  the input color table.
     * @param size int number of colors to read
     */
    void readColorTable(int[] tab, int size) throws IOException {
        final int count = 1 << (size + 1);
        for (int i = 0; i < count; i++) {
            tab[i] = 0xff000000 | (read() << 16) | (read() << 8) | read();
        }
    }

    /**
     * Reads offset chars and to check whether the read chars are equal to the value.
     *
     * @param offset the number of chars to be read.
     * @param value  the value to be check.
     * @return true if the reads chars are equals to value, otherwise false.
     * @throws java.io.IOException if some error happens
     */
    boolean readAndEquals(int offset, String value) throws IOException {
        final byte[] bytes = mPool.getBuf(offset);
        boolean res = read(bytes) == offset;
        if (res) {
            for (int i = 0; i < offset; i++) {
                if (bytes[i] != value.charAt(i)) {
                    res = false;
                    break;
                }
            }
        }
        mPool.returnBuf(bytes);
        return res;
    }

    /**
     * Skip block until zero indexed block.
     */
    void skipBlocks() throws IOException {
        while (true) {
            final int size = read();
            if (size == 0) {
                break;
            }
            skipBytes(size);
        }
    }
}
