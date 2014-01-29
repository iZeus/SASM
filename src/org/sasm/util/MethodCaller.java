package org.sasm.util;

import org.sasm.Opcodes;
import org.sasm.tree.*;

/**
 * @author Tyler Sedlar
 */
public class MethodCaller {

	/**
	 * Injects instructions in which prints out the method name it's in. This is useful for debugging.
	 *
	 * @param cn The owner of the method to inject into.
	 * @param mn The method to inject into.
	 */
	public static void apply(ClassNode cn, MethodNode mn) {
		InsnList instructions = mn.instructions;
		if (instructions.size() == 0) return;
		InsnList list = new InsnList();
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
		list.add(new LdcInsnNode(cn.name + "." + mn.name + mn.desc));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
		instructions.insertBefore(instructions.get(0), list);
	}
}
