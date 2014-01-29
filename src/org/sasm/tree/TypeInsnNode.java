package org.sasm.tree;

import org.sasm.MethodVisitor;

import java.util.Map;

/**
 * A node that represents a type instruction. A type instruction is an
 * instruction that takes a type descriptor as parameter.
 * 
 * @author Eric Bruneton
 */
public class TypeInsnNode extends AbstractInsnNode {

    /**
     * The val of this instruction. This val is an internal name (see
     * {@link org.sasm.Type}).
     */
    public String desc;

    /**
     * Constructs a new {@link TypeInsnNode}.
     * 
     * @param opcode
     *            the opcode of the type instruction to be constructed. This
     *            opcode must be NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
     * @param desc
     *            the val of the instruction to be constructed. This val
     *            is an internal name (see {@link org.sasm.Type}).
     */
    public TypeInsnNode(int opcode, String desc) {
        super(opcode);
        this.desc = desc;
    }

    /**
     * Sets the opcode of this instruction.
     * 
     * @param opcode
     *            the new instruction opcode. This opcode must be NEW,
     *            ANEWARRAY, CHECKCAST or INSTANCEOF.
     */
    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return TYPE_INSN;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitTypeInsn(opcode, desc);
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new TypeInsnNode(opcode, desc).cloneAnnotations(this);
    }
}