package com.tool.migration.migrator.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.algorithm.myers.MyersDiff;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.Delta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.tool.migration.migrator.MigrationException;
import com.tool.migration.migrator.Migrator;
import com.tool.migration.visitor.VisitorContext;
import com.tool.util.PropertyUtil;

public abstract class MigratorBase implements Migrator {

	private static final Logger logger = LoggerFactory.getLogger(MigratorBase.class);

	private static final File CODEFORMATTER_FILE = new File(
	        MigratorBase.class.getResource("/code_formatter.xml").getFile());

	private static final String LINE_SEPARATOR = PropertyUtil.getProperty("source.line.separator", "\n");

	private static final Charset FILE_ENCODING = Charset.forName(PropertyUtil.getProperty("source.encoding", "UTF-8"));

	private final CodeFormatter CODEFORMATTER = ToolFactory.createCodeFormatter(loadFormatterConfig());

	protected String getFileEncoding() {
		return FILE_ENCODING.name();
	}

	protected String getLineSeparator() {
		return LINE_SEPARATOR;
	}

	protected File getCodeFormatterFile() {
		return CODEFORMATTER_FILE;
	}

	public void execute(List<File> files) throws MigrationException {
		try {
			for (File targetFile : files) {
				logger.info("migrate {}", targetFile.getAbsolutePath());
				// FIXME outputFile
				final File outputFile = new File(targetFile.getParentFile().getParentFile(),
				        "edit/" + targetFile.getParentFile().getName() + "/" + targetFile.getName());
				final VisitorContext context = new VisitorContext();
				final String code = formatJavaCode(FileUtils.readFileToString(targetFile, getFileEncoding()));

				final CompilationUnit unit = JavaParser.parse(code);
				for (ModifierVisitor<VisitorContext> visitor : getModifierVisitors()) {
					logger.info(" > accept {}", visitor.getClass().getName());
					unit.accept(visitor, context);
				}

				final List<String> original = new ArrayList<String>(Arrays.asList(code.split(getLineSeparator())));
				final List<String> revised = new ArrayList<String>(
				        Arrays.asList(formatJavaCode(unit.toString()).split(getLineSeparator())));
				restoreDeletedComment(original, revised);
				restoreDeletedLines(original, revised);

				FileUtils.writeLines(outputFile, getFileEncoding(), revised);
				logger.info("finish. output={}", outputFile);
			}
		} catch (IOException | DiffException | MalformedTreeException | BadLocationException e) {
			throw new MigrationException(e);
		}
	}

	abstract List<ModifierVisitor<VisitorContext>> getModifierVisitors();

	protected Properties loadFormatterConfig() {
		final Properties prefs = new Properties();
		prefs.setProperty(JavaCore.COMPILER_SOURCE, CompilerOptions.VERSION_1_8);
		prefs.setProperty(JavaCore.COMPILER_COMPLIANCE, CompilerOptions.VERSION_1_8);
		prefs.setProperty(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, CompilerOptions.VERSION_1_8);
		try {
			final File file = getCodeFormatterFile();
			if (file != null && file.exists()) {
				org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
				org.w3c.dom.NodeList settings = document.getElementsByTagName("setting");
				for (int i = 0; i < settings.getLength(); i++) {
					org.w3c.dom.Node setting = settings.item(i);
					if (org.w3c.dom.Node.ELEMENT_NODE == setting.getNodeType()) {
						org.w3c.dom.Element element = (org.w3c.dom.Element) setting;
						prefs.setProperty(element.getAttribute("id"), element.getAttribute("value"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prefs;
	}

	private String formatJavaCode(String javaCode) throws MalformedTreeException, BadLocationException {
		final IDocument document = new Document(javaCode);
		final TextEdit edit = CODEFORMATTER.format(CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS,
		        javaCode, 0, javaCode.length(), 0, null);
		edit.apply(document);
		return document.get();
	}

	private static void restoreDeletedComment(List<String> original, List<String> revised) throws DiffException {
		final List<String> tmpCommentList = new ArrayList<String>();
		for (String originalLine : original) {
			final String originalComment = originalLine.replaceAll("^[ \t]*", "");
			if (!originalComment.startsWith("//")) {
				continue;
			}
			int idx = 0;
			final Iterator<String> it = revised.iterator();
			while (it.hasNext()) {
				final String revisedComment = it.next().replaceAll("^[ \t]*", "");
				if (originalComment.replaceAll("^//[ ]*", "").equals(revisedComment.replaceAll("^//[ ]*", ""))) {
					revised.addAll(idx, tmpCommentList);
					tmpCommentList.clear();
					break;
				}
				idx++;
			}
			if (idx >= revised.size()) {
				tmpCommentList.add(originalLine);
			}
		}
	}

	private static void restoreDeletedLines(List<String> original, List<String> revised) throws DiffException {
		int offset = 0;
		final Patch<String> diff = DiffUtils.diff(original, revised, new MyersDiff<>());
		for (Delta<String> delta : diff.getDeltas()) {
			final DeltaType type = delta.getType();
			final Chunk<String> originalChunk = delta.getOriginal();
			final Chunk<String> revisedChunk = delta.getRevised();
			if (DeltaType.DELETE.equals(type)) {
				boolean restore = true;
				for (String line : originalChunk.getLines()) {
					if (!"".equals(line.replaceAll("\t", "").trim())) {
						restore = false;
						break;
					}
				}
				if (restore) {
					int idx = revisedChunk.getPosition() + offset;
					if (idx < revised.size()) {
						revised.addAll(idx, originalChunk.getLines());
					} else {
						revised.addAll(originalChunk.getLines());
					}
					offset = offset + originalChunk.getLines().size();
				}
			} else if (DeltaType.CHANGE.equals(type)) {
				if (originalChunk.getLines().size() > revisedChunk.getLines().size()) {
					final List<String> lines = originalChunk.getLines();
					int restoreCount = 0;
					for (int i = 0; i < lines.size(); i++) {
						final String line = lines.get(i);
						if ("".equals(line.replaceAll("\t", "").trim())) {
							int idx = revisedChunk.getPosition() + offset + i;
							if (idx < revised.size()) {
								revised.add(idx, line);
							} else {
								revised.add(line);
							}
							restoreCount++;
						}
					}
					offset = offset + restoreCount;
				}
			}
		}
	}

}
