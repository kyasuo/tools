package com.tool.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyUtil {

	private static final Properties _props;
	static {
		_props = new Properties();
		try {
			_props.load(PropertyUtil.class.getResourceAsStream("/application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Integer getPropertyInt(String name) {
		return getPropertyInt(name, null);
	}

	public static Integer getPropertyInt(String name, Integer defaultValue) {
		final String value = getProperty(name, null);
		if (value == null) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}

	public static String getProperty(String name) {
		return getProperty(name, null);
	}

	public static String getProperty(String name, String defaultValue) {
		return _props.getProperty(name, defaultValue);
	}

	public static String[] getProperties(String name) {
		return getProperty(name, "").split(",");
	}

	public static List<String> getProperyListByPrefix(String prefix) {
		final List<String> properties = new ArrayList<String>();
		int i = 1;
		String value = null;
		prefix = prefix.endsWith(".") ? prefix : prefix + ".";
		while ((value = getProperty(prefix + i)) != null) {
			if (!"".equals(value.trim())) {
				properties.add(value);
			}
			i++;
		}
		return properties;
	}

}
