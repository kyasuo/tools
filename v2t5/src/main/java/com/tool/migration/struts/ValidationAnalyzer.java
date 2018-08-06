package com.tool.migration.struts;

import java.io.File;
import java.util.Map;

import com.tool.migration.struts.bean.FormBeanInfo;

public interface ValidationAnalyzer {

	/**
	 * analyze struts-config and validation files
	 * 
	 * @param strutsConfigFiles
	 *            struts-config file array
	 * @param validationFiles
	 *            validation file array
	 * @return {@link java.util.Map} FormBeanInfo(key: form/bean type(FQDN),
	 *         value: FormBeanInfo)
	 */
	Map<String, FormBeanInfo> analyze(File[] strutsConfigFiles, File[] validationFiles) throws Exception;

}
