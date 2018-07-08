package com.tool.cnv.ibatis.sqlmap.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import com.tool.cnv.ibatis.sqlmap.define.SqlType;

@SuppressWarnings("rawtypes")
public class SqlMapInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private SqlType type;
	private Class parameterClass;
	private Class resultClass;
	private final StringBuilder statement = new StringBuilder();
	private final Map<String, Class> propertyTypeMap = new TreeMap<String, Class>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SqlType getType() {
		return type;
	}

	public void setType(SqlType type) {
		this.type = type;
	}

	public Class getParameterClass() {
		return parameterClass;
	}

	public void setParameterClass(Class parameterClass) {
		this.parameterClass = parameterClass;
	}

	public Class getResultClass() {
		return resultClass;
	}

	public void setResultClass(Class resultClass) {
		this.resultClass = resultClass;
	}

	public String getStatement() {
		return statement.toString();
	}

	public void appendStatement(String statement) {
		this.statement.append(statement);
	}

	public Map<String, Class> getPropertyTypeMap() {
		return propertyTypeMap;
	}

	public void addPropertyType(String key, Class value) {
		this.propertyTypeMap.put(key, value);
	}

}
