package org.sasm.tree;

import org.sasm.Label;
import org.sasm.MethodVisitor;
import org.sasm.Opcodes;
import org.sasm.util.deob.flow.Block;

import java.util.*;

/**
 * A node that represents a LOOKUPSWITCH instruction.
 * 
 * @author Eric Bruneton
 */
public class LookupSwitchInsnNode extends AbstractInsnNode {

    /**
     * Beginning of the default handler block.
     */
    public LabelNode dflt;

    /**
     * The values of the keys. This list is a list of {@link Integer} objects.
     */
    public List<Integer> keys;

    /**
     * Beginnings of the handler block. This list is a list of
     * {@link org.sasm.tree.LabelNode} objects.
     */
    public List<LabelNode> labels;

    public Block dfltBlock;
    public Block[] blocks;

    /**
     * Constructs a new {@link LookupSwitchInsnNode}.
     * 
     * @param dflt
     *            beginning of the default handler block.
     * @param keys
     *            the values of the keys.
     * @param labels
     *            beginnings of the handler block. <tt>labels[i]</tt> is the
     *            beginning of the handler block for the <tt>keys[i]</tt> key.
     */
    public LookupSwitchInsnNode(final LabelNode dflt, final int[] keys, final LabelNode[] labels) {
        super(Opcodes.LOOKUPSWITCH);
        this.dflt = dflt;
        this.keys = new ArrayList<>(keys == null ? 0 : keys.length);
        this.labels = new ArrayList<>(labels == null ? 0 : labels.length);
        if (keys != null) {
	        for (int key : keys) {
		        this.keys.add(key);
	        }
        }
        if (labels != null) {
            this.labels.addAll(Arrays.asList(labels));
        }
    }

    public LookupSwitchInsnNode(Block dflt, int[] keys, Block[] blocks) {
        this(new LabelNode(dflt.label), keys, null);
        this.dfltBlock = dflt;
        this.blocks = blocks;
        for (Block b : blocks) this.labels.add(new LabelNode(b.label));
    }

    @Override
    public int getType() {
        return LOOKUPSWITCH_INSN;
    }

    @Override
    public void accept(final MethodVisitor mv) {
        int[] keys = new int[this.keys.size()];
        for (int i = 0; i < keys.length; ++i) {
            keys[i] = this.keys.get(i);
        }
        Label[] labels = new Label[this.labels.size()];
        for (int i = 0; i < labels.length; ++i) {
            labels[i] = this.labels.get(i).getLabel();
        }
        mv.visitLookupSwitchInsn(dflt.getLabel(), keys, labels);
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> labels) {
        LookupSwitchInsnNode clone = new LookupSwitchInsnNode(clone(dflt,
                labels), null, clone(this.labels, labels));
        clone.keys.addAll(keys);
        return clone.cloneAnnotations(this);
    }
}
