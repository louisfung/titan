package com.titan.serverpanel;

import info.monitorenter.gui.chart.Chart2D;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.toedter.calendar.JDateChooser;

public class ServerInfoPanel extends JPanel {
	private JDateChooser fromDateChooser;
	private JDateChooser toDateChooser;

	public ServerInfoPanel() {
		setLayout(new MigLayout("", "[200px][200px][200px][200px]", "[][][grow]"));

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, "cell 0 0 2 1,grow");

		JLabel lblFrom = new JLabel("From");
		panel.add(lblFrom);

		fromDateChooser = new JDateChooser();
		fromDateChooser.setDateFormatString("yyyy/MM/dd");
		fromDateChooser.setPreferredSize(new Dimension(150, 25));
		fromDateChooser.setDate(new Date());
		panel.add(fromDateChooser);

		JLabel lblTo = new JLabel("To");
		panel.add(lblTo);

		toDateChooser = new JDateChooser();
		toDateChooser.setDateFormatString("yyyy/MM/dd");
		toDateChooser.setPreferredSize(new Dimension(150, 25));
		toDateChooser.setDate(new Date());
		panel.add(toDateChooser);

		JLabel lblCpu = new JLabel("CPU");
		add(lblCpu, "cell 0 1");

		JLabel lblMemory = new JLabel("Memory");
		add(lblMemory, "cell 1 1");

		JLabel lblDiskIo = new JLabel("Disk I/O");
		add(lblDiskIo, "cell 2 1");

		JLabel lblNetwork = new JLabel("Network");
		add(lblNetwork, "cell 3 1");

		Chart2D chart2D = new Chart2D();
		add(chart2D, "cell 0 2,grow");

		Chart2D chart2D_1 = new Chart2D();
		add(chart2D_1, "cell 1 2,grow");

		Chart2D chart2D_2 = new Chart2D();
		add(chart2D_2, "cell 2 2,grow");

		Chart2D chart2D_3 = new Chart2D();
		add(chart2D_3, "cell 3 2,grow");

	}

}
