package com.tool.migration.struts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.io.FileUtils;
import org.apache.commons.validator.ValidatorResources;
import org.apache.struts.config.ConfigRuleSet;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ModuleConfigFactory;
import org.xml.sax.SAXException;

public class ConfigFileUtil {

	static final String[] registrations = { "-//Apache Software Foundation//DTD Struts Configuration 1.0//EN",
	        "/org/apache/struts/resources/struts-config_1_0.dtd",
	        "-//Apache Software Foundation//DTD Struts Configuration 1.1//EN",
	        "/org/apache/struts/resources/struts-config_1_1.dtd",
	        "-//Apache Software Foundation//DTD Struts Configuration 1.2//EN",
	        "/org/apache/struts/resources/struts-config_1_2.dtd",
	        "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN", "/org/apache/struts/resources/web-app_2_2.dtd",
	        "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", "/org/apache/struts/resources/web-app_2_3.dtd" };

	public static ModuleConfig loadStrutsConfigFiles(File[] configFiles) throws IOException, SAXException {
		final Digester configDigester = new Digester();
		configDigester.setNamespaceAware(true);
		configDigester.setValidating(false);
		configDigester.setUseContextClassLoader(true);
		configDigester.addRuleSet(new ConfigRuleSet());
		for (int i = 0; i < registrations.length; i += 2) {
			URL url = ConfigFileUtil.class.getResource(registrations[i + 1]);
			if (url != null) {
				configDigester.register(registrations[i], url.toString());
			}
		}
		final ModuleConfig moduleConfig = ModuleConfigFactory.createFactory().createModuleConfig("prefix");

		for (File configFile : configFiles) {
			configDigester.push(moduleConfig);
			configDigester.parse(configFile);
		}

		final FormBeanConfig formBeanConfig[] = moduleConfig.findFormBeanConfigs();
		for (int i = 0; i < formBeanConfig.length; i++) {
			if (formBeanConfig[i].getDynamic()) {
				formBeanConfig[i].getDynaActionFormClass();
			}
		}

		return moduleConfig;
	}

	public static ValidatorResources loadValidationFiles(File[] configFiles) throws IOException, SAXException {
		final List<InputStream> streamList = new ArrayList<InputStream>();
		for (File configFile : configFiles) {
			streamList.add(FileUtils.openInputStream(configFile));
		}
		return new ValidatorResources(streamList.toArray(new InputStream[] {}));
	}
}
