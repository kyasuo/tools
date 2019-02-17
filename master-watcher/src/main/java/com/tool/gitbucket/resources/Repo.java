package com.tool.gitbucket.resources;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Repo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String full_name;
	private String description;
	private Long watchers;
	private Long forks;
	@JsonProperty("private")
	private Boolean _private;
	private String default_branch;
	private Owner owner;
	private Long id;
	private Long forks_count;
	private Long watchers_count;
	private String url;
	private String http_url;
	private String clone_url;
	private String html_url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getWatchers() {
		return watchers;
	}

	public void setWatchers(Long watchers) {
		this.watchers = watchers;
	}

	public Long getForks() {
		return forks;
	}

	public void setForks(Long forks) {
		this.forks = forks;
	}

	public Boolean get_private() {
		return _private;
	}

	public void set_private(Boolean _private) {
		this._private = _private;
	}

	public String getDefault_branch() {
		return default_branch;
	}

	public void setDefault_branch(String default_branch) {
		this.default_branch = default_branch;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getForks_count() {
		return forks_count;
	}

	public void setForks_count(Long forks_count) {
		this.forks_count = forks_count;
	}

	public Long getWatchers_count() {
		return watchers_count;
	}

	public void setWatchers_count(Long watchers_count) {
		this.watchers_count = watchers_count;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHttp_url() {
		return http_url;
	}

	public void setHttp_url(String http_url) {
		this.http_url = http_url;
	}

	public String getClone_url() {
		return clone_url;
	}

	public void setClone_url(String clone_url) {
		this.clone_url = clone_url;
	}

	public String getHtml_url() {
		return html_url;
	}

	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}

}
