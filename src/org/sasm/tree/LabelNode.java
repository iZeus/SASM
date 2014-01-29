package org.sasm.tree;

import org.sasm.Label;
import org.sasm.MethodVisitor;

import java.util.Map;

/**
 * An {@link AbstractInsnNode} that encapsulates a {@link org.sasm.Label}.
 */
public class LabelNode extends AbstractInsnNode {

    private Label label;

    public LabelNode() {
        super(-1);
    }

    public LabelNode(final Label label) {
        super(-1);
        this.label = label;
    }

    @Override
    public int getType() {
        return LABEL;
    }

    public Label getLabel() {
        if (label == null) {
            label = new Label();
        }
        return label;
    }

    @Override
    public void accept(final MethodVisitor cv) {
        cv.visitLabel(getLabel());
    }

    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> labels) {
        return labels.get(this);
    }

    public void resetLabel() {
        label = null;
    }
}