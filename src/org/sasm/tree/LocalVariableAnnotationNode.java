package org.sasm.tree;

import org.sasm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A node that represents a type annotation on a local or resource variable.
 * 
 * @author Eric Bruneton
 */
public class LocalVariableAnnotationNode extends TypeAnnotationNode {

    /**
     * The fist instructions corresponding to the continuous ranges that make
     * the scope of this local variable (inclusive). Must not be <tt>null</tt>.
     */
    public List<LabelNode> start;

    /**
     * The last instructions corresponding to the continuous ranges that make
     * the scope of this local variable (exclusive). This list must have the
     * same size as the 'start' list. Must not be <tt>null</tt>.
     */
    public List<LabelNode> end;

    /**
     * The local variable's index out each range. This list must have the same
     * size as the 'start' list. Must not be <tt>null</tt>.
     */
    public List<Integer> index;

    /**
     * Constructs a new {@link LocalVariableAnnotationNode}. <i>Subclasses must
     * not use this constructor</i>. Instead, they must use the
     * {@link #LocalVariableAnnotationNode(int, org.sasm.TypePath, org.sasm.tree.LabelNode[], org.sasm.tree.LabelNode[], int[], String)}
     * version.
     *
     * @param typeRef
     *            a reference to the annotated type. See {@link org.sasm.TypeReference}.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param start
     *            the fist instructions corresponding to the continuous ranges
     *            that make the scope of this local variable (inclusive).
     * @param end
     *            the last instructions corresponding to the continuous ranges
     *            that make the scope of this local variable (exclusive). This
     *            array must have the same size as the 'start' array.
     * @param index
     *            the local variable's index out each range. This array must have
     *            the same size as the 'start' array.
     * @param desc
     *            the class descriptor of the annotation class.
     */
    public LocalVariableAnnotationNode(int typeRef, TypePath typePath, LabelNode[] start, LabelNode[] end, int[] index,
                                       String desc) {
        this(Opcodes.ASM5, typeRef, typePath, start, end, index, desc);
    }

    /**
     * Constructs a new {@link LocalVariableAnnotationNode}.
     *
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link org.sasm.Opcodes#ASM4} or {@link org.sasm.Opcodes#ASM5}.
     * @param typeRef
     *            a reference to the annotated type. See {@link org.sasm.TypeReference}.
     * @param start
     *            the fist instructions corresponding to the continuous ranges
     *            that make the scope of this local variable (inclusive).
     * @param end
     *            the last instructions corresponding to the continuous ranges
     *            that make the scope of this local variable (exclusive). This
     *            array must have the same size as the 'start' array.
     * @param index
     *            the local variable's index out each range. This array must have
     *            the same size as the 'start' array.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param desc
     *            the class descriptor of the annotation class.
     */
    public LocalVariableAnnotationNode(int api, int typeRef, TypePath typePath, LabelNode[] start, LabelNode[] end,
                                       int[] index, String desc) {
        super(api, typeRef, typePath, desc);
        this.start = new ArrayList<>(start.length);
        this.start.addAll(Arrays.asList(start));
        this.end = new ArrayList<>(end.length);
        this.end.addAll(Arrays.asList(end));
        this.index = new ArrayList<>(index.length);
        for (int i : index) {
            this.index.add(i);
        }
    }

    /**
     * Makes the given visitor visit this type annotation.
     *
     * @param mv the visitor that must visit this annotation.
     */
    public void accept(MethodVisitor mv) {
        Label[] start = new Label[this.start.size()];
        Label[] end = new Label[this.end.size()];
        int[] index = new int[this.index.size()];
        for (int i = 0; i < start.length; ++i) {
            start[i] = this.start.get(i).getLabel();
            end[i] = this.end.get(i).getLabel();
            index[i] = this.index.get(i);
        }
        accept(mv.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, true));
    }
}
