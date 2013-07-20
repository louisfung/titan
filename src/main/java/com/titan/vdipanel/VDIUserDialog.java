package com.titan.vdipanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import com.peterswing.CommonLib;
import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;

public class VDIUserDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTable table;

	GenericTableModel instanceTableModel = new GenericTableModel();
	SortableTableModel instanceSortableTableModel = new SortableTableModel(instanceTableModel);
	TableSorterColumnListener instanceTableSorterColumnListener;
	Vector<Object> col1 = new Vector<Object>();
	Vector<Object> col2 = new Vector<Object>();

	public VDIUserDialog(final Frame frame, String instanceName) {
		super(frame, "VDI user : ", true);
		setBounds(100, 100, 618, 437);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.getTableHeader().setReorderingAllowed(false);
				instanceTableSorterColumnListener = new TableSorterColumnListener(table, instanceSortableTableModel);
				table.setModel(instanceSortableTableModel);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.getTableHeader().addMouseListener(instanceTableSorterColumnListener);
				scrollPane.setViewportView(table);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnAddUser = new JButton("Add user");
				btnAddUser.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						AddVDIUserDialog dialog = new AddVDIUserDialog(frame);
						CommonLib.centerDialog(dialog);
						dialog.setVisible(true);
						if (dialog.ok) {
							col1.add(dialog.usernameTextField.getText());
							col2.add(dialog.comboBox.getSelectedItem());
							instanceSortableTableModel.fireTableDataChanged();
							instanceSortableTableModel.sortByColumn(instanceTableSorterColumnListener.sortCol, instanceTableSorterColumnListener.isSortAsc);

						}
					}
				});
				buttonPane.add(btnAddUser);
			}
			{
				JButton btnDeleteUser = new JButton("Delete user");
				buttonPane.add(btnDeleteUser);
			}
			{
				JButton saveButton = new JButton("Save");
				saveButton.setIcon(new ImageIcon(VDIUserDialog.class.getResource("/com/titan/image/famfamfam/disk.png")));
				saveButton.setActionCommand("OK");
				buttonPane.add(saveButton);
				getRootPane().setDefaultButton(saveButton);
			}
		}

		initTable();
	}

	private void initTable() {
		instanceTableModel.columnNames.clear();
		instanceTableModel.columnNames.add("Username");
		instanceTableModel.columnNames.add("Group");

		instanceTableModel.values.clear();
		instanceTableModel.values.add(col1);
		instanceTableModel.values.add(col2);

		col1.add("admin");
		col2.add("Administrator");

		instanceSortableTableModel.fireTableStructureChanged();
		instanceSortableTableModel.fireTableDataChanged();
		instanceSortableTableModel.sortByColumn(instanceTableSorterColumnListener.sortCol, instanceTableSorterColumnListener.isSortAsc);

		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
	}

}
