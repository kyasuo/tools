package com.tool.webhook.controller.resource;

import java.io.Serializable;

public class PullRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	private String url;
	private Long id;
	private String node_id;
	private String html_url;
	private String diff_url;
	private String patch_url;
	private String issue_url;
	private Long number;
	private String state;
	private Boolean locked;
	private String title;
	private User user;
	private String body;
	private String created_at;
	private String updated_at;
	private String closed_at;
	private String merged_at;
	private String merge_commit_sha;
	private String assignee;

	private String milestone;
	private String commits_url;
	private String review_comments_url;
	private String review_comment_url;
	private String comments_url;
	private String statuses_url;
	private Head head;
	private Base base;
	private Links _links;
	private String author_association;
	private String merged;
	private String mergeable;
	private String rebaseable;
	private String mergeable_state;
	private String merged_by;
	private Long comments;
	private Long review_comments;
	private String maintainer_can_modify;
	private Long commits;
	private Long additions;
	private Long deletions;
	private Long changed_files;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNode_id() {
		return node_id;
	}

	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	public String getHtml_url() {
		return html_url;
	}

	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}

	public String getDiff_url() {
		return diff_url;
	}

	public void setDiff_url(String diff_url) {
		this.diff_url = diff_url;
	}

	public String getPatch_url() {
		return patch_url;
	}

	public void setPatch_url(String patch_url) {
		this.patch_url = patch_url;
	}

	public String getIssue_url() {
		return issue_url;
	}

	public void setIssue_url(String issue_url) {
		this.issue_url = issue_url;
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

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getClosed_at() {
		return closed_at;
	}

	public void setClosed_at(String closed_at) {
		this.closed_at = closed_at;
	}

	public String getMerged_at() {
		return merged_at;
	}

	public void setMerged_at(String merged_at) {
		this.merged_at = merged_at;
	}

	public String getMerge_commit_sha() {
		return merge_commit_sha;
	}

	public void setMerge_commit_sha(String merge_commit_sha) {
		this.merge_commit_sha = merge_commit_sha;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getMilestone() {
		return milestone;
	}

	public void setMilestone(String milestone) {
		this.milestone = milestone;
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

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	public Base getBase() {
		return base;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public Links get_links() {
		return _links;
	}

	public void set_links(Links _links) {
		this._links = _links;
	}

	public String getAuthor_association() {
		return author_association;
	}

	public void setAuthor_association(String author_association) {
		this.author_association = author_association;
	}

	public String getMerged() {
		return merged;
	}

	public void setMerged(String merged) {
		this.merged = merged;
	}

	public String getMergeable() {
		return mergeable;
	}

	public void setMergeable(String mergeable) {
		this.mergeable = mergeable;
	}

	public String getRebaseable() {
		return rebaseable;
	}

	public void setRebaseable(String rebaseable) {
		this.rebaseable = rebaseable;
	}

	public String getMergeable_state() {
		return mergeable_state;
	}

	public void setMergeable_state(String mergeable_state) {
		this.mergeable_state = mergeable_state;
	}

	public String getMerged_by() {
		return merged_by;
	}

	public void setMerged_by(String merged_by) {
		this.merged_by = merged_by;
	}

	public Long getComments() {
		return comments;
	}

	public void setComments(Long comments) {
		this.comments = comments;
	}

	public Long getReview_comments() {
		return review_comments;
	}

	public void setReview_comments(Long review_comments) {
		this.review_comments = review_comments;
	}

	public String getMaintainer_can_modify() {
		return maintainer_can_modify;
	}

	public void setMaintainer_can_modify(String maintainer_can_modify) {
		this.maintainer_can_modify = maintainer_can_modify;
	}

	public Long getCommits() {
		return commits;
	}

	public void setCommits(Long commits) {
		this.commits = commits;
	}

	public Long getAdditions() {
		return additions;
	}

	public void setAdditions(Long additions) {
		this.additions = additions;
	}

	public Long getDeletions() {
		return deletions;
	}

	public void setDeletions(Long deletions) {
		this.deletions = deletions;
	}

	public Long getChanged_files() {
		return changed_files;
	}

	public void setChanged_files(Long changed_files) {
		this.changed_files = changed_files;
	}

}
