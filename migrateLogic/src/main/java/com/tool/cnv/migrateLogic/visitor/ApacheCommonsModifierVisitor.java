package com.tool.cnv.migrateLogic.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.tool.cnv.migrateLogic.ReplacementParameter;
import com.tool.cnv.migrateLogic.VisitorContext;

/**
 * Migrate from apache-commons libraries
 */
public class ApacheCommonsModifierVisitor extends VoidVisitorAdapter<VisitorContext> {

	/**
	 * Replacements of package statement
	 */
	private final Map<String, String> packageReplacements = new HashMap<String, String>();

	/**
	 * Constructor
	 */
	public ApacheCommonsModifierVisitor() {
		super();

		packageReplacements.put("org.apache.commons.lang.", "org.apache.commons.lang3.");
	}

	private boolean complete = false;

	@Override
	public void visit(ClassOrInterfaceDeclaration declaration, VisitorContext context) {
		if (!complete) {
			for (Entry<String, String> entry : packageReplacements.entrySet()) {
				context.addReplacementParameter(new ReplacementParameter(entry.getKey(), entry.getValue(), true));
			}
			complete = true;
		}
		super.visit(declaration, context);
	}

}
