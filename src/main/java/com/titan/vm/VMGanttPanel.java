package com.titan.vm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;

import com.titan.vm.gantt.GanttTableModel;
import com.toedter.calendar.JDateChooser;

public class VMGanttPanel extends JPanel {
	private JTable table;
	private JDateChooser fromDateChooser;
	private JDateChooser toDateChooser;
	private JComboBox comboBox;
	GanttTableModel model = new GanttTableModel();

	public VMGanttPanel() {
		setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);

		JTree tree = new JTree();
		scrollPane.setViewportView(tree);

		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1, BorderLayout.CENTER);

		table = new JTable(model);
		scrollPane_1.setViewportView(table);

		JPanel toolBar = new JPanel();
		FlowLayout flowLayout = (FlowLayout) toolBar.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(toolBar, BorderLayout.NORTH);

		fromDateChooser = new JDateChooser();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		fromDateChooser.setDate(calendar.getTime());
		fromDateChooser.setDateFormatString("yyyy/MM/dd");
		fromDateChooser.setPreferredSize(new Dimension(150, 25));
		toolBar.add(fromDateChooser);

		toDateChooser = new JDateChooser();
		fromDateChooser.setDate(Calendar.getInstance().getTime());
		toDateChooser.setPreferredSize(new Dimension(150, 25));
		toDateChooser.setDateFormatString("yyyy/MM/dd");
		toolBar.add(toDateChooser);

		comboBox = new JComboBox(new String[] { "mintue", "hour", "day", "month" });
		toolBar.add(comboBox);

		JRadioButton radioButton = new JRadioButton("max");
		radioButton.setSelected(true);
		toolBar.add(radioButton);

		JRadioButton radioButton_1 = new JRadioButton("min");
		toolBar.add(radioButton_1);

		JRadioButton radioButton_2 = new JRadioButton("avg");
		toolBar.add(radioButton_2);

		JButton refreshButton = new JButton("Refresh");
		toolBar.add(refreshButton);

		initTable();
	}

	private void initTable() {
		model.init(fromDateChooser.getDate(), toDateChooser.getDate());
	}

}
