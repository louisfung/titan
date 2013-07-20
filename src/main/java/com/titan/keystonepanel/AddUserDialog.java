package com.titan.keystonepanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.peterswing.CommonLib;

public class AddUserDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	private JPasswordField passwordTextField2;
	private JTextField emailTextField;
	Hashtable<String, String> allTenants;
	private JComboBox tenantComboBox;
	Frame frame;

	public AddUserDialog(final Frame frame, Hashtable<String, String> allTenants) {
		super(frame, true);
		this.frame = frame;
		this.allTenants = allTenants;
		setResizable(false);
		setTitle("Add user");
		setBounds(100, 100, 553, 173);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow 50][][grow]", "[][][]"));
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
			contentPanel.add(lblPassword, "cell 2 0,alignx trailing");
		}
		{
			passwordTextField = new JPasswordField();
			contentPanel.add(passwordTextField, "cell 3 0,growx");
		}
		{
			JLabel lblEmail = new JLabel("Email");
			contentPanel.add(lblEmail, "cell 0 1,alignx trailing");
		}
		{
			emailTextField = new JTextField();
			contentPanel.add(emailTextField, "cell 1 1,growx");
			emailTextField.setColumns(10);
		}
		{
			JLabel lblConfirmPassword = new JLabel("Confirm password");
			contentPanel.add(lblConfirmPassword, "cell 2 1,alignx trailing");
		}
		{
			passwordTextField2 = new JPasswordField();
			contentPanel.add(passwordTextField2, "cell 3 1,growx");
		}
		{
			JLabel lblPrimaryTenant = new JLabel("Primary tenant");
			contentPanel.add(lblPrimaryTenant, "cell 0 2,alignx trailing");
		}
		{
			JLabel lblPrimaryRole = new JLabel("Primary role");
			contentPanel.add(lblPrimaryRole, "cell 2 2,alignx trailing");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Add");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						boolean error = false;
						String errorMessage = "";
						if (usernameTextField.getText().trim().equals("")) {
							if (!errorMessage.equals("")) {
								errorMessage += "\n";
							}
							errorMessage += "Username can't be empty !";
							error = true;
						}
						if (passwordTextField.getText().equals("")) {
							if (!errorMessage.equals("")) {
								errorMessage += "\n";
							}
							errorMessage += "Password can't be empty !";
							error = true;
						}
						if (!passwordTextField.getText().equals(passwordTextField2.getText())) {
							if (!errorMessage.equals("")) {
								errorMessage += "\n";
							}
							errorMessage += "Password and confirm password mismatch !";
							error = true;
						}
						if (error) {
							JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
						} else {

						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		CommonLib.centerDialog(this);
	}

}
