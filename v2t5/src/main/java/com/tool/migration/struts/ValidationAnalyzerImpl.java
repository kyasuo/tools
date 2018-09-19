package com.tool.migration.struts;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.FormSet;
import org.apache.commons.validator.Msg;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.Var;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tool.migration.struts.bean.AnnotationInfo;
import com.tool.migration.struts.bean.FormBeanInfo;
import com.tool.migration.struts.bean.GroupType;
import com.tool.migration.struts.bean.PropertyInfo;
import com.tool.migration.struts.bean.VarDefine;
import com.tool.util.ClassUtil;
import com.tool.util.PropertyUtil;

public class ValidationAnalyzerImpl implements ValidationAnalyzer {

	private static final Logger logger = LoggerFactory.getLogger(ValidationAnalyzerImpl.class);

	private static final String ANNOTATION_PREFIX = PropertyUtil.getProperty("validation.annotation.prefix");
	private static final Map<String, VarDefine> VAR_MAPPING = new HashMap<String, VarDefine>();
	static {
		for (VarDefine value : VarDefine.values()) {
			VAR_MAPPING.put(value.getPrev(), value);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, FormBeanInfo> analyze(File[] strutsConfigFiles, File[] validationFiles) throws Exception {

		// load struts-config and validation files
		final ModuleConfig moduleConfig = ConfigFileUtil.loadStrutsConfigFiles(strutsConfigFiles);
		final ValidatorResources validationResources = ConfigFileUtil.loadValidationFiles(validationFiles);

		// parse validation info
		final Map<String, FormBeanInfo> validationMap = new HashMap<String, FormBeanInfo>();
		final Resolver resolver = new DefaultResolver();
		for (Form form : getValidationForms(validationResources)) {
			final ActionConfig actionConfig = moduleConfig.findActionConfig(form.getName());
			final FormBeanConfig formBeanConfig = moduleConfig.findFormBeanConfig(actionConfig.getName());
			if (formBeanConfig.getDynamic()) {
				logger.warn("DynaActionForm is not supported. form-validation={}, formName={}, actionPath={}", form,
				        formBeanConfig, actionConfig.getPath());
				continue;
			}

			final String formType = formBeanConfig.getType();
			final String groupType = getGroupType(formType, form.getName());
			if (!validationMap.containsKey(formType)) {
				validationMap.put(formType, new FormBeanInfo(formType));
			}
			final FormBeanInfo formInfo = validationMap.get(formType);
			formInfo.addGroupType(new GroupType(groupType));

			for (int i = 0; i < form.getFields().size(); i++) {
				final Field field = (Field) form.getFields().get(i);
				String property = field.getProperty();
				Class beanClass = ClassUtil.getClassIgnoreException(formType);
				if (resolver.hasNested(property)) {
					while (resolver.hasNested(property)) {
						final String name = resolver.next(property);
						beanClass = findClassIncludingField(beanClass, name);
						final String beanType = beanClass.getName();

						if (!validationMap.containsKey(beanType)) {
							validationMap.put(beanType, new FormBeanInfo(beanType));
						}
						final FormBeanInfo beanInfo = validationMap.get(beanType);

						if (!beanInfo.getPropertyMap().containsKey(name)) {
							beanInfo.addProperty(name, new PropertyInfo(name));
						}
						final PropertyInfo propertyInfo = beanInfo.getPropertyMap().get(name);
						propertyInfo.addAnnotaion(new AnnotationInfo(javax.validation.constraints.NotNull.class.getName(), new GroupType(groupType)));
						propertyInfo.addAnnotaion(new AnnotationInfo(javax.validation.Valid.class.getName(), null));

						beanClass = findPropertyType(ClassUtil.getClassIgnoreException(beanType), name);
						property = resolver.remove(property);
					}

					beanClass = findClassIncludingField(beanClass, property);
					final String beanType = beanClass.getName();
					if (!validationMap.containsKey(beanType)) {
						validationMap.put(beanType, new FormBeanInfo(beanType));
					}
					convertDependToAnnotation(validationMap.get(beanType), groupType, property, field);
				} else {
					beanClass = findClassIncludingField(beanClass, property);
					final String beanType = beanClass.getName();
					if (!validationMap.containsKey(beanType)) {
						validationMap.put(beanType, new FormBeanInfo(beanType));
					}
					convertDependToAnnotation(validationMap.get(beanType), groupType, property, field);
				}
			}

		}

		return validationMap;
	}

	private Class findClassIncludingField(Class target, String fieldName) {
		java.lang.reflect.Field field = null;
		Class clazz = target;
		while (clazz != null) {
			try {
				field = clazz.getDeclaredField(fieldName);
				break;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		clazz = field != null ? field.getDeclaringClass() : target;
		logger.debug("{} -> {} to {}", fieldName, target, clazz);
		return clazz;
	}

	@SuppressWarnings("unchecked")
	private List<Form> getValidationForms(ValidatorResources validationResources) {
		final List<Form> forms = new ArrayList<Form>();
		final FormSet formSet = (FormSet) getPrivateField(validationResources, "defaultFormSet");
		if (formSet != null) {
			forms.addAll(formSet.getForms().values());
		}
		return forms;
	}

	private Object getPrivateField(Object target, String fieldName) {
		try {
			final java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(target);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	private Class findPropertyType(Class targetClass, String propertyName) {
		if (targetClass == null) {
			return null;
		}
		try {
			final PropertyDescriptor descriptor = new PropertyDescriptor(propertyName, targetClass);
			java.lang.reflect.Type propertyType = null;
			if (descriptor.getReadMethod() != null) {
				propertyType = descriptor.getReadMethod().getGenericReturnType();
			}
			if (propertyType == null && descriptor.getWriteMethod() != null) {
				propertyType = descriptor.getWriteMethod().getGenericParameterTypes()[0];
			}
			if (propertyType != null) {
				final String propertyTypeName = propertyType.getTypeName();
				if (propertyTypeName.startsWith(java.util.List.class.getTypeName())
				        || propertyTypeName.startsWith(java.util.Set.class.getTypeName())) {
					return Class.forName(((ParameterizedType) propertyType).getActualTypeArguments()[0].getTypeName());
				} else {
					return Class.forName(propertyTypeName);
				}
			} else {
				return PropertyUtils.getPropertyType(targetClass.newInstance(), propertyName);
			}
		} catch (IntrospectionException | ClassNotFoundException | IllegalAccessException | InvocationTargetException
		        | NoSuchMethodException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	private void convertDependToAnnotation(FormBeanInfo beanInfo, String groupType, String property, Field field) {
		if (!beanInfo.getPropertyMap().containsKey(property)) {
			beanInfo.addProperty(property, new PropertyInfo(property));
		}
		final PropertyInfo propertyInfo = beanInfo.getPropertyMap().get(property);
		for (Object dependency : field.getDependencyList()) {
			propertyInfo.addAnnotaion(getValidationAnnotation(groupType, (String) dependency, field));
		}
	}

	protected AnnotationInfo getValidationAnnotation(String groupType, String depend, Field field) {
		final AnnotationInfo annotaionInfo = new AnnotationInfo(convertDependToAnnotation(depend),
		        new GroupType(groupType));

		Msg msg = field.getMessage(depend);
		if (msg != null) {
			annotaionInfo.addParam("message", msg.getKey());
		}

		for (Arg msgArg : field.getArgs(depend)) {
			if (msgArg != null) {
				annotaionInfo.addParam("fieldNames", msgArg.getKey());
			}
		}

		Var var = field.getVar(depend);
		if (var != null) {
			VarDefine define = VAR_MAPPING.get(var.getName());
			if (define != null) {
				annotaionInfo.addParam(define.getNext(), convertVarValue(var.getValue(), define));
			} else {
				logger.warn("var-name \"{}\" is unknown.", var.getName());
			}
		}

		return annotaionInfo;
	}

	protected Object convertVarValue(String varValue, VarDefine define) {
		// FIXME
		if (define.getType() == Integer.class) {
			return Integer.parseInt(varValue);
		} else {
			return varValue;
		}
	}

	protected String convertDependToAnnotation(String depend) {
		return ANNOTATION_PREFIX + StringUtils.capitalize(depend);
	}

	protected String getGroupType(String formType, String formName) {
		return formType + "." + formName.replaceAll("[/_-]", ""); // FIXME
	}

}
