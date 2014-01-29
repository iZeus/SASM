package org.sasm.tree;

import org.sasm.MethodVisitor;

import java.util.Map;

/**
 * A node that represents a method instruction. A method instruction is an
 * instruction that invokes a method.
 * 
 * @author Eric Bruneton
 */
public class MethodInsnNode extends AbstractInsnNode {

    /**
     * The internal name of the method's owner class (see
     * {@link org.sasm.Type#getInternalName() getInternalName}).
     */
    public String owner;

    /**
     * The method's name.
     */
    public String name;

    /**
     * The method's descriptor (see {@link org.sasm.Type}).
     */
    public String desc;

    /**
     * Constructs a new {@link MethodInsnNode}.
     * 
     * @param opcode
     *            the opcode of the type instruction to be constructed. This
     *            opcode must be INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
     *            INVOKEINTERFACE.
     * @param owner
     *            the internal name of the method's owner class (see
     *            {@link org.sasm.Type#getInternalName()
     *            getInternalName}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link org.sasm.Type}).
     */
    public MethodInsnNode(int opcode, String owner, String name, String desc) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    /**
     * Sets the opcode of this instruction.
     * 
     * @param opcode
     *            the new instruction opcode. This opcode must be INVOKEVIRTUAL,
     *            INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     */
    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return METHOD_INSN;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new MethodInsnNode(opcode, owner, name, desc);
    }
}