package com.c2.pandora;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jtable.SortableTableModel;

public class SimpleTableDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	public JTable table;
	public GenericTableModel tableModel = new GenericTableModel();
	public SortableTableModel sortableableModel = new SortableTableModel(tableModel);

	public SimpleTableDialog(JFrame frame, String title, boolean modal) {
		super(frame, title, modal);
		setBounds(100, 100, 800, 516);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setModel(sortableableModel);
				table.getTableHeader().setReorderingAllowed(false);
				scrollPane.setViewportView(table);
			}
		}
	}

}
