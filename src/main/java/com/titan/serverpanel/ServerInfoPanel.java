package com.titan.serverpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;
import com.titanserver.table.ServerDiagnostics;
import com.toedter.calendar.JDateChooser;

public class ServerInfoPanel extends JPanel implements Runnable {
	private JDateChooser fromDateChooser;
	private JDateChooser toDateChooser;
	//	ITrace2D cpuTrace = new Trace2DSimple();
	//	ITrace2D memoryTrace = new Trace2DSimple();
	boolean isRunning;

	TimeSeriesCollection cpuDataset = new TimeSeriesCollection();
	JFreeChart cpuChart = ChartFactory.createTimeSeriesChart("", "", "%", cpuDataset, false, false, false);
	ChartPanel cpuChartPanel = new ChartPanel(cpuChart);

	TimeSeriesCollection memoryDataset = new TimeSeriesCollection();
	JFreeChart memoryChart = ChartFactory.createTimeSeriesChart("", "", "%", memoryDataset, false, false, false);
	ChartPanel memoryChartPanel = new ChartPanel(memoryChart);

	TimeSeriesCollection diskDataset = new TimeSeriesCollection();
	JFreeChart diskChart = ChartFactory.createTimeSeriesChart("", "", "io/s", diskDataset, false, false, false);
	ChartPanel diskChartPanel = new ChartPanel(diskChart);

	TimeSeriesCollection networkDataset = new TimeSeriesCollection();
	JFreeChart networkChart = ChartFactory.createTimeSeriesChart("", "", "io/s", networkDataset, false, false, false);
	ChartPanel networkChartPanel = new ChartPanel(networkChart);

	TimeSeriesCollection cpuDetailDataset = new TimeSeriesCollection();
	JFreeChart cpuDetailChart = ChartFactory.createTimeSeriesChart("", "", "%", cpuDetailDataset, false, false, false);
	ChartPanel cpuDetailChartPanel = new ChartPanel(cpuDetailChart);

	TimeSeriesCollection memoryDetailDataset = new TimeSeriesCollection();
	JFreeChart memoryDetailChart = ChartFactory.createTimeSeriesChart("", "", "%", memoryDetailDataset, false, false, false);
	ChartPanel memoryDetailChartPanel = new ChartPanel(memoryDetailChart);

	int chartWidth = 250;
	int chartHeight = 250;
	private final JComboBox<String> fromComboBox = new JComboBox<String>();
	private final JComboBox<String> toComboBox = new JComboBox<String>();
	private final JButton btnRefresh = new JButton("Refresh");
	private final JLabel lblCpu = new JLabel("CPU");
	private final JLabel lblMemory = new JLabel("Memory");
	private final JLabel lblDiskIo = new JLabel("Disk I/O");
	private final JLabel lblNetwork = new JLabel("Network");
	private final JComboBox periodComboBox = new JComboBox(new String[] { "mintue", "hour", "day", "month" });
	private final JLabel lblCpuDetail = new JLabel("CPU Detail");
	private final JLabel lblMemoryDetail = new JLabel("Memory Detail");
	private final JLabel updateLabel = new JLabel("");
	ButtonGroup buttonGroup = new ButtonGroup();
	private final JRadioButton rdbtnMax = new JRadioButton("max");
	private final JRadioButton rdbtnMin = new JRadioButton("min");
	private final JRadioButton rdbtnAvg = new JRadioButton("avg");

	public ServerInfoPanel() {
		setLayout(new MigLayout("", "[][][][]", "[][][250px][][250px][][250px]"));

		buttonGroup.add(rdbtnMax);
		buttonGroup.add(rdbtnMin);
		buttonGroup.add(rdbtnAvg);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, "cell 0 0 4 1,grow");

		JLabel lblFrom = new JLabel("From");
		panel.add(lblFrom);

		fromDateChooser = new JDateChooser();
		fromDateChooser.setDateFormatString("yyyy/MM/dd");
		fromDateChooser.setDate(new Date());
		fromDateChooser.setPreferredSize(new Dimension(150, 25));
		panel.add(fromDateChooser);

		for (int x = 0; x < 24; x++) {
			fromComboBox.addItem(String.format("%02d", x) + ":00");
		}
		panel.add(fromComboBox);

		JLabel lblTo = new JLabel("To");
		panel.add(lblTo);

		toDateChooser = new JDateChooser();
		toDateChooser.setDateFormatString("yyyy/MM/dd");
		toDateChooser.setDate(new Date());
		toDateChooser.setPreferredSize(new Dimension(150, 25));
		panel.add(toDateChooser);

		for (int x = 0; x < 24; x++) {
			toComboBox.addItem(String.format("%02d", x) + ":59");
		}
		toComboBox.setSelectedItem("23:59");
		panel.add(toComboBox);
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(ServerInfoPanel.this).start();
			}
		});
		panel.add(periodComboBox);
		rdbtnMax.setSelected(true);

		panel.add(rdbtnMax);

		panel.add(rdbtnMin);

		panel.add(rdbtnAvg);

		panel.add(btnRefresh);

		panel.add(updateLabel);
		//		cpuChartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		changeChartStyle(cpuChart);
		changeChartStyle(cpuDetailChart);
		changeChartStyle(memoryChart);
		changeChartStyle(memoryDetailChart);
		changeChartStyle(diskChart);
		changeChartStyle(networkChart);

		add(lblCpu, "cell 0 1,alignx center");

		add(lblMemory, "cell 1 1,alignx center");

		add(lblDiskIo, "cell 2 1,alignx center");

		add(lblNetwork, "cell 3 1,alignx center");
		cpuChartPanel.setRangeZoomable(false);
		cpuChartPanel.setMouseZoomable(false);
		add(cpuChartPanel, "cell 0 2");
		memoryChartPanel.setRangeZoomable(false);
		memoryChartPanel.setMouseZoomable(false);
		add(memoryChartPanel, "cell 1 2");
		diskChartPanel.setMouseZoomable(false);
		diskChartPanel.setRangeZoomable(false);
		add(diskChartPanel, "cell 2 2");
		networkChartPanel.setMouseZoomable(false);
		networkChartPanel.setRangeZoomable(false);
		add(networkChartPanel, "cell 3 2");

		add(lblCpuDetail, "cell 0 3,alignx center");
		cpuDetailChartPanel.setMaximumDrawWidth(102400);
		cpuDetailChartPanel.setRangeZoomable(false);
		cpuDetailChartPanel.setMouseZoomable(false);

		add(cpuDetailChartPanel, "cell 0 4 4 1,grow");

		add(lblMemoryDetail, "cell 0 5");
		memoryDetailChartPanel.setMaximumDrawWidth(102400);
		memoryDetailChartPanel.setRangeZoomable(false);
		memoryDetailChartPanel.setMouseZoomable(false);

		add(memoryDetailChartPanel, "cell 0 6 4 1,grow");

		new Thread(this).start();
	}

	private void changeChartStyle(JFreeChart chart) {
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.getRenderer().setSeriesPaint(0, Color.blue);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		//		ValueAxis rangeAxis = chart.getXYPlot().getRangeAxis();
		//		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	}

	@Override
	public void run() {
		//		while (true) {

		if (isRunning) {
			return;
		}
		isRunning = true;
		updateLabel.setIcon(new ImageIcon(ServerInfoPanel.class.getResource("/com/titan/image/ajax-loader.gif")));
		updateLabel.setText("Updating charts");
		Command command = new Command();
		command.command = "getServerDiagnostics";
		Date fromDate = fromDateChooser.getDate();
		fromDate.setHours(Integer.parseInt(fromComboBox.getSelectedItem().toString().split(":")[0]));
		fromDate.setMinutes(Integer.parseInt(fromComboBox.getSelectedItem().toString().split(":")[1]));
		fromDate.setSeconds(0);
		Date toDate = toDateChooser.getDate();
		toDate.setHours(Integer.parseInt(toComboBox.getSelectedItem().toString().split(":")[0]));
		toDate.setMinutes(Integer.parseInt(toComboBox.getSelectedItem().toString().split(":")[1]));
		toDate.setSeconds(59);
		System.out.println(fromDate);
		System.out.println(toDate);
		command.parameters.add(fromDate);
		command.parameters.add(toDate);
		command.parameters.add(periodComboBox.getSelectedItem());
		if (rdbtnAvg.isSelected()) {
			command.parameters.add("avg");
		} else if (rdbtnMin.isSelected()) {
			command.parameters.add("min");
		} else {
			command.parameters.add("max");
		}
		command.parameters.add(periodComboBox.getSelectedItem());
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		List<ServerDiagnostics> list = (List<ServerDiagnostics>) r.map.get("result");

		//			int step = (int) Math.pow(10, String.valueOf(list.size()).length() - 2);
		//			if (step < 1) {
		//				step = 1;
		//			}
		//			step = 1;

		//			cpuSeries.clear();
		//			memorySeries.clear();
		//			diskSeries.clear();
		//			networkSeries.clear();
		TimeSeries cpuSeries = new TimeSeries("cpu");
		TimeSeries cpuDetailSeries = new TimeSeries("cpu");
		TimeSeries memorySeries = new TimeSeries("memory");
		TimeSeries memoryDetailSeries = new TimeSeries("memory");
		TimeSeries diskSeries = new TimeSeries("disk");
		TimeSeries networkSeries = new TimeSeries("network");
		System.out.println(list.get(list.size() - 1).getDate());
		System.out.println(list.get(0).getDate());
		for (int x = list.size() - 1; x >= 0; x--) {
			try {
				ServerDiagnostics s = list.get(x);
				Minute second = new Minute(s.getDate());
				cpuSeries.add(second, s.getCpu());
				cpuDetailSeries.add(second, s.getCpu());
				memorySeries.add(second, s.getMemory());
				memoryDetailSeries.add(second, s.getMemory());
				diskSeries.add(second, s.getDisk());
				networkSeries.add(second, s.getNetwork());
			} catch (Exception ex) {
			}
		}
		cpuDataset.removeAllSeries();
		cpuDataset.addSeries(cpuSeries);
		cpuDetailDataset.removeAllSeries();
		cpuDetailDataset.addSeries(cpuDetailSeries);
		memoryDataset.removeAllSeries();
		memoryDataset.addSeries(memorySeries);
		memoryDetailDataset.removeAllSeries();
		memoryDetailDataset.addSeries(memoryDetailSeries);
		diskDataset.removeAllSeries();
		diskDataset.addSeries(diskSeries);
		networkDataset.removeAllSeries();
		networkDataset.addSeries(networkSeries);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		isRunning = false;

		updateLabel.setIcon(null);
		updateLabel.setText(null);
		//		}
	}
}
