package com.titan.vm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

public class VMIconPanel extends JPanel implements Runnable {
	JSONArray servers;
	VMMainPanel vmMainPanel;
	Vector<VMPanel> panels = new Vector<VMPanel>();

	public VMIconPanel(VMMainPanel vmMainPanel) {
		this.vmMainPanel = vmMainPanel;

		new Thread(this).start();
	}

	public void init(int maxVMColumnCount) {
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
		// System.out.println(noOfVM + "," + maxRow + "," + maxCol);
		vmMainPanel.iconPanel.setLayout(new MigLayout("", colStr, rowStr));
		int row = 0;
		int col = 0;
		vmMainPanel.iconPanel.removeAll();
		panels.clear();
		for (int x = 0; x < servers.size(); x++) {
			JSONObject obj = servers.getJSONObject(x);
			//			String instanceId = TitanCommonLib.getJSONString(obj, "id", null);
			//			System.out.println(obj);
			VMPanel panel = new VMPanel(obj);
			panels.add(panel);
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					clearAllPanelsSelection();
					VMPanel panel = (VMPanel) e.getSource();
					vmMainPanel.selectedVM = panel.json;
					panel.setClicked(!panel.clicked);
					panel.setSelected(true);
					vmMainPanel.updatePropertyTable();
				}
			});
			add(panel, "cell " + col + " " + row);
			col++;
			if (col == maxCol) {
				col = 0;
				row++;
			}
		}
		updateUI();
	}

	private void clearAllPanelsSelection() {
		for (VMPanel panel : panels) {
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
				for (VMPanel vmPanel : panels) {
					if (instanceId.equals(TitanCommonLib.getJSONString(vmPanel.json, "id", null))) {
						vmPanel.json = obj;
						vmPanel.repaint();
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

}
