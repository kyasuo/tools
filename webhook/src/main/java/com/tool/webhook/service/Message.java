package com.tool.webhook.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<String> messageList = new ArrayList<String>();

	public List<String> getMessageList() {
		return messageList;
	}

	public void clearMessageList() {
		this.messageList.clear();
	}

	public void addMessage(String message) {
		this.messageList.add(message);
	}

}
