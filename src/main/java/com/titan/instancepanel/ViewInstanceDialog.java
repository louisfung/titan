package com.titan.instancepanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

public class ViewInstanceDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	JFrame frame;
	String instanceId;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_10;
	private JTextField textField_11;
	private JTextField textField_12;
	private JTextField textField_13;
	private JTextField textField_14;
	private JTextField textField_15;
	private JTextField textField_16;
	private JTextField textField_17;
	private JTextField textField_18;
	private JTextField textField_19;
	private JTextField textField_20;
	private JTextField textField_21;
	private JTextField textField_22;
	private JTextField textField_23;
	private JTextField textField_24;
	private JTextField textField_25;
	private JTextField textField_26;
	private JTextField textField_27;
	private JTextField textField_28;
	private JTextField textField_29;
	private JTextField textField_30;
	private JTextField textField_31;

	public ViewInstanceDialog(JFrame frame, String instanceId) {
		super(frame, true);
		this.frame = frame;
		this.instanceId = instanceId;
		setTitle("Instance Id : " + instanceId);

		setBounds(100, 100, 614, 687);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow][grow][][][grow]", "[][][][][][][][][][][][][][][][][][][][][]"));
		{
			JLabel label = new JLabel("");
			label.setIcon(new ImageIcon(ViewInstanceDialog.class.getResource("/com/titan/instancepanel/vmInformation.png")));
			contentPanel.add(label, "cell 0 0 5 1");
		}
		{
			JLabel lblName = new JLabel("Name");
			contentPanel.add(lblName, "cell 0 1,alignx trailing");
		}
		{
			textField = new JTextField();
			contentPanel.add(textField, "cell 1 1,growx");
			textField.setColumns(10);
		}
		{
			JLabel lblId = new JLabel("Id");
			contentPanel.add(lblId, "cell 3 1,alignx trailing");
		}
		{
			textField_1 = new JTextField();
			contentPanel.add(textField_1, "cell 4 1,growx");
			textField_1.setColumns(10);
		}
		{
			JLabel lblStatus = new JLabel("Status");
			contentPanel.add(lblStatus, "cell 0 2,alignx trailing");
		}
		{
			textField_2 = new JTextField();
			contentPanel.add(textField_2, "cell 1 2,growx");
			textField_2.setColumns(10);
		}
		{
			JLabel label = new JLabel("Progress");
			contentPanel.add(label, "cell 3 2,alignx trailing");
		}
		{
			textField_7 = new JTextField();
			textField_7.setColumns(10);
			contentPanel.add(textField_7, "cell 4 2,growx");
		}
		{
			JLabel lblAddress = new JLabel("Address");
			contentPanel.add(lblAddress, "cell 0 3,alignx trailing");
		}
		{
			textField_3 = new JTextField();
			textField_3.setColumns(10);
			contentPanel.add(textField_3, "cell 1 3,growx");
		}
		{
			JLabel lblInstanceName = new JLabel("Instance name");
			contentPanel.add(lblInstanceName, "cell 3 3,alignx trailing");
		}
		{
			textField_8 = new JTextField();
			textField_8.setColumns(10);
			contentPanel.add(textField_8, "cell 4 3,growx");
		}
		{
			JLabel lblHypervisorHostname = new JLabel("Hypervisor hostname");
			contentPanel.add(lblHypervisorHostname, "cell 0 4,alignx trailing");
		}
		{
			textField_4 = new JTextField();
			textField_4.setColumns(10);
			contentPanel.add(textField_4, "cell 1 4,growx");
		}
		{
			JLabel lblHost = new JLabel("Host");
			contentPanel.add(lblHost, "cell 3 4,alignx trailing");
		}
		{
			textField_9 = new JTextField();
			textField_9.setColumns(10);
			contentPanel.add(textField_9, "cell 4 4,growx");
		}
		{
			JLabel lblPowerState = new JLabel("Power state");
			contentPanel.add(lblPowerState, "cell 0 5,alignx trailing");
		}
		{
			textField_5 = new JTextField();
			textField_5.setColumns(10);
			contentPanel.add(textField_5, "cell 1 5,growx");
		}
		{
			JLabel lblVmState = new JLabel("VM state");
			contentPanel.add(lblVmState, "cell 3 5,alignx trailing");
		}
		{
			textField_10 = new JTextField();
			textField_10.setColumns(10);
			contentPanel.add(textField_10, "cell 4 5,growx");
		}
		{
			JLabel lblAvailabilityZone = new JLabel("Availability zone");
			contentPanel.add(lblAvailabilityZone, "cell 0 6,alignx trailing");
		}
		{
			textField_6 = new JTextField();
			textField_6.setColumns(10);
			contentPanel.add(textField_6, "cell 1 6,growx");
		}
		{
			JLabel lblTaskState = new JLabel("Task state");
			contentPanel.add(lblTaskState, "cell 3 6,alignx trailing");
		}
		{
			textField_11 = new JTextField();
			textField_11.setColumns(10);
			contentPanel.add(textField_11, "cell 4 6,growx");
		}
		{
			JLabel lblIpV = new JLabel("IP v4");
			contentPanel.add(lblIpV, "cell 0 7,alignx trailing");
		}
		{
			textField_12 = new JTextField();
			textField_12.setColumns(10);
			contentPanel.add(textField_12, "cell 1 7,growx");
		}
		{
			JLabel lblUserId = new JLabel("User Id");
			contentPanel.add(lblUserId, "cell 3 7,alignx trailing");
		}
		{
			textField_17 = new JTextField();
			textField_17.setColumns(10);
			contentPanel.add(textField_17, "cell 4 7,growx");
		}
		{
			JLabel lblIpV_1 = new JLabel("IP v6");
			contentPanel.add(lblIpV_1, "cell 0 8,alignx trailing");
		}
		{
			textField_13 = new JTextField();
			textField_13.setColumns(10);
			contentPanel.add(textField_13, "cell 1 8,growx");
		}
		{
			JLabel lblConfigDrive = new JLabel("Config drive");
			contentPanel.add(lblConfigDrive, "cell 3 8,alignx trailing");
		}
		{
			textField_18 = new JTextField();
			textField_18.setColumns(10);
			contentPanel.add(textField_18, "cell 4 8,growx");
		}
		{
			JLabel lblHostId = new JLabel("Host Id");
			contentPanel.add(lblHostId, "cell 0 9,alignx trailing");
		}
		{
			textField_14 = new JTextField();
			textField_14.setColumns(10);
			contentPanel.add(textField_14, "cell 1 9,growx");
		}
		{
			JLabel lblCreated = new JLabel("Created");
			contentPanel.add(lblCreated, "cell 3 9,alignx trailing");
		}
		{
			textField_19 = new JTextField();
			textField_19.setColumns(10);
			contentPanel.add(textField_19, "cell 4 9,growx");
		}
		{
			JLabel lblKeyName = new JLabel("Key name");
			contentPanel.add(lblKeyName, "cell 0 10,alignx trailing");
		}
		{
			textField_15 = new JTextField();
			textField_15.setColumns(10);
			contentPanel.add(textField_15, "cell 1 10,growx");
		}
		{
			JLabel lblUpdated = new JLabel("Updated");
			contentPanel.add(lblUpdated, "cell 3 10,alignx trailing");
		}
		{
			textField_20 = new JTextField();
			textField_20.setColumns(10);
			contentPanel.add(textField_20, "cell 4 10,growx");
		}
		{
			JLabel lblTenantId = new JLabel("Tenant Id");
			contentPanel.add(lblTenantId, "cell 0 11,alignx trailing");
		}
		{
			textField_16 = new JTextField();
			textField_16.setColumns(10);
			contentPanel.add(textField_16, "cell 1 11,growx");
		}
		{
			JLabel label = new JLabel("");
			label.setIcon(new ImageIcon(ViewInstanceDialog.class.getResource("/com/titan/instancepanel/flavorInformation.png")));
			contentPanel.add(label, "cell 0 12 5 1");
		}
		{
			JLabel lblId_1 = new JLabel("Id");
			contentPanel.add(lblId_1, "cell 0 13,alignx trailing");
		}
		{
			textField_21 = new JTextField();
			contentPanel.add(textField_21, "cell 1 13,growx");
			textField_21.setColumns(10);
		}
		{
			JLabel lblName_1 = new JLabel("Name");
			contentPanel.add(lblName_1, "cell 3 13,alignx trailing");
		}
		{
			textField_28 = new JTextField();
			contentPanel.add(textField_28, "cell 4 13,growx");
			textField_28.setColumns(10);
		}
		{
			JLabel lblDisable = new JLabel("Disable");
			contentPanel.add(lblDisable, "cell 0 14,alignx trailing");
		}
		{
			textField_22 = new JTextField();
			textField_22.setColumns(10);
			contentPanel.add(textField_22, "cell 1 14,growx");
		}
		{
			JLabel lblEphemeral = new JLabel("Ephemeral");
			contentPanel.add(lblEphemeral, "cell 3 14,alignx trailing");
		}
		{
			textField_29 = new JTextField();
			contentPanel.add(textField_29, "cell 4 14,growx");
			textField_29.setColumns(10);
		}
		{
			JLabel lblPublic = new JLabel("Public");
			contentPanel.add(lblPublic, "cell 0 15,alignx trailing");
		}
		{
			textField_23 = new JTextField();
			textField_23.setColumns(10);
			contentPanel.add(textField_23, "cell 1 15,growx");
		}
		{
			JLabel lblDisk = new JLabel("Disk");
			contentPanel.add(lblDisk, "cell 3 15,alignx trailing");
		}
		{
			textField_30 = new JTextField();
			contentPanel.add(textField_30, "cell 4 15,growx");
			textField_30.setColumns(10);
		}
		{
			JLabel lblRam = new JLabel("Ram");
			contentPanel.add(lblRam, "cell 0 16,alignx trailing");
		}
		{
			textField_24 = new JTextField();
			contentPanel.add(textField_24, "cell 1 16,growx");
			textField_24.setColumns(10);
		}
		{
			JLabel lblSwap = new JLabel("Swap");
			contentPanel.add(lblSwap, "cell 3 16,alignx trailing");
		}
		{
			textField_31 = new JTextField();
			contentPanel.add(textField_31, "cell 4 16,growx");
			textField_31.setColumns(10);
		}
		{
			JLabel lblFactor = new JLabel("Factor");
			contentPanel.add(lblFactor, "cell 0 17,alignx trailing");
		}
		{
			textField_25 = new JTextField();
			contentPanel.add(textField_25, "cell 1 17,growx");
			textField_25.setColumns(10);
		}
		{
			JLabel lblVcpus = new JLabel("VCpus");
			contentPanel.add(lblVcpus, "cell 0 18,alignx trailing");
		}
		{
			textField_26 = new JTextField();
			contentPanel.add(textField_26, "cell 1 18,growx");
			textField_26.setColumns(10);
		}
		{
			JLabel lblLinks = new JLabel("Links");
			contentPanel.add(lblLinks, "cell 0 19,alignx trailing");
		}
		{
			textField_27 = new JTextField();
			contentPanel.add(textField_27, "cell 1 19,growx");
			textField_27.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton saveButton = new JButton("Save");
				saveButton.setActionCommand("OK");
				buttonPane.add(saveButton);
				getRootPane().setDefaultButton(saveButton);
			}
		}
		setLocationRelativeTo(null);
	}

}
