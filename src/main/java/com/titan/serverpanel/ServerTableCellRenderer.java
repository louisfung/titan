package com.c2.pandora.serverpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.c2.pandora.thread.PandoraServerUpdateThread;
import com.c2.pandora.thread.Status;

public class ServerTableCellRenderer extends JPanel implements TableCellRenderer {
	JLabel jLabel = new JLabel();
	JFreeChart chart;
	ChartPanel chartPanel;
	XYSeriesCollection dataset = new XYSeriesCollection();
	Color rowSelectedColor = new Color(0xf4f4f4);
	JPanel cpuPanel = new JPanel(new BorderLayout());
	JProgressBar cpuProgressBar = new JProgressBar(0, 100);

	public ServerTableCellRenderer() {
		this.setLayout(new BorderLayout());
		this.add(jLabel, BorderLayout.CENTER);
		jLabel.setOpaque(true);
		jLabel.setHorizontalAlignment(JLabel.CENTER);

		chart = ChartFactory.createXYLineChart(null, // chart title
				null, // x axis label
				null, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				false, // tooltips
				false // urls
				);
		chart.removeLegend();

		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		//		plot.getRangeAxis().setVisible(false);
		plot.getDomainAxis().setVisible(false);
		plot.setOutlineVisible(false);

		XYSplineRenderer renderer = new XYSplineRenderer();
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesPaint(0, Color.black);
		plot.setRenderer(renderer);

		chartPanel = new ChartPanel(chart);
		//		cpuPanel.add(chartPanel, BorderLayout.CENTER);
		cpuPanel.add(cpuProgressBar, BorderLayout.CENTER);
		cpuProgressBar.setStringPainted(true);
		//		cpuProgressBar.setForeground(Color.black);
		UIManager.put("ProgressBar.selectionForeground", Color.black);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		try {
			if (column == 0) {
				if ((Boolean) value) {
					jLabel.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/c2/pandora/image/famfamfam/tick.png")));
					jLabel.setText("online");
				} else {
					jLabel.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/c2/pandora/image/famfamfam/cross.png")));
					jLabel.setText("online");
				}
			} else if (column == 3) {
				String serverID = (String) table.getValueAt(row, 1);
				if (PandoraServerUpdateThread.status.size() != 0 && PandoraServerUpdateThread.status.get(serverID) != null) {
					final XYSeries series1 = new XYSeries("data");
					Object objs[] = PandoraServerUpdateThread.status.get(serverID).toArray();

					for (int x = objs.length - 1; x >= 0; x--) {
						Status status = (Status) objs[x];
						series1.add(x, Double.parseDouble(status.cpu_combined));
						if ((new Date().getTime() - status.date.getTime()) / 1000 > 10) {
							break;
						}
					}
					dataset.removeAllSeries();
					dataset.addSeries(series1);
				}

				if (isSelected) {
					chart.setBackgroundPaint(table.getSelectionBackground());
				} else {
					if (row % 2 == 0) {
						chart.setBackgroundPaint(Color.white);
					} else {
						chart.setBackgroundPaint(rowSelectedColor);
					}
				}
				cpuProgressBar.setValue((int) Float.parseFloat((String) value));
				cpuProgressBar.setString((String) value + "%");
				return cpuPanel;
			} else {
				jLabel.setIcon(null);
				if (value == null) {
					jLabel.setText("");
				} else {
					jLabel.setText(value.toString());
				}
			}
			if (isSelected) {
				jLabel.setBackground(table.getSelectionBackground());
			} else {
				if (row % 2 == 0) {
					jLabel.setBackground(Color.white);
				} else {
					jLabel.setBackground(rowSelectedColor);
				}
			}
			return this;
		} catch (Exception ex) {
			return new JLabel();
		}
	}
}