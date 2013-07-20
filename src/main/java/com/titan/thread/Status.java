package com.c2.pandora.thread;

import java.util.Date;

public class Status implements Comparable {
	public Date date;
	public long maxMemory;
	public long freeMemory;
	public long allocatedMemory;
	public String cpu_combined;
	public String cpu_sys;
	public String cpu_user;
	public String os;
	public long outSegs;
	public long inSegs;

	public int compareTo(Object o) {
		return this.date.compareTo(((Status) o).date);
	}
}
