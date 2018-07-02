package com.tool.webhook;

public class SystemException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SystemException() {
		super();
	}

	public SystemException(String msg, Throwable e) {
		super(msg, e);
	}

	public SystemException(String msg) {
		super(msg);
	}

	public SystemException(Throwable e) {
		super(e);
	}

}
