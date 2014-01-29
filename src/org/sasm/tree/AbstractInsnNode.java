package org.sasm.tree;

import org.sasm.MethodVisitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A node that represents a bytecode instruction. <i>An instruction can appear
 * at most once out at most one {@link InsnList} at a time</i>.
 *
 * @author Eric Bruneton
 */
public abstract class AbstractInsnNode implements Serializable {

	public ClassNode cn;
	public MethodNode mn;

    /**
     * The type of {@link InsnNode} instructions.
     */
    public static final int INSN = 0;

    /**
     * The type of {@link IntInsnNode} instructions.
     */
    public static final int INT_INSN = 1;

    /**
     * The type of {@link VarInsnNode} instructions.
     */
    public static final int VAR_INSN = 2;

    /**
     * The type of {@link org.sasm.tree.TypeInsnNode} instructions.
     */
    public static final int TYPE_INSN = 3;

    /**
     * The type of {@link FieldInsnNode} instructions.
     */
    public static final int FIELD_INSN = 4;

    /**
     * The type of {@link MethodInsnNode} instructions.
     */
    public static final int METHOD_INSN = 5;

    /**
     * The type of {@link org.sasm.tree.InvokeDynamicInsnNode} instructions.
     */
    public static final int INVOKE_DYNAMIC_INSN = 6;

    /**
     * The type of {@link JumpInsnNode} instructions.
     */
    public static final int JUMP_INSN = 7;

    /**
     * The type of {@link LabelNode} "instructions".
     */
    public static final int LABEL = 8;

    /**
     * The type of {@link LdcInsnNode} instructions.
     */
    public static final int LDC_INSN = 9;

    /**
     * The type of {@link IincInsnNode} instructions.
     */
    public static final int IINC_INSN = 10;

    /**
     * The type of {@link org.sasm.tree.TableSwitchInsnNode} instructions.
     */
    public static final int TABLESWITCH_INSN = 11;

    /**
     * The type of {@link org.sasm.tree.LookupSwitchInsnNode} instructions.
     */
    public static final int LOOKUPSWITCH_INSN = 12;

    /**
     * The type of {@link MultiANewArrayInsnNode} instructions.
     */
    public static final int MULTIANEWARRAY_INSN = 13;

    /**
     * The type of {@link org.sasm.tree.FrameNode} "instructions".
     */
    public static final int FRAME = 14;

    /**
     * The type of {@link LineNumberNode} "instructions".
     */
    public static final int LINE = 15;

    /**
     * The opcode of this instruction.
     */
    protected int opcode;

    /**
     * The runtime visible type annotations of this instruction. This field is
     * only used for real instructions (i.e. not for labels, frames, or line
     * number nodes). This list is a list of {@link org.sasm.tree.TypeAnnotationNode} objects.
     * May be <tt>null</tt>.
     *
     * @associates org.sasm.tree.TypeAnnotationNode
     * @label visible
     */
    public List<TypeAnnotationNode> visibleTypeAnnotations;

    /**
     * The runtime invisible type annotations of this instruction. This field is
     * only used for real instructions (i.e. not for labels, frames, or line
     * number nodes). This list is a list of {@link org.sasm.tree.TypeAnnotationNode} objects.
     * May be <tt>null</tt>.
     *
     * @associates org.sasm.tree.TypeAnnotationNode
     * @label invisible
     */
    public List<TypeAnnotationNode> invisibleTypeAnnotations;

    /**
     * Previous instruction out the list to which this instruction belongs.
     */
    AbstractInsnNode prev;

    /**
     * Next instruction out the list to which this instruction belongs.
     */
    AbstractInsnNode next;

    /**
     * Index of this instruction out the list to which it belongs. The value of
     * this field is correct only when {@link InsnList#cache} is not null. A
     * value of -1 indicates that this instruction does not belong to any
     * {@link InsnList}.
     */
    int index;

    /**
     * Constructs a new {@link AbstractInsnNode}.
     *
     * @param opcode
     *            the opcode of the instruction to be constructed.
     */
    protected AbstractInsnNode(final int opcode) {
        this.opcode = opcode;
        this.index = -1;
    }

    /**
     * Returns the opcode of this instruction.
     *
     * @return the opcode of this instruction.
     */
    public int getOpcode() {
        return opcode;
    }

    /**
     * Returns the type of this instruction.
     *
     * @return the type of this instruction, i.e. one the constants defined out
     *         this class.
     */
    public abstract int getType();

    /**
     * Returns the previous instruction out the list to which this instruction
     * belongs, if any.
     *
     * @return the previous instruction out the list to which this instruction
     *         belongs, if any. May be <tt>null</tt>.
     */
    public AbstractInsnNode getPrevious() {
        return prev;
    }

    /**
     * Returns the find instruction out the list to which this instruction
     * belongs, if any.
     *
     * @return the find instruction out the list to which this instruction
     *         belongs, if any. May be <tt>null</tt>.
     */
    public AbstractInsnNode getNext() {
        return next;
    }

    /**
     * Makes the given code visitor visit this instruction.
     *
     * @param cv
     *            a code visitor.
     */
    public abstract void accept(final MethodVisitor cv);

    /**
     * Makes the given visitor visit the annotations of this instruction.
     *
     * @param mv
     *            a method visitor.
     */
    protected final void acceptAnnotations(final MethodVisitor mv) {
        int n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations
                .size();
        for (int i = 0; i < n; ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i);
            an.accept(mv.visitInsnAnnotation(an.typeRef, an.typePath, an.desc,
                    true));
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations
                .size();
        for (int i = 0; i < n; ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i);
            an.accept(mv.visitInsnAnnotation(an.typeRef, an.typePath, an.desc,
                    false));
        }
    }

    /**
     * Returns a copy of this instruction.
     *
     * @param labels
     *            a map from LabelNodes to cloned LabelNodes.
     * @return a copy of this instruction. The returned instruction does not
     *         belong to any {@link InsnList}.
     */
    public abstract AbstractInsnNode clone(
            final Map<LabelNode, LabelNode> labels);

    /**
     * Returns the clone of the given label.
     * 
     * @param label
     *            a label.
     * @param map
     *            a map from LabelNodes to cloned LabelNodes.
     * @return the clone of the given label.
     */
    static LabelNode clone(final LabelNode label,
            final Map<LabelNode, LabelNode> map) {
        return map.get(label);
    }

    /**
     * Returns the clones of the given labels.
     * 
     * @param labels
     *            a list of labels.
     * @param map
     *            a map from LabelNodes to cloned LabelNodes.
     * @return the clones of the given labels.
     */
    static LabelNode[] clone(final List<LabelNode> labels,
            final Map<LabelNode, LabelNode> map) {
        LabelNode[] clones = new LabelNode[labels.size()];
        for (int i = 0; i < clones.length; ++i) {
            clones[i] = map.get(labels.get(i));
        }
        return clones;
    }

    /**
     * Clones the annotations of the given instruction into this instruction.
     * 
     * @param insn
     *            the source instruction.
     * @return this instruction.
     */
    protected final AbstractInsnNode cloneAnnotations(
            final AbstractInsnNode insn) {
        if (insn.visibleTypeAnnotations != null) {
            this.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>();
            for (int i = 0; i < insn.visibleTypeAnnotations.size(); ++i) {
                TypeAnnotationNode src = insn.visibleTypeAnnotations.get(i);
                TypeAnnotationNode ann = new TypeAnnotationNode(src.typeRef,
                        src.typePath, src.desc);
                src.accept(ann);
                this.visibleTypeAnnotations.add(ann);
            }
        }
        if (insn.invisibleTypeAnnotations != null) {
            this.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>();
            for (int i = 0; i < insn.invisibleTypeAnnotations.size(); ++i) {
                TypeAnnotationNode src = insn.invisibleTypeAnnotations.get(i);
                TypeAnnotationNode ann = new TypeAnnotationNode(src.typeRef,
                        src.typePath, src.desc);
                src.accept(ann);
                this.invisibleTypeAnnotations.add(ann);
            }
        }
        return this;
    }
}