package org.sasm.tree;

import org.sasm.*;

import java.util.*;

/**
 * A node that represents a method.
 * 
 * @author Eric Bruneton
 * @author Tyler Sedlar
 */
public class MethodNode extends MethodVisitor {

	public ClassNode owner;

    /**
     * The method's access flags (see {@link org.sasm.Opcodes}). This field also
     * indicates if the method is synthetic and/or deprecated.
     */
    public int access;

    /**
     * The method's name.
     */
    public String name;

    /**
     * The method's descriptor (see {@link org.sasm.Type}).
     */
    public String desc;

    /**
     * The method's signature. May be <tt>null</tt>.
     */
    public String signature;

    /**
     * The internal names of the method's exception classes (see
     * {@link org.sasm.Type#getInternalName() getInternalName}). This list is a list of
     * {@link String} objects.
     */
    public List<String> exceptions;

    /**
     * The method parameter info (access flags and name)
     */
    public List<ParameterNode> parameters;

    /**
     * The runtime visible annotations of this method. This list is a list of
     * {@link org.sasm.tree.AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.sasm.tree.AnnotationNode
     * @label visible
     */
    public List<AnnotationNode> visibleAnnotations;

    /**
     * The runtime invisible annotations of this method. This list is a list of
     * {@link org.sasm.tree.AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.sasm.tree.AnnotationNode
     * @label invisible
     */
    public List<AnnotationNode> invisibleAnnotations;

    /**
     * The runtime visible type annotations of this method. This list is a list
     * of {@link org.sasm.tree.TypeAnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.sasm.tree.TypeAnnotationNode
     * @label visible
     */
    public List<TypeAnnotationNode> visibleTypeAnnotations;

    /**
     * The runtime invisible type annotations of this method. This list is a
     * list of {@link org.sasm.tree.TypeAnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.sasm.tree.TypeAnnotationNode
     * @label invisible
     */
    public List<TypeAnnotationNode> invisibleTypeAnnotations;

    /**
     * The non standard attributes of this method. This list is a list of
     * {@link org.sasm.Attribute} objects. May be <tt>null</tt>.
     *
     * @associates org.sasm.Attribute
     */
    public List<Attribute> attrs;

    /**
     * The default value of this annotation interface method. This field must be
     * a {@link Byte}, {@link Boolean}, {@link Character}, {@link Short},
     * {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     * {@link String} or {@link org.sasm.Type}, or an two elements String array (for
     * enumeration values), a {@link org.sasm.tree.AnnotationNode}, or a {@link java.util.List} of
     * values of one of the preceding types. May be <tt>null</tt>.
     */
    public Object annotationDefault;

    /**
     * The runtime visible parameter annotations of this method. These lists are
     * lists of {@link org.sasm.tree.AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.sasm.tree.AnnotationNode
     * @label invisible parameters
     */
    public List<AnnotationNode>[] visibleParameterAnnotations;

    /**
     * The runtime invisible parameter annotations of this method. These lists
     * are lists of {@link org.sasm.tree.AnnotationNode} objects. May be <tt>null</tt>.
     *
     * @associates org.sasm.tree.AnnotationNode
     * @label visible parameters
     */
    //public List<AnnotationNode>[] invisibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[3];
    public List<AnnotationNode>[] invisibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[3];

    /**
     * The instructions of this method. This list is a list of
     * {@link org.sasm.tree.AbstractInsnNode} objects.
     *
     * @associates org.sasm.tree.AbstractInsnNode
     * @label instructions
     */
    public InsnList instructions;

    /**
     * The try catch block of this method. This list is a list of
     * {@link org.sasm.tree.TryCatchBlockNode} objects.
     *
     * @associates org.sasm.tree.TryCatchBlockNode
     */
    public List<TryCatchBlockNode> tryCatchBlocks;

    /**
     * The maximum stack size of this method.
     */
    public int maxStack;

    /**
     * The maximum number of local variables of this method.
     */
    public int maxLocals;

    /**
     * The local variables of this method. This list is a list of
     * {@link org.sasm.tree.LocalVariableNode} objects. May be <tt>null</tt>
     *
     * @associates org.sasm.tree.LocalVariableNode
     */
    public List<LocalVariableNode> localVariables;

    /**
     * The visible local variable annotations of this method. This list is a
     * list of {@link org.sasm.tree.LocalVariableAnnotationNode} objects. May be <tt>null</tt>
     *
     * @associates org.sasm.tree.LocalVariableAnnotationNode
     */
    public List<LocalVariableAnnotationNode> visibleLocalVariableAnnotations;

    /**
     * The invisible local variable annotations of this method. This list is a
     * list of {@link org.sasm.tree.LocalVariableAnnotationNode} objects. May be <tt>null</tt>
     *
     * @associates org.sasm.tree.LocalVariableAnnotationNode
     */
    public List<LocalVariableAnnotationNode> invisibleLocalVariableAnnotations;

    /**
     * If the accept method has been called on this object.
     */
    private boolean visited;

    /**
     * Constructs an uninitialized {@link MethodNode}. <i>Subclasses must not
     * use this constructor</i>. Instead, they must use the
     * {@link #MethodNode(int)} version.
     */
    public MethodNode() {
        this(Opcodes.ASM5);
    }

    /**
     * Constructs an uninitialized {@link MethodNode}.
     *
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link org.sasm.Opcodes#ASM4} or {@link org.sasm.Opcodes#ASM5}.
     */
    public MethodNode(final int api) {
        super(api);
        this.instructions = new InsnList();
    }

    /**
     * Constructs a new {@link MethodNode}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the
     * {@link #MethodNode(int, int, String, String, String, String[])} version.
     *
     * @param access
     *            the method's access flags (see {@link org.sasm.Opcodes}). This
     *            parameter also indicates if the method is synthetic and/or
     *            deprecated.
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link org.sasm.Type}).
     * @param signature
     *            the method's signature. May be <tt>null</tt>.
     * @param exceptions
     *            the internal names of the method's exception classes (see
     *            {@link org.sasm.Type#getInternalName() getInternalName}). May be
     *            <tt>null</tt>.
     */
    public MethodNode(final int access, final String name, final String desc, final String signature,
                      final String[] exceptions) {
        this(Opcodes.ASM5, access, name, desc, signature, exceptions);
    }

    /**
     * Constructs a new {@link MethodNode}.
     *
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link org.sasm.Opcodes#ASM4} or {@link org.sasm.Opcodes#ASM5}.
     * @param access
     *            the method's access flags (see {@link org.sasm.Opcodes}). This
     *            parameter also indicates if the method is synthetic and/or
     *            deprecated.
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link org.sasm.Type}).
     * @param signature
     *            the method's signature. May be <tt>null</tt>.
     * @param exceptions
     *            the internal names of the method's exception classes (see
     *            {@link org.sasm.Type#getInternalName() getInternalName}). May be
     *            <tt>null</tt>.
     */
    public MethodNode(final int api, final int access, final String name, final String desc, final String signature,
                      final String[] exceptions) {
        super(api);
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = new ArrayList<>(exceptions == null ? 0 : exceptions.length);
        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        if (!isAbstract) {
            this.localVariables = new ArrayList<>(5);
        }
        this.tryCatchBlocks = new ArrayList<>();
        if (exceptions != null) {
            this.exceptions.addAll(Arrays.asList(exceptions));
        }
        this.instructions = new InsnList();
    }

    // ------------------------------------------------------------------------
    // Implementation of the MethodVisitor abstract class
    // ------------------------------------------------------------------------

    @Override
    public void visitParameter(String name, int access) {
        if (parameters == null) {
            parameters = new ArrayList<>(5);
        }
        parameters.add(new ParameterNode(name, access));
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationNode(new ArrayList<Object>(0) {
            @Override
            public boolean add(final Object o) {
                annotationDefault = o;
                return super.add(o);
            }
        });
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        AnnotationNode an = new AnnotationNode(desc);
        if (visible) {
            if (visibleAnnotations == null) {
                visibleAnnotations = new ArrayList<>(1);
            }
            visibleAnnotations.add(an);
        } else {
            if (invisibleAnnotations == null) {
                invisibleAnnotations = new ArrayList<>(1);
            }
            invisibleAnnotations.add(an);
        }
        return an;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (visibleTypeAnnotations == null) {
                visibleTypeAnnotations = new ArrayList<>(1);
            }
            visibleTypeAnnotations.add(an);
        } else {
            if (invisibleTypeAnnotations == null) {
                invisibleTypeAnnotations = new ArrayList<>(1);
            }
            invisibleTypeAnnotations.add(an);
        }
        return an;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
        AnnotationNode an = new AnnotationNode(desc);
        if (visible) {
            if (visibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                visibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
            }
            if (visibleParameterAnnotations[parameter] == null) {
                visibleParameterAnnotations[parameter] = new ArrayList<>(1);
            }
            visibleParameterAnnotations[parameter].add(an);
        } else {
            if (invisibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(this.desc).length;
                invisibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
            }
            if (invisibleParameterAnnotations[parameter] == null) {
                invisibleParameterAnnotations[parameter] = new ArrayList<>(1);
            }
            invisibleParameterAnnotations[parameter].add(an);
        }
        return an;
    }

    @Override
    public void visitAttribute(final Attribute attr) {
        if (attrs == null) {
            attrs = new ArrayList<Attribute>(1);
        }
        attrs.add(attr);
    }

    @Override
    public void visitCode() {
    }

    @Override
    public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {
        FrameNode insn = new FrameNode(type, nLocal, local == null ? null : getLabelNodes(local), nStack,
		        stack == null ? null : getLabelNodes(stack));
	    insn.cn = owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitInsn(final int opcode) {
        InsnNode insn = new InsnNode(opcode);
	    insn.cn = owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        IntInsnNode insn = new IntInsnNode(opcode, operand);
	    insn.cn = owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
        VarInsnNode insn = new VarInsnNode(opcode, var);
	    insn.cn = owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        TypeInsnNode insn = new TypeInsnNode(opcode, type);
	    insn.cn = owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        FieldInsnNode insn = new FieldInsnNode(opcode, owner, name, desc);
	    insn.cn = this.owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        MethodInsnNode insn = new MethodInsnNode(opcode, owner, name, desc);
	    insn.cn = this.owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        InvokeDynamicInsnNode insn = new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs);
	    insn.cn = this.owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        JumpInsnNode insn = new JumpInsnNode(opcode, getLabelNode(label));
	    insn.cn = this.owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitLabel(final Label label) {
        LabelNode insn = getLabelNode(label);
	    insn.cn = this.owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitLdcInsn(final Object cst) {
        LdcInsnNode insn = new LdcInsnNode(cst);
	    insn.cn = this.owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
        IincInsnNode insn = new IincInsnNode(var, increment);
	    insn.cn = owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
	    TableSwitchInsnNode insn = new TableSwitchInsnNode(min, max, getLabelNode(dflt), getLabelNodes(labels));
	    insn.cn = owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        LookupSwitchInsnNode insn = new LookupSwitchInsnNode(getLabelNode(dflt), keys, getLabelNodes(labels));
	    insn.cn = owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
	    MultiANewArrayInsnNode insn = new MultiANewArrayInsnNode(desc, dims);
	    insn.cn = owner;
	    insn.mn = this;
        instructions.add(insn);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        // Finds the last real instruction, i.e. the instruction targeted by
        // this annotation.
        AbstractInsnNode insn = instructions.getLast();
        while (insn.getOpcode() == -1) {
            insn = insn.getPrevious();
        }
        // Adds the annotation to this instruction.
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (insn.visibleTypeAnnotations == null) {
                insn.visibleTypeAnnotations = new ArrayList<>(1);
            }
            insn.visibleTypeAnnotations.add(an);
        } else {
            if (insn.invisibleTypeAnnotations == null) {
                insn.invisibleTypeAnnotations = new ArrayList<>(1);
            }
            insn.invisibleTypeAnnotations.add(an);
        }
        return an;
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        tryCatchBlocks.add(new TryCatchBlockNode(getLabelNode(start), getLabelNode(end), getLabelNode(handler), type));
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        TryCatchBlockNode tcb = tryCatchBlocks.get((typeRef & 0x00FFFF00) >> 8);
        TypeAnnotationNode an = new TypeAnnotationNode(typeRef, typePath, desc);
        if (visible) {
            if (tcb.visibleTypeAnnotations == null) {
                tcb.visibleTypeAnnotations = new ArrayList<>(1);
            }
            tcb.visibleTypeAnnotations.add(an);
        } else {
            if (tcb.invisibleTypeAnnotations == null) {
                tcb.invisibleTypeAnnotations = new ArrayList<>(1);
            }
            tcb.invisibleTypeAnnotations.add(an);
        }
        return an;
    }

    @Override
    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start,
                                   final Label end, final int index) {
        localVariables.add(new LocalVariableNode(name, desc, signature, getLabelNode(start), getLabelNode(end), index));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end,
                                                          int[] index, String desc, boolean visible) {
        LocalVariableAnnotationNode an = new LocalVariableAnnotationNode(typeRef, typePath, getLabelNodes(start),
		        getLabelNodes(end), index, desc);
        if (visible) {
            if (visibleLocalVariableAnnotations == null) {
                visibleLocalVariableAnnotations = new ArrayList<>(1);
            }
            visibleLocalVariableAnnotations.add(an);
        } else {
            if (invisibleLocalVariableAnnotations == null) {
                invisibleLocalVariableAnnotations = new ArrayList<>(1);
            }
            invisibleLocalVariableAnnotations.add(an);
        }
        return an;
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        LineNumberNode insn = new LineNumberNode(line, getLabelNode(start));
	    insn.cn = owner;
	    insn.mn = this;
	    instructions.add(insn);
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
    }

    @Override
    public void visitEnd() {
    }

    /**
     * Returns the LabelNode corresponding to the given Label. Creates a new
     * LabelNode if necessary. The default implementation of this method uses
     * the {@link org.sasm.Label#info} field to store associations between labels and
     * label nodes.
     *
     * @param l
     *            a Label.
     * @return the LabelNode corresponding to l.
     */
    protected LabelNode getLabelNode(final Label l) {
        if (!(l.info instanceof LabelNode)) {
            l.info = new LabelNode();
        }
        return (LabelNode) l.info;
    }

    private LabelNode[] getLabelNodes(final Label[] l) {
        LabelNode[] nodes = new LabelNode[l.length];
        for (int i = 0; i < l.length; ++i) {
            nodes[i] = getLabelNode(l[i]);
        }
        return nodes;
    }

    private Object[] getLabelNodes(final Object[] objs) {
        Object[] nodes = new Object[objs.length];
        for (int i = 0; i < objs.length; ++i) {
            Object o = objs[i];
            if (o instanceof Label) {
                o = getLabelNode((Label) o);
            }
            nodes[i] = o;
        }
        return nodes;
    }

    // ------------------------------------------------------------------------
    // Accept method
    // ------------------------------------------------------------------------

    /**
     * Checks that this method node is compatible with the given ASM API
     * version. This methods checks that this node, and all its nodes
     * recursively, do not contain elements that were introduced out more recent
     * versions of the ASM API than the given version.
     *
     * @param api
     *            an ASM API version. Must be one of {@link org.sasm.Opcodes#ASM4} or
     *            {@link org.sasm.Opcodes#ASM5}.
     */
    public void check(final int api) {
        if (api == Opcodes.ASM4) {
            if (visibleTypeAnnotations != null && visibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            if (invisibleTypeAnnotations != null && invisibleTypeAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            int n = tryCatchBlocks == null ? 0 : tryCatchBlocks.size();
            for (int i = 0; i < n; ++i) {
                TryCatchBlockNode tcb = tryCatchBlocks.get(i);
                if (tcb.visibleTypeAnnotations != null && tcb.visibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException();
                }
                if (tcb.invisibleTypeAnnotations != null && tcb.invisibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException();
                }
            }
            for (int i = 0; i < instructions.size(); ++i) {
                AbstractInsnNode insn = instructions.get(i);
                if (insn.visibleTypeAnnotations != null && insn.visibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException();
                }
                if (insn.invisibleTypeAnnotations != null && insn.invisibleTypeAnnotations.size() > 0) {
                    throw new RuntimeException();
                }
            }
            if (visibleLocalVariableAnnotations != null && visibleLocalVariableAnnotations.size() > 0) {
                throw new RuntimeException();
            }
            if (invisibleLocalVariableAnnotations != null && invisibleLocalVariableAnnotations.size() > 0) {
                throw new RuntimeException();
            }

        }
    }

    /**
     * Makes the given class visitor visit this method.
     * 
     * @param cv
     *            a class visitor.
     */
    public void accept(final ClassVisitor cv) {
        String[] exceptions = new String[this.exceptions.size()];
        this.exceptions.toArray(exceptions);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            accept(mv);
        }
    }

    /**
     * Makes the given method visitor visit this method.
     * 
     * @param mv
     *            a method visitor.
     */
    public void accept(final MethodVisitor mv) {
        // visits the method parameters
        int i, j, n;
        n = parameters == null ? 0 : parameters.size();
        for (i = 0; i < n; i++) {
            ParameterNode parameter = parameters.get(i);
            mv.visitParameter(parameter.name, parameter.access);
        }
        // visits the method attributes
        if (annotationDefault != null) {
            AnnotationVisitor av = mv.visitAnnotationDefault();
            AnnotationNode.accept(av, null, annotationDefault);
            if (av != null) {
                av.visitEnd();
            }
        }
        n = visibleAnnotations == null ? 0 : visibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            AnnotationNode an = visibleAnnotations.get(i);
            an.accept(mv.visitAnnotation(an.desc, true));
        }
        n = invisibleAnnotations == null ? 0 : invisibleAnnotations.size();
        for (i = 0; i < n; ++i) {
            AnnotationNode an = invisibleAnnotations.get(i);
            an.accept(mv.visitAnnotation(an.desc, false));
        }
        n = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            TypeAnnotationNode an = visibleTypeAnnotations.get(i);
            an.accept(mv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    true));
        }
        n = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            TypeAnnotationNode an = invisibleTypeAnnotations.get(i);
            an.accept(mv.visitTypeAnnotation(an.typeRef, an.typePath, an.desc,
                    false));
        }
        n = visibleParameterAnnotations == null ? 0
                : visibleParameterAnnotations.length;
        for (i = 0; i < n; ++i) {
            List<?> l = visibleParameterAnnotations[i];
            if (l == null) {
                continue;
            }
            for (j = 0; j < l.size(); ++j) {
                AnnotationNode an = (AnnotationNode) l.get(j);
                an.accept(mv.visitParameterAnnotation(i, an.desc, true));
            }
        }
        n = invisibleParameterAnnotations == null ? 0 : invisibleParameterAnnotations.length;
        for (i = 0; i < n; ++i) {
            List<?> l = invisibleParameterAnnotations[i];
            if (l == null) {
                continue;
            }
            for (j = 0; j < l.size(); ++j) {
                AnnotationNode an = (AnnotationNode) l.get(j);
                an.accept(mv.visitParameterAnnotation(i, an.desc, false));
            }
        }
        if (visited) {
            instructions.resetLabels();
        }
        n = attrs == null ? 0 : attrs.size();
        for (i = 0; i < n; ++i) {
            mv.visitAttribute(attrs.get(i));
        }
        // visits the method's code
        if (instructions.size() > 0) {
            mv.visitCode();
            // visits try catch block
            n = tryCatchBlocks == null ? 0 : tryCatchBlocks.size();
            for (i = 0; i < n; ++i) {
                tryCatchBlocks.get(i).updateIndex(i);
                tryCatchBlocks.get(i).accept(mv);
            }
            // visits instructions
            instructions.accept(mv);
            // visits local variables
            n = localVariables == null ? 0 : localVariables.size();
            for (i = 0; i < n; ++i) {
                localVariables.get(i).accept(mv);
            }
            // visits local variable annotations
            n = visibleLocalVariableAnnotations == null ? 0 : visibleLocalVariableAnnotations.size();
            for (i = 0; i < n; ++i) {
                visibleLocalVariableAnnotations.get(i).accept(mv);
            }
            n = invisibleLocalVariableAnnotations == null ? 0 : invisibleLocalVariableAnnotations.size();
            for (i = 0; i < n; ++i) {
                invisibleLocalVariableAnnotations.get(i).accept(mv);
            }
            // visits maxs
            mv.visitMaxs(maxStack, maxLocals);
            visited = true;
        }
        mv.visitEnd();
    }

	/**
	 * Gets the method's parameter descriptors.
	 *
	 * @return A List of the method's parameter descriptors.
	 */
	public List<String> getParameterTypes() {
		List<String> descs = new LinkedList<>();
		String desc = this.desc.substring(1);
		desc = desc.substring(0, desc.indexOf(')'));
		if (desc.isEmpty()) return descs;
		for (int i = 0; i < desc.length(); i++) {
			char c = desc.charAt(i);
			if (c == '[' || c == 'L') {
				String type = desc.substring(i, desc.indexOf(';'));
				descs.add(desc);
				i += type.length();
			} else {
				descs.add(Character.toString(c));
			}
		}
		return descs;
	}

	/**
	 * Gets the descriptor for the parameter at the given index.
	 *
	 * @param idx The index to get at.
	 * @return The descriptor for the parameter at the given index.
	 */
	public String getParameter(int idx) {
		try {
			return getParameterTypes().get(idx);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Gets the amount of parameters.
	 *
	 * @return the amount of parameters.
	 */
	public int getParameterCount() {
		return getParameterTypes().size();
	}
}
