package com.c2.pandora.imagepanel;

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

import com.c2.pandora.PandoraCommonLib;
import com.c2.pandora.communication.CommunicateLib;
import com.c2.pandoraserver.Command;
import com.c2.pandoraserver.ReturnCommand;

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
		this.frame = frame;
		setTitle("Upload image");

		setBounds(100, 100, 482, 301);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow][]", "[][][][][][][]"));
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
			JLabel lblFile = new JLabel("File");
			contentPanel.add(lblFile, "cell 0 1,alignx trailing");
		}
		{
			contentPanel.add(fileTextField, "cell 1 1,growx");
			fileTextField.setColumns(10);
		}
		{
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
			contentPanel.add(button, "cell 2 1");
		}
		{
			JLabel lblFormat = new JLabel("Format");
			contentPanel.add(lblFormat, "cell 0 2,alignx trailing");
		}
		{
			String[] fileFormats = { "AKI - Amazon Kernel Image", "AMI - Amazon Machine Image", "ARI - Amazon Ramdisk Image", "ISO", "QCow2 - Qemu copy on write image", "Raw",
					"VDI - Virtualbox", "VHD - Virtual PC", "VMDK - VMWare" };
			formatComboBox = new JComboBox(fileFormats);
			contentPanel.add(formatComboBox, "cell 1 2,growx");
		}
		{
			JLabel lblMinimumDisk = new JLabel("Minimum disk");
			contentPanel.add(lblMinimumDisk, "cell 0 3");
		}
		{
			minimumDisk.setModel(new SpinnerNumberModel());
			contentPanel.add(minimumDisk, "flowx,cell 1 3,growx");
		}
		{
			JLabel lblMinimumRam = new JLabel("Minimum ram");
			contentPanel.add(lblMinimumRam, "cell 0 4");
		}
		{
			minimumRam.setModel(new SpinnerNumberModel());
			contentPanel.add(minimumRam, "flowx,cell 1 4,growx");
		}
		{
			contentPanel.add(chckbxPublic, "cell 1 5");
		}
		{
			JLabel lblGb = new JLabel("GB");
			contentPanel.add(lblGb, "cell 1 3");
		}
		{
			JLabel label = new JLabel("GB");
			contentPanel.add(label, "cell 1 4");
		}
		{
			errorLabel.setForeground(Color.RED);
			errorLabel.setVisible(false);
			contentPanel.add(errorLabel, "cell 1 6");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton uploadButton = new JButton("Upload");
				uploadButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						uploadButton.setEnabled(false);
						refresh = true;
						errorLabel.setVisible(false);
						new Thread() {
							public void run() {
								String result = "";
								try {
									Command command = new Command();
									command.command = "send file";
									File file = new File(fileTextField.getText());
									ReturnCommand r = CommunicateLib.send(PandoraCommonLib.getCurrentServerIP(), command, file, UploadImageDialog.this);
									result = (String) r.map.get("result");

									if (result.equals("ok")) {
										command = new Command();
										command.command = "from pandora: glance image-create";
										HashMap<String, Object> parameters = new HashMap<String, Object>();
										parameters.put("$x-image-meta-name", nameTextField.getText());
										String format = formatComboBox.getSelectedItem().toString().split("-")[0].trim().toLowerCase();
										parameters.put("$x-image-meta-disk_format", format);
										parameters.put("$x-image-meta-min-ram", minimumRam.getValue());
										parameters.put("$x-image-meta-min-disk", minimumDisk.getValue());
										parameters.put("$x-image-meta-is-public", chckbxPublic.isSelected());
										parameters.put("$POSTDATA", file.getName());
										command.parameters.add(parameters);
										r = CommunicateLib.send(PandoraCommonLib.getCurrentServerIP(), command);
									} else {
										throw new Exception();
									}
									result = (String) r.map.get("result");
									if (result.contains("active")) {
										UploadImageDialog.this.setVisible(false);
									} else {
										errorLabel.setText(result);
										errorLabel.setVisible(true);
									}
								} catch (Exception ex) {
									errorLabel.setText((String) result);
									errorLabel.setVisible(true);
								}

								uploadButton.setEnabled(true);
							}
						}.start();
					}
				});
				buttonPane.add(uploadButton);
				getRootPane().setDefaultButton(uploadButton);
			}
		}
		setLocationRelativeTo(null);
	}
}
