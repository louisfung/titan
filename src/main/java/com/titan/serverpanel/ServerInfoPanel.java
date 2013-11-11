package com.titan.serverpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;
import com.titanserver.table.ServerDiagnostics;
import com.toedter.calendar.JDateChooser;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServerInfoPanel extends JPanel implements Runnable {
	private JDateChooser fromDateChooser;
	private JDateChooser toDateChooser;
	//	ITrace2D cpuTrace = new Trace2DSimple();
	//	ITrace2D memoryTrace = new Trace2DSimple();

	TimeSeries cpuSeries = new TimeSeries("cpu");
	TimeSeriesCollection cpuDataset = new TimeSeriesCollection(cpuSeries);
	JFreeChart cpuChart = ChartFactory.createTimeSeriesChart("", "", "%", cpuDataset, false, false, false);
	ChartPanel cpuChartPanel = new ChartPanel(cpuChart);

	TimeSeries memorySeries = new TimeSeries("memory");
	TimeSeriesCollection memoryDataset = new TimeSeriesCollection(memorySeries);
	JFreeChart memoryChart = ChartFactory.createTimeSeriesChart("", "", "%", memoryDataset, false, false, false);
	ChartPanel memoryChartPanel = new ChartPanel(memoryChart);

	TimeSeries diskSeries = new TimeSeries("disk");
	TimeSeriesCollection diskDataset = new TimeSeriesCollection(diskSeries);
	JFreeChart diskChart = ChartFactory.createTimeSeriesChart("", "", "io/s", diskDataset, false, false, false);
	ChartPanel diskChartPanel = new ChartPanel(diskChart);

	TimeSeries networkSeries = new TimeSeries("network");
	TimeSeriesCollection networkDataset = new TimeSeriesCollection(networkSeries);
	JFreeChart networkChart = ChartFactory.createTimeSeriesChart("", "", "io/s", networkDataset, false, false, false);
	ChartPanel networkChartPanel = new ChartPanel(networkChart);

	int chartWidth = 250;
	int chartHeight = 250;
	private final JComboBox<String> fromComboBox = new JComboBox<String>();
	private final JComboBox<String> toComboBox = new JComboBox<String>();
	private final JButton btnRefresh = new JButton("Refresh");

	public ServerInfoPanel() {
		setLayout(new MigLayout("", "[200px,grow][200px][200px][200px]", "[][grow][][]"));

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, "cell 0 0 3 1,grow");

		JLabel lblFrom = new JLabel("From");
		panel.add(lblFrom);

		fromDateChooser = new JDateChooser();
		fromDateChooser.setDateFormatString("yyyy/MM/dd");
		fromDateChooser.setPreferredSize(new Dimension(150, 25));
		fromDateChooser.setDate(new Date());
		panel.add(fromDateChooser);

		for (int x = 0; x < 24; x++) {
			fromComboBox.addItem(String.format("%2d", x) + ":00");
		}
		panel.add(fromComboBox);

		JLabel lblTo = new JLabel("To");
		panel.add(lblTo);

		toDateChooser = new JDateChooser();
		toDateChooser.setDateFormatString("yyyy/MM/dd");
		toDateChooser.setPreferredSize(new Dimension(150, 25));
		toDateChooser.setDate(new Date());
		panel.add(toDateChooser);

		for (int x = 0; x < 24; x++) {
			toComboBox.addItem(String.format("%2d", x) + ":59");
		}
		toComboBox.setSelectedItem("23:59");
		panel.add(toComboBox);
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		panel.add(btnRefresh);

		JPanel panel_1 = new JPanel();
		add(panel_1, "cell 0 1 4 1,grow");

		panel_1.add(cpuChartPanel);
		cpuChartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		changeChartStyle(cpuChart);

		changeChartStyle(memoryChart);
		memoryChartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		panel_1.add(memoryChartPanel);

		changeChartStyle(diskChart);
		diskChartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		panel_1.add(diskChartPanel);

		changeChartStyle(networkChart);
		networkChartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		panel_1.add(networkChartPanel);

		new Thread(this).start();
	}

	private void changeChartStyle(JFreeChart chart) {
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.getRenderer().setSeriesPaint(0, Color.blue);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		//		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		//		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	}

	@Override
	public void run() {
		while (true) {
			Command command = new Command();
			command.command = "getServerDiagnostics";
			Date fromDate = toDateChooser.getDate();
			fromDate.setHours(0);
			fromDate.setMinutes(0);
			fromDate.setSeconds(0);
			Date toDate = toDateChooser.getDate();
			toDate.setHours(23);
			toDate.setMinutes(59);
			toDate.setSeconds(59);
			command.parameters.add(fromDate);
			command.parameters.add(toDate);
			ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
			List<ServerDiagnostics> list = (List<ServerDiagnostics>) r.map.get("result");
			int step = (int) Math.pow(10, String.valueOf(list.size()).length() - 2);
			if (step < 1) {
				step = 1;
			}

			cpuSeries.clear();
			memorySeries.clear();
			diskSeries.clear();
			networkSeries.clear();

			for (int x = list.size() - 1; x >= 0; x -= step) {
				ServerDiagnostics s = list.get(x);
				cpuSeries.add(new Minute(s.getDate().getMinutes(), new Hour(s.getDate())), s.getCpu());
				memorySeries.add(new Minute(s.getDate().getMinutes(), new Hour(s.getDate())), s.getMemory());
				diskSeries.add(new Minute(s.getDate().getMinutes(), new Hour(s.getDate())), s.getDisk());
				networkSeries.add(new Minute(s.getDate().getMinutes(), new Hour(s.getDate())), s.getNetwork());
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
