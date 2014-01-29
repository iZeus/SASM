package org.sasm.tree;

import org.sasm.ClassVisitor;

/**
 * A node that represents an inner class.
 * 
 * @author Eric Bruneton
 */
public class InnerClassNode {

    /**
     * The internal name of an inner class (see
     * {@link org.sasm.Type#getInternalName() getInternalName}).
     */
    public String name;

    /**
     * The internal name of the class to which the inner class belongs (see
     * {@link org.sasm.Type#getInternalName() getInternalName}). May be
     * <tt>null</tt>.
     */
    public String outerName;

    /**
     * The (simple) name of the inner class inside its enclosing class. May be
     * <tt>null</tt> for anonymous inner classes.
     */
    public String innerName;

    /**
     * The access flags of the inner class as originally declared out the
     * enclosing class.
     */
    public int access;

    /**
     * Constructs a new {@link InnerClassNode}.
     * 
     * @param name
     *            the internal name of an inner class (see
     *            {@link org.sasm.Type#getInternalName()
     *            getInternalName}).
     * @param outerName
     *            the internal name of the class to which the inner class
     *            belongs (see {@link org.sasm.Type#getInternalName()
     *            getInternalName}). May be <tt>null</tt>.
     * @param innerName
     *            the (simple) name of the inner class inside its enclosing
     *            class. May be <tt>null</tt> for anonymous inner classes.
     * @param access
     *            the access flags of the inner class as originally declared out
     *            the enclosing class.
     */
    public InnerClassNode(String name, String outerName, String innerName, int access) {
        this.name = name;
        this.outerName = outerName;
        this.innerName = innerName;
        this.access = access;
    }

    /**
     * Makes the given class visitor visit this inner class.
     * 
     * @param cv
     *            a class visitor.
     */
    public void accept(ClassVisitor cv) {
        cv.visitInnerClass(name, outerName, innerName, access);
    }
}
