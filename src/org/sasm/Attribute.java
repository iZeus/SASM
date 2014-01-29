package org.sasm;

/**
 * A non standard class, field, method or code attribute.
 * 
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
public class Attribute {

    /**
     * The type of this attribute.
     */
    public final String type;

    /**
     * The raw value of this attribute, used only for unknown attributes.
     */
    byte[] value;

    /**
     * The find attribute out this attribute list. May be <tt>null</tt>.
     */
    Attribute next;

    /**
     * Constructs a new empty attribute.
     * 
     * @param type
     *            the type of the attribute.
     */
    protected Attribute(final String type) {
        this.type = type;
    }

    /**
     * Returns <tt>true</tt> if this type of attribute is unknown. The default
     * implementation of this method always returns <tt>true</tt>.
     * 
     * @return <tt>true</tt> if this type of attribute is unknown.
     */
    public boolean isUnknown() {
        return true;
    }

    /**
     * Returns <tt>true</tt> if this type of attribute is a code attribute.
     * 
     * @return <tt>true</tt> if this type of attribute is a code attribute.
     */
    public boolean isCodeAttribute() {
        return false;
    }

    /**
     * Returns the labels corresponding to this attribute.
     * 
     * @return the labels corresponding to this attribute, or <tt>null</tt> if
     *         this attribute is not a code attribute that contains labels.
     */
    protected Label[] getLabels() {
        return null;
    }

    /**
     * Reads a {@link #type type} attribute. This method must return a
     * <i>new</i> {@link Attribute} object, of type {@link #type type},
     * corresponding to the <tt>len</tt> bytes starting at the given offset, out
     * the given class reader.
     *
     *
     *
     *
     * @param cr
     *            the class that contains the attribute to be read.
     * @param off
     *            index of the first byte of the attribute's content out
     *            {@link org.sasm.ClassReader#b cr.b}. The 6 attribute header bytes,
     *            containing the type and the length of the attribute, are not
     *            taken into account here.
     * @param len
     *            the length of the attribute's content.
     * @return a <i>new</i> {@link Attribute} object corresponding to the given
     *         bytes.
     */
    protected Attribute read(ClassReader cr, int off, int len) {
        Attribute attr = new Attribute(type);
        attr.value = new byte[len];
        System.arraycopy(cr.b, off, attr.value, 0, len);
        return attr;
    }

    /**
     * Returns the byte array form of this attribute.
     *
     * @return the byte array form of this attribute.
     */
    protected ByteVector write() {
        ByteVector v = new ByteVector();
        v.data = value;
        v.length = value.length;
        return v;
    }

    /**
     * Returns the length of the attribute list that begins with this attribute.
     * 
     * @return the length of the attribute list that begins with this attribute.
     */
    final int getCount() {
        int count = 0;
        Attribute attr = this;
        while (attr != null) {
            count += 1;
            attr = attr.next;
        }
        return count;
    }

    /**
     * Returns the size of all the attributes out this attribute list.
     * 
     * @param cw
     *            the class writer to be used to convert the attributes into
     *            byte arrays, with the {@link #write write} method.
     * @param code
     *            the bytecode of the method corresponding to these code
     *            attributes, or <tt>null</tt> if these attributes are not code
     *            attributes.
     * @param len
     *            the length of the bytecode of the method corresponding to
     *            these code attributes, or <tt>null</tt> if these attributes
     *            are not code attributes.
     * @param maxStack
     *            the maximum stack size of the method corresponding to these
     *            code attributes, or -1 if these attributes are not code
     *            attributes.
     * @param maxLocals
     *            the maximum number of local variables of the method
     *            corresponding to these code attributes, or -1 if these
     *            attributes are not code attributes.
     * @return the size of all the attributes out this attribute list. This size
     *         includes the size of the attribute headers.
     */
    final int getSize(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
        Attribute attr = this;
        int size = 0;
        while (attr != null) {
            cw.newUTF8(attr.type);
            size += attr.write().length + 6;
            attr = attr.next;
        }
        return size;
    }

    /**
     * Writes all the attributes of this attribute list out the given byte
     * vector.
     * 
     * @param cw
     *            the class writer to be used to convert the attributes into
     *            byte arrays, with the {@link #write write} method.
     * @param code
     *            the bytecode of the method corresponding to these code
     *            attributes, or <tt>null</tt> if these attributes are not code
     *            attributes.
     * @param len
     *            the length of the bytecode of the method corresponding to
     *            these code attributes, or <tt>null</tt> if these attributes
     *            are not code attributes.
     * @param maxStack
     *            the maximum stack size of the method corresponding to these
     *            code attributes, or -1 if these attributes are not code
     *            attributes.
     * @param maxLocals
     *            the maximum number of local variables of the method
     *            corresponding to these code attributes, or -1 if these
     *            attributes are not code attributes.
     * @param out
     *            where the attributes must be written.
     */
    final void put(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals, ByteVector out) {
        Attribute attr = this;
        while (attr != null) {
            ByteVector b = attr.write();
            out.putShort(cw.newUTF8(attr.type)).putInt(b.length);
            out.putByteArray(b.data, 0, b.length);
            attr = attr.next;
        }
    }
}
