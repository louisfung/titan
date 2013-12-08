package com.titan.vm;

public class Property {
	public String type;
	public String name;
	public String value;
	public boolean isData;

	public Property(String type, String name, String value, boolean isData) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.isData = isData;
	}
}
