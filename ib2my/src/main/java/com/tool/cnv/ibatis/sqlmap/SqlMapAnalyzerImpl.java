package com.tool.cnv.ibatis.sqlmap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.common.xml.NodeletException;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapParser;
import com.ibatis.sqlmap.engine.builder.xml.XmlParserState;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMapping;
import com.ibatis.sqlmap.engine.mapping.sql.SqlChild;
import com.ibatis.sqlmap.engine.mapping.sql.SqlText;
import com.ibatis.sqlmap.engine.mapping.sql.dynamic.DynamicSql;
import com.ibatis.sqlmap.engine.mapping.sql.dynamic.elements.SqlTag;
import com.ibatis.sqlmap.engine.mapping.sql.stat.StaticSql;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.mapping.statement.StatementType;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;
import com.tool.cnv.ibatis.sqlmap.bean.SqlMapInfo;
import com.tool.cnv.ibatis.sqlmap.define.SqlType;

@SuppressWarnings("rawtypes")
public class SqlMapAnalyzerImpl implements SqlMapAnalyzer {

	static final Logger logger = LoggerFactory.getLogger(SqlMapAnalyzerImpl.class);
	static final Map<StatementType, SqlType> TYPEMAP = new HashMap<StatementType, SqlType>();
	static {
		TYPEMAP.put(StatementType.SELECT, SqlType.SELECT);
		TYPEMAP.put(StatementType.INSERT, SqlType.INSERT);
		TYPEMAP.put(StatementType.DELETE, SqlType.DELETE);
		TYPEMAP.put(StatementType.UPDATE, SqlType.UPDATE);
	}

	/**
	 * @see SqlMapAnalyzer
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, SqlMapInfo> analyze(File xml) {
		final XmlParserState xmlParserState = createXmlParserState(xml);
		final Iterator<String> statementNames = xmlParserState.getConfig().getDelegate().getMappedStatementNames();
		final Map<String, SqlMapInfo> sqlMap = new HashMap<String, SqlMapInfo>();
		MappedStatement statement;
		final String namespace = StringUtils.defaultString((String) getPrivateField(xmlParserState, "namespace"), "");
		while (statementNames.hasNext()) {
			statement = xmlParserState.getConfig().getDelegate().getMappedStatement(statementNames.next());
			final SqlMapInfo sqlMapInfo = new SqlMapInfo();
			sqlMapInfo.setId(statement.getId());
			sqlMapInfo.setType(TYPEMAP.get(statement.getStatementType()));
			sqlMapInfo.setParameterClass(statement.getParameterClass());
			sqlMapInfo.setResultClass(
					(statement.getResultMap() != null ? statement.getResultMap().getResultClass() : null));
			final StatementScope scope = new StatementScope(new SessionScope());
			scope.setStatement(statement);
			sqlMapInfo.appendStatement(statement.getSql().getSql(scope, null).trim());
			if (statement.getSql() instanceof StaticSql) {
				if (0 < statement.getParameterMap().getParameterCount()) {
					convertParameterMappings(statement.getParameterMap().getParameterMappings(),
							sqlMapInfo.getParameterClass(), sqlMapInfo);
				}
			} else if (statement.getSql() instanceof DynamicSql) {
				findAllProperties((List<SqlChild>) getPrivateField((DynamicSql) statement.getSql(), "children"),
						sqlMapInfo.getParameterClass(), sqlMapInfo);
			} else {
				logger.error(statement.getSql().getClass() + " is not supported.");
			}
			sqlMap.put(StringUtils.join(new String[] { namespace, sqlMapInfo.getId() }, "."), sqlMapInfo);
		}
		return sqlMap;
	}

	@SuppressWarnings("unchecked")
	private void findAllProperties(List<SqlChild> children, Class parameterClass, SqlMapInfo sqlMapInfo) {
		for (SqlChild child : children) {
			if (child instanceof SqlText) {
				final SqlText sqlText = (SqlText) child;
				convertParameterMappings(sqlText.getParameterMappings(), parameterClass, sqlMapInfo);
				sqlMapInfo.appendStatement(sqlText.getText());
			} else if (child instanceof SqlTag) {
				final SqlTag sqlTag = (SqlTag) child;
				if (StringUtils.isNotEmpty(sqlTag.getPropertyAttr())) {
					sqlMapInfo.addPropertyType(sqlTag.getPropertyAttr(),
							findPropertyType(parameterClass, sqlTag.getPropertyAttr()));
				}
				if (StringUtils.isNotEmpty(sqlTag.getComparePropertyAttr())) {
					sqlMapInfo.addPropertyType(sqlTag.getComparePropertyAttr(),
							findPropertyType(parameterClass, sqlTag.getComparePropertyAttr()));
				}
				sqlMapInfo.appendStatement(buildBeginSqlTag(sqlTag));
				findAllProperties((List<SqlChild>) getPrivateField(child, "children"), parameterClass, sqlMapInfo);
				sqlMapInfo.appendStatement("</" + sqlTag.getName() + ">");
			} else {
				logger.error(child.getClass() + " is not supported.");
			}
		}
	}

	private String buildBeginSqlTag(SqlTag sqlTag) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<" + sqlTag.getName());
		if (StringUtils.isNotEmpty(sqlTag.getPrependAttr())) {
			sb.append(" prepend=\"" + sqlTag.getPrependAttr() + "\"");
		}
		if (StringUtils.isNotEmpty(sqlTag.getPropertyAttr())) {
			sb.append(" property=\"" + sqlTag.getPropertyAttr() + "\"");
		}
		if (StringUtils.isNotEmpty(sqlTag.getRemoveFirstPrepend())) {
			sb.append(" removeFirstPrepend=\"" + sqlTag.getRemoveFirstPrepend() + "\"");
		}
		if (StringUtils.isNotEmpty(sqlTag.getComparePropertyAttr())) {
			sb.append(" compareProperty=\"" + sqlTag.getComparePropertyAttr() + "\"");
		}
		if (StringUtils.isNotEmpty(sqlTag.getCompareValueAttr())) {
			sb.append(" compareValue=\"" + sqlTag.getCompareValueAttr() + "\"");
		}
		if (StringUtils.isNotEmpty(sqlTag.getOpenAttr())) {
			sb.append(" open=\"" + sqlTag.getOpenAttr() + "\"");
		}
		if (StringUtils.isNotEmpty(sqlTag.getCloseAttr())) {
			sb.append(" close=\"" + sqlTag.getCloseAttr() + "\"");
		}
		if (StringUtils.isNotEmpty(sqlTag.getConjunctionAttr())) {
			sb.append(" conjunction=\"" + sqlTag.getConjunctionAttr() + "\"");
		}
		sb.append(">");
		return sb.toString();
	}

	private XmlParserState createXmlParserState(File xml) {
		final XmlParserState xmlParserState = new XmlParserState();
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(xml));
			(new SqlMapParser(xmlParserState)).parse(stream);
		} catch (NodeletException | FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		return xmlParserState;
	}

	private Class findPropertyType(Class targetClass, String propertyName) {
		if (targetClass == null) {
			return null;
		}
		Class resultClass = targetClass;
		try {
			for (String name : StringUtils.split(propertyName, ".")) {
				if (resultClass == null) {
					break;
				}
				resultClass = PropertyUtils.getPropertyType(resultClass.newInstance(), name);
			}
			return resultClass;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	private Object getPrivateField(Object target, String fieldName) {
		try {
			final Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(target);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void convertParameterMappings(ParameterMapping[] mappings, Class parameterClass, SqlMapInfo sqlMapInfo) {
		if (mappings == null) {
			return;
		}
		for (ParameterMapping mapping : mappings) {
			sqlMapInfo.addPropertyType(mapping.getPropertyName(),
					findPropertyType(parameterClass, mapping.getPropertyName()));
		}
	}

}
