package com.titan.serverpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.titan.TitanSetting;
import com.titanserver.structure.TitanServerDefinition;

public class AddServerDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldID;
	private JTextField textFieldIP;
	private JLabel lblError = new JLabel("");
	Frame frame;

	public AddServerDialog(Frame frame) {
		super(frame, true);
		setResizable(false);
		setModal(true);
		setTitle("Add server");
		setBounds(100, 100, 238, 160);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setLayout(new MigLayout("", "[20px][grow]", "[20px]10px[20px]10px[]"));
		{
			JLabel lblId = new JLabel("ID");
			contentPanel.add(lblId, "cell 0 0,alignx trailing,aligny center");
		}
		{
			textFieldID = new JTextField();
			contentPanel.add(textFieldID, "cell 1 0,growx");
			textFieldID.setColumns(10);
		}
		{
			JLabel lblIp = new JLabel("IP");
			contentPanel.add(lblIp, "cell 0 1,alignx trailing,aligny center");
		}
		{
			textFieldIP = new JTextField();
			contentPanel.add(textFieldIP, "cell 1 1,growx");
			textFieldIP.setColumns(10);
		}
		{
			lblError.setForeground(Color.RED);
			contentPanel.add(lblError, "cell 1 2");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (textFieldID.getText().trim().equals("")) {
							lblError.setText("ID cannot be empty");
							return;
						}
						if (textFieldIP.getText().trim().equals("")) {
							lblError.setText("IP cannot be empty");
							return;
						}

						int duplicate = 0;
						for (int x = TitanSetting.getInstance().titanServers.size() - 1; x >= 0; x--) {
							TitanServerDefinition server = TitanSetting.getInstance().titanServers.get(x);
							if (server.id.equals(textFieldID.getText())) {
								duplicate++;
							}
						}
						if (duplicate > 0) {
							lblError.setText("ID is duplicated with other");
							return;
						}

						TitanServerDefinition titanServerDefinition = new TitanServerDefinition();
						titanServerDefinition.id = textFieldID.getText();
						titanServerDefinition.ip = textFieldIP.getText();
						TitanSetting.getInstance().titanServers.add(titanServerDefinition);
						TitanSetting.getInstance().save();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
