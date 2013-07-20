package com.c2.pandora;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

import com.c2.pandoraserver.structure.PandoraServerDefinition;

public class PandoraSetting {
	private static PandoraSetting setting = null;
	public String lastUsername;
	public String lastIP;
	public Vector<PandoraServerDefinition> pandoraServers = new Vector<PandoraServerDefinition>();
	public int pandoraServerUpdateThread_milliSeconds;

	public PandoraSetting() {
	}

	public static PandoraSetting getInstance() {
		if (setting == null) {
			setting = load();
		}
		return setting;
	}

	public void save() {
		try {
			FileWriter outputWriter = new FileWriter(new File("pandora.xml"));
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

	private static PandoraSetting load() {
		try {
			File file = new File("pandora.xml");
			if (!file.exists()) {
				PandoraSetting setting = new PandoraSetting();
				setting.save();
				return setting;
			}

			FileReader reader = new FileReader(file);

			BeanReader beanReader = new BeanReader();
			beanReader.registerBeanClass("Setting", PandoraSetting.class);

			PandoraSetting setting = (PandoraSetting) beanReader.parse(reader);
			return setting;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Loading pandora.xml error.", "Error", JOptionPane.ERROR_MESSAGE);
			new File("pandora.xml").delete();
			PandoraSetting pandoraSetting = new PandoraSetting();
			pandoraSetting.save();
			return pandoraSetting;
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

	public Vector<PandoraServerDefinition> getPandoraServers() {
		return pandoraServers;
	}

	public void setPandoraServers(Vector<PandoraServerDefinition> pandoraServers) {
		this.pandoraServers = pandoraServers;
	}

	public void addPandoraServers(PandoraServerDefinition pandoraServer) {
		if (pandoraServers != null) {
			pandoraServers.add(pandoraServer);
		}
	}

	public int getPandoraServerUpdateThread_milliSeconds() {
		if (pandoraServerUpdateThread_milliSeconds <= 0) {
			pandoraServerUpdateThread_milliSeconds = 1000;
		}
		return pandoraServerUpdateThread_milliSeconds;
	}

	public void setPandoraServerUpdateThread_milliSeconds(int pandoraServerUpdateThread_milliSeconds) {
		this.pandoraServerUpdateThread_milliSeconds = pandoraServerUpdateThread_milliSeconds;
	}
}
