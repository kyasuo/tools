package com.tool.cnv.migrateLogic.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.tool.cnv.migrateLogic.ReplacementParameter;
import com.tool.cnv.migrateLogic.VisitorContext;
import com.tool.cnv.migrateLogic.util.RegexUtil;

/**
 * Migrate from commons-logging and log4j to slf4j
 */
public class LoggerModifierVisitor extends VoidVisitorAdapter<VisitorContext> {

	private static final String[] SLF4J_IMPORT_ARRAY = new String[] { "import org.slf4j.Logger;",
	        "import org.slf4j.LoggerFactory;" };

	/**
	 * Replacements of import statement
	 */
	private final Map<String, String> importReplacements = new HashMap<String, String>();

	/**
	 * Logger element types
	 */
	private final Set<String> loggerElementTypes = new HashSet<String>();

	/**
	 * Constructor
	 */
	public LoggerModifierVisitor() {
		super();

		importReplacements.put("import org.apache.log4j.Logger;", null);
		importReplacements.put("import org.apache.commons.logging.Log;", null);
		importReplacements.put("import org.apache.commons.logging.LogFactory;", null);

		loggerElementTypes.add("Logger");
		loggerElementTypes.add("org.apache.log4j.Logger");
		loggerElementTypes.add("Log");
		loggerElementTypes.add("LogFactory");
		loggerElementTypes.add("org.apache.commons.logging.Log");
		loggerElementTypes.add("org.apache.commons.logging.LogFactory");
	}

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
				context.addReplacementParameter(new ReplacementParameter(declaration.toString(), StringUtils.EMPTY, false));
			} else {
				context.addReplacementParameter(new ReplacementParameter(importStatement, replacement, false));
			}
		}
		super.visit(declaration, context);
	}

	/**
	 * Replace logger field
	 */
	@Override
	public void visit(FieldDeclaration declaration, VisitorContext context) {
		final String elementType = declaration.getElementType().asString();
		if (loggerElementTypes.contains(elementType)) {

			final FieldDeclaration cDeclaration = declaration.clone();

			// remove comment attached to field
			cDeclaration.removeComment();
			cDeclaration.removeJavaDocComment();

			final VariableDeclarator variable = cDeclaration.getVariable(0);
			variable.setType("Logger");
			if (variable.getInitializer().isPresent()) {
				final MethodCallExpr expression = (MethodCallExpr) variable.getInitializer().get();
				final Expression scope = expression.getScope().get();
				if (scope instanceof NameExpr) {
					((NameExpr) scope).setName("LoggerFactory");
				} else if (scope instanceof FieldAccessExpr) {
					expression.setScope(new NameExpr("LoggerFactory"));
				}
				expression.setName("getLogger");
			}

			context.addReplacementParameter(new ReplacementParameter(
			        RegexUtil.escape(declaration.getTokenRange().get().toString()), cDeclaration.toString(), false));

			for (String imp : SLF4J_IMPORT_ARRAY) {
				context.addImportStatements(imp);
			}

		}
		super.visit(declaration, context);
	}

}
