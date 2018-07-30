package com.tool.migration.visitor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VisitorContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<String, Map<String, Object>> context = new HashMap<String, Map<String, Object>>();

	public void add(String name, String key, Object value) {
		if (!context.containsKey(name)) {
			context.put(name, new HashMap<String, Object>());
		}
		context.get(name).put(key, value);
	}

	public Object get(String name, String key) {
		if (!context.containsKey(name)) {
			context.put(name, new HashMap<String, Object>());
		}
		return context.get(name).get(key);
	}
}
