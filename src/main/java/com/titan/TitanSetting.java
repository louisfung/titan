package com.titan;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

import com.titanserver.structure.TitanServerDefinition;

public class TitanSetting {
	private static TitanSetting setting = null;
	public String lastUsername;
	public String lastIP;
	public Vector<TitanServerDefinition> titanServers = new Vector<TitanServerDefinition>();
	public int titanServerUpdateThread_milliSeconds;

	public TitanSetting() {
	}

	public static TitanSetting getInstance() {
		if (setting == null) {
			setting = load();
		}
		return setting;
	}

	public void save() {
		try {
			FileWriter outputWriter = new FileWriter(new File("titan.xml"));
			outputWriter.write("<?xml version='1.0' ?>\n");

			BeanWriter writer = new BeanWriter(outputWriter);
			writer.enablePrettyPrint();
			writer.setWriteIDs(false);

			writer.write("Setting", this);
			outputWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static TitanSetting load() {
		try {
			File file = new File("titan.xml");
			if (!file.exists()) {
				TitanSetting setting = new TitanSetting();
				setting.save();
				return setting;
			}

			FileReader reader = new FileReader(file);

			BeanReader beanReader = new BeanReader();
			beanReader.registerBeanClass("Setting", TitanSetting.class);

			TitanSetting setting = (TitanSetting) beanReader.parse(reader);
			return setting;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Loading titan.xml error.", "Error", JOptionPane.ERROR_MESSAGE);
			new File("titan.xml").delete();
			TitanSetting titanSetting = new TitanSetting();
			titanSetting.save();
			return titanSetting;
		}
	}

	public String getLastUsername() {
		return lastUsername;
	}

	public String getLastIP() {
		return lastIP;
	}

	public void setLastUsername(String lastUsername) {
		this.lastUsername = lastUsername;
	}

	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	public Vector<TitanServerDefinition> getTitanServers() {
		return titanServers;
	}

	public void setTitanServers(Vector<TitanServerDefinition> titanServers) {
		this.titanServers = titanServers;
	}

	public void addTitanServers(TitanServerDefinition titanServer) {
		if (titanServers != null) {
			titanServers.add(titanServer);
		}
	}

	public int getTitanServerUpdateThread_milliSeconds() {
		if (titanServerUpdateThread_milliSeconds <= 0) {
			titanServerUpdateThread_milliSeconds = 1000;
		}
		return titanServerUpdateThread_milliSeconds;
	}

	public void setTitanServerUpdateThread_milliSeconds(int titanServerUpdateThread_milliSeconds) {
		this.titanServerUpdateThread_milliSeconds = titanServerUpdateThread_milliSeconds;
	}
}
