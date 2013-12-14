package com.titan.storagepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
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
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;

public class CreateVolumeTypeDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField nameTextField;
	private JLabel errorLabel = new JLabel("");
	Frame frame;

	public CreateVolumeTypeDialog(final Frame frame) {
		super(frame, true);
		setResizable(false);
		setModal(true);
		setTitle("Create volume type");
		setBounds(100, 100, 392, 209);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setLayout(new MigLayout("", "[20px,grow][grow]", "[20px]10px[20px]10px[grow]"));
		{
			JLabel nameLabel = new JLabel("Name");
			contentPanel.add(nameLabel, "cell 0 0,alignx trailing,aligny center");
		}
		{
			nameTextField = new JTextField();
			contentPanel.add(nameTextField, "cell 1 0,growx");
			nameTextField.setColumns(10);
		}
		{
			JLabel lblVolumeTypeIs = new JLabel("");
			contentPanel.add(lblVolumeTypeIs, "cell 0 1");
		}
		{
			errorLabel.setForeground(Color.RED);
			contentPanel.add(errorLabel, "cell 1 1");
		}
		{
			JLabel lblNewLabel = new JLabel(
					"<html><body>Volume type is the characteristics of a volumne, you can choose \"Dell storage server\", \"SSD\", \"SCSI RAID\" and etc as the name</body></html>");
			contentPanel.add(lblNewLabel, "cell 0 2 2 1");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton addButton = new JButton("Add");
				addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (nameTextField.getText().trim().equals("")) {
							errorLabel.setText("Name cannot be empty");
							return;
						}

						errorLabel.setVisible(false);
						Command command = new Command();
						command.command = "from titan: cinder type-create";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$name", nameTextField.getText());
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						HttpResult httpResult = (HttpResult) r.map.get("result");
						try {
							JSONObject j = JSONObject.fromObject(httpResult.content).getJSONObject("volume_type");
							if (j.isNullObject()) {
								throw new Exception();
							}
							setVisible(false);
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(frame, r.map.get("result"), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				addButton.setActionCommand("OK");
				buttonPane.add(addButton);
				getRootPane().setDefaultButton(addButton);
			}
			CommonLib.centerDialog(this);
		}
	}
}
