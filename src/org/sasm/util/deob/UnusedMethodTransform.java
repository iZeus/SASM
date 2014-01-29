package org.sasm.util.deob;

import org.sasm.Opcodes;
import org.sasm.Type;
import org.sasm.tree.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Tyler Sedlar
 */
public class UnusedMethodTransform extends Transform {

	/**
	 * Finds and removes all unused methods.
	 *
	 * @param classes A map of classes to search within.
	 */
    @Override
    public void transform(Map<String, ClassNode> classes) {
        int unused = 0;
        int total = 0;
        int removed = -1;
        long start = System.nanoTime();
        while (removed != 0) {
            removed = 0;
            Set<String> used = new HashSet<>();
            Set<String> inherited = new HashSet<>();
            for (ClassNode cn : classes.values()) {
                for (MethodNode mn : cn.methods) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain instanceof MethodInsnNode) {
                            MethodInsnNode min = (MethodInsnNode) ain;
                            used.add(min.owner + "." + min.name + min.desc);
                        }
                    }
                }
                if (!cn.superName.equals("java/lang/Object")) {
                    if (cn.superName.startsWith("java")) {
                        try {
                            Class<?> clazz = Class.forName(cn.superName.replaceAll("/", "."));
                            for (Method method : clazz.getDeclaredMethods()) {
                                int mods = method.getModifiers();
                                if (Modifier.isPrivate(mods) || Modifier.isStatic(mods)) continue;
                                String desc = Type.getMethodDescriptor(method);
                                inherited.add(method.getName() + desc);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ClassNode node = classes.get(cn.superName);
                        for (MethodNode mn : node.methods) {
                            if ((mn.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) != 0) {
                                continue;
                            }
                            inherited.add(mn.name + mn.desc);
                        }
                    }
                }
                for (String iface : cn.interfaces) {
                    if (iface.startsWith("java")) {
                        try {
                            Class<?> clazz = Class.forName(iface.replaceAll("/", "."));
                            for (Method method : clazz.getDeclaredMethods()) {
                                String desc = Type.getMethodDescriptor(method);
                                inherited.add(method.getName() + desc);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ClassNode node = classes.get(iface.replaceAll("/", "."));
                        for (MethodNode mn : node.methods) inherited.add(mn.name + mn.desc);
                    }
                }
            }
            for (ClassNode cn : classes.values()) {
                List<MethodNode> remove = new ArrayList<>();
                for (MethodNode mn : cn.methods) {
                    total++;
                    if (mn.name.contains("<") || mn.name.contains(">")) continue;
                    if (!used.contains(cn.name + "." + mn.name + mn.desc) && !inherited.contains(mn.name + mn.desc)) {
                        unused++;
                        removed++;
                        remove.add(mn);
                    }
                }
                for (MethodNode mn : remove) cn.methods.remove(mn);
            }
        }
        long end = System.nanoTime();
        System.out.println("Removed " + unused + "/" + total + " unused methods in " +
                String.format("%.2f", (end - start) / 1e9) + " secs");
    }
}
