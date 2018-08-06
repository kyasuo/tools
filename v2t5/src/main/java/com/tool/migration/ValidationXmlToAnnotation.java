package com.tool.migration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tool.migration.struts.ValidationAnalyzer;
import com.tool.migration.struts.ValidationAnalyzerImpl;
import com.tool.migration.struts.bean.AnnotationInfo;
import com.tool.migration.struts.bean.FormBeanInfo;
import com.tool.migration.struts.bean.GroupType;
import com.tool.migration.struts.bean.PropertyInfo;
import com.tool.util.PropertyUtil;

public class ValidationXmlToAnnotation {
	private static final File WEBINF_DIR = new File(PropertyUtil.getProperty("web-inf.dir"));

	private static final File SRC_DIR = new File(PropertyUtil.getProperty("source.dir"));

	private static final String LINE_SEPARATOR = PropertyUtil.getProperty("source.line.separator", "\n");

	private static final String SPACE_INDENT = StringUtils.leftPad("",
	        PropertyUtil.getPropertyInt("source.space.indent", 4), " ");

	private static final String ENCODING = PropertyUtil.getProperty("source.encoding", "UTF-8");

	private static final Logger logger = LoggerFactory.getLogger(ValidationXmlToAnnotation.class);

	private static final Pattern FIELD_PATTERN = Pattern.compile("^[ \t]+private [^ ]+ ([^ =;]+).*;.*$");

	private static final ValidationAnalyzer analyzer = new ValidationAnalyzerImpl();

	public static void main(String[] args) throws Exception {

		final Map<String, FormBeanInfo> validationMap = analyzer.analyze(
		        FileUtils.listFiles(WEBINF_DIR, getStrutsConfigFilter(), FileFilterUtils.trueFileFilter())
		                .toArray(new File[] {}),
		        FileUtils.listFiles(WEBINF_DIR, getValidationFilter(), FileFilterUtils.trueFileFilter())
		                .toArray(new File[] {}));

		convertFormBean(validationMap);
	}

	private static void convertFormBean(Map<String, FormBeanInfo> validationMap) throws IOException {

		final Set<String> importStatements = new TreeSet<String>();
		final Set<String> grouptInterfaces = new TreeSet<String>();
		for (Entry<String, FormBeanInfo> fbiEntry : validationMap.entrySet()) {
			final File src = new File(SRC_DIR, fbiEntry.getKey().replace(".", "/") + ".java");
			final File dest = new File(src.getParentFile(), "edit_" + src.getName()); // FIXME
			if (!src.exists()) {
				logger.warn("{} is not found." + src);
				continue;
			}

			final FormBeanInfo formBeanInfo = fbiEntry.getValue();
			final Set<String> groupTypeSet = new HashSet<String>();
			for (GroupType groupType : formBeanInfo.getGroupTypeSet()) {
				grouptInterfaces.add(SPACE_INDENT + "public static interface " + groupType.getSimpleType() + " {};");
				groupTypeSet.add(groupType.getType());
			}

			final Map<String, List<String>> annotaionLineMap = new HashMap<String, List<String>>();
			for (Entry<String, PropertyInfo> piEntry : formBeanInfo.getPropertyMap().entrySet()) {
				final PropertyInfo pInfo = piEntry.getValue();
				for (Entry<String, AnnotationInfo> aiInfo : pInfo.getAnnotaionMap().entrySet()) {
					final String fieldName = piEntry.getKey();
					if (!annotaionLineMap.containsKey(fieldName)) {
						annotaionLineMap.put(fieldName, new ArrayList<String>());
					}
					annotaionLineMap.get(fieldName).add(SPACE_INDENT + aiInfo.getValue().toAnnotaion());
					importStatements.add("import " + aiInfo.getKey() + ";");

					final AnnotationInfo annotaionInfo = aiInfo.getValue();
					if (annotaionInfo.getGroupType() != null
					        && !groupTypeSet.contains(annotaionInfo.getGroupType().getType())) {
						importStatements.add("import " + annotaionInfo.getGroupType().getType() + ";");
					}
				}
			}

			logger.info("convert FormBean from {} to {}.", src, dest);
			final List<String> lines = new ArrayList<String>();
			Matcher mch;
			for (String line : FileUtils.readLines(src, ENCODING)) {
				if (line.startsWith("package ") && !importStatements.isEmpty()) {
					lines.add(line);
					lines.add("");
					lines.addAll(importStatements);
					lines.add("");
				} else if (line.startsWith("public class ") && !grouptInterfaces.isEmpty()) {
					lines.add(line);
					lines.add("");
					lines.addAll(grouptInterfaces);
					lines.add("");
				} else {
					mch = FIELD_PATTERN.matcher(line);
					if (mch.find()) {
						final String fieldName = mch.group(1);
						if (annotaionLineMap.containsKey(fieldName)) {
							lines.addAll(annotaionLineMap.get(fieldName));
							annotaionLineMap.remove(fieldName);
						}
					}
					lines.add(line);
				}
			}
			if (!annotaionLineMap.isEmpty()) {
				logger.warn(" >> annotaionLineMap is not empty.", annotaionLineMap);
			}
			FileUtils.writeLines(dest, ENCODING, lines);
			importStatements.clear();
			grouptInterfaces.clear();
		}

	}

	private static IOFileFilter getStrutsConfigFilter() {
		return FileFilterUtils.and(FileFilterUtils.suffixFileFilter(".xml"),
		        FileFilterUtils.prefixFileFilter("struts-"));
	}

	private static IOFileFilter getValidationFilter() {
		return FileFilterUtils.and(FileFilterUtils.suffixFileFilter(".xml"), FileFilterUtils.or(
		        FileFilterUtils.prefixFileFilter("validator-rules"), FileFilterUtils.prefixFileFilter("validation")));
	}
}
