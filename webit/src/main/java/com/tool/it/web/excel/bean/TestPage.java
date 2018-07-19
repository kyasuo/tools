package com.tool.it.web.excel.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestPage implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<Element> elementList = new ArrayList<Element>();

	private Element clickElement = null;

	private final String name;

	public TestPage(String name) {
		super();
		this.name = name;
	}

	public List<Element> getElementList() {
		return elementList;
	}

	public void addElement(Element element) {
		this.elementList.add(element);
	}

	public Element getClickElement() {
		return clickElement;
	}

	public void setClickElement(Element clickElement) {
		this.clickElement = clickElement;
	}

	public String getName() {
		return name;
	}

}
