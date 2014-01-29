package org.sasm.util.deob.flow;

import org.sasm.Label;
import org.sasm.tree.AbstractInsnNode;
import org.sasm.tree.LabelNode;
import org.sasm.tree.MethodNode;

import java.util.*;

/**
 * @author Tyler Sedlar
 */
public class Block {

    public MethodNode owner = null;

    public final Label label;
    public final List<AbstractInsnNode> instructions = new LinkedList<>();
    public final List<Block> preds = new ArrayList<>();

    public Block next, target;

    public Stack<AbstractInsnNode> stack = new Stack<>();

	/**
	 * Constructs a block for the given label.
	 *
	 * @param label The label in which to create a block from.
	 */
    public Block(Label label) {
        this.label = label;
        this.instructions.add(new LabelNode(label));
    }

	/**
	 * Checks if the block is empty.
	 *
	 * @return <t>true</t> if the block is empty, otherwise <t>false.</t>
	 */
    public boolean isEmpty() {
        return preds.isEmpty() && instructions.size() <= 1;
    }
}