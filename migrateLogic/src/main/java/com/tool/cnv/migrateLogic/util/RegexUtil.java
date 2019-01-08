package com.tool.cnv.migrateLogic.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.tool.cnv.migrateLogic.ReplacementParameter;

public class RegexUtil {

	private static final String JAVADOC_COMMENT_REGEX = "[\\t\\s]+/\\*/?([^/]|[^\\*]/)*\\*/[\\t\\s\r\n]*";
	private static final String APPEND_IMPORT_REGEX = "(import[^;]+;)(?!.*import)";

	private static final char ESCAPE_CHAR = '\\';
	private static final char[] ESCAPE_TARGETS = { '\\', '*', '+', '.', '?', '{', '}', '(', ')', '[', ']', '^', '$',
	        '-', '|', '/' };

	public static String escape(String regex) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < regex.length(); i++) {
			char c = regex.charAt(i);
			if (ArrayUtils.contains(ESCAPE_TARGETS, c)) {
				sb.append(ESCAPE_CHAR);
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static String regexWithCommnet(String regex) {
		return JAVADOC_COMMENT_REGEX + regex;
	}

	public static ReplacementParameter addImports(String[] imports, String lineSeparator) {
		final List<String> elems = new ArrayList<String>();
		elems.add("$1" + lineSeparator);
		elems.addAll(Arrays.asList(imports));
		return new ReplacementParameter(APPEND_IMPORT_REGEX, StringUtils.join(elems, lineSeparator), false);
	}

	public static ReplacementParameter addField(String className, String fieldInfo, String lineSeparator) {
		final List<String> elems = new ArrayList<String>();
		elems.add("$1" + lineSeparator);
		elems.add(fieldInfo);
		return new ReplacementParameter("(class " + className + " [^\\{]*\\{)", StringUtils.join(elems, lineSeparator),
		        false);
	}

	private static final String INDENT1 = StringUtils.leftPad("", PropertyUtil.getPropertyInt("source.indent.width"),
	        PropertyUtil.getProperty("source.indent.char"));
	private static final String INDENT2 = INDENT1 + INDENT1;
	private static final String INDENT3 = INDENT1 + INDENT2;
	private static final String INDENT4 = INDENT1 + INDENT3;
	private static final String COMMENT_BEGIN = INDENT2 + "/**";
	private static final String COMMENT_PREFIX = INDENT2 + " * ";
	private static final String COMMENT_END = INDENT2 + " */";

	public static ReplacementParameter addConstructor(String className, Map<String, String> argMap,
	        String lineSeparator) {

		// build constructor
		int count = 0;
		final StringBuilder constructorComments = new StringBuilder();
		constructorComments.append(COMMENT_BEGIN + lineSeparator);
		constructorComments.append(COMMENT_PREFIX + "コンストラクタ" + lineSeparator);
		constructorComments.append(COMMENT_PREFIX + lineSeparator);
		final StringBuilder constructorArgs = new StringBuilder();
		final StringBuilder constructorBody = new StringBuilder();
		constructorBody.append(INDENT4 + "super();" + lineSeparator);
		for (Entry<String, String> entry : argMap.entrySet()) {
			constructorComments.append(COMMENT_PREFIX + "@param " + entry.getKey() + lineSeparator);
			constructorArgs.append(entry.getValue() + " " + entry.getKey() + ", ");
			if (count % 2 == 1) {
				constructorArgs.append(lineSeparator);
				constructorArgs.append(INDENT3);
			}
			constructorBody.append(INDENT4 + "this." + entry.getKey() + " = " + entry.getKey() + ";" + lineSeparator);
			count++;
		}
		constructorArgs.delete(constructorArgs.lastIndexOf(","), constructorArgs.length());
		constructorComments.append(COMMENT_END + lineSeparator);

		// build replacement
		final StringBuilder replacement = new StringBuilder();
		replacement.append(lineSeparator);
		replacement.append(lineSeparator);
		replacement.append(constructorComments);
		replacement.append(INDENT2);
		replacement.append("public ");
		replacement.append(className);
		replacement.append("(");
		replacement.append(constructorArgs.toString());
		replacement.append(") {");
		replacement.append(lineSeparator);
		replacement.append(constructorBody.toString());
		replacement.append(INDENT2);
		replacement.append("}");

		return new ReplacementParameter("(private[^;]+;)(?!.*private)", "$1" + replacement.toString(), false);
	}

}
