package org.sasm.util;

import org.sasm.ClassReader;
import org.sasm.ClassWriter;
import org.sasm.tree.ClassNode;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.*;

/**
 * @author Tyler Sedlar
 */
public class JarArchive {

	private final File jarFile;
	public final Manifest manifest;
	public final Map<String, byte[]> rawClasses = new HashMap<>();
	public final Map<String, ClassNode> classes = new HashMap<>();

	/**
	 * Constructs a JarArchive based on the given jar file location.
	 *
	 * @param jarFile The jar to read from.
	 * @throws IOException
	 */
	public JarArchive(File jarFile) throws IOException {
		try (JarFile jar = new JarFile(jarFile)) {
			this.jarFile = jarFile;
			manifest = jar.getManifest();
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (!name.endsWith(".class")) continue;
				String clazz = name.replace(".class", "");
				InputStream input = jar.getInputStream(entry);
				ClassReader reader = new ClassReader(input);
				ClassWriter writer = new ClassWriter(reader, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
				rawClasses.put(clazz, writer.toByteArray());
				ClassNode cn = new ClassNode();
				reader.accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
				classes.put(clazz, cn);
			}
		}
	}

	/**
	 * Dumps the jar to the given location.
	 *
	 * @param target The location to dump to.
	 * @throws IOException
	 */
	public void dump(File target) throws IOException {
		try (JarOutputStream output = new JarOutputStream(new FileOutputStream(target), manifest)) {
			for (Map.Entry<String, ClassNode> entry : classes.entrySet()) {
				output.putNextEntry(new JarEntry(entry.getKey().replaceAll("\\.", "/") + ".class"));
				ClassWriter writer = new ClassWriter(0);
				entry.getValue().accept(writer);
				output.write(writer.toByteArray());
				output.closeEntry();
			}
			output.flush();
		}
	}

	/**
	 * Dumps back into the already given jar file.
	 *
	 * @throws IOException
	 */
	public void dump() throws IOException {
		dump(jarFile);
	}
}
