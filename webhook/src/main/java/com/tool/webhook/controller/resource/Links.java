package com.tool.webhook.controller.resource;

import java.io.Serializable;

public class Links implements Serializable {

	private static final long serialVersionUID = 1L;

	private Link self;
	private Link html;
	private Link issue;
	private Link comments;
	private Link review_comments;
	private Link review_comment;
	private Link commits;
	private Link statuses;

	public Link getSelf() {
		return self;
	}

	public void setSelf(Link self) {
		this.self = self;
	}

	public Link getHtml() {
		return html;
	}

	public void setHtml(Link html) {
		this.html = html;
	}

	public Link getIssue() {
		return issue;
	}

	public void setIssue(Link issue) {
		this.issue = issue;
	}

	public Link getComments() {
		return comments;
	}

	public void setComments(Link comments) {
		this.comments = comments;
	}

	public Link getReview_comments() {
		return review_comments;
	}

	public void setReview_comments(Link review_comments) {
		this.review_comments = review_comments;
	}

	public Link getReview_comment() {
		return review_comment;
	}

	public void setReview_comment(Link review_comment) {
		this.review_comment = review_comment;
	}

	public Link getCommits() {
		return commits;
	}

	public void setCommits(Link commits) {
		this.commits = commits;
	}

	public Link getStatuses() {
		return statuses;
	}

	public void setStatuses(Link statuses) {
		this.statuses = statuses;
	}

}
