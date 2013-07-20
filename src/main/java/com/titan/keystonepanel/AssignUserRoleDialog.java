package com.titan.keystonepanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.peterswing.CommonLib;
import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;

public class AssignUserRoleDialog extends JDialog {

	SortableTableModel roleTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener roleTableSorterColumnListener;
	Frame frame;
	String userId;
	String username;
	String defaultTenantId;
	Hashtable<String, String> allRoles;
	Hashtable<String, String> allTenants;
	private JComboBox tenantComboBox;
	private JComboBox roleComboBox;
	String defaultTenantName;

	public AssignUserRoleDialog(Frame frame, String userId, String username, String defaultTenantName, Hashtable<String, String> allRoles, Hashtable<String, String> allTenants) {
		super(frame, true);
		this.frame = frame;
		this.userId = userId;
		this.username = username;
		this.defaultTenantName = defaultTenantName;
		this.allRoles = allRoles;
		this.allTenants = allTenants;
		setTitle("Assigne role for " + username);
		setBounds(100, 100, 385, 160);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton addButton = new JButton("Add");
				addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				addButton.setActionCommand("OK");
				buttonPane.add(addButton);
				getRootPane().setDefaultButton(addButton);
			}
		}

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][grow]", "[][]"));

		JLabel lblTenant = new JLabel("Tenant");
		panel.add(lblTenant, "cell 0 0,alignx trailing");

		Enumeration ee = allTenants.keys();
		String tenantId = null;

		List<String> temp1 = new ArrayList(allTenants.values());
		Collections.sort(temp1, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		tenantComboBox = new JComboBox(temp1.toArray());
		if (defaultTenantName != null) {
			tenantComboBox.setSelectedItem(defaultTenantName);
		}
		panel.add(tenantComboBox, "cell 1 0,growx");

		JLabel lblRole = new JLabel("Role");
		panel.add(lblRole, "cell 0 1,alignx trailing");

		List<String> temp2 = new ArrayList(allRoles.values());
		Collections.sort(temp2, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		roleComboBox = new JComboBox(temp2.toArray());
		panel.add(roleComboBox, "cell 1 1,growx");

		CommonLib.centerDialog(this);
	}
}
