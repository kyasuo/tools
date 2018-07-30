package com.tool.migration.visitor;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.ModifierVisitor;

/**
 * Migrate commons-lang package name from lang to lang3
 */
public class CommonsLangModifierVisitor extends ModifierVisitor<VisitorContext> {

	private static final String BEFORE_PACKAGE = "org.apache.commons.lang.";
	private static final String AFTER_PACKAGE = "org.apache.commons.lang3.";

	/**
	 * Change import statement
	 */
	@Override
	public Node visit(ImportDeclaration declaration, VisitorContext context) {

		final String importName = declaration.getNameAsString();

		if (importName.startsWith(BEFORE_PACKAGE)) {
			declaration.setName(importName.replace(BEFORE_PACKAGE, AFTER_PACKAGE));
		}

		return super.visit(declaration, context);
	}

}
