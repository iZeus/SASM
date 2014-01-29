package org.sasm.util.deob.flow;

import org.sasm.*;
import org.sasm.tree.*;

import java.util.*;

/**
 * @author Tyler Sedlar
 */
public class FlowVisitor extends MethodVisitor {

    public final MethodNode mn;
    public final List<Block> blocks = new ArrayList<>();

    private Block current = new Block(new Label());

	/**
	 * Fixes the control flow of the given method.
	 *
	 * @param mn The method in which control flow will be fixed.
	 */
    public FlowVisitor(MethodNode mn) {
        super(Opcodes.ASM5);
        this.blocks.add(current);
        (this.mn = mn).accept(this);
        mn.instructions.clear();
        for (Block block : blocks) {
            for (AbstractInsnNode ain : block.instructions) mn.instructions.add(ain);
        }
    }

    private Block[] getBlocks(Label[] labels) {
        Block[] blocks = new Block[labels.length];
        for (int i = 0; i < labels.length; i++) blocks[i] = getBlock(labels[i]);
        return blocks;
    }

	/**
	 * Gets the block for the given label.
	 *
	 * @param label The label to get a block from.
	 * @return The given label's block.
	 */
    public Block getBlock(Label label) {
        return getBlock(label, true);
    }

	/**
	 * Gets the block for the given label.
	 *
	 * @param label The label to get a block from.
	 * @param add <t>true</t> to add the block to the next preds, otherwise <t>false.</t>
	 * @return A block for the given label.
	 */
    public Block getBlock(Label label, boolean add) {
        if (!(label.info instanceof Block)) {
            label.info = new Block(label);
            if (add) {
                current.next = ((Block) label.info);
                current.next.preds.add(current.next);
            }
            blocks.add((Block) label.info);
        }
        return (Block) label.info;
    }

    public void visitCode() {}

    public void visitInsn(int opcode) {
        current.instructions.add(new InsnNode(opcode));
        switch (opcode) {
            case Opcodes.RETURN:
            case Opcodes.IRETURN:
            case Opcodes.ARETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.LRETURN:
            case Opcodes.ATHROW: {
                current = getBlock(new Label(), false);
                break;
            }
            default: {
                break;
            }
        }
    }

    public void visitIntInsn(int opcode, int operand) {
        current.instructions.add(new IntInsnNode(opcode, operand));
    }

    public void visitVarInsn(int opcode, int var) {
        current.instructions.add(new VarInsnNode(opcode, var));
    }

    public void visitTypeInsn(int opcode, String type) {
        current.instructions.add(new TypeInsnNode(opcode, type));
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        current.instructions.add(new FieldInsnNode(opcode, owner, name, desc));
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        current.instructions.add(new MethodInsnNode(opcode, owner, name, desc));
    }

    public void visitInvokeDynamicInsn(String name, String desc, Handle handle, Object... args) {
        current.instructions.add(new InvokeDynamicInsnNode(name, desc, handle, args));
    }

    public void visitJumpInsn(int opcode, Label label) {
        Block block = getBlock(label);
        current.target = block;
        current.target.preds.add(current.target);
        if (opcode != Opcodes.GOTO) current.instructions.add(new JumpInsnNode(opcode, block));
        Stack<AbstractInsnNode> stack = current.stack;
        current = getBlock(new Label(), opcode != Opcodes.GOTO);
        current.stack = stack;
    }

    public void visitLabel(Label label) {
        if (label == null || label.info == null) return;
        Stack<AbstractInsnNode> stack = current == null ? new Stack<AbstractInsnNode>() : current.stack;
        current = getBlock(label);
        current.stack = stack;
    }

    public void visitLdcInsn(Object cst) {
        current.instructions.add(new LdcInsnNode(cst));
    }

    public void visitIincInsn(int var, int increment) {
        current.instructions.add(new IincInsnNode(var, increment));
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        current.instructions.add(new TableSwitchInsnNode(min, max, getBlock(dflt), getBlocks(labels)));
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        current.instructions.add(new LookupSwitchInsnNode(getBlock(dflt), keys, getBlocks(labels)));
    }

    public void visitMultiANewArrayInsn(String desc, int dims) {
        current.instructions.add(new MultiANewArrayInsnNode(desc, dims));
    }

    public void visitEnd() {
        List<Block> empty = new ArrayList<>();
        for (Block block : blocks) {
            block.owner = mn;
            if (block.isEmpty()) empty.add(block);
        }
        blocks.removeAll(empty);
        Collections.sort(blocks, new Comparator<Block>() {
            public int compare(Block b1, Block b2) {
                return mn.instructions.indexOf(new LabelNode(b1.label)) - mn.instructions.indexOf(new LabelNode(b2.label));
            }
        });
    }
}
