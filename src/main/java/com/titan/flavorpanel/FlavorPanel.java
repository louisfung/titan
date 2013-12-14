package com.titan.flavorpanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.peterswing.CommonLib;
import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jprogressbardialog.JProgressBarDialog;
import com.peterswing.advancedswing.jtable.ComputerUnit;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;
import com.titan.BooleanTableCellRenderer;
import com.titan.MainPanel;
import com.titan.SimpleTableDialog;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;

public class FlavorPanel extends JPanel implements Runnable, MainPanel {
	private JTable flavorTable;
	final JFrame frame;
	JProgressBarDialog d;
	GenericTableModel flavorTableModel = new GenericTableModel();
	SortableTableModel sortableTableModel = new SortableTableModel(flavorTableModel);
	TableSorterColumnListener tableSorterColumnListener;

	public FlavorPanel(final JFrame frame) {
		this.frame = frame;
		setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);

		flavorTable = new JTable();
		flavorTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String flavorId = (String) sortableTableModel.getValueAt(flavorTable.getSelectedRow(), sortableTableModel.getColumnIndex("Id"));
					SimpleTableDialog dialog = new SimpleTableDialog(frame, flavorId, true);
					dialog.tableModel.editables.put(1, true);

					dialog.tableModel.columnNames.clear();
					dialog.tableModel.columnNames.add("Name");
					dialog.tableModel.columnNames.add("Value");

					Vector<Object> col1 = new Vector<Object>();
					Vector<Object> col2 = new Vector<Object>();

					dialog.tableModel.values.clear();

					for (int x = 0; x < sortableTableModel.getColumnCount(); x++) {
						col1.add(sortableTableModel.getColumnName(x));
						col2.add(sortableTableModel.getValueAt(flavorTable.getSelectedRow(), x));
					}

					dialog.tableModel.values.add(col1);
					dialog.tableModel.values.add(col2);

					dialog.sortableableModel.fireTableStructureChanged();
					dialog.sortableableModel.fireTableDataChanged();

					CommonLib.centerDialog(dialog);
					dialog.setVisible(true);
				}
			}
		});
		tableSorterColumnListener = new TableSorterColumnListener(flavorTable, sortableTableModel);
		flavorTable.getTableHeader().setReorderingAllowed(false);
		flavorTable.setModel(sortableTableModel);
		flavorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		flavorTable.getTableHeader().addMouseListener(tableSorterColumnListener);
		flavorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(flavorTable);

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		add(panel_2, BorderLayout.SOUTH);

		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateFlavorDialog dialog = new CreateFlavorDialog(FlavorPanel.this.frame);
				dialog.setVisible(true);
				refresh();
			}
		});
		panel_2.add(btnCreate);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (flavorTable.getSelectedRowCount() == 1) {
					String flavorId = (String) sortableTableModel.getValueAt(flavorTable.getSelectedRow(), flavorTableModel.getColumnIndex("Id"));
					String flavorName = (String) sortableTableModel.getValueAt(flavorTable.getSelectedRow(), flavorTableModel.getColumnIndex("Name"));
					int temp = JOptionPane.showConfirmDialog(FlavorPanel.this.frame, "Confirm to delete flavor " + flavorName + " ?", "Warning", JOptionPane.YES_NO_OPTION);
					if (temp == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: nova delete-flavor";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$flavorId", flavorId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						refresh();
					}
				}
			}
		});
		panel_2.add(btnDelete);

		refresh();
	}

	public void refresh() {
		d = new JProgressBarDialog(frame, true);
		d.thread = new Thread(this);
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.setVisible(true);
	}

	public void run() {
		d.jProgressBar.setString("nova endpoints");
		Command command = new Command();
		command.command = "from titan: nova flavor-list";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		HttpResult httpResult = (HttpResult) r.map.get("result");
		JSONArray flavors = JSONObject.fromObject(httpResult.content).getJSONArray("flavors");
		flavorTableModel.columnNames.clear();
		flavorTableModel.columnNames.add("Id");
		flavorTableModel.columnNames.add("Name");
		flavorTableModel.columnNames.add("Disable");
		flavorTableModel.columnNames.add("Ephemeral");
		flavorTableModel.columnNames.add("Disk");
		flavorTableModel.columnNames.add("Public");
		flavorTableModel.columnNames.add("Ram");
		flavorTableModel.columnNames.add("Factor");
		flavorTableModel.columnNames.add("Swap");
		flavorTableModel.columnNames.add("VCpus");
		flavorTableModel.columnNames.add("Links");

		flavorTableModel.columnTypes.clear();
		flavorTableModel.columnTypes.put(3, Integer.class);
		flavorTableModel.columnTypes.put(4, ComputerUnit.class);
		flavorTableModel.columnTypes.put(6, ComputerUnit.class);
		flavorTableModel.columnTypes.put(8, Integer.class);
		flavorTableModel.columnTypes.put(9, Integer.class);

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		Vector<Object> col3 = new Vector<Object>();
		Vector<Object> col4 = new Vector<Object>();
		Vector<Object> col5 = new Vector<Object>();
		Vector<Object> col6 = new Vector<Object>();
		Vector<Object> col7 = new Vector<Object>();
		Vector<Object> col8 = new Vector<Object>();
		Vector<Object> col9 = new Vector<Object>();
		Vector<Object> col10 = new Vector<Object>();
		Vector<Object> col11 = new Vector<Object>();

		for (int x = 0; x < flavors.size(); x++) {
			JSONObject obj = flavors.getJSONObject(x);
			col1.add(obj.getString("id"));
			col2.add(obj.getString("name"));
			col3.add(obj.getString("OS-FLV-DISABLED:disabled"));
			col4.add(obj.getString("OS-FLV-EXT-DATA:ephemeral"));
			col5.add(obj.getString("disk") + " GB");
			col6.add(obj.getString("os-flavor-access:is_public"));
			col7.add(obj.getString("ram") + " MB");
			col8.add(obj.getString("rxtx_factor"));
			col9.add(obj.getString("swap"));
			col10.add(obj.getString("vcpus"));
			String linksStr = "";
			for (int z = 0; z < obj.getJSONArray("links").size(); z++) {
				JSONObject link = obj.getJSONArray("links").getJSONObject(z);
				linksStr += "rel=" + link.getString("rel") + ", ";
				linksStr += "href=" + link.getString("href") + ", ";
			}
			col11.add(linksStr);
		}
		flavorTableModel.values.clear();
		flavorTableModel.values.add(col1);
		flavorTableModel.values.add(col2);
		flavorTableModel.values.add(col3);
		flavorTableModel.values.add(col4);
		flavorTableModel.values.add(col5);
		flavorTableModel.values.add(col6);
		flavorTableModel.values.add(col7);
		flavorTableModel.values.add(col8);
		flavorTableModel.values.add(col9);
		flavorTableModel.values.add(col10);
		flavorTableModel.values.add(col11);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JTextField.LEFT);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);

		sortableTableModel.fireTableStructureChanged();
		sortableTableModel.fireTableDataChanged();
		sortableTableModel.sortByColumn(tableSorterColumnListener.sortCol, tableSorterColumnListener.isSortAsc);

		flavorTable.getColumnModel().getColumn(0).setPreferredWidth(80);
		flavorTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		flavorTable.getColumnModel().getColumn(2).setPreferredWidth(60);
		flavorTable.getColumnModel().getColumn(3).setPreferredWidth(80);
		flavorTable.getColumnModel().getColumn(4).setPreferredWidth(50);
		flavorTable.getColumnModel().getColumn(5).setPreferredWidth(50);
		flavorTable.getColumnModel().getColumn(6).setPreferredWidth(70);
		flavorTable.getColumnModel().getColumn(7).setPreferredWidth(50);
		flavorTable.getColumnModel().getColumn(8).setPreferredWidth(50);
		flavorTable.getColumnModel().getColumn(9).setPreferredWidth(50);
		flavorTable.getColumnModel().getColumn(10).setPreferredWidth(1200);

		flavorTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		flavorTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		flavorTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		flavorTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		flavorTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		flavorTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		flavorTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		flavorTable.getColumnModel().getColumn(7).setCellRenderer(new BooleanTableCellRenderer());
		flavorTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
		flavorTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
	}

}
