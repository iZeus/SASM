package org.sasm.tree;

import org.sasm.Opcodes;
import org.sasm.TypePath;

/**
 * A node that represents a type annotationn.
 * 
 * @author Eric Bruneton
 */
public class TypeAnnotationNode extends AnnotationNode {

    /**
     * A reference to the annotated type. See {@link org.sasm.TypeReference}.
     */
    public int typeRef;

    /**
     * The path to the annotated type argument, wildcard bound, array element
     * type, or static outer type within the referenced type. May be
     * <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     */
    public TypePath typePath;

    /**
     * Constructs a new {@link org.sasm.tree.AnnotationNode}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the
     * {@link #TypeAnnotationNode(int, int, org.sasm.TypePath, String)} version.
     *
     * @param typeRef
     *            a reference to the annotated type. See {@link org.sasm.TypeReference}.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param desc
     *            the class descriptor of the annotation class.
     */
    public TypeAnnotationNode(final int typeRef, final TypePath typePath, final String desc) {
        this(Opcodes.ASM5, typeRef, typePath, desc);
    }

    /**
     * Constructs a new {@link org.sasm.tree.AnnotationNode}.
     *
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link org.sasm.Opcodes#ASM4} or {@link org.sasm.Opcodes#ASM5}.
     * @param typeRef
     *            a reference to the annotated type. See {@link org.sasm.TypeReference}.
     * @param typePath
     *            the path to the annotated type argument, wildcard bound, array
     *            element type, or static inner type within 'typeRef'. May be
     *            <tt>null</tt> if the annotation targets 'typeRef' as a whole.
     * @param desc
     *            the class descriptor of the annotation class.
     */
    public TypeAnnotationNode(final int api, final int typeRef, final TypePath typePath, final String desc) {
        super(api, desc);
        this.typeRef = typeRef;
        this.typePath = typePath;
    }
}
