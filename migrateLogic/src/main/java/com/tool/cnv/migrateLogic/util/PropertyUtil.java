package com.tool.cnv.migrateLogic.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tool.cnv.migrateLogic.MigrationToolRunner;

public class PropertyUtil {

	private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

	private static final Properties PROP = new Properties();

	static {
		try {
			PROP.load(MigrationToolRunner.class.getResourceAsStream("/application.properties"));
		} catch (IOException e) {
			logger.error("failure to load application.properties", e);
		}
	}

	public static String getProperty(String key) {
		return getProperty(key, null);
	}

	public static String getProperty(String key, String defaultValue) {
		return PROP.getProperty(key, defaultValue);
	}

	public static int getPropertyInt(String key) {
		return Integer.parseInt(getProperty(key));
	}

	public static int getPropertyInt(String key, String defaultValue) {
		return Integer.parseInt(getProperty(key, defaultValue));
	}
}
