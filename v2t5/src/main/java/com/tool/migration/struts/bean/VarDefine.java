package com.tool.migration.struts.bean;

public enum VarDefine {

	// TODO
	MIN("min", "min", Integer.class), MAX("max", "max", Integer.class), MINLENGTH("minlength", "minlength",
	        Integer.class), MAXLENGTH("maxlength", "maxlength", Integer.class), MASK("mask", "mask", String.class);

	private final String prev;
	private final String next;
	@SuppressWarnings("rawtypes")
	private final Class type;

	@SuppressWarnings("rawtypes")
	private VarDefine(String prev, String next, Class type) {
		this.prev = prev;
		this.next = next;
		this.type = type;
	}

	public String getPrev() {
		return prev;
	}

	public String getNext() {
		return next;
	}

	@SuppressWarnings("rawtypes")
	public Class getType() {
		return type;
	}

}
