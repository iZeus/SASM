package org.sasm.tree;

import org.sasm.MethodVisitor;
import org.sasm.util.deob.flow.Block;

import java.util.Map;

/**
 * A node that represents a jump instruction. A jump instruction is an
 * instruction that may jump to another instruction.
 * 
 * @author Eric Bruneton
 */
public class JumpInsnNode extends AbstractInsnNode {

    /**
     * The val of this instruction. This val is a label that designates
     * the instruction to which this instruction may jump.
     */
    public LabelNode label;

    public Block block;

    public JumpInsnNode(int opcode, LabelNode label) {
        super(opcode);
        this.label = label;
    }

    public JumpInsnNode(int opcode, Block block) {
        this(opcode, new LabelNode(block.label));
        this.block = block;
    }

    /**
     * Sets the opcode of this instruction.
     * 
     * @param opcode
     *            the new instruction opcode. This opcode must be IFEQ, IFNE,
     *            IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT,
     *            IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO,
     *            JSR, IFNULL or IFNONNULL.
     */
    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return JUMP_INSN;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitJumpInsn(opcode, label.getLabel());
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new JumpInsnNode(opcode, clone(label, labels)).cloneAnnotations(this);
    }
}
