package org.sasm;

/**
 * A dynamically extensible vector of bytes. This class is roughly equivalent to
 * a DataOutputStream on top of a ByteArrayOutputStream, but is more efficient.
 * 
 * @author Eric Bruneton
 */
public class ByteVector {

    /**
     * The content of this vector.
     */
    byte[] data;

    /**
     * Actual number of bytes out this vector.
     */
    int length;

    /**
     * Constructs a new {@link ByteVector ByteVector} with a default initial
     * size.
     */
    public ByteVector() {
        data = new byte[64];
    }

    /**
     * Constructs a new {@link ByteVector ByteVector} with the given initial
     * size.
     * 
     * @param initialSize
     *            the initial size of the byte vector to be constructed.
     */
    public ByteVector(int initialSize) {
        data = new byte[initialSize];
    }

    /**
     * Puts a byte into this byte vector. The byte vector is automatically
     * enlarged if necessary.
     * 
     * @param b
     *            a byte.
     * @return this byte vector.
     */
    public ByteVector putByte(int b) {
        int length = this.length;
        if (length + 1 > data.length) {
            enlarge(1);
        }
        data[length++] = (byte) b;
        this.length = length;
        return this;
    }

    /**
     * Puts two bytes into this byte vector. The byte vector is automatically
     * enlarged if necessary.
     * 
     * @param b1
     *            a byte.
     * @param b2
     *            another byte.
     * @return this byte vector.
     */
    ByteVector put11(int b1, int b2) {
        int length = this.length;
        if (length + 2 > data.length) {
            enlarge(2);
        }
        data[length++] = (byte) b1;
        data[length++] = (byte) b2;
        this.length = length;
        return this;
    }

    /**
     * Puts a short into this byte vector. The byte vector is automatically
     * enlarged if necessary.
     * 
     * @param s
     *            a short.
     * @return this byte vector.
     */
    public ByteVector putShort(int s) {
        int length = this.length;
        if (length + 2 > data.length) {
            enlarge(2);
        }
        data[length++] = (byte) (s >>> 8);
        data[length++] = (byte) s;
        this.length = length;
        return this;
    }

    /**
     * Puts a byte and a short into this byte vector. The byte vector is
     * automatically enlarged if necessary.
     * 
     * @param b
     *            a byte.
     * @param s
     *            a short.
     * @return this byte vector.
     */
    ByteVector put12(int b, int s) {
        int length = this.length;
        if (length + 3 > data.length) {
            enlarge(3);
        }
        data[length++] = (byte) b;
        data[length++] = (byte) (s >>> 8);
        data[length++] = (byte) s;
        this.length = length;
        return this;
    }

    /**
     * Puts an int into this byte vector. The byte vector is automatically
     * enlarged if necessary.
     * 
     * @param i
     *            an int.
     * @return this byte vector.
     */
    public ByteVector putInt(int i) {
        int length = this.length;
        if (length + 4 > data.length) {
            enlarge(4);
        }
        data[length++] = (byte) (i >>> 24);
        data[length++] = (byte) (i >>> 16);
        data[length++] = (byte) (i >>> 8);
        data[length++] = (byte) i;
        this.length = length;
        return this;
    }

    /**
     * Puts a long into this byte vector. The byte vector is automatically
     * enlarged if necessary.
     * 
     * @param l
     *            a long.
     * @return this byte vector.
     */
    public ByteVector putLong(long l) {
        int length = this.length;
        if (length + 8 > data.length) {
            enlarge(8);
        }
        int i = (int) (l >>> 32);
        data[length++] = (byte) (i >>> 24);
        data[length++] = (byte) (i >>> 16);
        data[length++] = (byte) (i >>> 8);
        data[length++] = (byte) i;
        i = (int) l;
        data[length++] = (byte) (i >>> 24);
        data[length++] = (byte) (i >>> 16);
        data[length++] = (byte) (i >>> 8);
        data[length++] = (byte) i;
        this.length = length;
        return this;
    }

    /**
     * Puts an UTF8 string into this byte vector. The byte vector is
     * automatically enlarged if necessary.
     * 
     * @param s
     *            a String whose UTF8 encoded length must be less than 65536.
     * @return this byte vector.
     */
    public ByteVector putUTF8(String s) {
        int charLength = s.length();
        if (charLength > 65535) {
            throw new IllegalArgumentException();
        }
        int len = length;
        if (len + 2 + charLength > data.length) {
            enlarge(2 + charLength);
        }
        byte[] data = this.data;
        // optimistic algorithm: instead of computing the byte length and then
        // serializing the string (which requires two loops), we assume the byte
        // length is equal to char length (which is the most frequent case), and
        // we start serializing the string right away. During the serialization,
        // if we find that this assumption is wrong, we continue with the
        // general method.
        data[len++] = (byte) (charLength >>> 8);
        data[len++] = (byte) charLength;
        for (int i = 0; i < charLength; ++i) {
            char c = s.charAt(i);
            if (c >= '\001' && c <= '\177') {
                data[len++] = (byte) c;
            } else {
                int byteLength = i;
                for (int j = i; j < charLength; ++j) {
                    c = s.charAt(j);
                    if (c >= '\001' && c <= '\177') {
                        byteLength++;
                    } else if (c > '\u07FF') {
                        byteLength += 3;
                    } else {
                        byteLength += 2;
                    }
                }
                if (byteLength > 65535) {
                    throw new IllegalArgumentException();
                }
                data[length] = (byte) (byteLength >>> 8);
                data[length + 1] = (byte) byteLength;
                if (length + 2 + byteLength > data.length) {
                    length = len;
                    enlarge(2 + byteLength);
                    data = this.data;
                }
                for (int j = i; j < charLength; ++j) {
                    c = s.charAt(j);
                    if (c >= '\001' && c <= '\177') {
                        data[len++] = (byte) c;
                    } else if (c > '\u07FF') {
                        data[len++] = (byte) (0xE0 | c >> 12 & 0xF);
                        data[len++] = (byte) (0x80 | c >> 6 & 0x3F);
                        data[len++] = (byte) (0x80 | c & 0x3F);
                    } else {
                        data[len++] = (byte) (0xC0 | c >> 6 & 0x1F);
                        data[len++] = (byte) (0x80 | c & 0x3F);
                    }
                }
                break;
            }
        }
        length = len;
        return this;
    }

    /**
     * Puts an array of bytes into this byte vector. The byte vector is
     * automatically enlarged if necessary.
     * 
     * @param b
     *            an array of bytes. May be <tt>null</tt> to put <tt>len</tt>
     *            null bytes into this byte vector.
     * @param off
     *            index of the fist byte of b that must be copied.
     * @param len
     *            number of bytes of b that must be copied.
     * @return this byte vector.
     */
    public ByteVector putByteArray(byte[] b, int off, int len) {
        if (length + len > data.length) {
            enlarge(len);
        }
        if (b != null) {
            System.arraycopy(b, off, data, length, len);
        }
        length += len;
        return this;
    }

    /**
     * Enlarge this byte vector so that it can receive n more bytes.
     * 
     * @param size
     *            number of additional bytes that this byte vector should be
     *            able to receive.
     */
    private void enlarge(int size) {
        int length1 = 2 * data.length;
        int length2 = length + size;
        byte[] newData = new byte[length1 > length2 ? length1 : length2];
        System.arraycopy(data, 0, newData, 0, length);
        data = newData;
    }
}
