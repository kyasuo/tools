package com.tool.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtil {

	private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
	private static final Properties _props;
	static {
		_props = new Properties();
		try {
			_props.load(PropertyUtil.class.getResourceAsStream("/application.properties"));
		} catch (IOException e) {
			logger.error("error", e);
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

}
