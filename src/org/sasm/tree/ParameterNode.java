package org.sasm.tree;

import org.sasm.MethodVisitor;

/**
 * A node that represents a parameter access and name.
 * 
 * @author Remi Forax
 */
public class ParameterNode {
    /**
     * The parameter's name.
     */
    public String name;

    /**
     * The parameter's access flags (see {@link org.sasm.Opcodes}).
     * Valid values are <tt>ACC_FINAL</tt>, <tt>ACC_SYNTHETIC</tt> and
     * <tt>ACC_MANDATED</tt>.
     */
    public int access;

    /**
     * Constructs a new {@link ParameterNode}.
     * 
     * @param access
     *            The parameter's access flags. Valid values are
     *            <tt>ACC_FINAL</tt>, <tt>ACC_SYNTHETIC</tt> or/and
     *            <tt>ACC_MANDATED</tt> (see {@link org.sasm.Opcodes}).
     * @param name
     *            the parameter's name.
     */
    public ParameterNode(String name, int access) {
        this.name = name;
        this.access = access;
    }

    /**
     * Makes the given visitor visit this parameter declaration.
     * 
     * @param mv
     *            a method visitor.
     */
    public void accept(MethodVisitor mv) {
        mv.visitParameter(name, access);
    }
}
