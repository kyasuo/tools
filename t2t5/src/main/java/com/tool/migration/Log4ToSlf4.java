package com.tool.migration;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.tool.migration.migrator.MigrationException;
import com.tool.migration.migrator.Migrator;
import com.tool.migration.migrator.impl.LogggerMigrator;

public class Log4ToSlf4 {

	static final File BASE_DIR = new File("");

	public static void main(String[] args) throws MigrationException {
		Migrator migrator = new LogggerMigrator();
		migrator.execute(new ArrayList<File>(FileUtils.listFiles(BASE_DIR, new String[] { "java" }, true)));
	}

}
