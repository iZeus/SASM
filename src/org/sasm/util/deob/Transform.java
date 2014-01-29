package org.sasm.util.deob;

import org.sasm.tree.ClassNode;

import java.util.Map;

/**
 * @author Tyler Sedlar
 */
public abstract class Transform {

    public abstract void transform(Map<String, ClassNode> classes);
}
