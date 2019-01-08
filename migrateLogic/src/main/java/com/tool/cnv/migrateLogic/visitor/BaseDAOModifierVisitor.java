package com.tool.cnv.migrateLogic.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.tool.cnv.migrateLogic.ReplacementParameter;
import com.tool.cnv.migrateLogic.VisitorContext;
import com.tool.cnv.migrateLogic.util.PropertyUtil;
import com.tool.cnv.migrateLogic.util.RegexUtil;

/**
 * Migrate from QueryDAO/UpdateDAO to Repository
 */
public abstract class BaseDAOModifierVisitor extends VoidVisitorAdapter<VisitorContext> {

	/**
	 * DAO field name
	 */
	private String daoFieldName = null;

	/**
	 * Static fields
	 */
	private final Map<String, String> staticFields = new HashMap<String, String>();

	/**
	 * Replacements of import statement
	 */
	private final Map<String, String> importReplacements = new HashMap<String, String>();

	/**
	 * DAO element types
	 */
	private final Set<String> daoElementTypes = new HashSet<String>();

	/**
	 * Constructor
	 */
	public BaseDAOModifierVisitor() {
		super();
		setupImportReplacements(importReplacements);
		setupDaoElementTypes(daoElementTypes);
	}

	abstract void setupImportReplacements(Map<String, String> importReplacements);

	abstract void setupDaoElementTypes(Set<String> daoElementTypes);

	/**
	 * Replace import statements
	 */
	@Override
	public void visit(ImportDeclaration declaration, VisitorContext context) {
		final String importStatement = declaration.toString().trim();
		if (importReplacements.containsKey(importStatement)) {
			final String replacement = importReplacements.get(importStatement);
			if (replacement == null) {
				// regex with line separator characters
				context.addReplacementParameter(
				        new ReplacementParameter(declaration.toString(), StringUtils.EMPTY, false));
			} else {
				context.addReplacementParameter(new ReplacementParameter(importStatement, replacement, false));
			}
		}
		super.visit(declaration, context);
	}

	/**
	 * Replace dao field
	 */
	@Override
	public void visit(FieldDeclaration declaration, VisitorContext context) {
		final VariableDeclarator variable = declaration.getVariable(0);
		final String elementType = declaration.getElementType().asString();
		if (daoElementTypes.contains(elementType)) {
			daoFieldName = variable.getNameAsString();
			if (declaration.getComment().isPresent() || declaration.hasJavaDocComment()) {
				context.addReplacementParameter(new ReplacementParameter(
				        RegexUtil.regexWithCommnet(RegexUtil.escape(declaration.getTokenRange().get().toString())),
				        StringUtils.EMPTY, false));
			} else {
				context.addReplacementParameter(new ReplacementParameter(
				        RegexUtil.escape(declaration.getTokenRange().get().toString()), StringUtils.EMPTY, false));
			}
		} else if (declaration.isStatic() && String.class.getSimpleName().equals(elementType)
		        || String.class.getName().equals(elementType)) {
			if (variable.getInitializer().isPresent() && !variable.getInitializer().get().isNullLiteralExpr()
			        && !variable.getInitializer().get().isArrayInitializerExpr()) {
				// cache static field to get sqlid
				staticFields.put(variable.getNameAsString(),
				        variable.getInitializer().get().asStringLiteralExpr().getValue());
			}
		}
		super.visit(declaration, context);
	}

	/**
	 * Replace dao field accessors
	 */
	@Override
	public void visit(MethodDeclaration declaration, VisitorContext context) {
		if (declaration.isPublic()) {
			final String methodName = declaration.getNameAsString();
			final NodeList<Parameter> parameters = declaration.getParameters();
			boolean remove = false;
			if (methodName.startsWith("set") && parameters.size() == 1) {
				remove = daoElementTypes.contains(parameters.get(0).getType().asString());
			} else if (methodName.startsWith("get") && parameters.size() == 0) {
				remove = daoElementTypes.contains(declaration.getType().asString());
			}
			if (remove) {
				if (declaration.getComment().isPresent() || declaration.hasJavaDocComment()) {
					context.addReplacementParameter(new ReplacementParameter(
					        RegexUtil.regexWithCommnet(RegexUtil.escape(declaration.getTokenRange().get().toString())),
					        StringUtils.EMPTY, false));
				} else {
					context.addReplacementParameter(new ReplacementParameter(
					        RegexUtil.escape(declaration.getTokenRange().get().toString()), StringUtils.EMPTY, false));
				}
			}
		}
		super.visit(declaration, context);
	}

	private static final String INDENT = StringUtils.leftPad("", PropertyUtil.getPropertyInt("source.indent.width"),
	        PropertyUtil.getProperty("source.indent.char"));
	private static final String FIELD_COMMENT_BEGIN = INDENT + "/**";
	private static final String FIELD_COMMENT_PREFIX = INDENT + " * ";
	private static final String FIELD_COMMENT_END = INDENT + " */";
	private static final String FIELD_PREFIX = INDENT + "private final ";

	protected void addRepositoryField(VisitorContext context, String repository, String fieldName) {
		final String repositoryName = repository.substring(repository.lastIndexOf(".") + 1);
		context.addImportStatements("import " + repository + ";");
		context.addFieldMap(fieldName,
		        StringUtils.join(new String[] { FIELD_COMMENT_BEGIN, FIELD_COMMENT_PREFIX + repositoryName,
		                FIELD_COMMENT_END, FIELD_PREFIX + repositoryName + " " + fieldName + ";" },
		                context.getLineSeparator()));
	}

	private static final String REPOSITORY_PACKAGE_BASE = PropertyUtil.getProperty("repository.package.base");
	private static final String REPOSITORY_NAME_PREFIX = PropertyUtil.getProperty("repository.name.prefix");
	private static final String REPOSITORY_NAME_SUFFIX = PropertyUtil.getProperty("repository.name.suffix");
	private static final String REPOSITORY_METHOD_PREFIX = PropertyUtil.getProperty("repository.method.prefix");
	private static final String REPOSITORY_METHOD_SUFFIX = PropertyUtil.getProperty("repository.method.suffix");

	protected String getFieldNameFromSqlId(String sqlId) {
		return REPOSITORY_NAME_PREFIX.substring(0, 1).toLowerCase() + REPOSITORY_NAME_PREFIX.substring(1)
		        + sqlId.substring(0, 4).toUpperCase() + REPOSITORY_NAME_SUFFIX;
	}

	protected String getMethodNameFromSqlId(String sqlId) {
		return REPOSITORY_METHOD_PREFIX + sqlId + REPOSITORY_METHOD_SUFFIX;
	}

	protected String getRepositoryFromSqlId(String sqlId) {
		return REPOSITORY_PACKAGE_BASE + "." + sqlId.substring(0, 3).toLowerCase() + "." + REPOSITORY_NAME_PREFIX
		        + sqlId.substring(0, 4).toUpperCase() + REPOSITORY_NAME_SUFFIX;
	}

	protected String getSqlId(Expression expr) {
		if (expr instanceof StringLiteralExpr) {
			return ((StringLiteralExpr) expr).asString();
		} else if (expr instanceof NameExpr) {
			return staticFields.get(((NameExpr) expr).getNameAsString());
		} else {
			return null;
		}
	}

	protected boolean isMethodCallExprForDaoField(Expression expr) {
		if (StringUtils.isEmpty(daoFieldName)) {
			return false;
		}
		if (expr instanceof NameExpr) {
			return StringUtils.equals(daoFieldName, ((NameExpr) expr).getNameAsString());
		} else {
			return false;
		}
	}
}
