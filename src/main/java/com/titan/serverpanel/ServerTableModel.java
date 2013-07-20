package com.c2.pandora.serverpanel;

import java.util.Date;

import javax.swing.table.DefaultTableModel;

import com.c2.pandora.Global;
import com.c2.pandora.PandoraCommonLib;
import com.c2.pandora.PandoraSetting;
import com.c2.pandora.communication.CommunicateLib;
import com.c2.pandora.thread.PandoraServerUpdateThread;
import com.c2.pandora.thread.Status;
import com.c2.pandoraserver.Command;
import com.c2.pandoraserver.ReturnCommand;
import com.c2.pandoraserver.structure.PandoraServerDefinition;

public class ServerTableModel extends DefaultTableModel {
	String columnNames[] = { "", "ID", "IP", "CPU", "Ram", "OS" };
	PandoraServerDefinition primaryServer;

	public ServerTableModel() {
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public int getColumnCount() {
		if (columnNames == null) {
			return 0;
		}
		return columnNames.length;
	}

	public int getRowCount() {
		return PandoraSetting.getInstance().pandoraServers.size() + 1;
	}

	public void setValueAt(Object aValue, int row, int column) {
		this.fireTableDataChanged();
	}

	public Object getValueAt(final int row, int column) {
		try {
			PandoraServerDefinition server;
			if (row < 0) {
				return null;
			} else if (row == 0) {
				if (primaryServer == null) {
					primaryServer = new PandoraServerDefinition();
					primaryServer.ip = Global.primaryServerIP;
					primaryServer.id = "";

					Command command = new Command();
					command.command = "getID";
					ReturnCommand r = CommunicateLib.send(PandoraCommonLib.getCurrentServerIP(), command);
					if (r != null) {
						primaryServer.id = r.message;
						ServerTableModel.this.fireTableDataChanged();
					}
				}
				server = primaryServer;
			} else {
				//			if (row - 1 < PandoraSetting.getInstance().pandoraServers.size()) {
				server = PandoraSetting.getInstance().pandoraServers.get(row - 1);
				//			} else {
				//				return null;
				//			}
			}
			boolean noData = false;
			Status status = null;
			if (PandoraServerUpdateThread.status.size() == 0 || PandoraServerUpdateThread.status.get(server.id) == null) {
				noData = true;
			} else {
				Object objs[] = PandoraServerUpdateThread.status.get(server.id).toArray();
				status = (Status) objs[objs.length - 1];
			}
			if (column == 0) {
				if (noData) {
					return false;
				} else {
					return (new Date().getTime() - status.date.getTime()) / 1000 < 4;
				}
			} else if (column == 1) {
				return server.id;
			} else if (column == 2) {
				return server.ip;
			} else if (column == 3) {
				if (noData) {
					return "";
				} else {
					return status.cpu_combined;
				}
			} else if (column == 4) {
				if (noData) {
					return "";
				} else {
					return status.freeMemory * 100 / status.maxMemory;
				}
			} else if (column == 5) {
				if (noData) {
					return "";
				} else {
					return status.os;
				}
			} else {
				return "";
			}
		} catch (Exception ex) {
			return "";
		}
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public Class getColumnClass(int columnIndex) {
		if (getValueAt(0, columnIndex) == null) {
			return Object.class;
		}
		return getValueAt(0, columnIndex).getClass();
	}

	public int getColumnIndex(String columnName) {
		for (int x = 0; x < columnNames.length; x++) {
			if (columnNames[x].equals(columnName)) {
				return x;
			}
		}
		return -1;
	}
}
