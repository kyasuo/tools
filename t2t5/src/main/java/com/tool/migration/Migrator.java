package com.tool.migration;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.tool.migration.visitor.CommonsLangModifierVisitor;
import com.tool.migration.visitor.LoggerModifierVisitor;
import com.tool.migration.visitor.QueryDAOModifierVisitor;
import com.tool.migration.visitor.VisitorContext;

public class Migrator {

	static final File BASE_DIR = new File("");

	static final Charset encoding = Charset.forName("UTF-8");

	static final List<ModifierVisitor<VisitorContext>> visitors = new ArrayList<ModifierVisitor<VisitorContext>>();
	static {
		visitors.add(new LoggerModifierVisitor());
		visitors.add(new QueryDAOModifierVisitor());
		visitors.add(new CommonsLangModifierVisitor());
	}

	public static void main(String[] args) throws FileNotFoundException {

		for (File java : FileUtils.listFiles(BASE_DIR, new String[] { "java" }, true)) {
			final VisitorContext context = new VisitorContext();
			CompilationUnit unit = JavaParser.parse(java, encoding);
			System.out.println("***********************************************");
			System.out.println(java.getName());
			for (ModifierVisitor<VisitorContext> visitor : visitors) {
				unit.accept(visitor, context);
			}
			System.out.println(unit);
			System.out.println("***********************************************");
		}

	}

}
