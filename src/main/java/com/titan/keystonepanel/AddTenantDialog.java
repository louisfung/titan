package com.titan.keystonepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.sf.json.JSONObject;

import com.peterswing.CommonLib;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class AddTenantDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField nameTextField;
	private JTextField descriptionTextField;
	private JCheckBox checkBox;
	Frame frame;
	private JLabel error1;

	public AddTenantDialog(final Frame frame) {
		super(frame, true);
		this.frame = frame;
		setResizable(false);
		setTitle("Create tenant");
		setBounds(100, 100, 361, 171);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow][]", "[][][]"));
		{
			JLabel lblName = new JLabel("Name");
			contentPanel.add(lblName, "cell 0 0,alignx trailing");
		}
		{
			nameTextField = new JTextField();
			contentPanel.add(nameTextField, "cell 1 0,growx");
			nameTextField.setColumns(10);
		}
		{
			error1 = new JLabel("");
			error1.setForeground(Color.RED);
			contentPanel.add(error1, "cell 2 0");
		}
		{
			JLabel lblDescription = new JLabel("Description");
			contentPanel.add(lblDescription, "cell 0 1,alignx trailing");
		}
		{
			descriptionTextField = new JTextField();
			contentPanel.add(descriptionTextField, "cell 1 1,growx");
			descriptionTextField.setColumns(10);
		}
		{
			JLabel lblPrimaryTenant = new JLabel("Enabled");
			contentPanel.add(lblPrimaryTenant, "cell 0 2,alignx trailing");
		}
		{
			checkBox = new JCheckBox("");
			checkBox.setSelected(true);
			contentPanel.add(checkBox, "cell 1 2");
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
						if (nameTextField.getText().trim().equals("")) {
							error1.setText("Name cannot be empty !");
							error = true;
						}
						if (error) {
							return;
						}
						Command command = new Command();
						command.command = "from titan: keystone tenant-create";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$name", nameTextField.getText());
						parameters.put("$description", descriptionTextField.getText());
						parameters.put("$enabled", checkBox.isSelected() ? "true" : "false");
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						String msg = (String) r.map.get("result");

						if (msg == null) {
							JOptionPane.showMessageDialog(frame, "Error, return value is null", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						} else if (msg.contains("error")) {
							String errorMessage = JSONObject.fromObject(msg).getJSONObject("error").getString("message");
							if (errorMessage.length() > 80) {
								errorMessage = CommonLib.splitString(errorMessage, "\n", 80);
							}
							JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
							return;
						} else {
							setVisible(false);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				CommonLib.centerDialog(this);
			}
		}
	}
}
