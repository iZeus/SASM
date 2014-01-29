package org.sasm.tree;

import org.sasm.Handle;
import org.sasm.MethodVisitor;
import org.sasm.Opcodes;

import java.util.Map;

/**
 * A node that represents an invokedynamic instruction.
 * 
 * @author Remi Forax
 */
public class InvokeDynamicInsnNode extends AbstractInsnNode {

    /**
     * Invokedynamic name.
     */
    public String name;

    /**
     * Invokedynamic descriptor.
     */
    public String desc;

    /**
     * Bootstrap method
     */
    public Handle bsm;

    /**
     * Bootstrap constant arguments
     */
    public Object[] bsmArgs;

    /**
     * Constructs a new {@link InvokeDynamicInsnNode}.
     * 
     * @param name
     *            invokedynamic name.
     * @param desc
     *            invokedynamic descriptor (see {@link org.sasm.Type}).
     * @param bsm
     *            the bootstrap method.
     * @param bsmArgs
     *            the boostrap constant arguments.
     */
    public InvokeDynamicInsnNode(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
        super(Opcodes.INVOKEDYNAMIC);
        this.name = name;
        this.desc = desc;
        this.bsm = bsm;
        this.bsmArgs = bsmArgs;
    }

    @Override
    public int getType() {
        return INVOKE_DYNAMIC_INSN;
    }

    @Override
    public void accept(final MethodVisitor mv) {
        mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> labels) {
        return new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs).cloneAnnotations(this);
    }
}