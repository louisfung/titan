package com.titan.serverpanel;

import java.util.Date;

import javax.swing.table.DefaultTableModel;

import com.titan.Global;
import com.titan.TitanCommonLib;
import com.titan.TitanSetting;
import com.titan.communication.CommunicateLib;
import com.titan.thread.Status;
import com.titan.thread.TitanServerUpdateThread;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;
import com.titanserver.structure.TitanServerDefinition;

public class ServerTableModel extends DefaultTableModel {
	String columnNames[] = { "", "ID", "IP", "CPU", "Ram", "OS" };
	TitanServerDefinition primaryServer;

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
		return TitanSetting.getInstance().titanServers.size() + 1;
	}

	public void setValueAt(Object aValue, int row, int column) {
		this.fireTableDataChanged();
	}

	public Object getValueAt(final int row, int column) {
		try {
			TitanServerDefinition server;
			if (row < 0) {
				return null;
			} else if (row == 0) {
				if (primaryServer == null) {
					primaryServer = new TitanServerDefinition();
					primaryServer.ip = Global.primaryServerIP;
					primaryServer.id = "";

					Command command = new Command();
					command.command = "getID";
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
					if (r != null) {
						primaryServer.id = r.message;
						ServerTableModel.this.fireTableDataChanged();
					}
				}
				server = primaryServer;
			} else {
				//			if (row - 1 < TitanSetting.getInstance().titanServers.size()) {
				server = TitanSetting.getInstance().titanServers.get(row - 1);
				//			} else {
				//				return null;
				//			}
			}
			boolean noData = false;
			Status status = null;
			if (TitanServerUpdateThread.status.size() == 0 || TitanServerUpdateThread.status.get(server.id) == null) {
				noData = true;
			} else {
				Object objs[] = TitanServerUpdateThread.status.get(server.id).toArray();
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
