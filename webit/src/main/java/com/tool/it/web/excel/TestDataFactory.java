package com.tool.it.web.excel;

import java.io.File;

public class TestDataFactory {

	public static TestData create(String filePath) throws TestDataException {
		return create(new File(filePath));
	}

	public static TestData create(File file) throws TestDataException {
		return new TestData(file);
	}
}
