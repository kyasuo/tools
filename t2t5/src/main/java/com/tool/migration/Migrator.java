package com.tool.migration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

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
import com.tool.migration.visitor.CommonsLangModifierVisitor;
import com.tool.migration.visitor.LoggerModifierVisitor;
import com.tool.migration.visitor.QueryDAOModifierVisitor;
import com.tool.migration.visitor.VisitorContext;

public class Migrator {

	static final File BASE_DIR = new File("");

	static final Charset ENCODING = Charset.forName("UTF-8");

	static final String LINE_SEPARATOR = "\r\n";

	static final CodeFormatter CODEFORMATTER = ToolFactory.createCodeFormatter(loadFormatterConfig());

	static final List<ModifierVisitor<VisitorContext>> visitors = new ArrayList<ModifierVisitor<VisitorContext>>();
	static {
		visitors.add(new LoggerModifierVisitor());
		visitors.add(new QueryDAOModifierVisitor());
		visitors.add(new CommonsLangModifierVisitor());
	}

	static Properties loadFormatterConfig() {
		final Properties prefs = new Properties();
		prefs.setProperty(JavaCore.COMPILER_SOURCE, CompilerOptions.VERSION_1_8);
		prefs.setProperty(JavaCore.COMPILER_COMPLIANCE, CompilerOptions.VERSION_1_8);
		prefs.setProperty(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, CompilerOptions.VERSION_1_8);
		// FIXME read xml file
		prefs.setProperty(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
		prefs.setProperty(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
		return prefs;
	}

	public static void main(String[] args) throws IOException, DiffException {

		for (File java : FileUtils.listFiles(BASE_DIR, new String[] { "java" }, true)) {
			final VisitorContext context = new VisitorContext();
			final String code = formatJavaCode(FileUtils.readFileToString(java, ENCODING));

			final CompilationUnit unit = JavaParser.parse(code);
			System.out.println("***********************************************");
			System.out.println(java.getName());
			for (ModifierVisitor<VisitorContext> visitor : visitors) {
				unit.accept(visitor, context);
			}
			System.out.println(unit);
			System.out.println("***********************************************");

			final List<String> original = new ArrayList<String>(Arrays.asList(code.split(LINE_SEPARATOR)));
			final List<String> revised = new ArrayList<String>(
					Arrays.asList(formatJavaCode(unit.toString()).split(LINE_SEPARATOR)));
			restoreDeletedComment(original, revised);
			restoreDeletedLines(original, revised);

			FileUtils.writeLines(new File(java.getParentFile().getParentFile(), "alogic/" + java.getName()),
					ENCODING.toString(), revised);
		}
	}

	private static String formatJavaCode(String javaCode) {
		final IDocument document = new Document(javaCode);
		try {
			final TextEdit edit = CODEFORMATTER.format(
					CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS, javaCode, 0, javaCode.length(),
					0, null);
			edit.apply(document);
			return document.get();
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
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
				if (originalComment.equals(revisedComment)) {
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
					int notEmptyCount = 0;
					for (int i = 0; i < lines.size(); i++) {
						final String line = lines.get(i);
						if ("".equals(line.replaceAll("\t", "").trim())) {
							int idx = revisedChunk.getPosition() + offset + i;
							// FIXME - (notEmptyCount / 2);
							if (idx < revised.size()) {
								revised.add(idx, line);
							} else {
								revised.add(line);
							}
							restoreCount++;
						} else {
							notEmptyCount++;
						}
					}
					offset = offset + restoreCount;
				}
			}
		}
	}

}
