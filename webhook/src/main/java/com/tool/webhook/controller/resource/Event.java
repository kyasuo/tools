package com.tool.webhook.controller.resource;

import java.io.Serializable;

public class Event implements Serializable {
	private static final long serialVersionUID = 1L;
	private Sender sender;
	private Repository repository;

	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

}
