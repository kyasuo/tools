package com.tool.migration.visitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.tool.util.BooleanUtil;
import com.tool.util.ClassUtil;
import com.tool.util.JavaParserUtil;

/**
 * Migrate from QueryDAO to Repository
 */
public class QueryDAOModifierVisitor extends ModifierVisitor<VisitorContext> {

	// FIXME get field value from property files
	private static final String REPOSITORY_BASE = "com.tool.migration.mybatis.repository";
	private static final String REPOSITORY_PREFIX = "Test";
	private static final String REPOSITORY_SUFFIX = "Repository";
	private static final String METHOD_PREFIX = "exec";
	private static final String METHOD_SUFFIX = "";

	private static final String DAO_FIELD_EXISTS_KEY = "daoFieldExists";
	private static final String DAO_FIELD_NAME = "daoFieldName";
	@SuppressWarnings("rawtypes")
	private static final Set<Class> DAOCLASSES = new HashSet<Class>();
	private static final Set<Modifier> PRIVATE_FINAL_MODIFIER = new HashSet<Modifier>();
	static {
		DAOCLASSES.add(jp.terasoluna.fw.dao.QueryDAO.class);
		PRIVATE_FINAL_MODIFIER.add(Modifier.PRIVATE);
		PRIVATE_FINAL_MODIFIER.add(Modifier.FINAL);
	}

	/**
	 * Remove unnecessary import statements
	 */
	@Override
	public Node visit(ImportDeclaration declaration, VisitorContext context) {
		if (DAOCLASSES.contains(ClassUtil.getClassIgnoreException(declaration.getNameAsString()))) {
			context.add(QueryDAOModifierVisitor.class.getName(), DAO_FIELD_EXISTS_KEY, true);
			return null;
		}
		return super.visit(declaration, context);
	}

	/**
	 * Remove QueryDao field
	 */
	@Override
	public Visitable visit(FieldDeclaration declaration, VisitorContext context) {
		final boolean daoFieldExists = BooleanUtil
		        .toBoolean(context.get(QueryDAOModifierVisitor.class.getName(), DAO_FIELD_EXISTS_KEY));
		final String elementType = declaration.getElementType().asString();
		if (daoFieldExists && ClassUtil.isFieldBySimpleName(DAOCLASSES, elementType)
		        || ClassUtil.isFieldByName(DAOCLASSES, elementType)) {
			context.add(QueryDAOModifierVisitor.class.getName(), DAO_FIELD_NAME,
			        declaration.getVariable(0).getNameAsString());
			return null;
		}
		return super.visit(declaration, context);
	}

	/**
	 * Remove QueryDao field accessors
	 */
	@Override
	public Visitable visit(MethodDeclaration declaration, VisitorContext context) {
		final boolean daoFieldExists = BooleanUtil
		        .toBoolean(context.get(QueryDAOModifierVisitor.class.getName(), DAO_FIELD_EXISTS_KEY));
		final String methodName = declaration.getNameAsString();
		final NodeList<Parameter> parameters = declaration.getParameters();

		if (declaration.isPublic() && methodName.startsWith("set") && parameters.size() == 1) {
			final String type = parameters.get(0).getType().asString();
			if (daoFieldExists && ClassUtil.isFieldBySimpleName(DAOCLASSES, type)
			        || ClassUtil.isFieldByName(DAOCLASSES, type)) {
				return null;
			}
		} else if (declaration.isPublic() && methodName.startsWith("get") && parameters.size() == 0) {
			final String type = declaration.getType().asString();
			if (daoFieldExists && ClassUtil.isFieldBySimpleName(DAOCLASSES, type)
			        || ClassUtil.isFieldByName(DAOCLASSES, type)) {
				return null;
			}
		}
		return super.visit(declaration, context);
	}

	/**
	 * Change method call expression
	 */
	@Override
	public Visitable visit(MethodCallExpr expression, VisitorContext context) {

		final String daoField = (String) context.get(QueryDAOModifierVisitor.class.getName(), DAO_FIELD_NAME);
		final Expression scope = expression.getScope().get();
		if (daoField != null && scope instanceof NameExpr && ((NameExpr) scope).getNameAsString().equals(daoField)) {
			final String callExprName = expression.getNameAsString();
			final NodeList<Expression> arguments = expression.getArguments();

			// get sqlId from arguments
			final String sqlId = arguments.removeFirst().asStringLiteralExpr().asString();
			adjustArguments(arguments);

			// get repository, fieldName and methodName from sqlId
			final String repository = getRepositoryFromSqlId(sqlId);
			final String fieldName = getFieldNameFromSqlId(sqlId);
			final String methodName = getMethodNameFromSqlId(sqlId);

			// add import and field statements
			expression.tryAddImportToParentCompilationUnit(ClassUtil.getClassIgnoreException(repository));
			ClassOrInterfaceDeclaration parent = (ClassOrInterfaceDeclaration) JavaParserUtil
			        .getParentExpression(expression, ClassOrInterfaceDeclaration.class);
			JavaParserUtil.addFieldFirst(parent, repository.substring(repository.lastIndexOf(".") + 1), fieldName,
			        PRIVATE_FINAL_MODIFIER);

			// change expression scope, name and arguments
			if ("executeForObject".equals(callExprName)) {
				expression.setScope(new NameExpr(fieldName));
				expression.setName(methodName);
				expression.setArguments(arguments);

				final VariableDeclarator resultVariable = (VariableDeclarator) JavaParserUtil
				        .getParentExpression(expression, VariableDeclarator.class);
				if (resultVariable != null) {
					// convert resultType from object to list
					final ClassOrInterfaceType originalType = (ClassOrInterfaceType) resultVariable.getType()
					        .getElementType();
					final String originalName = resultVariable.getNameAsString();
					final String newName = originalName + "List";
					final ClassOrInterfaceType newType = new ClassOrInterfaceType();
					newType.setName("List<" + originalType.getNameAsString() + ">");
					resultVariable.setType(newType);
					resultVariable.setName(newName);
					expression.tryAddImportToParentCompilationUnit(List.class);

					// create new variable statement for original object
					final VariableDeclarator newVariable = new VariableDeclarator();
					newVariable.setName(originalName);
					newVariable.setType(originalType);
					newVariable.setInitializer("! " + newName + ".isEmptry() ? " + newName + ".get(0) : null");
					final VariableDeclarationExpr newVariableExpression = new VariableDeclarationExpr();
					newVariableExpression.addVariable(newVariable);

					// find current position and add new variable statement
					final BlockStmt blogckStatement = (BlockStmt) JavaParserUtil.getParentExpression(expression,
					        BlockStmt.class);
					int index = JavaParserUtil.findIndexOfCurrentStatement(blogckStatement,
					        (ExpressionStmt) JavaParserUtil.getParentExpression(expression, ExpressionStmt.class));
					blogckStatement.addStatement(index, new EmptyStmt());
					blogckStatement.addStatement(index, newVariableExpression);
				}
			} else if ("executeForObjectList".equals(callExprName)) {
				expression.setScope(new NameExpr(fieldName));
				expression.setName(methodName);
				expression.setArguments(arguments);
			} else {
				// FIXME not support
			}
		}
		return super.visit(expression, context);
	}

	protected void adjustArguments(NodeList<Expression> arguments) {
		final Expression sqlInput = arguments.removeFirst();
		final Expression schema = arguments.removeFirst();
		arguments.clear();
		arguments.add(sqlInput);
		arguments.add(schema);
	}

	protected String getFieldNameFromSqlId(String sqlId) {
		return REPOSITORY_PREFIX.substring(0, 1).toLowerCase() + REPOSITORY_PREFIX.substring(1)
		        + sqlId.substring(0, 4).toUpperCase() + REPOSITORY_SUFFIX;
	}

	protected String getMethodNameFromSqlId(String sqlId) {
		return METHOD_PREFIX + sqlId + METHOD_SUFFIX;
	}

	protected String getRepositoryFromSqlId(String sqlId) {
		return REPOSITORY_BASE + "." + sqlId.substring(0, 3).toLowerCase() + "." + REPOSITORY_PREFIX
		        + sqlId.substring(0, 4).toUpperCase() + REPOSITORY_SUFFIX;
	}

}
