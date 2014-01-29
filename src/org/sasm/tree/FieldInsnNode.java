package org.sasm.tree;

import org.sasm.MethodVisitor;

import java.util.Map;

/**
 * A node that represents a field instruction. A field instruction is an
 * instruction that loads or stores the value of a field of an object.
 * 
 * @author Eric Bruneton
 */
public class FieldInsnNode extends AbstractInsnNode {

    /**
     * The internal name of the field's owner class (see
     * {@link org.sasm.Type#getInternalName() getInternalName}).
     */
    public String owner;

    /**
     * The field's name.
     */
    public String name;

    /**
     * The field's descriptor (see {@link org.sasm.Type}).
     */
    public String desc;

    /**
     * Constructs a new {@link FieldInsnNode}.
     * 
     * @param opcode
     *            the opcode of the type instruction to be constructed. This
     *            opcode must be GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     * @param owner
     *            the internal name of the field's owner class (see
     *            {@link org.sasm.Type#getInternalName()
     *            getInternalName}).
     * @param name
     *            the field's name.
     * @param desc
     *            the field's descriptor (see {@link org.sasm.Type}).
     */
    public FieldInsnNode(int opcode, String owner, String name, String desc) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    /**
     * Sets the opcode of this instruction.
     * 
     * @param opcode
     *            the new instruction opcode. This opcode must be GETSTATIC,
     *            PUTSTATIC, GETFIELD or PUTFIELD.
     */
    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return FIELD_INSN;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitFieldInsn(opcode, owner, name, desc);
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new FieldInsnNode(opcode, owner, name, desc).cloneAnnotations(this);
    }
}
