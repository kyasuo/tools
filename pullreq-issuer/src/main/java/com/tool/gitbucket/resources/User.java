package com.tool.gitbucket.resources;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Boolean site_admin;
	private String login;
	private String email;
	private String type;
	private String created_at;
	private String url;
	private String html_url;
	private String avatar_url;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getSite_admin() {
		return site_admin;
	}

	public void setSite_admin(Boolean site_admin) {
		this.site_admin = site_admin;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHtml_url() {
		return html_url;
	}

	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

}
