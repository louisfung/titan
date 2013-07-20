package com.titan.settingpanel.screenpermission;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;

import com.peterswing.CommonLib;
import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;
import com.titan.communication.CommunicateLib;
import com.titan.settingpanel.SettingPanel;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;
import com.titanserver.table.ScreenPermission;

public class AddScreenPermissionGroupDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField groupNameTextField;

	SortableTableModel tableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener columnListener;
	private JTable table;
	Frame frame;
	SettingPanel settingPanel;

	public AddScreenPermissionGroupDialog(Frame frame, SettingPanel settingPanel) {
		super(frame, "Add screen permission group", true);
		this.frame = frame;
		this.settingPanel = settingPanel;
		setBounds(100, 100, 669, 472);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][300px][grow]", "[][][grow]"));
		{
			JLabel lblGroupName = new JLabel("Group name");
			contentPanel.add(lblGroupName, "cell 0 0,alignx trailing");
		}
		{
			groupNameTextField = new JTextField();
			contentPanel.add(groupNameTextField, "cell 1 0,growx,aligny baseline");
			groupNameTextField.setColumns(10);
		}
		{
			JLabel lblPermissions = new JLabel("Permissions");
			contentPanel.add(lblPermissions, "cell 0 1");
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, "cell 0 2 3 1,grow");
			{
				table = new JTable();
				columnListener = new TableSorterColumnListener(table, tableModel);
				table.getTableHeader().setReorderingAllowed(false);
				table.setModel(tableModel);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				table.getTableHeader().addMouseListener(columnListener);
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				scrollPane.setViewportView(table);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save");
				okButton.setIcon(new ImageIcon(AddScreenPermissionGroupDialog.class.getResource("/com/titan/image/famfamfam/disk.png")));
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		refresh();
		CommonLib.centerDialog(this);
	}

	private void refresh() {
		try {
			GenericTableModel model = (GenericTableModel) tableModel.model;
			model.columnNames.clear();
			model.columnNames.add("");
			model.columnNames.add("Name");
			model.columnNames.add("Description");

			Vector<Object> col1 = new Vector<Object>();
			Vector<Object> col2 = new Vector<Object>();
			Vector<Object> col3 = new Vector<Object>();

			Command command = new Command();
			command.command = "get screen permissions";
			ReturnCommand r = CommunicateLib.send(command);
			List<ScreenPermission> allScreenPermissions = (List<ScreenPermission>) r.map.get("result");

			for (ScreenPermission screenPermission : allScreenPermissions) {
				col1.add(true);
				col2.add(screenPermission.getName());
				col3.add(screenPermission.getDescription());
			}

			model.values.clear();
			model.values.add(col1);
			model.values.add(col2);
			model.values.add(col3);

			tableModel.fireTableStructureChanged();
			tableModel.fireTableDataChanged();

			table.getColumnModel().getColumn(0).setPreferredWidth(80);
			table.getColumnModel().getColumn(1).setPreferredWidth(120);
			table.getColumnModel().getColumn(2).setPreferredWidth(400);

			DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
			rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JTextField.CENTER);

			table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
