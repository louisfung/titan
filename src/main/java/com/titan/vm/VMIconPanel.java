package com.titan.vm;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.titan.TitanCommonLib;
import com.titan.TitanSetting;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class VMIconPanel extends JPanel implements Runnable, VMPanel {
	JSONArray servers;
	VMMainPanel vmMainPanel;
	Vector<VMIcon> vmIcons = new Vector<VMIcon>();

	public VMIconPanel(VMMainPanel vmMainPanel) {
		this.vmMainPanel = vmMainPanel;

		new Thread(this).start();
	}

	@SuppressWarnings("unchecked")
	public void init(String searchPattern, int maxVMColumnCount) {
		if (searchPattern != null) {
			searchPattern = searchPattern.trim();
		}
		TitanSetting.getInstance().maxVMColumnCount = maxVMColumnCount;
		TitanSetting.getInstance().save();
		Command command = new Command();
		command.command = "from titan: nova list";
		if (servers == null) {
			ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
			servers = JSONObject.fromObject(r.map.get("result")).getJSONArray("servers");
		}
		vmMainPanel.colLabel.setText(TitanSetting.getInstance().maxVMColumnCount + " columns");
		int noOfVM = servers.size();
		int maxCol = maxVMColumnCount;
		int maxRow = (int) Math.ceil(((float) noOfVM) / maxVMColumnCount);
		String colStr = StringUtils.repeat("[]", maxCol);
		String rowStr = StringUtils.repeat("[]", maxRow);
		System.out.println(noOfVM + "," + maxRow + "," + maxCol);
		vmMainPanel.iconPanel.setLayout(new MigLayout("", colStr, rowStr));
		int row = 0;
		int col = 0;
		Vector<VMIcon> icons = new Vector<VMIcon>();
		for (Component c : getComponents()) {
			icons.add((VMIcon) c);
		}
		outer: for (VMIcon vmIcon : icons) {
			String instanceId = TitanCommonLib.getJSONString(vmIcon.json, "id", null);
			for (int x = 0; x < servers.size(); x++) {
				JSONObject obj = servers.getJSONObject(x);
				String tempInstanceId = TitanCommonLib.getJSONString(obj, "id", null);
				if (instanceId.equals(tempInstanceId)) {
					continue outer;
				}
			}
			icons.remove(vmIcon);
		}
		outer: for (int x = 0; x < servers.size(); x++) {
			JSONObject obj = servers.getJSONObject(x);
			String instanceId = TitanCommonLib.getJSONString(obj, "id", null);
			for (VMIcon vmIcon : icons) {
				String tempInstanceId = TitanCommonLib.getJSONString(vmIcon.json, "id", null);
				if (instanceId.equals(tempInstanceId)) {
					continue outer;
				}
			}
			VMIcon vmIcon = new VMIcon(obj);
			vmIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					clearAllPanelsSelection();
					VMIcon panel = (VMIcon) e.getSource();
					vmMainPanel.selectedVM = panel.json;
					panel.setClicked(!panel.clicked);
					panel.setSelected(true);
					if (vmMainPanel.isUpdatePropertyTableThreadRunning) {
						vmMainPanel.isUpdatePropertyTableThreadTrigger = true;
					}
					new Thread(vmMainPanel).start();
				}
			});
			icons.add(vmIcon);
		}

		removeAll();
		synchronized (vmIcons) {
			for (VMIcon vmIcon : icons) {
				String name = TitanCommonLib.getJSONString(vmIcon.json, "name", null);
				if (searchPattern == null || searchPattern.equals("") || name.toLowerCase().contains(searchPattern.toLowerCase())) {

					add(vmIcon, "cell " + col + " " + row);
					col++;
					if (col == maxCol) {
						col = 0;
						row++;
					}
				}
			}
		}
		updateUI();
	}

	private void clearAllPanelsSelection() {
		for (VMIcon panel : vmIcons) {
			panel.setSelected(false);
		}
	}

	public void run() {
		while (true) {
			Command command = new Command();
			command.command = "from titan: nova list";
			ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
			servers = JSONObject.fromObject(r.map.get("result")).getJSONArray("servers");
			for (int x = 0; x < servers.size(); x++) {
				JSONObject obj = servers.getJSONObject(x);
				String instanceId = TitanCommonLib.getJSONString(obj, "id", null);
				synchronized (vmIcons) {
					for (VMIcon vmPanel : vmIcons) {
						if (instanceId.equals(TitanCommonLib.getJSONString(vmPanel.json, "id", null))) {
							vmPanel.json = obj;
							vmPanel.repaint();
						}
					}
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Vector<JSONObject> getSelectedVM() {
		Vector<JSONObject> r = new Vector<JSONObject>();
		for (VMIcon vmPanel : vmIcons) {
			if (vmPanel.clicked) {
				//				String instanceId = TitanCommonLib.getJSONString(vmPanel.json, "id", null);
				//				if (instanceId != null) {
				r.add(vmPanel.json);
				//				}
			}
		}
		return r;
	}
}
