package com.tool.webhook.controller.resource;

public class PushEvent extends Event {

	private static final long serialVersionUID = 1L;

	private String ref;
	private String head;
	private String before;
	private Long size;
	private Long distinct_size;
	private Commit[] commits;

	private String after;
	private Boolean created;
	private Boolean deleted;
	private Boolean forced;
	private String base_ref;
	private String compare;

	private Commit head_commit;
	private Pusher pusher;

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public Boolean getCreated() {
		return created;
	}

	public void setCreated(Boolean created) {
		this.created = created;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getForced() {
		return forced;
	}

	public void setForced(Boolean forced) {
		this.forced = forced;
	}

	public String getBase_ref() {
		return base_ref;
	}

	public void setBase_ref(String base_ref) {
		this.base_ref = base_ref;
	}

	public String getCompare() {
		return compare;
	}

	public void setCompare(String compare) {
		this.compare = compare;
	}

	public Pusher getPusher() {
		return pusher;
	}

	public void setPusher(Pusher pusher) {
		this.pusher = pusher;
	}

	public Commit[] getCommits() {
		return commits;
	}

	public void setCommits(Commit[] commits) {
		this.commits = commits;
	}

	public Commit getHead_commit() {
		return head_commit;
	}

	public void setHead_commit(Commit head_commit) {
		this.head_commit = head_commit;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getDistinct_size() {
		return distinct_size;
	}

	public void setDistinct_size(Long distinct_size) {
		this.distinct_size = distinct_size;
	}

}
