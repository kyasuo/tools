package com.tool.util;

import java.util.Collection;

public class ClassUtil {

	@SuppressWarnings("rawtypes")
	public static Class getClass(String name) throws ClassNotFoundException {
		return Class.forName(name);
	}

	@SuppressWarnings("rawtypes")
	public static Class getClassIgnoreException(String name) {
		try {
			return getClass(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public static boolean isFieldBySimpleName(Collection<Class> classes, String type) {
		for (Class clz : classes) {
			if (clz.getSimpleName().equals(type)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes" })
	public static boolean isFieldByName(Collection<Class> classes, String type) {
		for (Class clz : classes) {
			if (clz.getName().equals(type)) {
				return true;
			}
		}
		return false;
	}
}
