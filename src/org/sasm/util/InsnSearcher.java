package org.sasm.util;

import org.sasm.Opcodes;
import org.sasm.tree.*;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tyler Sedlar
 */
public class InsnSearcher {

	public static final String[] OPCODES = {"NOP", "ACONST_NULL", "ICONST_M1", "ICONST_0", "ICONST_1", "ICONST_2", "ICONST_3", "ICONST_4", "ICONST_5", "LCONST_0", "LCONST_1", "FCONST_0", "FCONST_1", "FCONST_2", "DCONST_0", "DCONST_1", "BIPUSH", "SIPUSH", "LDC", "", "", "ILOAD", "LLOAD", "FLOAD", "DLOAD", "ALOAD", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "IALOAD", "LALOAD", "FALOAD", "DALOAD", "AALOAD", "BALOAD", "CALOAD", "SALOAD", "ISTORE", "LSTORE", "FSTORE", "DSTORE", "ASTORE", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "IASTORE", "LASTORE", "FASTORE", "DASTORE", "AASTORE", "BASTORE", "CASTORE", "SASTORE", "POP", "POP2", "DUP", "DUP_X1", "DUP_X2", "DUP2", "DUP2_X1", "DUP2_X2", "SWAP", "IADD", "LADD", "FADD", "DADD", "ISUB", "LSUB", "FSUB", "DSUB", "IMUL", "LMUL", "FMUL", "DMUL", "IDIV", "LDIV", "FDIV", "DDIV", "IREM", "LREM", "FREM", "DREM", "INEG", "LNEG", "FNEG", "DNEG", "ISHL", "LSHL", "ISHR", "LSHR", "IUSHR", "LUSHR", "IAND", "LAND", "IOR", "LOR", "IXOR", "LXOR", "IINC", "I2L", "I2F", "I2D", "L2I", "L2F", "L2D", "F2I", "F2L", "F2D", "D2I", "D2L", "D2F", "I2B", "I2C", "I2S", "LCMP", "FCMPL", "FCMPG", "DCMPL", "DCMPG", "IFEQ", "IFNE", "IFLT", "IFGE", "IFGT", "IFLE", "IF_ICMPEQ", "IF_ICMPNE", "IF_ICMPLT", "IF_ICMPGE", "IF_ICMPGT", "IF_ICMPLE", "IF_ACMPEQ", "IF_ACMPNE", "GOTO", "JSR", "RET", "TABLESWITCH", "LOOKUPSWITCH", "IRETURN", "LRETURN", "FRETURN", "DRETURN", "ARETURN", "RETURN", "GETSTATIC", "PUTSTATIC", "GETFIELD", "PUTFIELD", "INVOKEVIRTUAL", "INVOKESPECIAL", "INVOKESTATIC", "INVOKEINTERFACE", "INVOKEDYNAMIC", "NEW", "NEWARRAY", "ANEWARRAY", "ARRAYLENGTH", "ATHROW", "CHECKCAST", "INSTANCEOF", "MONITORENTER", "MONITOREXIT", "", "MULTIANEWARRAY", "IFNULL", "IFNONNULL"};

	public static final Pattern SPLIT_PATTERN = Pattern.compile("(.*?)(?![^|])"),
			VALUE_PATTERN = Pattern.compile("\\[(.*?)\\]");

	private static final Map<Integer, List<List<Map<String, String>>>> CACHE = new HashMap<>();

	/**
	 * Gets the opcode value for the given opcode name.
	 *
	 * @param opcodeName The name of the opcode.
	 * @return The opcode value for the given opcode name.
	 */
	public static int getOpcode(String opcodeName) {
		int idx = 0;
		for (String opcode : OPCODES) {
			if (opcodeName.equalsIgnoreCase(opcode)) return idx;
			idx++;
		}
		return -1;
	}

	private static List<List<Map<String, String>>> parse(String... patterns) {
		int patternKey = Arrays.hashCode(patterns);
		if (CACHE.containsKey(patternKey)) {
			return CACHE.get(patternKey);
		}
		List<List<Map<String, String>>> parsed = new LinkedList<>();
		for (String pattern : patterns) {
			List<Map<String, String>> current = new LinkedList<>();
			Matcher splitter = SPLIT_PATTERN.matcher(pattern);
			while (splitter.find()) {
				String group = splitter.group(1);
				if (group.isEmpty()) continue;
				Map<String, String> values = new HashMap<>();
				String opcode = group;
				boolean parsable = opcode.contains("[");
				if (parsable) opcode = opcode.substring(0, opcode.indexOf('['));
				values.put("opcode", Integer.toString(getOpcode(opcode)));
				if (!parsable) {
					current.add(values);
					continue;
				}
				Matcher matcher = VALUE_PATTERN.matcher(group);
				while (matcher.find()) {
					String innards = matcher.group(1);
					String[] split = innards.split("=");
					String key = split[0];
					String val = split.length > 1 ? split[1] : "";
					char last = key.charAt(key.length() - 1);
					if (last == '^' || last == '$' || last == '*' || last == '|' || last == '!' || last == '~') {
						val = last + val;
						key = key.substring(0, key.length() - 1);
					}
					values.put(key, val);
				}
				current.add(values);
			}
			parsed.add(current);
		}
		CACHE.put(patternKey, parsed);
		return parsed;
	}

	private static boolean equals(AbstractInsnNode ain, Map<String, String> insn) {
		String opcode = insn.get("opcode");
		if (!opcode.equals("*") && !Integer.toString(ain.getOpcode()).equals(opcode)) return false;
		String owner = insn.get("owner");
		String name = insn.get("name");
		String desc = insn.get("desc");
		if (ain instanceof FieldInsnNode) {
			FieldInsnNode fin = (FieldInsnNode) ain;
			return (name == null || Stringer.match(fin.name, name)) &&
					(owner == null || Stringer.match(fin.owner, owner)) &&
					(desc == null || Stringer.match(fin.desc, desc));
		} else if (ain instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) ain;
			return (name == null || Stringer.match(min.name, name)) &&
					(owner == null || Stringer.match(min.owner, owner)) &&
					(desc == null || Stringer.match(min.desc, desc));
		} else if (ain instanceof TypeInsnNode) {
			TypeInsnNode tin = (TypeInsnNode) ain;
			return desc == null || Stringer.match(tin.desc, desc);
		} else if (ain instanceof IntInsnNode) {
			IntInsnNode iin = (IntInsnNode) ain;
			String operand = insn.get("operand");
			return operand == null || Stringer.match(Integer.toString(iin.operand), operand);
		} else if (ain instanceof VarInsnNode) {
			VarInsnNode vin = (VarInsnNode) ain;
			String var = insn.get("var");
			return var == null || Stringer.match(Integer.toString(vin.var), var);
		} else if (ain instanceof LdcInsnNode) {
			LdcInsnNode lin = (LdcInsnNode) ain;
			String cst = insn.get("cst");
			return cst == null || (lin.cst != null && Stringer.match(lin.cst.toString(), cst));
		}
		return true;
	}

	private static boolean equals(AbstractInsnNode ain, List<Map<String, String>> maps) {
		for (Map<String, String> map : maps) {
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

	private static AbstractInsnNode next(AbstractInsnNode ain, List<Map<String, String>> parsed) {
		int highestDist = -1;
		for (Map<String, String> map : parsed) {
			int dist = map.containsKey("dist") ? Integer.parseInt(map.get("dist")) : 10;
			if (dist > highestDist) highestDist = dist;
		}
		int dist = 0;
		while ((ain = ain.getNext()) != null && dist < highestDist) {
			for (Map<String, String> map : parsed) {
				int d = map.containsKey("dist") ? Integer.parseInt(map.get("dist")) : 10;
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

	private static AbstractInsnNode prev(AbstractInsnNode ain, List<Map<String, String>> parsed) {
		int highestDist = -1;
		for (Map<String, String> map : parsed) {
			int dist = map.containsKey("dist") ? Integer.parseInt(map.get("dist")) : 10;
			if (dist > highestDist) highestDist = dist;
		}
		int dist = 0;
		while ((ain = ain.getPrevious()) != null && dist < highestDist) {
			for (Map<String, String> map : parsed) {
				int d = map.containsKey("dist") ? Integer.parseInt(map.get("dist")) : 10;
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

	private static List<AbstractInsnNode> search(MethodNode mn, List<List<Map<String, String>>> parsed) {
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
		List<List<Map<String, String>>> parsed = parse(pattern);
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
		List<Map<String, String>> parsed = parse(pattern).get(0);
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
	public static int indexOf(MethodNode mn, String... pattern) {
		List<List<Map<String, String>>> parsed = parse(pattern);
		int idx = -1;
		loop: for (AbstractInsnNode insn : mn.instructions.toArray()) {
			idx++;
			for (int i = 0; i < parsed.size(); i++) {
				if (insn == null) {
					continue loop;
				}
				if (equals(insn, parsed.get(i))) {
					if (i + 1 < parsed.size()) {
						insn = next(insn, parsed.get(i + 1));
					}
				} else {
					continue loop;
				}
			}
			return idx;
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
	public static int lastIndexOf(MethodNode mn, String... pattern) {
		List<List<Map<String, String>>> parsed = parse(pattern);
		int index = -1;
		int idx = -1;
		loop: for (AbstractInsnNode insn : mn.instructions.toArray()) {
			idx++;
			for (int i = 0; i < parsed.size(); i++) {
				if (insn == null) {
					continue loop;
				}
				if (equals(insn, parsed.get(i))) {
					if (i + 1 < parsed.size()) {
						insn = next(insn, parsed.get(i + 1));
					}
				} else {
					continue loop;
				}
			}
			index = idx;
		}
		return index != -1 ? index : -1;
	}
}
