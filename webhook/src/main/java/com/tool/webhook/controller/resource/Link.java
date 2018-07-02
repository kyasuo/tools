package com.tool.webhook.controller.resource;

import java.io.Serializable;

public class Link implements Serializable {
	private static final long serialVersionUID = 1L;
	private String href;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
