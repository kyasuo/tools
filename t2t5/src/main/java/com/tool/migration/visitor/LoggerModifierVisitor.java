package com.tool.migration.visitor;

import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.tool.util.BooleanUtil;
import com.tool.util.ClassUtil;

/**
 * Migrate from commons-logging and log4j to slf4j
 */
public class LoggerModifierVisitor extends ModifierVisitor<VisitorContext> {

	private static final String LOG_FIELD_EXISTS_KEY = "logFieldExists";
	@SuppressWarnings("rawtypes")
	private static final Set<Class> LOGCLASSES = new HashSet<Class>();
	static {
		LOGCLASSES.add(org.apache.log4j.Logger.class);
		LOGCLASSES.add(org.apache.commons.logging.Log.class);
		LOGCLASSES.add(org.apache.commons.logging.LogFactory.class);
	}

	/**
	 * Remove unnecessary import statements
	 */
	@Override
	public Node visit(ImportDeclaration declaration, VisitorContext context) {
		if (LOGCLASSES.contains(ClassUtil.getClassIgnoreException(declaration.getNameAsString()))) {
			context.add(LoggerModifierVisitor.class.getName(), LOG_FIELD_EXISTS_KEY, true);
			return null;
		}
		return super.visit(declaration, context);
	}

	/**
	 * Change logger field
	 */
	@Override
	public Visitable visit(FieldDeclaration declaration, VisitorContext context) {
		final boolean logFieldExists = BooleanUtil
				.toBoolean(context.get(LoggerModifierVisitor.class.getName(), LOG_FIELD_EXISTS_KEY));
		final String elementType = declaration.getElementType().asString();
		if (logFieldExists && ClassUtil.isFieldBySimpleName(LOGCLASSES, elementType)
				|| ClassUtil.isFieldByName(LOGCLASSES, elementType)) {
			changeLoggerField(declaration);
		}
		return super.visit(declaration, context);
	}

	protected void changeLoggerField(FieldDeclaration declaration) {
		declaration.tryAddImportToParentCompilationUnit(org.slf4j.Logger.class);
		declaration.tryAddImportToParentCompilationUnit(org.slf4j.LoggerFactory.class);
		final VariableDeclarator variableDeclaration = declaration.getVariable(0);
		variableDeclaration.setType(org.slf4j.Logger.class);
		if (variableDeclaration.getInitializer().isPresent()) {
			final MethodCallExpr expression = (MethodCallExpr) variableDeclaration.getInitializer().get();
			final Expression scope = expression.getScope().get();
			if (scope instanceof NameExpr) {
				((NameExpr) scope).setName(org.slf4j.LoggerFactory.class.getSimpleName());
			} else if (scope instanceof FieldAccessExpr) {
				expression.setScope(new NameExpr(org.slf4j.LoggerFactory.class.getSimpleName()));
			}
			expression.setName("getLogger");
		}
	}

}
