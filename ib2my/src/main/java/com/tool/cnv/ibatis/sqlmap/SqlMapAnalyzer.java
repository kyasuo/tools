package com.tool.cnv.ibatis.sqlmap;

import java.io.File;
import java.util.Map;

import com.tool.cnv.ibatis.sqlmap.bean.SqlMapInfo;

public interface SqlMapAnalyzer {

	/**
	 * analyze SqlMap file
	 * 
	 * @param xml
	 *            {@link java.io.File} SqlMap file
	 * @return {@link java.util.Map} SqlMap(key: NAMESPACE + SQLID, value: SqlMapInfo)
	 */
	Map<String, SqlMapInfo> analyze(File xml);

}
