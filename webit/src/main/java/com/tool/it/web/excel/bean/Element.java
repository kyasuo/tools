package com.tool.it.web.excel.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Element implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum AType {

		UNKNOWN, LINK, BUTTON, SUBMIT, DOWNLOAD, TEXT, TEXTAREA, RADIO, CHECKBOX, SELECT;
		private static final List<String> stringValues = new ArrayList<String>();

		static {
			for (int i = 0; i < values().length; i++) {
				stringValues.add(values()[i].name());
			}
		}

		public static List<String> getStringValues() {
			return stringValues;
		}
	}

	private String lName;
	private String pName;
	private AType attr;
	private String initValue;
	private String inputValue;
	private boolean click;

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public AType getAttr() {
		return attr;
	}

	public void setAttr(AType attr) {
		this.attr = attr;
	}

	public String getInitValue() {
		return initValue;
	}

	public void setInitValue(String initValue) {
		this.initValue = initValue;
	}

	public String getInputValue() {
		return inputValue;
	}

	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}

	public boolean isClick() {
		return click;
	}

	public void setClick(boolean click) {
		this.click = click;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("{lName=" + lName);
		sb.append(",pName=" + pName);
		sb.append(",attr=" + attr);
		sb.append(",initValue=" + initValue);
		sb.append(",inputValue=" + inputValue);
		sb.append(",click=" + click + "}");
		return sb.toString();
	}

}
