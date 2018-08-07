package com.tool.migration.migrator;

public class MigrationException extends Exception {

	private static final long serialVersionUID = 1L;

	public MigrationException(String msg, Throwable e) {
		super(msg, e);
	}

	public MigrationException(String msg) {
		super(msg);
	}

	public MigrationException(Throwable e) {
		super(e);
	}

}
