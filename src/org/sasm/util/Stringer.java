package org.sasm.util;

import java.util.regex.Pattern;

/**
 * @author Tyler Sedlar
 */
public class Stringer {

	/**
	 * Matches a given string with a given matcher sequence.
	 * @param string String to match
	 * @param matcher Match sequence, string should start with ^, $, *, |, !, or ~.
	 *                ^ - startsWith
	 *                $ - endsWith
	 *                * - contains
	 *                | - contains "str-"
	 *                ! - not contains
	 *                ~ - regex match
	 * @return <t>true</t> if the string has been matched, otherwise <t>false.</t>
	 */
	public static boolean match(String string, String matcher) {
		if (matcher.isEmpty()) return string.isEmpty();
		char start = matcher.charAt(0);
		String subbed = matcher.substring(1);
		if (start == '^') {
			return string.startsWith(subbed);
		} else if (start == '$') {
			return string.endsWith(subbed);
		} else if (start == '*') {
			return string.contains(subbed);
		} else if (start == '|') {
			return string.contains(subbed + "-");
		} else if (start == '!') {
			return !string.contains(subbed);
		} else if (start == '~') {
			return Pattern.compile(subbed).matcher(string).find();
		} else {
			return string.equals(matcher);
		}
	}

	/**
	 * Formats the string in sequential order, replacing the value '%s'.
	 * @param string The string to format
	 * @param replaces The strings to replace '%s' with, in sequential order.
	 * @return The formatted string.
	 */
	public static String format(String string, String... replaces) {
		String result = string;
		for (String replace : replaces) {
			result = result.replaceFirst("%s", replace);
		}
		return result;
	}
}
