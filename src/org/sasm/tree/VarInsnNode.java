package org.sasm.tree;

import org.sasm.MethodVisitor;

import java.util.Map;

/**
 * A node that represents a local variable instruction. A local variable
 * instruction is an instruction that loads or stores the value of a local
 * variable.
 * 
 * @author Eric Bruneton
 */
public class VarInsnNode extends AbstractInsnNode {

    /**
     * The val of this instruction. This val is the index of a local
     * variable.
     */
    public int var;

    /**
     * Constructs a new {@link VarInsnNode}.
     * 
     * @param opcode
     *            the opcode of the local variable instruction to be
     *            constructed. This opcode must be ILOAD, LLOAD, FLOAD, DLOAD,
     *            ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
     * @param var
     *            the val of the instruction to be constructed. This val
     *            is the index of a local variable.
     */
    public VarInsnNode(int opcode, int var) {
        super(opcode);
        this.var = var;
    }

    /**
     * Sets the opcode of this instruction.
     * 
     * @param opcode
     *            the new instruction opcode. This opcode must be ILOAD, LLOAD,
     *            FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or
     *            RET.
     */
    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return VAR_INSN;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitVarInsn(opcode, var);
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new VarInsnNode(opcode, var).cloneAnnotations(this);
    }
}