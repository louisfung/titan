package com.c2.pandora.instancepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.c2.pandora.PandoraCommonLib;
import com.c2.pandora.communication.CommunicateLib;
import com.c2.pandora.imagepanel.DownloadImageDialog;
import com.c2.pandoraserver.Command;
import com.c2.pandoraserver.ReturnCommand;
import com.peterswing.CommonLib;
import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jtable.ComputerUnit;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;

public class LaunchInstanceDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField instanceNameTextField;

	GenericTableModel flavorTableModel = new GenericTableModel();
	SortableTableModel flavorSortableTableModel = new SortableTableModel(flavorTableModel);
	TableSorterColumnListener flavorTableSorterColumnListener;

	GenericTableModel imageTableModel = new GenericTableModel();
	SortableTableModel imageSortableTableModel = new SortableTableModel(imageTableModel);
	TableSorterColumnListener imageTableSorterColumnListener;

	private JTable flavorTable;
	private JTable imageTable;
	private JSplitPane splitPane;
	private JLabel errorLabel1;
	private JSpinner spinner;

	public LaunchInstanceDialog(final Frame frame) {
		super(frame, "Launch instance", true);
		setBounds(100, 100, 905, 456);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("insets 5 5 0 5", "[][grow]", "[][][][grow]"));
		{
			JLabel lblInstanceName = new JLabel("Instance Name");
			contentPanel.add(lblInstanceName, "cell 0 0,alignx trailing");
		}
		{
			instanceNameTextField = new JTextField();
			instanceNameTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					errorLabel1.setVisible(false);
				}
			});
			contentPanel.add(instanceNameTextField, "cell 1 0,growx");
			instanceNameTextField.setColumns(10);
		}
		{
			JLabel lblCount = new JLabel("Count");
			contentPanel.add(lblCount, "cell 0 1");
		}
		{
			spinner = new JSpinner();
			spinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));
			spinner.setValue(1);
			spinner.setPreferredSize(new Dimension(150, 25));
			contentPanel.add(spinner, "cell 1 1");
		}
		{
			errorLabel1 = new JLabel("");
			errorLabel1.setForeground(Color.RED);
			contentPanel.add(errorLabel1, "cell 1 2");
		}
		{
			splitPane = new JSplitPane();
			contentPanel.add(splitPane, "cell 0 3 2 1,grow");
			{
				JPanel leftPanel = new JPanel();
				splitPane.setLeftComponent(leftPanel);
				leftPanel.setLayout(new MigLayout("insets 0", "[grow]", "[][grow][]"));
				{
					JLabel lblFlavor = new JLabel("Flavor");
					leftPanel.add(lblFlavor, "cell 0 0");
				}
				{
					JScrollPane scrollPane = new JScrollPane();
					leftPanel.add(scrollPane, "cell 0 1,grow");
					{
						flavorTable = new JTable();
						flavorTableSorterColumnListener = new TableSorterColumnListener(flavorTable, flavorSortableTableModel);
						flavorTable.getTableHeader().setReorderingAllowed(false);
						flavorTable.setModel(flavorSortableTableModel);
						flavorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						flavorTable.getTableHeader().addMouseListener(flavorTableSorterColumnListener);
						flavorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						scrollPane.setViewportView(flavorTable);
					}
				}
			}
			{
				JPanel rightPanel = new JPanel();
				splitPane.setRightComponent(rightPanel);
				rightPanel.setLayout(new MigLayout("insets 0", "[grow]", "[][grow][]"));
				{
					JLabel lblImageSnapshot = new JLabel("Image / snapshot");
					rightPanel.add(lblImageSnapshot, "cell 0 0");
				}
				{
					JScrollPane scrollPane = new JScrollPane();
					rightPanel.add(scrollPane, "cell 0 1,grow");
					{
						imageTable = new JTable();
						imageTableSorterColumnListener = new TableSorterColumnListener(imageTable, imageSortableTableModel);
						imageTable.getTableHeader().setReorderingAllowed(false);
						imageTable.setModel(imageSortableTableModel);
						imageTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						imageTable.getTableHeader().addMouseListener(imageTableSorterColumnListener);
						imageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						scrollPane.setViewportView(imageTable);
					}
				}
			}
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			{
				JButton btnLaunch = new JButton("Launch");
				btnLaunch.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						boolean error = false;
						if (instanceNameTextField.getText().trim().equals("")) {
							errorLabel1.setText("Instance name cannot be empty!");
							errorLabel1.setVisible(true);
							error = true;
						}
						if (flavorTable.getSelectedRowCount() == 0 || imageTable.getSelectedRowCount() == 0) {
							JOptionPane.showMessageDialog(frame, "Please select one flavor and one image !", "Error", JOptionPane.ERROR_MESSAGE);
							error = true;
						}
						if (error) {
							return;
						} else {
							Command command = new Command();
							command.command = "from pandora: nova boot";
							HashMap<String, String> parameters = new HashMap<String, String>();
							parameters.put("$flavorRef", (String) flavorTable.getValueAt(flavorTable.getSelectedRow(), 0));
							parameters.put("$name", instanceNameTextField.getText());
							parameters.put("$min_count", String.valueOf(spinner.getValue()));
							parameters.put("$imageRef", (String) imageTable.getValueAt(imageTable.getSelectedRow(), 0));
							command.parameters.add(parameters);
							ReturnCommand r = CommunicateLib.send(PandoraCommonLib.getCurrentServerIP(), command);
							System.out.println(r.map.get("result"));
							if (r.map.get("result").toString().contains("overLimit")) {
								JSONObject result = JSONObject.fromObject(r.map.get("result")).getJSONObject("overLimit");
								JOptionPane.showMessageDialog(frame, result.getString("message"), "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							setVisible(false);
						}
					}
				});
				{
					JButton btnDownloadImage = new JButton("Download image");
					btnDownloadImage.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							DownloadImageDialog dialog = new DownloadImageDialog(frame);
							CommonLib.centerDialog(dialog);
							dialog.setVisible(true);
						}
					});
					panel.add(btnDownloadImage);
				}
				panel.add(btnLaunch);
			}
		}

		initFlavorTable();
		initImageTable();

		splitPane.setDividerLocation(getWidth() / 2);
		CommonLib.centerDialog(this);
	}

	private void initFlavorTable() {
		Command command = new Command();
		command.command = "from pandora: nova flavor-list";
		ReturnCommand r = CommunicateLib.send(PandoraCommonLib.getCurrentServerIP(), command);
		JSONArray flavors = JSONObject.fromObject(r.map.get("result")).getJSONArray("flavors");
		flavorTableModel.columnNames.clear();
		flavorTableModel.columnNames.add("Id");
		flavorTableModel.columnNames.add("Name");
		flavorTableModel.columnNames.add("VCpus");
		flavorTableModel.columnNames.add("Ram");
		flavorTableModel.columnNames.add("Disk");

		flavorTableModel.columnTypes.clear();
		flavorTableModel.columnTypes.put(3, ComputerUnit.class);
		flavorTableModel.columnTypes.put(4, ComputerUnit.class);

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		Vector<Object> col3 = new Vector<Object>();
		Vector<Object> col4 = new Vector<Object>();
		Vector<Object> col5 = new Vector<Object>();

		for (int x = 0; x < flavors.size(); x++) {
			JSONObject obj = flavors.getJSONObject(x);
			col1.add(obj.getString("id"));
			col2.add(obj.getString("name"));
			col3.add(obj.getString("vcpus"));
			col4.add(obj.getString("ram") + " MB");
			col5.add(obj.getString("disk") + " GB");
		}
		flavorTableModel.values.clear();
		flavorTableModel.values.add(col1);
		flavorTableModel.values.add(col2);
		flavorTableModel.values.add(col3);
		flavorTableModel.values.add(col4);
		flavorTableModel.values.add(col5);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JTextField.LEFT);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);

		flavorSortableTableModel.fireTableStructureChanged();
		flavorSortableTableModel.fireTableDataChanged();
		flavorSortableTableModel.sortByColumn(3, flavorTableSorterColumnListener.isSortAsc);

		flavorTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		flavorTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
		flavorTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

		flavorTable.getColumnModel().getColumn(0).setPreferredWidth(10);
	}

	private void initImageTable() {
		Command command = new Command();
		command.command = "from pandora: glance image-list";
		ReturnCommand r = CommunicateLib.send(PandoraCommonLib.getCurrentServerIP(), command);
		JSONArray images = JSONObject.fromObject(r.map.get("result")).getJSONArray("images");
		imageTableModel.columnNames.clear();
		imageTableModel.columnNames.add("Id");
		imageTableModel.columnNames.add("Name");
		imageTableModel.columnNames.add("Size");
		imageTableModel.columnNames.add("Type");

		imageTableModel.columnTypes.clear();
		imageTableModel.columnTypes.put(2, ComputerUnit.class);

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		Vector<Object> col3 = new Vector<Object>();
		Vector<Object> col4 = new Vector<Object>();

		for (int x = 0; x < images.size(); x++) {
			JSONObject obj = images.getJSONObject(x);
			col1.add(obj.getString("id"));
			col2.add(obj.getString("name"));
			col3.add(CommonLib.convertFilesize(Long.parseLong(obj.getString("size"))));
			col4.add(obj.getString("disk_format"));
		}
		imageTableModel.values.clear();
		imageTableModel.values.add(col1);
		imageTableModel.values.add(col2);
		imageTableModel.values.add(col3);
		imageTableModel.values.add(col4);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JTextField.LEFT);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);

		imageSortableTableModel.fireTableStructureChanged();
		imageSortableTableModel.fireTableDataChanged();
		imageSortableTableModel.sortByColumn(1, imageTableSorterColumnListener.isSortAsc);

		imageTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
		imageTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

		imageTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		imageTable.getColumnModel().getColumn(1).setPreferredWidth(250);
	}

}
