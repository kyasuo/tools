package com.tool.cnv.migrateLogic.util;

import java.util.List;

public class ListUtil {

	/**
	 * Return first element of List. <br/>
	 * If List is null or empty, return null.
	 * 
	 * @param list
	 * @return first element
	 */
	public <E> E first(List<E> list) {
		E result = null;
		if (list == null || list.isEmpty()) {
			return result;
		}
		return list.get(0);
	}

}
