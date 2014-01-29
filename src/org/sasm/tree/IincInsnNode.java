package org.sasm.tree;

import org.sasm.MethodVisitor;
import org.sasm.Opcodes;

import java.util.Map;

/**
 * A node that represents an IINC instruction.
 * 
 * @author Eric Bruneton
 */
public class IincInsnNode extends AbstractInsnNode {

    /**
     * Index of the local variable to be incremented.
     */
    public int var;

    /**
     * Amount to increment the local variable by.
     */
    public int incr;

    /**
     * Constructs a new {@link IincInsnNode}.
     * 
     * @param var
     *            index of the local variable to be incremented.
     * @param incr
     *            increment amount to increment the local variable by.
     */
    public IincInsnNode(int var, int incr) {
        super(Opcodes.IINC);
        this.var = var;
        this.incr = incr;
    }

    @Override
    public int getType() {
        return IINC_INSN;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitIincInsn(var, incr);
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new IincInsnNode(var, incr).cloneAnnotations(this);
    }
}