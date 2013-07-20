package com.c2.pandora.thread;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import com.c2.pandora.PandoraSetting;
import com.c2.pandora.communication.CommunicateLib;
import com.c2.pandora.serverpanel.ServerPanel;
import com.c2.pandora.serverpanel.ServerTableModel;
import com.c2.pandoraserver.Command;
import com.c2.pandoraserver.ReturnCommand;

public class PandoraServerUpdateThread implements Runnable {
	public static HashMap<String, TreeSet<Status>> status = new HashMap<String, TreeSet<Status>>();

	public void run() {
		ServerTableModel model = ServerPanel.serverTableModel;
		while (true) {
			for (int x = 0; x < model.getRowCount(); x++) {
				try {
					String id = (String) model.getValueAt(x, model.getColumnIndex("ID"));
					String remoteAddr = (String) model.getValueAt(x, model.getColumnIndex("IP"));
					Command command = new Command();
					command.command = "updateStatus";
					ReturnCommand r;
					if (remoteAddr.contains(":")) {
						r = CommunicateLib.send(remoteAddr.split(":")[0], Integer.parseInt(remoteAddr.split(":")[1]), command);
					} else {
						r = CommunicateLib.send(remoteAddr, 4444, command);
					}

					if (r != null) {
						Status s = new Status();
						s.date = new Date();
						s.maxMemory = (Long) r.map.get("maxMemory");
						s.freeMemory = (Long) r.map.get("freeMemory");
						s.allocatedMemory = (Long) r.map.get("allocatedMemory");
						s.cpu_combined = (String) r.map.get("cpu_combined");
						s.cpu_sys = (String) r.map.get("cpu_sys");
						s.cpu_user = (String) r.map.get("cpu_user");
						s.os = (String) r.map.get("os");
						s.outSegs = (Long) r.map.get("outSegs");
						s.inSegs = (Long) r.map.get("inSegs");

						TreeSet<Status> t = status.get(id);
						if (t == null) {
							t = new TreeSet<Status>();
							t.add(s);
							status.put(id, t);
						} else {
							t.add(s);
						}
					}
					int selectedRowIndex = ServerPanel.table.getSelectedRow();
					model.fireTableDataChanged();
					if (selectedRowIndex >= 0 && selectedRowIndex < model.getRowCount()) {
						ServerPanel.table.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);
					}
				} catch (Exception ex) {
				}
			}
			try {
				Thread.currentThread().sleep(PandoraSetting.getInstance().pandoraServerUpdateThread_milliSeconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
