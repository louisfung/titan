package com.titan.storagepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import net.sf.json.JSONObject;

import com.peterswing.CommonLib;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;

public class CreateVolumeDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField nameTextField;
	Frame frame;
	private JSpinner sizeSpinner;
	private JComboBox volumeTypeComboBox;
	private JTextArea descriptionTextArea;
	private JLabel errorLabel;
	private Hashtable<String, String> volumeTypes = new Hashtable<String, String>();

	public CreateVolumeDialog(final Frame frame, Hashtable<String, String> volumeTypes) {
		super(frame, true);
		this.frame = frame;
		this.volumeTypes = volumeTypes;
		setModal(true);
		setTitle("Create volume");
		setBounds(100, 100, 469, 308);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[20px]10px[0px]10px[20px]10px[20px][grow]"));
		{
			JLabel nameLabel = new JLabel("Name");
			contentPanel.add(nameLabel, "cell 0 0,alignx trailing");
		}
		{
			nameTextField = new JTextField();
			contentPanel.add(nameTextField, "cell 1 0,growx");
		}
		{
			errorLabel = new JLabel("");
			errorLabel.setForeground(Color.RED);
			errorLabel.setVisible(false);
			contentPanel.add(errorLabel, "cell 1 1");
		}
		{
			JLabel lblVolumeTypeIs = new JLabel("");
			contentPanel.add(lblVolumeTypeIs, "flowx,cell 0 2");
		}
		{
			JLabel lblVolumeType = new JLabel("Volume type");
			contentPanel.add(lblVolumeType, "cell 0 2,alignx right");
		}
		{
			List<String> temp1 = new ArrayList(volumeTypes.values());
			Collections.sort(temp1, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareToIgnoreCase(o2);
				}
			});
			volumeTypeComboBox = new JComboBox(temp1.toArray());
			contentPanel.add(volumeTypeComboBox, "cell 1 2");
		}
		{
			JLabel lblSize = new JLabel("Size");
			contentPanel.add(lblSize, "cell 0 3,alignx right");
		}
		{
			sizeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
			sizeSpinner.setPreferredSize(new Dimension(80, 10));
			contentPanel.add(sizeSpinner, "flowx,cell 1 3");
		}
		{
			JLabel lblGb = new JLabel("GB");
			contentPanel.add(lblGb, "cell 1 3");
		}
		{
			JLabel lblDescription = new JLabel("Description");
			contentPanel.add(lblDescription, "cell 0 4,alignx right");
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBorder(new LineBorder(Color.lightGray));
			contentPanel.add(scrollPane, "cell 1 4,grow");
			{
				descriptionTextArea = new JTextArea();
				scrollPane.setViewportView(descriptionTextArea);
			}
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
							errorLabel.setVisible(true);
							return;
						}

						errorLabel.setVisible(false);
						Command command = new Command();
						command.command = "from titan: cinder create";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$displayName", nameTextField.getText());
						parameters.put("$displayDescription", descriptionTextArea.getText());
						parameters.put("$size", String.valueOf(sizeSpinner.getValue()));
						parameters.put("$volumeType", (String) volumeTypeComboBox.getSelectedItem());
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						HttpResult httpResult = (HttpResult) r.map.get("result");
						try {
							JSONObject j = JSONObject.fromObject(httpResult.content).getJSONObject("volume");
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
