package com.tool.migration.migrator.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.tool.migration.visitor.LoggerModifierVisitor;
import com.tool.migration.visitor.VisitorContext;

public class LogggerMigrator extends MigratorBase {

	@Override
	List<ModifierVisitor<VisitorContext>> getModifierVisitors() {
		List<ModifierVisitor<VisitorContext>> visitors = new ArrayList<ModifierVisitor<VisitorContext>>();
		visitors.add(new LoggerModifierVisitor());
		return visitors;
	}

}
