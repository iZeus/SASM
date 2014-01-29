package org.sasm.tree;

import org.sasm.MethodVisitor;

import java.util.Map;

/**
 * A node that represents an instruction with a single int val.
 * 
 * @author Eric Bruneton
 */
public class IntInsnNode extends AbstractInsnNode {

    /**
     * The val of this instruction.
     */
    public int operand;

    /**
     * Constructs a new {@link IntInsnNode}.
     * 
     * @param opcode
     *            the opcode of the instruction to be constructed. This opcode
     *            must be BIPUSH, SIPUSH or NEWARRAY.
     * @param operand
     *            the val of the instruction to be constructed.
     */
    public IntInsnNode(final int opcode, final int operand) {
        super(opcode);
        this.operand = operand;
    }

    /**
     * Sets the opcode of this instruction.
     * 
     * @param opcode
     *            the new instruction opcode. This opcode must be BIPUSH, SIPUSH
     *            or NEWARRAY.
     */
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return INT_INSN;
    }

    @Override
    public void accept(final MethodVisitor mv) {
        mv.visitIntInsn(opcode, operand);
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> labels) {
        return new IntInsnNode(opcode, operand).cloneAnnotations(this);
    }
}
