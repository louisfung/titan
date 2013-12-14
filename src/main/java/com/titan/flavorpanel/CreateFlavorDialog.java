package com.titan.flavorpanel;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.sf.json.JSONObject;

import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;

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

	public CreateFlavorDialog(final JFrame frame) {
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
						command.command = "from titan: nova create-flavor";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$name", nameTextField.getText());
						parameters.put("$vcpu", vcpuTextField.getText());
						parameters.put("$ram", ramTextField.getText());
						parameters.put("$root", rootTextField.getText());
						parameters.put("$ephemermal", ephemermalTextField.getText());
						parameters.put("$swap", swapTextField.getText());
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						HttpResult httpResult = (HttpResult) r.map.get("result");
						try {
							JSONObject j = JSONObject.fromObject(httpResult.content).getJSONObject("flavor");
							if (j.isNullObject()) {
								throw new Exception();
							}
							setVisible(false);
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(frame, httpResult.content, "Error", JOptionPane.ERROR_MESSAGE);
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
