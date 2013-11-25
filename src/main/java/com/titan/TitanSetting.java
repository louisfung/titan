package com.titan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.titanserver.structure.TitanServerDefinition;

public class TitanSetting {
	private static TitanSetting setting = null;
	public String lastUsername;
	public String lastIP;
	public Vector<TitanServerDefinition> titanServers = new Vector<TitanServerDefinition>();
	public int titanServerUpdateThread_milliSeconds = 1000;
	public int x;
	public int y;
	public int width;
	public int height;
	public int maxVMColumnCount = 10;

	public TitanSetting() {
	}

	public static TitanSetting getInstance() {
		if (setting == null) {
			setting = load();
		}
		return setting;
	}

	public void save() {
		XStream xstream = new XStream();
		xstream.alias("Setting", TitanSetting.class);
		String xml = xstream.toXML(this);
		try {
			IOUtils.write(xml, new FileOutputStream(new File("titan.xml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static TitanSetting load() {
		try {
			XStream xstream = new XStream();
			xstream.alias("Setting", TitanSetting.class);
			TitanSetting setting = (TitanSetting) xstream.fromXML(new FileInputStream(new File("titan.xml")));
			return setting;
		} catch (Exception ex) {
			new File("titan.xml").delete();
			TitanSetting Setting = new TitanSetting();
			Setting.save();
			return Setting;
		}
	}
}
