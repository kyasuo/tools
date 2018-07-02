package com.tool.webhook.controller.resource;

public class PullRequestEvent extends Event {
	private static final long serialVersionUID = 1L;

	private String action;
	private Long number;
	private Changes changes;
	private PullRequest pull_request;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public Changes getChanges() {
		return changes;
	}

	public void setChanges(Changes changes) {
		this.changes = changes;
	}

	public PullRequest getPull_request() {
		return pull_request;
	}

	public void setPull_request(PullRequest pull_request) {
		this.pull_request = pull_request;
	}

}
