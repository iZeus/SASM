package org.sasm.tree;

import org.sasm.MethodVisitor;
import org.sasm.Opcodes;

import java.util.*;

/**
 * A node that represents a stack map frame. These nodes are pseudo instruction
 * nodes out order to be inserted out an instruction list. In fact these nodes
 * must(*) be inserted <i>just before</i> any instruction node <b>i</b> that
 * follows an unconditionnal branch instruction such as GOTO or THROW, that is
 * the target of a jump instruction, or that starts an exception handler block.
 * The stack map frame types must describe the values of the local variables and
 * of the val stack elements <i>just before</i> <b>i</b> is executed. <br>
 * <br>
 * (*) this is mandatory only for classes whose version is greater than or equal
 * to {@link org.sasm.Opcodes#V1_6 V1_6}.
 *
 * @author Eric Bruneton
 */
public class FrameNode extends AbstractInsnNode {

    /**
     * The type of this frame. Must be {@link org.sasm.Opcodes#F_NEW} for expanded
     * frames, or {@link org.sasm.Opcodes#F_FULL}, {@link org.sasm.Opcodes#F_APPEND},
     * {@link org.sasm.Opcodes#F_CHOP}, {@link org.sasm.Opcodes#F_SAME} or
     * {@link org.sasm.Opcodes#F_APPEND}, {@link org.sasm.Opcodes#F_SAME1} for compressed frames.
     */
    public int type;

    /**
     * The types of the local variables of this stack map frame. Elements of
     * this list can be Integer, String or LabelNode objects (for primitive,
     * reference and uninitialized types respectively - see
     * {@link org.sasm.MethodVisitor}).
     */
    public List<Object> local;

    /**
     * The types of the val stack elements of this stack map frame. Elements
     * of this list can be Integer, String or LabelNode objects (for primitive,
     * reference and uninitialized types respectively - see
     * {@link org.sasm.MethodVisitor}).
     */
    public List<Object> stack;

    private FrameNode() {
        super(-1);
    }

    /**
     * Constructs a new {@link FrameNode}.
     *
     * @param type
     *            the type of this frame. Must be {@link org.sasm.Opcodes#F_NEW} for
     *            expanded frames, or {@link org.sasm.Opcodes#F_FULL},
     *            {@link org.sasm.Opcodes#F_APPEND}, {@link org.sasm.Opcodes#F_CHOP},
     *            {@link org.sasm.Opcodes#F_SAME} or {@link org.sasm.Opcodes#F_APPEND},
     *            {@link org.sasm.Opcodes#F_SAME1} for compressed frames.
     * @param nLocal
     *            number of local variables of this stack map frame.
     * @param local
     *            the types of the local variables of this stack map frame.
     *            Elements of this list can be Integer, String or LabelNode
     *            objects (for primitive, reference and uninitialized types
     *            respectively - see {@link org.sasm.MethodVisitor}).
     * @param nStack
     *            number of val stack elements of this stack map frame.
     * @param stack
     *            the types of the val stack elements of this stack map
     *            frame. Elements of this list can be Integer, String or
     *            LabelNode objects (for primitive, reference and uninitialized
     *            types respectively - see {@link org.sasm.MethodVisitor}).
     */
    public FrameNode(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        super(-1);
        this.type = type;
        switch (type) {
        case Opcodes.F_NEW:
        case Opcodes.F_FULL:
            this.local = asList(nLocal, local);
            this.stack = asList(nStack, stack);
            break;
        case Opcodes.F_APPEND:
            this.local = asList(nLocal, local);
            break;
        case Opcodes.F_CHOP:
            this.local = Arrays.asList(new Object[nLocal]);
            break;
        case Opcodes.F_SAME:
            break;
        case Opcodes.F_SAME1:
            this.stack = asList(1, stack);
            break;
        }
    }

    @Override
    public int getType() {
        return FRAME;
    }

    /**
     * Makes the given visitor visit this stack map frame.
     * 
     * @param mv
     *            a method visitor.
     */
    @Override
    public void accept(MethodVisitor mv) {
        switch (type) {
        case Opcodes.F_NEW:
        case Opcodes.F_FULL:
            mv.visitFrame(type, local.size(), asArray(local), stack.size(), asArray(stack));
            break;
        case Opcodes.F_APPEND:
            mv.visitFrame(type, local.size(), asArray(local), 0, null);
            break;
        case Opcodes.F_CHOP:
            mv.visitFrame(type, local.size(), null, 0, null);
            break;
        case Opcodes.F_SAME:
            mv.visitFrame(type, 0, null, 0, null);
            break;
        case Opcodes.F_SAME1:
            mv.visitFrame(type, 0, null, 1, asArray(stack));
            break;
        }
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        FrameNode clone = new FrameNode();
        clone.type = type;
        if (local != null) {
            clone.local = new ArrayList<>();
            for (int i = 0; i < local.size(); ++i) {
                Object l = local.get(i);
                if (l instanceof LabelNode) {
                    l = labels.get(l);
                }
                clone.local.add(l);
            }
        }
        if (stack != null) {
            clone.stack = new ArrayList<>();
            for (int i = 0; i < stack.size(); ++i) {
                Object s = stack.get(i);
                if (s instanceof LabelNode) {
                    s = labels.get(s);
                }
                clone.stack.add(s);
            }
        }
        return clone;
    }

    // ------------------------------------------------------------------------

    private static List<Object> asList(int n, Object[] o) {
        return Arrays.asList(o).subList(0, n);
    }

    private static Object[] asArray(List<Object> l) {
        Object[] objs = new Object[l.size()];
        for (int i = 0; i < objs.length; ++i) {
            Object o = l.get(i);
            if (o instanceof LabelNode) {
                o = ((LabelNode) o).getLabel();
            }
            objs[i] = o;
        }
        return objs;
    }
}
