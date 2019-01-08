package com.tool.cnv.migrateLogic;

public class ReplacementParameter {

	private final boolean replaceAll;
	private final String regex;
	private final String replacement;

	public ReplacementParameter(String regex, String replacement, boolean replaceAll) {
		super();
		this.regex = regex;
		this.replacement = replacement;
		this.replaceAll = replaceAll;
	}

	public String getRegex() {
		return regex;
	}

	public String getReplacement() {
		return replacement;
	}

	public boolean isReplaceAll() {
		return replaceAll;
	}

}
