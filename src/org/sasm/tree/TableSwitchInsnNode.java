package org.sasm.tree;

import org.sasm.Label;
import org.sasm.MethodVisitor;
import org.sasm.Opcodes;
import org.sasm.util.deob.flow.Block;

import java.util.*;

/**
 * A node that represents a TABLESWITCH instruction.
 * 
 * @author Eric Bruneton
 */
public class TableSwitchInsnNode extends AbstractInsnNode {

    /**
     * The minimum key value.
     */
    public int min;

    /**
     * The maximum key value.
     */
    public int max;

    /**
     * Beginning of the default handler block.
     */
    public LabelNode dflt;

    /**
     * Beginnings of the handler block. This list is a list of
     * {@link org.sasm.tree.LabelNode} objects.
     */
    public List<LabelNode> labels;

    public Block dfltBlock;
    public Block[] blocks;

    /**
     * Constructs a new {@link TableSwitchInsnNode}.
     * 
     * @param min
     *            the minimum key value.
     * @param max
     *            the maximum key value.
     * @param dflt
     *            beginning of the default handler block.
     * @param labels
     *            beginnings of the handler block. <tt>labels[i]</tt> is the
     *            beginning of the handler block for the <tt>min + i</tt> key.
     */
    public TableSwitchInsnNode(final int min, final int max, final LabelNode dflt, final LabelNode... labels) {
        super(Opcodes.TABLESWITCH);
        this.min = min;
        this.max = max;
        this.dflt = dflt;
        this.labels = new ArrayList<>();
        if (labels != null) {
            this.labels.addAll(Arrays.asList(labels));
        }
    }

    public TableSwitchInsnNode(int min, int max, Block dflt, Block... blocks) {
        this(min, max, new LabelNode(dflt.label));
        this.dfltBlock = dflt;
        this.blocks = blocks;
        for (Block b : blocks) {
            this.labels.add(new LabelNode(b.label));
        }
    }

    @Override
    public int getType() {
        return TABLESWITCH_INSN;
    }

    @Override
    public void accept(final MethodVisitor mv) {
        Label[] labels = new Label[this.labels.size()];
        for (int i = 0; i < labels.length; ++i) {
            labels[i] = this.labels.get(i).getLabel();
        }
        mv.visitTableSwitchInsn(min, max, dflt.getLabel(), labels);
        acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> labels) {
        return new TableSwitchInsnNode(min, max, clone(dflt, labels), clone(this.labels, labels)).cloneAnnotations(this);
    }
}