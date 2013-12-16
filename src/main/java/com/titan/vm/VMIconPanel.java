package com.titan.vm;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
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
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;

public class VMIconPanel extends JPanel implements Runnable, VMPanel {
	JSONArray servers;
	VMMainPanel vmMainPanel;
	Vector<VMIcon> vmIcons = new Vector<VMIcon>();
	String searchPattern;
	int maxVMColumnCount;

	public VMIconPanel(VMMainPanel vmMainPanel) {
		this.vmMainPanel = vmMainPanel;

		new Thread(this).start();
	}

	public void init() {
		init(searchPattern, maxVMColumnCount);
	}

	public void init(String searchPattern, int maxVMColumnCount) {
		if (maxVMColumnCount == 0) {
			return;
		}
		this.searchPattern = searchPattern;
		this.maxVMColumnCount = maxVMColumnCount;
		if (searchPattern != null) {
			searchPattern = searchPattern.trim();
		}
		TitanSetting.getInstance().maxVMColumnCount = maxVMColumnCount;
		TitanSetting.getInstance().save();

		vmMainPanel.colLabel.setText(TitanSetting.getInstance().maxVMColumnCount + " columns");
		reAddVMIconsToVector();

		int noOfVM = servers.size();
		int maxCol = maxVMColumnCount;
		int maxRow = (int) Math.ceil(((float) noOfVM) / maxVMColumnCount);
		String colStr = StringUtils.repeat("[]", maxCol);
		String rowStr = StringUtils.repeat("[]", maxRow);
		//		System.out.println(noOfVM + "," + maxRow + "," + maxCol);
		vmMainPanel.iconPanel.setLayout(new MigLayout("", colStr, rowStr));

		removeAll();
		int row = 0;
		int col = 0;
		synchronized (vmIcons) {
			for (VMIcon vmIcon : vmIcons) {
				String name = TitanCommonLib.getJSONString(vmIcon.json, "name", null);
				if (searchPattern == null || searchPattern.equals("") || name.toLowerCase().contains(searchPattern.toLowerCase())) {
					add(vmIcon, "cell " + col + " " + row + ",aligny top");
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

	private void reAddVMIconsToVector() {
		synchronized (vmIcons) {
			Command command = new Command();
			command.command = "from titan: nova list";
			ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
			HttpResult httpResult = (HttpResult) r.map.get("result");
			servers = JSONObject.fromObject(httpResult.content).getJSONArray("servers");

			vmIcons.clear();
			for (Component c : getComponents()) {
				vmIcons.add((VMIcon) c);
			}

			outer: for (VMIcon vmIcon : vmIcons) {
				String instanceId = TitanCommonLib.getJSONString(vmIcon.json, "id", null);
				for (int x = 0; x < servers.size(); x++) {
					JSONObject obj = servers.getJSONObject(x);
					String tempInstanceId = TitanCommonLib.getJSONString(obj, "id", null);
					if (instanceId.equals(tempInstanceId)) {
						continue outer;
					}
				}
				System.out.println("remove " + vmIcon.vmName);
				vmIcons.remove(vmIcon);
			}
			outer: for (int x = 0; x < servers.size(); x++) {
				JSONObject obj = servers.getJSONObject(x);
				String instanceId = TitanCommonLib.getJSONString(obj, "id", null);
				for (VMIcon vmIcon : vmIcons) {
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
						if ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
							panel.setClicked(!panel.clicked);
						}
						panel.setSelected(true);
						if (vmMainPanel.isUpdatePropertyTableThreadRunning) {
							vmMainPanel.isUpdatePropertyTableThreadTrigger = true;
						}
						new Thread(vmMainPanel).start();
					}

					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							vmMainPanel.remote();
						}
					}
				});
				vmIcons.add(vmIcon);
			}

			Collections.sort(vmIcons);
		}
	}

	private void clearAllPanelsSelection() {
		for (VMIcon panel : vmIcons) {
			panel.setSelected(false);
		}
	}

	public void run() {
		while (true) {
			init();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Vector<JSONObject> getSelectedVM() {
		Vector<JSONObject> r = new Vector<JSONObject>();
		synchronized (vmIcons) {
			for (VMIcon vmPanel : vmIcons) {
				if (vmPanel.clicked) {
					r.add(vmPanel.json);
				}
			}
			if (r.size() == 0) {
				for (VMIcon vmPanel : vmIcons) {
					if (vmPanel.selected) {
						r.add(vmPanel.json);
					}
				}
			}
		}
		return r;
	}
}
