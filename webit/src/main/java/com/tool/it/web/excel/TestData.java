package com.tool.it.web.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.tool.it.web.excel.bean.Element;
import com.tool.it.web.excel.bean.Element.AType;
import com.tool.it.web.excel.bean.TestPage;
import com.tool.util.PropertyUtil;

public class TestData implements Iterator<TestPage> {

	private static final String CLICK_MARKER = PropertyUtil.getProperty("click.marker", "○");
	private static final int ROW_INDEX = PropertyUtil.getPropertyInt("row.index");
	private static final int LNAME_INDEX = PropertyUtil.getPropertyInt("col.lname");
	private static final int PNAME_INDEX = PropertyUtil.getPropertyInt("col.pname");
	private static final int ATTR_INDEX = PropertyUtil.getPropertyInt("col.attr");
	private static final int INITVALUE_INDEX = PropertyUtil.getPropertyInt("col.initValue");
	private static final int INPUTVALUE_INDEX = PropertyUtil.getPropertyInt("col.inputValue");
	private static final int CLICK_INDEX = PropertyUtil.getPropertyInt("col.click");
	private final Deque<TestPage> queue = new ArrayDeque<TestPage>();

	public TestData(File file) throws TestDataException {
		if (!file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("filePath is invalid. filePath = " + file.getAbsolutePath());
		}
		loadExcelFile(file);
	}

	private void loadExcelFile(File file) throws TestDataException {
		Workbook workbook = null;
		Sheet sheet = null;
		Row row = null;
		try {
			workbook = WorkbookFactory.create(file, null, true);
			final Iterator<Sheet> sheets = workbook.sheetIterator();
			while (sheets.hasNext()) {
				sheet = sheets.next();
				final TestPage test = new TestPage(sheet.getSheetName());
				final Iterator<Row> rows = sheet.rowIterator();
				int index = 0;
				while (rows.hasNext()) {
					row = rows.next();
					index++;
					if (index <= ROW_INDEX) {
						continue;
					}
					final Element element = new Element();
					element.setlName(getCellCalue(row.getCell(LNAME_INDEX)));
					element.setpName(getCellCalue(row.getCell(PNAME_INDEX)));
					element.setAttr(getAtype(row.getCell(ATTR_INDEX)));
					element.setInitValue(getCellCalue(row.getCell(INITVALUE_INDEX)));
					element.setInputValue(getCellCalue(row.getCell(INPUTVALUE_INDEX)));
					element.setClick(CLICK_MARKER.equals(getCellCalue(row.getCell(CLICK_INDEX))));
					test.addElement(element);
					if (test.getClickElement() == null && element.isClick()) {
						test.setClickElement(element);
					}
				}
				queue.offer(test);
			}
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			throw new TestDataException(e);
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private AType getAtype(Cell cell) {
		final String type = getCellCalue(cell);
		if (type == null || !AType.getStringValues().contains(type.toUpperCase())) {
			return AType.UNKNOWN;
		}
		return AType.valueOf(type.toUpperCase());
	}

	private String getCellCalue(Cell cell) {
		if (cell == null) {
			return null;
		}
		if (CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
			return String.valueOf((int) cell.getNumericCellValue());
		} else {
			return cell.getStringCellValue();
		}
	}

	@Override
	public boolean hasNext() {
		return queue.peek() != null;
	}

	@Override
	public TestPage next() {
		return queue.poll();
	}

}
