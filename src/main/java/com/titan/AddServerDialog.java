package com.titan;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.titanserver.structure.TitanServerDefinition;

public class AddServerDialog extends JDialog {
	private JTextField idTextField;
	private JTextField ipTextField;

	public AddServerDialog(final Frame frame) {
		super(frame, "Add titan server", true);
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][][grow]"));

		JLabel lblId = new JLabel("ID");
		getContentPane().add(lblId, "cell 0 0,alignx trailing");

		idTextField = new JTextField();
		getContentPane().add(idTextField, "cell 1 0,growx");
		idTextField.setColumns(10);

		JLabel lblIp = new JLabel("IP");
		getContentPane().add(lblIp, "cell 0 1,alignx trailing");

		ipTextField = new JTextField();
		getContentPane().add(ipTextField, "cell 1 1,growx");
		ipTextField.setColumns(10);

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 2 2 1,grow");

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (idTextField.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(frame, "ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (ipTextField.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(frame, "IP cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				TitanServerDefinition titanServerDefinition = new TitanServerDefinition();
				titanServerDefinition.id = idTextField.getText();
				titanServerDefinition.ip = ipTextField.getText();
				TitanSetting.getInstance().titanServers.add(titanServerDefinition);
				TitanSetting.getInstance().save();
				setVisible(false);
			}
		});
		panel.add(btnAdd);
		setSize(300, 130);
	}
}
