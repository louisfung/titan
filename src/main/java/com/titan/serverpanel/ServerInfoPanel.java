package com.titan.serverpanel;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import info.monitorenter.gui.chart.Chart2D;
import com.toedter.calendar.JDateChooser;
import javax.swing.JLabel;
import java.awt.FlowLayout;

public class ServerInfoPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public ServerInfoPanel() {
		setLayout(new MigLayout("", "[200px][200px][200px][200px]", "[][][grow]"));
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, "cell 0 0 2 1,grow");
		
		JLabel lblFrom = new JLabel("From");
		panel.add(lblFrom);
		
		JDateChooser dateChooser = new JDateChooser();
		panel.add(dateChooser);
		
		JLabel lblTo = new JLabel("To");
		panel.add(lblTo);
		
		JDateChooser dateChooser_1 = new JDateChooser();
		panel.add(dateChooser_1);
		
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
