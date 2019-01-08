package com.tool.cnv.migrateLogic;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.tool.cnv.migrateLogic.util.PropertyUtil;
import com.tool.cnv.migrateLogic.util.RegexUtil;
import com.tool.cnv.migrateLogic.visitor.ApacheCommonsModifierVisitor;
import com.tool.cnv.migrateLogic.visitor.ConstructorModifierVisitor;
import com.tool.cnv.migrateLogic.visitor.LoggerModifierVisitor;
import com.tool.cnv.migrateLogic.visitor.QueryDAOModifierVisitor;
import com.tool.cnv.migrateLogic.visitor.UpdateDAOModifierVisitor;

public class MigrationToolRunner {

	private static final Logger logger = LoggerFactory.getLogger(MigrationToolRunner.class);

	private static final String ENCODING = PropertyUtil.getProperty("source.file.encoding", "UTF-8");
	private static final String LINE_SEPARATOR = PropertyUtil.getProperty("source.file.lineseparator",
	        System.lineSeparator());
	private static final File INPUT_DIR = new File(PropertyUtil.getProperty("input.dir"));
	private static final File OUTPUT_DIR = new File(PropertyUtil.getProperty("output.dir"));

	public static void main(String[] args) throws Exception {

		for (File inputFile : FileUtils.listFiles(INPUT_DIR, FileFilterUtils.suffixFileFilter(".java"),
		        FileFilterUtils.trueFileFilter())) {
			logger.info("Start to migrate input = " + inputFile.getAbsolutePath());

			// prepare
			String inputFileName = inputFile.getName();
			String fileContents = FileUtils.readFileToString(inputFile, ENCODING);
			VisitorContext context = new VisitorContext(LINE_SEPARATOR, ENCODING, inputFileName);

			// common
			CompilationUnit cUnit = JavaParser.parse(fileContents);
			cUnit.accept(new ApacheCommonsModifierVisitor(), context);
			cUnit.accept(new LoggerModifierVisitor(), context);
			cUnit.accept(new QueryDAOModifierVisitor(), context);
			cUnit.accept(new UpdateDAOModifierVisitor(), context);
			addImportsAndFields(context);
			fileContents = replaceFileContents(fileContents, context.getReplacementParameters());

			// logic(constructor injection, implements, extends, annotation)
			if (isLogic(inputFile)) {
				context.clear();
				cUnit = JavaParser.parse(fileContents);
				cUnit.accept(new ConstructorModifierVisitor(), context);
				addConstructor(context);
				fileContents = replaceFileContents(fileContents, context.getReplacementParameters());
			}

			// output
			final File outputFile = getOutputFile(cUnit.getPackageDeclaration().get().getNameAsString(), inputFileName);
			FileUtils.writeStringToFile(outputFile, fileContents, ENCODING);
			logger.info("Finish output = " + outputFile.getAbsolutePath());
		}

	}

	private static boolean isLogic(File inputFile) {
		boolean result = false;

		// FIXME determine if logic case by using filename, filepath and so on.
		if (inputFile.getName().startsWith("XXXX") && inputFile.getName().endsWith("Impl.java")) {
			result = true;
		} else if (inputFile.getAbsolutePath().contains("logic")) {
			result = true;
		}

		return result;
	}

	private static File getOutputFile(String packageStatement, String fileName) {
		return new File(OUTPUT_DIR, packageStatement.replace(".", File.separator) + File.separator + fileName);
	}

	private static void addConstructor(VisitorContext context) {
		if (!context.getConstructorArgMap().isEmpty()) {
			context.addReplacementParameter(RegexUtil.addConstructor(context.getClassName(),
			        context.getConstructorArgMap(), context.getLineSeparator()));

		}
	}

	private static void addImportsAndFields(VisitorContext context) {
		if (!context.getImportStatements().isEmpty()) {
			context.addReplacementParameter(RegexUtil.addImports(context.getImportStatements().toArray(new String[] {}),
			        context.getLineSeparator()));
		}
		for (Entry<String, String> entry : context.getFieldMap().entrySet()) {
			context.addReplacementParameter(
			        RegexUtil.addField(context.getClassName(), entry.getValue(), context.getLineSeparator()));
		}
	}

	private static String replaceFileContents(String fileContents, List<ReplacementParameter> parameters) {
		for (ReplacementParameter parameter : parameters) {
			if (parameter.isReplaceAll()) {
				fileContents = Pattern.compile(parameter.getRegex(), Pattern.DOTALL).matcher(fileContents)
				        .replaceAll(parameter.getReplacement());
			} else {
				fileContents = Pattern.compile(parameter.getRegex(), Pattern.DOTALL).matcher(fileContents)
				        .replaceFirst(parameter.getReplacement());
			}
		}
		return fileContents;
	}

}
