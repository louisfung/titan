package com.c2.pandora.flavorpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.sf.json.JSONObject;

import com.c2.pandora.PandoraCommonLib;
import com.c2.pandora.communication.CommunicateLib;
import com.c2.pandoraserver.Command;
import com.c2.pandoraserver.ReturnCommand;

public class CreateFlavorDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	JFrame frame;
	private JTextField nameTextField;
	private JTextField vcpuTextField;
	private JTextField ramTextField;
	private JTextField rootTextField;
	private JTextField ephemermalTextField;
	private JTextField swapTextField;
	private JLabel errorLabel;

	public CreateFlavorDialog(JFrame frame) {
		super(frame, true);
		this.frame = frame;
		setTitle("Create flavor");

		setBounds(100, 100, 340, 273);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow][]", "[][][][][]"));
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
			JLabel lblVcpu = new JLabel("vCPU");
			contentPanel.add(lblVcpu, "cell 0 1,alignx trailing");
		}
		{
			vcpuTextField = new JTextField();
			contentPanel.add(vcpuTextField, "cell 1 1,growx");
			vcpuTextField.setColumns(10);
		}
		{
			JLabel lblRammb = new JLabel("Ram (MB)");
			contentPanel.add(lblRammb, "cell 0 2,alignx trailing");
		}
		{
			ramTextField = new JTextField();
			contentPanel.add(ramTextField, "cell 1 2,growx");
			ramTextField.setColumns(10);
		}
		{
			JLabel lblRootDiskgb = new JLabel("Root Disk (GB)");
			contentPanel.add(lblRootDiskgb, "cell 0 3,alignx trailing");
		}
		{
			rootTextField = new JTextField();
			contentPanel.add(rootTextField, "cell 1 3,growx");
			rootTextField.setColumns(10);
		}
		{
			JLabel lblEphemermalDiskgb = new JLabel("Ephemermal Disk (GB)");
			contentPanel.add(lblEphemermalDiskgb, "cell 0 4,alignx trailing");
		}
		{
			ephemermalTextField = new JTextField();
			contentPanel.add(ephemermalTextField, "cell 1 4,growx");
			ephemermalTextField.setColumns(10);
		}
		{
			JLabel lblSwapDiskgb = new JLabel("Swap Disk (GB)");
			contentPanel.add(lblSwapDiskgb, "cell 0 5,alignx trailing");
		}
		{
			swapTextField = new JTextField();
			contentPanel.add(swapTextField, "cell 1 5,growx");
			swapTextField.setColumns(10);
		}
		{
			JCheckBox chckbxDisable = new JCheckBox("Disable");
			contentPanel.add(chckbxDisable, "cell 1 6");
		}
		{
			errorLabel = new JLabel("");
			errorLabel.setForeground(Color.RED);
			contentPanel.add(errorLabel, "cell 0 7 2 1");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton createButton = new JButton("Create");
				createButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						errorLabel.setVisible(false);
						Command command = new Command();
						command.command = "from pandora: nova create-flavor";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$name", nameTextField.getText());
						parameters.put("$vcpu", vcpuTextField.getText());
						parameters.put("$ram", ramTextField.getText());
						parameters.put("$root", rootTextField.getText());
						parameters.put("$ephemermal", ephemermalTextField.getText());
						parameters.put("$swap", swapTextField.getText());
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(PandoraCommonLib.getCurrentServerIP(), command);
						try {
							JSONObject j = JSONObject.fromObject(r.map.get("result")).getJSONObject("flavor");
							if (j.isNullObject()) {
								throw new Exception();
							}
							setVisible(false);
						} catch (Exception ex) {
							errorLabel.setText(r.map.get("result").toString());
							errorLabel.setVisible(true);
						}
					}
				});
				createButton.setActionCommand("");
				buttonPane.add(createButton);
				getRootPane().setDefaultButton(createButton);
			}
		}
		setLocationRelativeTo(null);
	}
}
