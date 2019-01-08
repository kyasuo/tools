package com.tool.cnv.migrateLogic.visitor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.tool.cnv.migrateLogic.ReplacementParameter;
import com.tool.cnv.migrateLogic.VisitorContext;
import com.tool.cnv.migrateLogic.util.RegexUtil;

/**
 * Migrate from setter injection to constructor one
 */
public class ConstructorModifierVisitor extends VoidVisitorAdapter<VisitorContext> {

	private final Map<String, FieldDeclaration> privateFields = new HashMap<String, FieldDeclaration>();

	/**
	 * Replace constructor
	 */
	@Override
	public void visit(ConstructorDeclaration declaration, VisitorContext context) {
		context.addReplacementParameter(
		        new ReplacementParameter(RegexUtil.escape(declaration.toString()), StringUtils.EMPTY, false));
		super.visit(declaration, context);
	}

	/**
	 * Find private final fields
	 */
	@Override
	public void visit(FieldDeclaration declaration, VisitorContext context) {
		final String fieldName = declaration.getVariable(0).getNameAsString();
		if (declaration.getModifiers().size() == 2 && declaration.isPrivate() && declaration.isFinal()) {
			// add constructor argument
			context.addConstructorArgMap(fieldName, declaration.getElementType().asString());
		} else if (declaration.getModifiers().size() == 1 && declaration.isPrivate()) {
			privateFields.put(fieldName, declaration.clone());
		}
		// FIXME change field injection(@Autowired,@Inject)
		super.visit(declaration, context);
	}

	/**
	 * Replace private field accessors
	 */
	@Override
	public void visit(MethodDeclaration declaration, VisitorContext context) {
		if (declaration.isPublic()) {
			final String methodName = declaration.getNameAsString();
			final NodeList<Parameter> parameters = declaration.getParameters();
			String fieldName = null;
			FieldDeclaration fDeclaration = null;
			if (methodName.startsWith("set") && parameters.size() == 1) {
				fieldName = StringUtils.uncapitalize(methodName.replace("set", ""));
				fDeclaration = privateFields.get(fieldName);
			}
			if (fDeclaration != null) {
				// replace method
				if (declaration.getComment().isPresent() || declaration.hasJavaDocComment()) {
					context.addReplacementParameter(new ReplacementParameter(
					        RegexUtil.regexWithCommnet(RegexUtil.escape(declaration.getTokenRange().get().toString())),
					        StringUtils.EMPTY, false));
				} else {
					context.addReplacementParameter(new ReplacementParameter(
					        RegexUtil.escape(declaration.getTokenRange().get().toString()), StringUtils.EMPTY, false));
				}
				// replace field
				final String original = fDeclaration.toString();
				fDeclaration.addModifier(Keyword.FINAL);
				fDeclaration.getVariable(0).removeInitializer();
				final String replacement = fDeclaration.toString();
				context.addReplacementParameter(
				        new ReplacementParameter(RegexUtil.escape(original.substring(original.indexOf("private"))),
				                replacement.substring(replacement.indexOf("private")), false));
				// add constructor argument
				context.addConstructorArgMap(fieldName, parameters.get(0).getType().asString());
			}
		}
		super.visit(declaration, context);
	}
}
