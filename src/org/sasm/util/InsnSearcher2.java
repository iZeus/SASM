package org.sasm.util;

import org.sasm.tree.*;

import java.util.*;

/**
 * Offers CSS-selector search functionality to instruction nodes.
 *
 * @author Tyler Sedlar
 * @author Finn Thompson
 */
public class InsnSearcher2 {

	public static final String[] OPCODES = {"NOP", "ACONST_NULL", "ICONST_M1", "ICONST_0", "ICONST_1", "ICONST_2", "ICONST_3", "ICONST_4", "ICONST_5", "LCONST_0", "LCONST_1", "FCONST_0", "FCONST_1", "FCONST_2", "DCONST_0", "DCONST_1", "BIPUSH", "SIPUSH", "LDC", "", "", "ILOAD", "LLOAD", "FLOAD", "DLOAD", "ALOAD", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "IALOAD", "LALOAD", "FALOAD", "DALOAD", "AALOAD", "BALOAD", "CALOAD", "SALOAD", "ISTORE", "LSTORE", "FSTORE", "DSTORE", "ASTORE", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "IASTORE", "LASTORE", "FASTORE", "DASTORE", "AASTORE", "BASTORE", "CASTORE", "SASTORE", "POP", "POP2", "DUP", "DUP_X1", "DUP_X2", "DUP2", "DUP2_X1", "DUP2_X2", "SWAP", "IADD", "LADD", "FADD", "DADD", "ISUB", "LSUB", "FSUB", "DSUB", "IMUL", "LMUL", "FMUL", "DMUL", "IDIV", "LDIV", "FDIV", "DDIV", "IREM", "LREM", "FREM", "DREM", "INEG", "LNEG", "FNEG", "DNEG", "ISHL", "LSHL", "ISHR", "LSHR", "IUSHR", "LUSHR", "IAND", "LAND", "IOR", "LOR", "IXOR", "LXOR", "IINC", "I2L", "I2F", "I2D", "L2I", "L2F", "L2D", "F2I", "F2L", "F2D", "D2I", "D2L", "D2F", "I2B", "I2C", "I2S", "LCMP", "FCMPL", "FCMPG", "DCMPL", "DCMPG", "IFEQ", "IFNE", "IFLT", "IFGE", "IFGT", "IFLE", "IF_ICMPEQ", "IF_ICMPNE", "IF_ICMPLT", "IF_ICMPGE", "IF_ICMPGT", "IF_ICMPLE", "IF_ACMPEQ", "IF_ACMPNE", "GOTO", "JSR", "RET", "TABLESWITCH", "LOOKUPSWITCH", "IRETURN", "LRETURN", "FRETURN", "DRETURN", "ARETURN", "RETURN", "GETSTATIC", "PUTSTATIC", "GETFIELD", "PUTFIELD", "INVOKEVIRTUAL", "INVOKESPECIAL", "INVOKESTATIC", "INVOKEINTERFACE", "INVOKEDYNAMIC", "NEW", "NEWARRAY", "ANEWARRAY", "ARRAYLENGTH", "ATHROW", "CHECKCAST", "INSTANCEOF", "MONITORENTER", "MONITOREXIT", "", "MULTIANEWARRAY", "IFNULL", "IFNONNULL"};

	private static final Map<String, List<Map<String, SelectorValue>>> CACHE = new HashMap<>();

	private static class SelectorValue {
		public final char operator;
		public final String value;

		public SelectorValue(char operator, String value) {
			this.operator = operator;
			this.value = value;
		}

		@Override
		public String toString() {
			if (operator > 0) {
				return String.format("%s[operator=%c,value=%s]", getClass().getSimpleName(), operator, value);
			} else {
				return String.format("%s[value=%s]", getClass().getSimpleName(), value);
			}
		}
	}

	/**
	 * Gets the opcode value for the given opcode name.
	 *
	 * @param opcodeName The name of the opcode.
	 * @return The opcode value for the given opcode name.
	 */
	public static int getOpcode(String opcodeName) {
		for (int i = 0; i < OPCODES.length; i++) {
			if (OPCODES[i].equalsIgnoreCase(opcodeName)) {
				return i;
			}
		}
		return -1;
	}

	private static List<List<Map<String, SelectorValue>>> parse(String[] pattern) {
		List<List<Map<String, SelectorValue>>> parsed = new ArrayList<>(pattern.length);
		boolean[] filled = new boolean[pattern.length];
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pattern.length; i++) {
			if (CACHE.containsKey(pattern[i])) {
				parsed.add(CACHE.get(pattern[i]));
				filled[i] = true;
			} else {
				sb.append(pattern[i]).append(' ');
			}
		}
		if (sb.length() > 0) {
			List<List<Map<String, SelectorValue>>> temp = parse(sb.substring(0, sb.length() - 1));
			int idx = 0;
			for (int i = 0; i < pattern.length; i++) {
				if (!filled[i]) {
					List<Map<String, SelectorValue>> current = temp.get(idx++);
					parsed.add(i, current);
					CACHE.put(pattern[i], current);
				}
			}
		}
		return parsed;
	}

	private static List<List<Map<String, SelectorValue>>> parse(String pattern) {
		List<List<Map<String, SelectorValue>>> parsed = new ArrayList<>();
		List<Map<String, SelectorValue>> current = new ArrayList<>();
		Map<String, SelectorValue> temp = new HashMap<>();
		StringBuilder buffer = new StringBuilder();
		String key = null;
		char operator = 0;
		boolean escaped = false;
		boolean attribute = false;
		for (char c : pattern.toCharArray()) {
			switch (c) {
				case '\\':
					if (escaped) {
						buffer.append(c);
					}
					escaped = !escaped;
					break;
				case ' ':
					if (attribute) {
						if (escaped) {
							buffer.append('\\');
							escaped = false;
						}
						buffer.append(c);
					} else {
						if (escaped) {
							buffer.append(c);
							escaped = false;
						} else {
							if (buffer.length() > 0) {
								String opcode = buffer.toString();
								String value = opcode.equals("*") ? opcode : Integer.toString(getOpcode(opcode));
								temp.put("opcode", new SelectorValue(operator, value));
								buffer.setLength(0);
							}
							current.add(new HashMap<>(temp));
							temp.clear();
							parsed.add(new ArrayList<>(current));
							current.clear();
						}
					}
					break;
				case '=':
				case '*':
				case '^':
				case '$':
				case '!':
				case '~':
					if (!attribute) {
						if (escaped) {
							buffer.append('\\');
							escaped = false;
						}
						buffer.append(c);
					} else {
						if (escaped) {
							buffer.append(c);
							escaped = false;
						} else {
							if (c != '=') {
								operator = c;
							}
							if (key == null) {
								key = buffer.toString();
								buffer.setLength(0);
							}
						}
					}
					break;
				case '[':
					if (!attribute) {
						if (escaped) {
							buffer.append('\\');
							buffer.append(c);
							escaped = false;
						} else {
							if (buffer.length() > 0) {
								String opcode = buffer.toString();
								String value = opcode.equals("*") ? opcode : Integer.toString(getOpcode(opcode));
								temp.put("opcode", new SelectorValue(operator, value));
								buffer.setLength(0);
							}
							attribute = true;
						}
					} else {
						if (escaped) {
							buffer.append('\\');
							escaped = false;
						}
						buffer.append(c);
					}
					break;
				case ']':
					if (escaped) {
						buffer.append(c);
						escaped = false;
					} else {
						String value = buffer.toString();
						buffer.setLength(0);
						temp.put(key, new SelectorValue(operator, value));
						key = null;
						operator = 0;
						attribute = false;
					}
					break;
				case '|':
					if (escaped || attribute) {
						buffer.append(c);
					} else {
						if (buffer.length() > 0) {
							String opcode = buffer.toString();
							String value = opcode.equals("*") ? opcode : Integer.toString(getOpcode(opcode));
							temp.put("opcode", new SelectorValue(operator, value));
							buffer.setLength(0);
						}
						current.add(new HashMap<>(temp));
						temp.clear();
					}
					break;
				default:
					if (escaped) {
						buffer.append('\\');
						escaped = false;
					}
					buffer.append(c);
			}
		}
		if (buffer.length() > 0) {
			String opcode = buffer.toString();
			String value = opcode.equals("*") ? opcode : Integer.toString(getOpcode(opcode));
			temp.put("opcode", new SelectorValue(operator, value));
		}
		current.add(new HashMap<>(temp));
		parsed.add(current);
		return parsed;
	}

	private static boolean match(String str, SelectorValue value) {
		if (str.isEmpty()) {
			return value.value.isEmpty();
		}
		if (value.operator > 0) {
			switch (value.operator) {
				case '^':
					return str.startsWith(value.value);
				case '$':
					return str.endsWith(value.value);
				case '*':
					return str.contains(value.value);
				case '!':
					return !str.contains(value.value);
				case '~':
					return str.matches(value.value);
			}
		}
		return str.equals(value.value);
	}

	private static boolean equals(AbstractInsnNode ain, Map<String, SelectorValue> insn) {
		String opcode = insn.get("opcode").value;
		if (!opcode.equals("*") && !Integer.toString(ain.getOpcode()).equals(opcode)) return false;
		SelectorValue owner = insn.get("owner");
		SelectorValue name = insn.get("name");
		SelectorValue desc = insn.get("desc");
		if (ain instanceof FieldInsnNode) {
			FieldInsnNode fin = (FieldInsnNode) ain;
			return (name == null || match(fin.name, name)) &&
					(owner == null || match(fin.owner, owner)) &&
					(desc == null || match(fin.desc, desc));
		} else if (ain instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) ain;
			return (name == null || match(min.name, name)) &&
					(owner == null || match(min.owner, owner)) &&
					(desc == null || match(min.desc, desc));
		} else if (ain instanceof TypeInsnNode) {
			TypeInsnNode tin = (TypeInsnNode) ain;
			return desc == null || match(tin.desc, desc);
		} else if (ain instanceof IntInsnNode) {
			IntInsnNode iin = (IntInsnNode) ain;
			SelectorValue operand = insn.get("operand");
			return operand == null || match(Integer.toString(iin.operand), operand);
		} else if (ain instanceof VarInsnNode) {
			VarInsnNode vin = (VarInsnNode) ain;
			SelectorValue var = insn.get("var");
			return var == null || match(Integer.toString(vin.var), var);
		} else if (ain instanceof LdcInsnNode) {
			LdcInsnNode lin = (LdcInsnNode) ain;
			SelectorValue cst = insn.get("cst");
			return cst == null || (lin.cst != null && match(lin.cst.toString(), cst));
		}
		return true;
	}

	private static boolean equals(AbstractInsnNode ain, List<Map<String, SelectorValue>> maps) {
		for (Map<String, SelectorValue> map : maps) {
			if (equals(ain, map)) return true;
		}
		return false;
	}

	/**
	 * Checks if the instruction matches the given single-pattern.
	 *
	 * @param ain The instruction to check.
	 * @param pattern The single-pattern to match with.
	 * @return <t>true</t> if the instruction matches the single-pattern, else <t>false.</t>
	 */
	public static boolean equals(AbstractInsnNode ain, String pattern) {
		return equals(ain, parse(pattern).get(0));
	}

	private static AbstractInsnNode next(AbstractInsnNode ain, List<Map<String, SelectorValue>> parsed) {
		int highestDist = -1;
		for (Map<String, SelectorValue> map : parsed) {
			int dist = map.containsKey("dist") ? Integer.parseInt(map.get("dist").value) : 10;
			if (dist > highestDist) highestDist = dist;
		}
		int dist = 0;
		while ((ain = ain.getNext()) != null && dist < highestDist) {
			for (Map<String, SelectorValue> map : parsed) {
				int d = map.containsKey("dist") ? Integer.parseInt(map.get("dist").value) : 10;
				if (dist < d) {
					if (equals(ain, map)) return ain;
				}
			}
			dist++;
		}
		return null;
	}

	/**
	 * Gets the next instruction matching the given single-pattern.
	 *
	 * @param ain The instruction to start search at.
	 * @param pattern The single-pattern to search for. ("getfield[desc=I]")
	 * @return The next instruction matching the given single-pattern.
	 */
	public static AbstractInsnNode next(AbstractInsnNode ain, String pattern) {
		return next(ain, parse(pattern).get(0));
	}


	/**
	 * Gets the next instruction matching the given single-pattern.
	 *
	 * @param mn The method to start search within.
	 * @param pattern The single-pattern to search for. ("getfield[desc=I]")
	 * @return The next instruction matching the given single-pattern.
	 */
	public static AbstractInsnNode next(MethodNode mn, String pattern) {
		if (mn.instructions.size() == 0) return null;
		if (pattern.contains("dist=10")) {
			pattern = pattern.replace("dist=10", "dist=" + (mn.instructions.size() - 2));
		} else {
			pattern += "[dist=" + (mn.instructions.size() - 2) + "]";
		}
		return next(mn.instructions.get(0), pattern);
	}

	private static AbstractInsnNode prev(AbstractInsnNode ain, List<Map<String, SelectorValue>> parsed) {
		int highestDist = -1;
		for (Map<String, SelectorValue> map : parsed) {
			int dist = map.containsKey("dist") ? Integer.parseInt(map.get("dist").value) : 10;
			if (dist > highestDist) highestDist = dist;
		}
		int dist = 0;
		while ((ain = ain.getPrevious()) != null && dist < highestDist) {
			for (Map<String, SelectorValue> map : parsed) {
				int d = map.containsKey("dist") ? Integer.parseInt(map.get("dist").value) : 10;
				if (dist < d) {
					if (equals(ain, map)) return ain;
				}
			}
			dist++;
		}
		return null;
	}

	/**
	 * Gets the previous instruction matching the given single-pattern.
	 *
	 * @param ain The instruction to start search at.
	 * @param pattern The pattern to search for. ("getfield[desc=I]")
	 * @return The previous instruction matching the given single-pattern.
	 */
	public static AbstractInsnNode prev(AbstractInsnNode ain, String pattern) {
		return prev(ain, parse(pattern).get(0));
	}

	private static List<AbstractInsnNode> search(MethodNode mn, List<List<Map<String, SelectorValue>>> parsed) {
		loop: for (AbstractInsnNode insn : mn.instructions.toArray()) {
			List<AbstractInsnNode> instructions = new ArrayList<>();
			for (int i = 0; i < parsed.size(); i++) {
				if (insn == null) {
					instructions.clear();
					continue loop;
				}
				if (equals(insn, parsed.get(i))) {
					instructions.add(insn);
					if (i + 1 < parsed.size()) {
						insn = next(insn, parsed.get(i + 1));
					}
				} else {
					instructions.clear();
					continue loop;
				}
			}
			return instructions;
		}
		return null;
	}

	/**
	 * Searches for the given pattern inside of the given method.
	 *
	 * @param mn The method to search within.
	 * @param pattern The pattern to search for. ("getfield[desc=I]", "imul")
	 * @return A List of instructions that matches the given pattern.
	 */
	public static List<AbstractInsnNode> search(MethodNode mn, String... pattern) {
		return search(mn, parse(pattern));
	}

	/**
	 * Searches for the given pattern inside of the given class and matching a method with the given description.
	 *
	 * @param cn The class to search within.
	 * @param methodDesc The method descriptor to match (uses Stringer)
	 * @param pattern The pattern to search for. ("getfield[desc=I]", "imul")
	 * @return A List of instructions that matches the given pattern.
	 */
	public static List<AbstractInsnNode> search(ClassNode cn, String methodDesc, String[] pattern) {
		List<List<Map<String, SelectorValue>>> parsed = parse(pattern);
		for (MethodNode mn : cn.methods) {
			if (methodDesc == null || Stringer.match(mn.desc, methodDesc)) {
				List<AbstractInsnNode> insn = search(mn, parsed);
				if (insn != null) return insn;
			}
		}
		return null;
	}

	/**
	 * Searches for the given pattern inside of the given class.
	 *
	 * @param cn The class to search within.
	 * @param pattern The pattern to search for. ("getfield[desc=I]", "imul")
	 * @return A List of instructions that matches the given pattern.
	 */
	public static List<AbstractInsnNode> search(ClassNode cn, String... pattern) {
		return search(cn, null, pattern);
	}

	/**
	 * Counts the amount of matching instructions based off the given single-pattern.
	 *
	 * @param mn The method to search within.
	 * @param pattern A single-pattern string ("getfield[desc=I]")
	 * @return The amount of matching instructions passed off the given single-pattern.
	 */
	public static int count(MethodNode mn, String pattern) {
		List<Map<String, SelectorValue>> parsed = parse(pattern).get(0);
		int count = 0;
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (equals(ain, parsed)) count++;
		}
		return count;
	}

	/**
	 * Finds the index of the given single-pattern.
	 *
	 * @param mn The method to search within.
	 * @param pattern A pattern string ("getfield[desc=I]|putfield")
	 * @return The index of the first found matching instruction.
	 */
	public static int indexOf(MethodNode mn, String pattern) {
		List<Map<String, SelectorValue>> parsed = parse(pattern).get(0);
		AbstractInsnNode[] insn = mn.instructions.toArray();
		for (int i = 0; i < insn.length; i++) {
			if (equals(insn[i], parsed)) return i;
		}
		return -1;
	}

	/**
	 * Finds the last index of the given pattern.
	 *
	 * @param mn The method to search within.
	 * @param pattern A pattern string ("getfield[desc=I]|putfield")
	 * @return The last index of the first found matching instruction.
	 */
	public static int lastIndexOf(MethodNode mn, String pattern) {
		List<Map<String, SelectorValue>> parsed = parse(pattern).get(0);
		AbstractInsnNode[] insn = mn.instructions.toArray();
		for (int i = insn.length - 1; i > 0; i--) {
			if (equals(insn[i], parsed)) return i;
		}
		return -1;
	}
}
