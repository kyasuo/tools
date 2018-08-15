package com.tool.migration.struts.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class AnnotationInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String type;

	private final Map<String, Set<Object>> params = new LinkedHashMap<String, Set<Object>>();

	private final GroupType groupType;

	public AnnotationInfo(String type, GroupType groupType) {
		super();
		this.type = type;
		this.groupType = groupType;
		if (groupType != null) {
			this.addParam("groups", groupType);
		}
	}

	public String getType() {
		return type;
	}

	public Map<String, Set<Object>> getParams() {
		return params;
	}

	public void addParams(String key, Set<Object> values) {
		if (!this.params.containsKey(key)) {
			this.params.put(key, new LinkedHashSet<Object>());
		}
		this.params.get(key).addAll(values);
	}

	public void addParam(String key, Object value) {
		if (!this.params.containsKey(key)) {
			this.params.put(key, new LinkedHashSet<Object>());
		}
		this.params.get(key).add(value);
	}

	public String toAnnotaion() {
		final StringBuilder sb = new StringBuilder();
		sb.append("@");
		sb.append(type.substring(type.lastIndexOf(".") + 1));
		if (!params.isEmpty()) {
			final List<String> paramList = new ArrayList<String>();
			for (Entry<String, Set<Object>> entry : params.entrySet()) {
				final Set<Object> values = entry.getValue();
				if (values.size() == 1) {
					paramList.add(entry.getKey() + " = " + format(values.iterator().next()));
				} else {
					final List<String> valueList = new ArrayList<String>(); // FIXME use hashset if entry key is groups
					for (Object value : values) {
						valueList.add(format(value));
					}
					paramList.add(entry.getKey() + " = { " + StringUtils.join(valueList, ", ") + " }");
				}
			}
			sb.append("(");
			sb.append(StringUtils.join(paramList, ", "));
			sb.append(")");
		}
		return sb.toString();
	}

	private String format(Object obj) {
		if (obj instanceof String) {
			return "\"" + obj + "\"";
		} else if (obj instanceof GroupType) {
			return ((GroupType) obj).getSimpleTypeClass();
		} else {
			return obj.toString();
		}
	}

	public GroupType getGroupType() {
		return groupType;
	}

}
