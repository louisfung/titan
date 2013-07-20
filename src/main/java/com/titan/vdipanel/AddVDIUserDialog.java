package com.titan.vdipanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

public class AddVDIUserDialog extends JDialog {
	public boolean ok;
	private final JPanel contentPanel = new JPanel();
	JTextField usernameTextField;
	private JPasswordField passwordField;
	private JPasswordField passwordField2;
	JComboBox comboBox;

	public AddVDIUserDialog(Frame frame) {
		super(frame, "Add VDI user", true);
		setBounds(100, 100, 335, 171);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
		{
			JLabel lblUsername = new JLabel("Username");
			contentPanel.add(lblUsername, "cell 0 0,alignx trailing");
		}
		{
			usernameTextField = new JTextField();
			contentPanel.add(usernameTextField, "cell 1 0,growx");
			usernameTextField.setColumns(10);
		}
		{
			JLabel lblPassword = new JLabel("Password");
			contentPanel.add(lblPassword, "cell 0 1,alignx trailing");
		}
		{
			passwordField = new JPasswordField();
			contentPanel.add(passwordField, "cell 1 1,growx");
		}
		{
			JLabel lblConfirmPassword = new JLabel("Confirm password");
			contentPanel.add(lblConfirmPassword, "cell 0 2,alignx trailing");
		}
		{
			passwordField2 = new JPasswordField();
			contentPanel.add(passwordField2, "cell 1 2,growx");
		}
		{
			JLabel lblGroup = new JLabel("Group");
			contentPanel.add(lblGroup, "cell 0 3,alignx trailing");
		}
		{
			comboBox = new JComboBox(new String[] { "Administrator", "View only" });
			contentPanel.add(comboBox, "cell 1 3,growx");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ok = true;
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
