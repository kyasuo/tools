package com.tool.util;

import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;

public class JavaParserUtil {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Node getParentExpression(Expression expression, Class cls) {
		Node parent = expression;
		while ((parent = parent.getParentNode().get()) != null) {
			if (cls.isAssignableFrom(parent.getClass())) {
				return parent;
			}
		}
		return null;
	}

	public static FieldDeclaration addFieldFirst(ClassOrInterfaceDeclaration declaration, String type, String name,
			Set<Modifier> modifiers) {
		for (BodyDeclaration<?> member : declaration.getMembers()) {
			if (!member.isFieldDeclaration()) {
				continue;
			}
			final FieldDeclaration field = member.asFieldDeclaration();
			if (type.equals(field.getVariable(0).getTypeAsString())
					&& name.equals(field.getVariable(0).getNameAsString())) {
				return field;
			}
		}
		final FieldDeclaration fieldDeclaration = new FieldDeclaration();
		fieldDeclaration.getVariables().add(new VariableDeclarator(JavaParser.parseType(type), name));
		for (Modifier modifier : modifiers) {
			fieldDeclaration.setModifier(modifier, true);
		}
		fieldDeclaration.setJavadocComment(type);
		declaration.getMembers().addFirst(fieldDeclaration);
		return fieldDeclaration;
	}
}
