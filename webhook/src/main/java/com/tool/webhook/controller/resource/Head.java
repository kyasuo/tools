package com.tool.webhook.controller.resource;

import java.io.Serializable;

public class Head implements Serializable {

	private static final long serialVersionUID = 1L;

	private String label;
	private String ref;
	private String sha;
	private User user;
	private Repository repo;
	private Base base;
	private Links _links;
	private String author_association;
	private Boolean merged;
	private Boolean mergeable;
	private Boolean rebaseable;
	private String mergeable_state;
	private String merged_by;
	private Long comments;
	private Long review_comments;
	private Boolean maintainer_can_modify;
	private Long commits;
	private Long additions;
	private Long deletions;
	private Long changed_files;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Repository getRepo() {
		return repo;
	}

	public void setRepo(Repository repo) {
		this.repo = repo;
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

	public Boolean getMerged() {
		return merged;
	}

	public void setMerged(Boolean merged) {
		this.merged = merged;
	}

	public Boolean getMergeable() {
		return mergeable;
	}

	public void setMergeable(Boolean mergeable) {
		this.mergeable = mergeable;
	}

	public Boolean getRebaseable() {
		return rebaseable;
	}

	public void setRebaseable(Boolean rebaseable) {
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

	public Boolean getMaintainer_can_modify() {
		return maintainer_can_modify;
	}

	public void setMaintainer_can_modify(Boolean maintainer_can_modify) {
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
