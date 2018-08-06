package com.tool.migration.struts.bean;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PropertyInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;

	private final Map<String, AnnotationInfo> annotaionMap = new LinkedHashMap<String, AnnotationInfo>();

	public PropertyInfo(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Map<String, AnnotationInfo> getAnnotaionMap() {
		return annotaionMap;
	}

	public void addAnnotaion(AnnotationInfo annotaionInfo) {
		final String type = annotaionInfo.getType();
		if (!this.annotaionMap.containsKey(type)) {
			this.annotaionMap.put(type, new AnnotationInfo(type, annotaionInfo.getGroupType()));
		}
		for (Entry<String, Set<Object>> entry : annotaionInfo.getParams().entrySet()) {
			this.annotaionMap.get(type).addParams(entry.getKey(), entry.getValue());
		}
	}
}
