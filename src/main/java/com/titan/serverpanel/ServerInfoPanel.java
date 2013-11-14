package com.titan.serverpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
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

	int chartWidth = 250;
	int chartHeight = 250;
	private final JComboBox<String> fromComboBox = new JComboBox<String>();
	private final JComboBox<String> toComboBox = new JComboBox<String>();
	private final JButton btnRefresh = new JButton("Refresh");

	public ServerInfoPanel() {
		setLayout(new MigLayout("", "[grow]", "[][grow][][]"));

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, "cell 0 0,grow");

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
			}
		});

		panel.add(btnRefresh);

		JPanel mainChartPanel = new JPanel();
		add(mainChartPanel, "cell 0 1,grow");
		mainChartPanel.setLayout(new MigLayout("", "[24%:n:24%][24%:n:24%][24%:n:24%][grow]", "[250px]"));

		mainChartPanel.add(cpuChartPanel, "cell 0 0,alignx center,aligny top");
		//		cpuChartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		changeChartStyle(cpuChart);

		changeChartStyle(memoryChart);
		//		memoryChartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		mainChartPanel.add(memoryChartPanel, "cell 1 0,alignx center,aligny top");

		changeChartStyle(diskChart);
		//		diskChartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		mainChartPanel.add(diskChartPanel, "cell 2 0,alignx center,aligny top");

		changeChartStyle(networkChart);
		//		networkChartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		mainChartPanel.add(networkChartPanel, "cell 3 0,alignx center,aligny top");

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
		while (true) {
			Command command = new Command();
			command.command = "getServerDiagnostics";
			Date fromDate = toDateChooser.getDate();
			fromDate.setHours(Integer.parseInt(fromComboBox.getSelectedItem().toString().split(":")[0]));
			fromDate.setMinutes(Integer.parseInt(fromComboBox.getSelectedItem().toString().split(":")[1]));
			fromDate.setSeconds(0);
			Date toDate = toDateChooser.getDate();
			toDate.setHours(Integer.parseInt(toComboBox.getSelectedItem().toString().split(":")[0]));
			toDate.setMinutes(Integer.parseInt(toComboBox.getSelectedItem().toString().split(":")[1]));
			toDate.setSeconds(59);
			command.parameters.add(fromDate);
			command.parameters.add(toDate);
			ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
			List<ServerDiagnostics> list = (List<ServerDiagnostics>) r.map.get("result");

			int step = (int) Math.pow(10, String.valueOf(list.size()).length() - 2);
			if (step < 1) {
				step = 1;
			}
			step = 1;

			//			cpuSeries.clear();
			//			memorySeries.clear();
			//			diskSeries.clear();
			//			networkSeries.clear();
			TimeSeries cpuSeries = new TimeSeries("cpu");
			TimeSeries memorySeries = new TimeSeries("memory");
			TimeSeries diskSeries = new TimeSeries("disk");
			TimeSeries networkSeries = new TimeSeries("network");
			for (int x = list.size() - 1; x >= 0; x -= step) {
				try {
					ServerDiagnostics s = list.get(x);
					Second second = new Second(s.getDate());
					cpuSeries.add(second, s.getCpu());
					memorySeries.add(second, s.getMemory());
					diskSeries.add(second, s.getDisk());
					networkSeries.add(second, s.getNetwork());
				} catch (Exception ex) {
				}
			}
			cpuDataset.removeAllSeries();
			cpuDataset.addSeries(cpuSeries);
			memoryDataset.removeAllSeries();
			memoryDataset.addSeries(memorySeries);
			diskDataset.removeAllSeries();
			diskDataset.addSeries(diskSeries);
			networkDataset.removeAllSeries();
			networkDataset.addSeries(networkSeries);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
