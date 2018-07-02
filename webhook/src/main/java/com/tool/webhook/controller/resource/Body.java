package com.tool.webhook.controller.resource;

import java.io.Serializable;

public class Body implements Serializable {

	private static final long serialVersionUID = 1L;

	private String from;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
