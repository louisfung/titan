package com.titan.vm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.titan.OS;
import com.titan.TitanCommonLib;
import com.titan.TitanSetting;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class VMIconPanel extends JPanel implements ActionListener {
	JSONArray servers;
	VMMainPanel vmMainPanel;
	Vector<VMPanel> panels = new Vector<VMPanel>();

	public VMIconPanel(VMMainPanel vmMainPanel) {
		this.vmMainPanel = vmMainPanel;

		Timer timer = new Timer(1000, this);
		timer.start();
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
			String vmName = TitanCommonLib.getJSONString(obj, "name", "No name");
			//			String instanceId = TitanCommonLib.getJSONString(obj, "id", null);
			//			System.out.println(obj);
			VMPanel panel = new VMPanel(obj);
			panels.add(panel);

			if (vmName.length() > 10) {
				vmName = vmName.substring(0, 10);
			}
			panel.label.setText("<html>" + vmName + "<br>34d 10m</html>");
			panel.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					clearAllPanelsSelection();
					VMPanel panel = (VMPanel) e.getSource();
					vmMainPanel.json = panel.json;
					panel.setSelected(true);
				}

				private void clearAllPanelsSelection() {
					for (VMPanel panel : panels) {
						panel.setSelected(false);
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
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

	@Override
	public void actionPerformed(ActionEvent e) {
		this.repaint();
	}

}
