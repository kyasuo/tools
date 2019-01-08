package com.tool.cnv.migrateLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;

public class VisitorContext {

	private final String lineSeparator;
	private final String encoding;
	private final String fileName;
	private final String className;

	private final List<ReplacementParameter> replacementParameters = new ArrayList<ReplacementParameter>();
	private final Set<String> importStatements = new TreeSet<String>();
	private final Map<String, String> fieldMap = new TreeMap<String, String>(Collections.reverseOrder());
	private final Map<String, String> constructorArgMap = new TreeMap<String, String>();

	public VisitorContext(String lineSeparator, String encoding, String fileName) {
		super();
		this.lineSeparator = lineSeparator;
		this.encoding = encoding;
		this.fileName = fileName;
		this.className = FilenameUtils.getBaseName(fileName);
	}

	public void clear() {
		this.replacementParameters.clear();
		this.importStatements.clear();
		this.fieldMap.clear();
		this.constructorArgMap.clear();
	}

	public void clearFieldMap() {
		this.fieldMap.clear();
	}

	public void addFieldMap(String field, String comment) {
		this.fieldMap.put(field, comment);
	}

	public Map<String, String> getFieldMap() {
		return this.fieldMap;
	}

	public void clearImportStatements() {
		this.importStatements.clear();
	}

	public void addImportStatements(String imp) {
		this.importStatements.add(imp);
	}

	public Set<String> getImportStatements() {
		return this.importStatements;
	}

	public void clearReplacementParameters() {
		this.replacementParameters.clear();
	}

	public void addReplacementParameter(ReplacementParameter parameter) {
		this.replacementParameters.add(parameter);
	}

	public List<ReplacementParameter> getReplacementParameters() {
		return replacementParameters;
	}

	public void clearConstructorArgMap() {
		this.constructorArgMap.clear();
	}

	public void addConstructorArgMap(String key, String value) {
		this.constructorArgMap.put(key, value);
	}

	public Map<String, String> getConstructorArgMap() {
		return constructorArgMap;
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getFileName() {
		return fileName;
	}

	public String getClassName() {
		return className;
	}

}
