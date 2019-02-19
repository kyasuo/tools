package com.tool.gitbucket.resources;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Label implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String node_id;
	private String url;
	private String name;
	private String description;
	private String color;
	@JsonProperty("default")
	private Boolean _default;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Boolean get_default() {
		return _default;
	}

	public void set_default(Boolean _default) {
		this._default = _default;
	}

}
