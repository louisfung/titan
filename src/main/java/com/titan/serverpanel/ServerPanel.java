package com.c2.pandora.serverpanel;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyMinimumViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.util.Range;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.Mem;

import com.c2.pandora.PandoraCommonLib;
import com.c2.pandora.PandoraSetting;
import com.c2.pandora.communication.CommunicateLib;
import com.c2.pandora.thread.PandoraServerUpdateThread;
import com.c2.pandora.thread.Status;
import com.c2.pandoraserver.Command;
import com.c2.pandoraserver.ReturnCommand;
import com.c2.pandoraserver.structure.PandoraServerDefinition;
import com.peterswing.advancedswing.searchtextfield.JSearchTextField;

import eu.hansolo.steelseries.gauges.Radial;

public class ServerPanel extends JPanel {
	public static JTable table = new JTable();
	public static ServerTableModel serverTableModel = new ServerTableModel();
	JTable tableCpu;
	JTable tableCpuInfoList;
	JTable tableCpuPerc;
	JTable tableFSList;
	JTable tableOther;
	JTable tableMem;
	//	ChartPanel chartPanel;
	//	JFreeChart chart;
	//	XYSeriesCollection dataset = new XYSeriesCollection();
	Chart2D chart = new Chart2D();
	ITrace2D combinedTrace = new Trace2DLtd(200);
	ITrace2D sysTrace = new Trace2DLtd(200);
	ITrace2D userTrace = new Trace2DLtd(200);
	Radial radialCpu = new Radial();
	Radial radialNetwork = new Radial();
	Radial radialMemory = new Radial();

	public ServerPanel() {
		table.getTableHeader().setReorderingAllowed(false);
		chart.getAxisY().setRangePolicy(new RangePolicyMinimumViewport(new Range(-1, +1)));
		chart.addTrace(combinedTrace);
		chart.addTrace(sysTrace);
		chart.addTrace(userTrace);
		chart.getAxisX().setPaintGrid(true);
		chart.getAxisY().setPaintGrid(true);
		chart.setGridColor(Color.LIGHT_GRAY);
		combinedTrace.setName("sys + user");
		sysTrace.setName("sys");
		userTrace.setName("user");
		combinedTrace.setColor(Color.blue);
		sysTrace.setColor(new Color(0, 160, 0));
		userTrace.setColor(Color.red);

		//		chart = ChartFactory.createXYLineChart(null, // chart title
		//				null, // x axis label
		//				null, // y axis label
		//				dataset, // data
		//				PlotOrientation.VERTICAL, true, // include legend
		//				false, // tooltips
		//				false // urls
		//				);
		//		chart.removeLegend();
		//
		//		XYPlot plot = chart.getXYPlot();
		//		plot.setBackgroundPaint(Color.white);
		//		plot.setDomainGridlinePaint(Color.white);
		//		plot.setRangeGridlinePaint(Color.white);
		//		//		plot.getRangeAxis().setVisible(false);
		//		plot.getDomainAxis().setVisible(false);
		//		plot.setOutlineVisible(false);
		//
		//		XYSplineRenderer renderer = new XYSplineRenderer();
		//		renderer.setSeriesShapesVisible(0, false);
		//		renderer.setSeriesPaint(0, Color.black);
		//		plot.setRenderer(renderer);
		//		chartPanel = new ChartPanel(chart);

		JScrollPane scrollPane = new JScrollPane();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Command command = new Command();
				command.command = "getPandoraServerInfo";
				command.parameters.add(table.getValueAt(table.getSelectedRow(), 1));
				ReturnCommand r = CommunicateLib.send(PandoraCommonLib.getCurrentServerIP(), command);
				if (r != null) {
					DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
					rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment(JTextField.CENTER);

					////////////////////////////////////////////////////////////////////////////////////

					Cpu cpu = (Cpu) r.map.get("cpu");
					String s[][] = new String[][] {
							{ "Idle", String.valueOf(cpu.getIdle()), "Irq", String.valueOf(cpu.getIrq()), "Nice", String.valueOf(cpu.getNice()), "Soft Irq",
									String.valueOf(cpu.getSoftIrq()) },

							{ "Stolen", String.valueOf(cpu.getStolen()), "Sys", String.valueOf(cpu.getSys()), "Total", String.valueOf(cpu.getTotal()), "User",
									String.valueOf(cpu.getUser()) }, { "Wait", String.valueOf(cpu.getWait()), "", "", "", "", "", "" } };
					tableCpu.setModel(new DefaultTableModel(s, new String[] { "Property", "Value", "Property", "Value", "Property", "Value", "Property", "Value" }));

					tableCpu.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					tableCpu.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					tableCpu.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
					tableCpu.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
					tableCpu.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
					tableCpu.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
					tableCpu.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

					////////////////////////////////////////////////////////////////////////////////////

					CpuInfo[] cpuInfoList = (CpuInfo[]) r.map.get("cpuInfoList");
					s = new String[cpuInfoList.length][12];
					for (int x = 0; x < cpuInfoList.length; x++) {
						CpuInfo cpuInfo = cpuInfoList[x];
						int y = 0;
						s[x][y++] = String.valueOf(cpuInfo.getModel());
						s[x][y++] = String.valueOf(cpuInfo.getVendor());
						s[x][y++] = String.valueOf(cpuInfo.getCacheSize());
						s[x][y++] = String.valueOf(cpuInfo.getCoresPerSocket());
						s[x][y++] = String.valueOf(cpuInfo.getMhz());
						s[x][y++] = String.valueOf(cpuInfo.getTotalCores());
					}
					tableCpuInfoList.setModel(new DefaultTableModel(s, new String[] { "Vendor", "Model", "Cache size", "Core per socket", "Mhz", "Total cores" }));

					tableCpuInfoList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					tableCpuInfoList.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					tableCpuInfoList.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
					tableCpuInfoList.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
					tableCpuInfoList.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
					tableCpuInfoList.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
					tableCpuInfoList.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
					tableCpuInfoList.getColumnModel().getColumn(0).setPreferredWidth(250);
					tableCpuInfoList.getColumnModel().getColumn(1).setPreferredWidth(80);
					tableCpuInfoList.getColumnModel().getColumn(2).setPreferredWidth(150);
					tableCpuInfoList.getColumnModel().getColumn(3).setPreferredWidth(150);
					tableCpuInfoList.getColumnModel().getColumn(4).setPreferredWidth(150);
					tableCpuInfoList.getColumnModel().getColumn(5).setPreferredWidth(120);

					////////////////////////////////////////////////////////////////////////////////////

					CpuPerc cpuPerc = (CpuPerc) r.map.get("cpuPerc");
					s = new String[][] { { "Idle", String.valueOf(cpuPerc.getIdle()) }, { "Irq", String.valueOf(cpuPerc.getIrq()) }, { "Nice", String.valueOf(cpuPerc.getNice()) },
							{ "Soft Irq", String.valueOf(cpuPerc.getSoftIrq()) }, { "Stolen", String.valueOf(cpuPerc.getStolen()) }, { "Sys", String.valueOf(cpuPerc) },
							{ "Combined", String.valueOf(cpuPerc.getCombined()) }, { "User", String.valueOf(cpuPerc.getUser()) }, { "Wait", String.valueOf(cpuPerc.getWait()) },
							{ "Stolen", String.valueOf(cpuPerc.getStolen()) } };
					tableCpuPerc.setModel(new DefaultTableModel(s, new String[] { "Property", "Value" }));

					tableCpuPerc.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					tableCpuPerc.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					tableCpuPerc.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
					tableCpuPerc.getColumnModel().getColumn(1).setPreferredWidth(500);

					////////////////////////////////////////////////////////////////////////////////////

					FileSystem fsList[] = (FileSystem[]) r.map.get("fileSystemList");
					s = new String[fsList.length][7];
					for (int x = 0; x < fsList.length; x++) {
						FileSystem fs = fsList[x];
						int y = 0;
						s[x][y++] = String.valueOf(fs.getDevName());
						s[x][y++] = String.valueOf(fs.getDirName());
						s[x][y++] = String.valueOf(fs.getFlags());
						s[x][y++] = String.valueOf(fs.getOptions());
						s[x][y++] = String.valueOf(fs.getSysTypeName());
						s[x][y++] = String.valueOf(fs.getType());
						s[x][y++] = String.valueOf(fs.getTypeName());
					}
					tableFSList.setModel(new DefaultTableModel(s, new String[] { "Dev name", "Dir name", "Flags", "Options", "Sys type name", "type", "type name" }));

					tableFSList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					tableFSList.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					tableFSList.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
					tableFSList.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
					tableFSList.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
					tableFSList.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
					tableFSList.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
					tableFSList.getColumnModel().getColumn(1).setPreferredWidth(300);
					tableFSList.getColumnModel().getColumn(3).setPreferredWidth(300);
					tableFSList.getColumnModel().getColumn(4).setPreferredWidth(200);

					////////////////////////////////////////////////////////////////////////////////////
					double averages[] = (double[]) r.map.get("loadAverage");
					s = new String[2 + averages.length][2];
					s[0][0] = "FQDN";
					s[0][1] = (String) r.map.get("fqdn");

					s[1][0] = "Load Averages";
					s[1][1] = "";

					for (int x = 0; x < averages.length; x++) {
						s[x + 2][0] = "";
						s[x + 2][1] = String.valueOf(averages[x]);
					}

					tableOther.setModel(new DefaultTableModel(s, new String[] { "Property", "Value" }));

					tableOther.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					tableOther.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					tableOther.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
					tableOther.getColumnModel().getColumn(0).setPreferredWidth(200);
					tableOther.getColumnModel().getColumn(1).setPreferredWidth(200);

					////////////////////////////////////////////////////////////////////////////////////

					Mem mem = (Mem) r.map.get("mem");

					s = new String[][] { { "Actual free", String.valueOf(mem.getActualFree()) }, { "Actual used", String.valueOf(mem.getActualUsed()) },
							{ "Free", String.valueOf(mem.getFree()) }, { "Free percent", String.valueOf(mem.getFreePercent()) + "%" }, { "Ram", String.valueOf(mem.getRam()) },
							{ "Total", String.valueOf(mem.getTotal()) }, { "Used", String.valueOf(mem.getUsed()) }, { "Used percent", String.valueOf(mem.getUsedPercent()) } };
					tableMem.setModel(new DefaultTableModel(s, new String[] { "Property", "Value" }));

					tableMem.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					tableMem.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					tableMem.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

					tableMem.getColumnModel().getColumn(0).setPreferredWidth(150);
					tableMem.getColumnModel().getColumn(1).setPreferredWidth(500);
				}
			}
		});

		table.setModel(serverTableModel);
		for (int x = 0; x < table.getColumnCount(); x++) {
			table.getColumnModel().getColumn(x).setCellRenderer(new ServerTableCellRenderer());
		}

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);

		JSearchTextField searchTextField = new JSearchTextField();

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(scrollPane);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.TRAILING)
										.addGroup(Alignment.LEADING,
												groupLayout.createSequentialGroup().addContainerGap().addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE))
										.addComponent(panel, GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
										.addGroup(
												Alignment.LEADING,
												groupLayout.createSequentialGroup().addContainerGap()
														.addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addContainerGap().addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(panel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)));

		JPanel panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel_1.add(tabbedPane, BorderLayout.CENTER);

		JScrollPane scrollPane_7 = new JScrollPane();
		tabbedPane.addTab("Info", null, scrollPane_7, null);

		JPanel panelInfo = new JPanel();
		scrollPane_7.setViewportView(panelInfo);

		radialCpu.setTitle("Cpu");
		radialCpu.setUnitString("%");
		radialCpu.setTrackStart(70);
		radialCpu.setTrackStop(100);
		radialCpu.setTrackVisible(true);
		radialCpu.setLcdVisible(true);
		radialCpu.setValueAnimated(50);
		radialCpu.setTrackStartColor(Color.orange);
		radialCpu.setTrackStopColor(Color.red);

		radialMemory.setValueAnimated(50.0);
		radialMemory.setUnitString("%");
		radialMemory.setTrackVisible(true);
		radialMemory.setTrackStopColor(Color.RED);
		radialMemory.setTrackStop(100.0);
		radialMemory.setTrackStartColor(Color.ORANGE);
		radialMemory.setTrackStart(70.0);
		radialMemory.setTitle("Ram");
		radialMemory.setLcdVisible(true);

		radialNetwork.setValueAnimated(50.0);
		radialNetwork.setUnitString("Mbps");
		radialNetwork.setTrackVisible(true);
		radialNetwork.setTrackStopColor(Color.RED);
		radialNetwork.setTrackStop(100.0);
		radialNetwork.setTrackStartColor(Color.ORANGE);
		radialNetwork.setTrackStart(70.0);
		radialNetwork.setTitle("Network");
		radialNetwork.setLcdVisible(true);

		GroupLayout gl_panelInfo = new GroupLayout(panelInfo);
		gl_panelInfo.setHorizontalGroup(gl_panelInfo.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panelInfo.createSequentialGroup().addContainerGap().addComponent(radialCpu, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE).addGap(20)
						.addComponent(radialMemory, GroupLayout.PREFERRED_SIZE, 201, GroupLayout.PREFERRED_SIZE).addGap(20)
						.addComponent(radialNetwork, GroupLayout.PREFERRED_SIZE, 201, GroupLayout.PREFERRED_SIZE).addGap(12)
						.addComponent(chart, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE).addContainerGap()));
		gl_panelInfo.setVerticalGroup(gl_panelInfo
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panelInfo
								.createSequentialGroup()
								.addGap(47)
								.addGroup(
										gl_panelInfo.createParallelGroup(Alignment.TRAILING)
												.addComponent(radialMemory, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
												.addComponent(radialCpu, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(radialNetwork, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)))
				.addGroup(gl_panelInfo.createSequentialGroup().addContainerGap().addComponent(chart, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE).addGap(9)));
		panelInfo.setLayout(gl_panelInfo);

		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Cpu", null, scrollPane_1, null);

		tableCpu = new JTable();
		tableCpu.getTableHeader().setReorderingAllowed(false);
		tableCpu.getTableHeader().setReorderingAllowed(false);
		tableCpu.getTableHeader().setReorderingAllowed(false);
		scrollPane_1.setViewportView(tableCpu);

		JScrollPane scrollPane_2 = new JScrollPane();
		tabbedPane.addTab("Cpu info list", null, scrollPane_2, null);

		tableCpuInfoList = new JTable();
		tableCpuInfoList.getTableHeader().setReorderingAllowed(false);
		scrollPane_2.setViewportView(tableCpuInfoList);

		JScrollPane scrollPane_3 = new JScrollPane();
		tabbedPane.addTab("Cpu Perc", null, scrollPane_3, null);

		tableCpuPerc = new JTable();
		tableCpuPerc.getTableHeader().setReorderingAllowed(false);
		scrollPane_3.setViewportView(tableCpuPerc);

		JScrollPane scrollPane_4 = new JScrollPane();
		tabbedPane.addTab("File system list", null, scrollPane_4, null);

		tableFSList = new JTable();
		tableFSList.getTableHeader().setReorderingAllowed(false);
		scrollPane_4.setViewportView(tableFSList);

		JScrollPane scrollPane_5 = new JScrollPane();
		tabbedPane.addTab("Other", null, scrollPane_5, null);

		tableOther = new JTable();
		tableOther.getTableHeader().setReorderingAllowed(false);
		scrollPane_5.setViewportView(tableOther);

		JScrollPane scrollPane_6 = new JScrollPane();
		tabbedPane.addTab("Mem", null, scrollPane_6, null);

		tableMem = new JTable();
		tableMem.getTableHeader().setReorderingAllowed(false);
		scrollPane_6.setViewportView(tableMem);

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddServerDialog dialog = new AddServerDialog();
				dialog.setVisible(true);
				((ServerTableModel) table.getModel()).fireTableDataChanged();
			}
		});
		panel.add(btnAdd);

		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel.add(btnEdit);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() == 1) {
					ServerTableModel model = (ServerTableModel) table.getModel();
					int rowNo = table.getSelectedRow();
					String id = (String) model.getValueAt(rowNo, 1);
					String ip = (String) model.getValueAt(rowNo, 2);
					for (int x = PandoraSetting.getInstance().pandoraServers.size() - 1; x >= 0; x--) {
						PandoraServerDefinition server = PandoraSetting.getInstance().pandoraServers.get(x);
						if (server.id.equals(id) && server.ip.equals(ip)) {
							PandoraSetting.getInstance().pandoraServers.remove(x);
						}
					}
					PandoraSetting.getInstance().save();
					model.fireTableDataChanged();
				}
			}
		});
		panel.add(btnDelete);

		JButton btnShell = new JButton("Shell");
		panel.add(btnShell);
		setLayout(groupLayout);

		splitPane.setDividerLocation(150);

		Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			private long m_starttime = System.currentTimeMillis();
			int lastx = 0;

			@Override
			public void run() {
				try {
					String serverID = (String) table.getValueAt(table.getSelectedRow(), 1);
					if (serverID != null) {
						if (PandoraServerUpdateThread.status.size() != 0 && PandoraServerUpdateThread.status.get(serverID) != null) {
							Object objs[] = PandoraServerUpdateThread.status.get(serverID).toArray();
							int start = objs.length - 1;
							for (int x = start; x >= lastx; x--) {
								Status status = (Status) objs[x];
								double value = Double.parseDouble(status.cpu_combined);
								combinedTrace.addPoint(((double) System.currentTimeMillis() - m_starttime), value);
								radialCpu.setValue(value);

								value = Double.parseDouble(status.cpu_sys);
								sysTrace.addPoint(((double) System.currentTimeMillis() - m_starttime), value);

								value = Double.parseDouble(status.cpu_user);
								userTrace.addPoint(((double) System.currentTimeMillis() - m_starttime), value);

								radialMemory.setValue(status.allocatedMemory * 100 / status.maxMemory);
								radialNetwork.setValue(status.inSegs);
							}
							lastx = start;
						} else {
							radialCpu.setValue(0);
						}
					} else {
						radialCpu.setValue(0);
					}
				} catch (Exception ex) {
				}
			}

		};
		timer.schedule(task, 1000, 20);

		table.setRowSelectionInterval(0, 0);
	}
}
