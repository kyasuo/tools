package com.tool.migration.migrator;

import java.io.File;
import java.util.List;

public interface Migrator {

	void execute(List<File> files) throws MigrationException;

}
