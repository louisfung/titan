package com.titan.storagepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;

public class UploadImageDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	JFrame frame;
	private JTextField nameTextField;
	private JTextField fileTextField = new JTextField();
	JComboBox formatComboBox;
	JSpinner minimumDisk = new JSpinner();
	JSpinner minimumRam = new JSpinner();
	JCheckBox chckbxPublic = new JCheckBox("Public");
	JLabel errorLabel = new JLabel();
	public boolean refresh;

	public UploadImageDialog(JFrame frame) {
		super(frame, true);
		setResizable(false);
		this.frame = frame;
		setTitle("Upload image");

		setBounds(100, 100, 482, 280);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[80px][310px][4px][16px][22px]", "[][][][][][][1px]"));
		JLabel lblName = new JLabel("Name");
		contentPanel.add(lblName, "cell 0 0,alignx right,aligny center");
		nameTextField = new JTextField();
		contentPanel.add(nameTextField, "cell 1 0 3 1,growx,aligny top");
		nameTextField.setColumns(10);
		JLabel lblFile = new JLabel("File");
		contentPanel.add(lblFile, "cell 0 1,alignx right,aligny center");
		contentPanel.add(fileTextField, "cell 1 1 3 1,growx,aligny center");
		fileTextField.setColumns(10);
		JButton button = new JButton("...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(UploadImageDialog.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					fileTextField.setText(file.getAbsolutePath());
					if (file.getName().toLowerCase().contains(".qcow2")) {
						formatComboBox.setSelectedItem("QCow2 - Qemu copy on write image");
					}
					if (nameTextField.getText().trim().equals("")) {
						nameTextField.setText(file.getName());
					}
				}
			}
		});
		contentPanel.add(button, "cell 4 1,alignx left,aligny top");
		JLabel lblFormat = new JLabel("Format");
		contentPanel.add(lblFormat, "cell 0 2,alignx right,aligny center");
		String[] fileFormats = { "AKI - Amazon Kernel Image", "AMI - Amazon Machine Image", "ARI - Amazon Ramdisk Image", "ISO", "QCow2 - Qemu copy on write image", "Raw",
				"VDI - Virtualbox", "VHD - Virtual PC", "VMDK - VMWare" };
		formatComboBox = new JComboBox(fileFormats);
		contentPanel.add(formatComboBox, "cell 1 2 3 1,growx,aligny top");
		JLabel lblMinimumDisk = new JLabel("Minimum disk");
		contentPanel.add(lblMinimumDisk, "cell 0 3,alignx left,aligny center");
		minimumDisk.setModel(new SpinnerNumberModel());
		contentPanel.add(minimumDisk, "cell 1 3,growx,aligny top");
		JLabel lblMinimumRam = new JLabel("Minimum ram");
		contentPanel.add(lblMinimumRam, "cell 0 4,alignx left,aligny center");
		minimumRam.setModel(new SpinnerNumberModel());
		contentPanel.add(minimumRam, "cell 1 4,growx,aligny top");
		contentPanel.add(chckbxPublic, "cell 1 5,alignx left,aligny top");
		JLabel lblGb = new JLabel("GB");
		contentPanel.add(lblGb, "cell 3 3,alignx left,aligny center");
		JLabel label = new JLabel("GB");
		contentPanel.add(label, "cell 3 4,alignx left,aligny center");
		errorLabel.setForeground(Color.RED);
		errorLabel.setVisible(false);
		contentPanel.add(errorLabel, "cell 1 6,alignx left,aligny top");
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		final JButton uploadButton = new JButton("Upload");
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadButton.setEnabled(false);
				refresh = true;
				errorLabel.setVisible(false);
				new Thread() {
					public void run() {
						try {
							Command command = new Command();
							command.command = "send file";
							File file = new File(fileTextField.getText());
							ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command, file, UploadImageDialog.this);
							String result = (String) r.map.get("result");

							if (result.equals("ok")) {
								command = new Command();
								command.command = "from titan: glance image-create";
								HashMap<String, Object> parameters = new HashMap<String, Object>();
								parameters.put("$x-image-meta-name", nameTextField.getText());
								String format = formatComboBox.getSelectedItem().toString().split("-")[0].trim().toLowerCase();
								parameters.put("$x-image-meta-disk_format", format);
								parameters.put("$x-image-meta-min-ram", minimumRam.getValue());
								parameters.put("$x-image-meta-min-disk", minimumDisk.getValue());
								parameters.put("$x-image-meta-is-public", chckbxPublic.isSelected());
								parameters.put("$POSTDATA", file.getName());
								command.parameters.add(parameters);
								r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
								HttpResult result2 = (HttpResult) r.map.get("result");
								if (result2.content.contains("active")) {
									UploadImageDialog.this.setVisible(false);
								} else {
									errorLabel.setText(result2.content);
									errorLabel.setVisible(true);
								}
							} else {
								throw new Exception();
							}

						} catch (Exception ex) {
							ex.printStackTrace();
							errorLabel.setText("error");
							errorLabel.setVisible(true);
						}

						uploadButton.setEnabled(true);
					}
				}.start();
			}
		});
		buttonPane.add(uploadButton);
		getRootPane().setDefaultButton(uploadButton);
		setLocationRelativeTo(null);
	}
}
