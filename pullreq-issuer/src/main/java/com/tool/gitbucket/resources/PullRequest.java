package com.tool.gitbucket.resources;

import java.io.Serializable;

public class PullRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long number;
	private String state;
	private String updated_at;
	private String created_at;
	private Branch head;
	private Base base;
	private Boolean merged;
	private String title;
	private String body;
	private User user;
	private Label[] labels;
	private String html_url;
	private String url;
	private String commits_url;
	private String review_comments_url;
	private String review_comment_url;
	private String comments_url;
	private String statuses_url;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public Branch getHead() {
		return head;
	}

	public void setHead(Branch head) {
		this.head = head;
	}

	public Base getBase() {
		return base;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public Boolean getMerged() {
		return merged;
	}

	public void setMerged(Boolean merged) {
		this.merged = merged;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Label[] getLabels() {
		return labels;
	}

	public void setLabels(Label[] labels) {
		this.labels = labels;
	}

	public String getHtml_url() {
		return html_url;
	}

	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCommits_url() {
		return commits_url;
	}

	public void setCommits_url(String commits_url) {
		this.commits_url = commits_url;
	}

	public String getReview_comments_url() {
		return review_comments_url;
	}

	public void setReview_comments_url(String review_comments_url) {
		this.review_comments_url = review_comments_url;
	}

	public String getReview_comment_url() {
		return review_comment_url;
	}

	public void setReview_comment_url(String review_comment_url) {
		this.review_comment_url = review_comment_url;
	}

	public String getComments_url() {
		return comments_url;
	}

	public void setComments_url(String comments_url) {
		this.comments_url = comments_url;
	}

	public String getStatuses_url() {
		return statuses_url;
	}

	public void setStatuses_url(String statuses_url) {
		this.statuses_url = statuses_url;
	}

}
