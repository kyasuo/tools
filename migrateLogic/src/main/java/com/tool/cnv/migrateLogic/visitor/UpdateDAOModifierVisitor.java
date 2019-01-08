package com.tool.cnv.migrateLogic.visitor;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.tool.cnv.migrateLogic.ReplacementParameter;
import com.tool.cnv.migrateLogic.VisitorContext;
import com.tool.cnv.migrateLogic.util.RegexUtil;

/**
 * Migrate from UpdateDAO to Repository
 */
public class UpdateDAOModifierVisitor extends BaseDAOModifierVisitor {

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UpdateDAOModifierVisitor.class);

	@Override
	void setupImportReplacements(Map<String, String> importReplacements) {
		importReplacements.put("import jp.terasoluna.fw.dao.UpdateDAO;", null);
	}

	@Override
	void setupDaoElementTypes(Set<String> daoElementTypes) {
		daoElementTypes.add("UpdateDAO");
		daoElementTypes.add("jp.terasoluna.fw.dao.UpdateDAO");
	}

	/**
	 * Replace method call expression
	 */
	@Override
	public void visit(MethodCallExpr expr, VisitorContext context) {

		if (!isMethodCallExprForDaoField(expr.getScope().get())) {
			super.visit(expr, context);
			return;
		}

		// detect sqlId and get repository, fieldName and methodName
		final String sqlId = getSqlId(expr.getArgument(0));
		if (sqlId == null) {
			logger.warn("failure to replace method call expression because sqlId cannot be detected from argument. ["
			        + expr.getTokenRange().get().toString() + "]");
			super.visit(expr, context);
			return;
		}
		final String repository = getRepositoryFromSqlId(sqlId);
		final String fieldName = getFieldNameFromSqlId(sqlId);
		final String methodName = getMethodNameFromSqlId(sqlId);

		// create replacement methodcallexpr
		final MethodCallExpr cExpr = expr.clone();
		final String callExprName = cExpr.getNameAsString();

		// change fieldName and methodName
		((NameExpr) cExpr.getScope().get()).setName(fieldName);
		cExpr.setName(methodName);
		// remove first argument
		cExpr.getArguments().removeFirst();

		// adjust arguments
		String replacement = null;
		if ("execute".equals(callExprName)) {
			// FIXME adjust arguments if needed
			replacement = cExpr.toString();
		} else {
			logger.warn(
			        "failure to replace method call expression because callExprName is unexpected. expected = [execute], actual = ["
			                + callExprName + "]");
			super.visit(expr, context);
			return;
		}

		// set result
		addRepositoryField(context, repository, fieldName);
		context.addReplacementParameter(
		        new ReplacementParameter(RegexUtil.escape(expr.getTokenRange().get().toString()), replacement, false));

		super.visit(expr, context);
	}

}
