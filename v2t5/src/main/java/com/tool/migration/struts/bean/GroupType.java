package com.tool.migration.struts.bean;

import java.io.Serializable;

public class GroupType implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String type;

	public GroupType(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getSimpleType() {
		return type.substring(type.lastIndexOf(".") + 1);
	}

	public String getSimpleTypeClass() {
		return getSimpleType() + ".class";
	}
}
