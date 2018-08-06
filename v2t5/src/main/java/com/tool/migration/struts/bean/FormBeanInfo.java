package com.tool.migration.struts.bean;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class FormBeanInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String type;

	private final Set<GroupType> groupTypeSet = new LinkedHashSet<GroupType>();

	private final Map<String, PropertyInfo> propertyMap = new LinkedHashMap<String, PropertyInfo>();

	public FormBeanInfo(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public Set<GroupType> getGroupTypeSet() {
		return groupTypeSet;
	}

	public void addGroupType(GroupType groupType) {
		this.groupTypeSet.add(groupType);
	}

	public Map<String, PropertyInfo> getPropertyMap() {
		return propertyMap;
	}

	public void addProperty(String name, PropertyInfo propertyInfo) {
		this.propertyMap.put(name, propertyInfo);
	}

}
