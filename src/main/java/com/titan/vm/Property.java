package com.titan.vm;

public class Property implements Comparable<Property> {
	public String type;
	public String name;
	public String value;
	public boolean isData;
	public boolean expand = true;
	public boolean isVisible = true;
	public boolean isEditable;

	public Property() {
	}

	public Property(String type, String name, String value) {
		this(type, name, value, true, false);
	}

	public Property(String type, String name, String value, boolean isData) {
		this(type, name, value, isData, false);
	}

	public Property(String type, String name, String value, boolean isData, boolean isEditable) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.isData = isData;
		this.isEditable = isEditable;
	}

	public String toString() {
		return type + "," + name + "," + value;
	}

	//	@Override
	//	public int compare(Object o1, Object o2) {
	//		System.out.println("compare");
	//		Property p1 = (Property) o1;
	//		Property p2 = (Property) o2;
	//		return p1.name.compareTo(p2.name);
	//	}

	@Override
	public int compareTo(Property o) {
		if (type.equals(o.type)) {
			if (name.equals("")) {
				return -1;
			} else if (o.name.equals("")) {
				return 1;
			} else {
				if (name.equals("name")) {
					return -1;
				} else if (o.name.equals("name")) {
					return 1;
				} else {
					return name.compareToIgnoreCase(o.name);
				}
			}
		}
		//		if (name.equals("")) {
		//			return type.compareToIgnoreCase(o.type);
		//		} else {
		//			if (!o.name.equals("") && type.equals(o.type)) {
		//				return name.compareToIgnoreCase(o.name);
		//			}
		//		}
		return 0;
		//		if (isData && o.isData) {
		//			return name.compareToIgnoreCase(o.name);
		//		} else {
		//			if (!isData && type.equals(o.type)) {
		//				return 1;
		//			} else if (!o.isData && type.equals(o.type)) {
		//				return -1;
		//			} else {
		//				return type.compareToIgnoreCase(o.type);
		//			}
		//		}
	}
}
