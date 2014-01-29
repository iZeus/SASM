package org.sasm.util;

import org.sasm.Opcodes;
import org.sasm.tree.*;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Tyler Sedlar
 */
public class Assembly {

	/**
	 * Gets the instruction name for an opcode value.
	 *
	 * @param opcode The opcode in which name is returned.
	 * @return The instruction name for the given opcode value.
	 */
    public static String getName(int opcode) {
        try {
            return InsnSearcher.OPCODES[opcode];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "null";
        }
    }

	/**
	 * Gets the display value for the given instruction.
	 *
	 * @param ain The instruction to get display value for.
	 * @return The display value for the given instruction.
	 */
    public static String getDisplayValue(AbstractInsnNode ain) {
        String name = getName(ain.getOpcode());
        if (name.equals("null")) {
            return ain.toString();
        }
        StringBuilder builder = new StringBuilder(name);
        builder.append(" ");
        if (ain instanceof FieldInsnNode) {
            FieldInsnNode fin = (FieldInsnNode) ain;
            builder.append(fin.desc).append(" ").append(fin.owner).append(".").append(fin.name);
        } else if (ain instanceof MethodInsnNode) {
            MethodInsnNode min = (MethodInsnNode) ain;
            builder.append(min.desc).append(" ").append(min.owner).append(".").append(min.name);
        } else if (ain instanceof IntInsnNode) {
            builder.append(((IntInsnNode) ain).operand);
        } else if (ain instanceof VarInsnNode) {
            builder.append(((VarInsnNode) ain).var);
        } else if (ain instanceof LdcInsnNode) {
            LdcInsnNode ldc = (LdcInsnNode) ain;
            Object cst = ldc.cst;
            if (cst == null) {
                builder.append("null");
            } else {
                if (cst instanceof String) {
                    builder.append("\"").append(cst).append("\"");
                } else {
                    builder.append(cst);
                }
            }
        } else if (ain instanceof JumpInsnNode) {
            builder.append(((JumpInsnNode) ain).label.getLabel().toString());
        }
        return builder.toString();
    }

	/**
	 * Renames the given class from old to new value.
	 *
	 * @param classes A map of classnodes.
	 * @param oldValue The class name to change from.
	 * @param newValue The class name to change to.
	 */
	public static void renameClass(Map<String, ClassNode> classes, String oldValue, String newValue) {
		ClassNode clazz = classes.get(oldValue);
		clazz.name = newValue;
		classes.put(newValue, clazz);
		for (ClassNode cn : classes.values()) {
			if (cn.superName.equals(oldValue)) {
				cn.superName = newValue;
			}
			for (FieldNode fn : cn.fields) {
				if (fn.desc.contains("L" + oldValue + ";")) {
					fn.desc = fn.desc.replaceAll("L" + oldValue + ";", "L" + newValue + ";");
				}
			}
			for (MethodNode mn : cn.methods) {
				if (mn.desc.contains("L" + oldValue + ";")) {
					mn.desc = mn.desc.replaceAll("L" + oldValue + ";", "L" + newValue + ";");
				}
				for (AbstractInsnNode ain : mn.instructions.toArray()) {
					if (ain instanceof FieldInsnNode) {
						FieldInsnNode fin = (FieldInsnNode) ain;
						if (fin.owner.equals(oldValue)) {
							fin.owner = newValue;
						}
						if (fin.desc.contains(oldValue + ";")) {
							fin.desc = fin.desc.replaceAll("L" + oldValue + ";", "L" + newValue + ";");
						}
					} else if (ain instanceof MethodInsnNode) {
						MethodInsnNode min = (MethodInsnNode) ain;
						if (min.owner.equals(oldValue)) {
							min.owner = newValue;
						}
						if (min.desc.contains("L" + oldValue + ";")) {
							min.desc = min.desc.replaceAll("L" + oldValue + ";", "L" + newValue + ";");
						}
					} else if (ain instanceof LdcInsnNode) {
						LdcInsnNode ldc = (LdcInsnNode) ain;
						Object cst = ldc.cst;
						if (cst != null && cst instanceof String) {
							String str = cst.toString();
							if (str.startsWith(oldValue + ".") && str.endsWith("(")) {
								ldc.cst = str.replaceFirst(oldValue + ".", newValue + ".");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Adds a field to the given class.
	 *
	 * @param cn The class to add the field to.
	 * @param access The field's access.
	 * @param name The name of the field.
	 * @param desc The descriptor of the field.
	 */
	public static void addField(ClassNode cn, int access, String name, String desc) {
		cn.fields.add(new FieldNode(access, name, desc, null, null));
	}

	/**
	 * Renames the given class from old to new value.
	 *
	 * @param classes A map of classnodes.
	 * @param oldValue The class name to change from.
	 * @param newValue The class name to change to.
	 */
	public static void renameField(Map<String, ClassNode> classes, String clazz, String oldValue, String newValue) {
		for (FieldNode fn : classes.get(clazz).fields) {
			if (fn.name.equals(oldValue)) {
				fn.name = newValue;
				break;
			}
		}
		for (ClassNode cn : classes.values()) {
			for (MethodNode mn : cn.methods) {
				for (AbstractInsnNode ain : mn.instructions.toArray()) {
					if (ain instanceof FieldInsnNode) {
						FieldInsnNode fin = (FieldInsnNode) ain;
						if (fin.owner.equals(clazz) && fin.name.equals(oldValue)) {
							fin.name = newValue;
						}
					}
				}
			}
		}
	}

	/**
	 * Writes the given class to a file.
	 *
	 * @param cn The class to write.
	 * @param file The file to write to.
	 */
	public static void write(ClassNode cn, File file) {
		file.getParentFile().mkdirs();
		try (FileOutputStream fos = new FileOutputStream(file)) {
			try (PrintStream out = new PrintStream(fos)) {
				String prefix = "";
				if ((cn.access & Opcodes.ACC_PUBLIC) > 0) {
					prefix += "public";
				} else if ((cn.access & Opcodes.ACC_PRIVATE) > 0) {
					prefix += "private";
				} else {
					prefix += "protected";
				}
				if ((cn.access & Opcodes.ACC_STATIC) > 0) {
					prefix += " static";
				}
				if ((cn.access & Opcodes.ACC_ENUM) > 0) {
					if ((cn.access & Opcodes.ACC_ABSTRACT) > 0) {
						prefix += " abstract";
					}
					prefix += " enum";
				} else if ((cn.access & Opcodes.ACC_INTERFACE) > 0) {
					prefix += " interface";
				} else {
					if ((cn.access & Opcodes.ACC_ABSTRACT) > 0) {
						prefix += " abstract";
					}
					prefix += " class";
				}
				out.println(prefix + " " + cn.name + " extends " + cn.superName + (cn.interfaces.isEmpty() ? ""
						: " implements " + Arrays.toString(cn.interfaces.toArray(new String[cn.interfaces.size()]))) + " {");
				out.println();
				for (FieldNode fn : cn.fields) {
					String mods = "";
					if ((fn.access & Opcodes.ACC_PUBLIC) > 0) {
						mods += "public";
					} else {
						if ((fn.access & Opcodes.ACC_PRIVATE) > 0) {
							mods += "private";
						} else {
							mods += "protected";
						}
					}
					if ((fn.access & Opcodes.ACC_STATIC) > 0) {
						mods += " static";
					}
					if ((fn.access & Opcodes.ACC_FINAL) > 0) {
						mods += " final";
					}
					out.println("\t" + mods + " " + fn.desc + " " + fn.name);
				}
				for (MethodNode mn : cn.methods) {
					out.println();
					String mods = "";
					if ((mn.access & Opcodes.ACC_PUBLIC) > 0) {
						mods += "public";
					} else {
						if ((mn.access & Opcodes.ACC_PRIVATE) > 0) {
							mods += "private";
						} else {
							mods += "protected";
						}
					}
					if ((mn.access & Opcodes.ACC_STATIC) > 0) {
						mods += " static";
					} else {
						if ((mn.access & Opcodes.ACC_ABSTRACT) > 0) {
							mods += " abstract";
						}
					}
					if ((mn.access & Opcodes.ACC_FINAL) > 0) {
						mods += " final";
					}
					out.println("\t" + mods + " " + mn.name + mn.desc + " {");
					for (AbstractInsnNode ain : mn.instructions.toArray()) {
						out.println("\t\t" + Assembly.getDisplayValue(ain));
					}
					out.println("\t}");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
