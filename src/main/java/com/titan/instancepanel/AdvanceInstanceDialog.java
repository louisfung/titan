package com.c2.pandora.instancepanel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.c2.pandora.PandoraCommonLib;
import com.c2.pandora.communication.CommunicateLib;
import com.c2.pandoraserver.Command;
import com.c2.pandoraserver.ReturnCommand;
import com.peterswing.CommonLib;
import com.peterswing.advancedswing.enhancedtextarea.EnhancedTextArea;

public class AdvanceInstanceDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private final JButton btnBlockstats = new JButton("Blockstats");
	private final EnhancedTextArea enhancedTextArea = new EnhancedTextArea();
	private String instanceName;

	public AdvanceInstanceDialog(Frame frame, String instanceName) {
		super(frame, "Launch instance", true);
		this.instanceName = instanceName;
		setTitle("Advance control panel");
		setBounds(100, 100, 824, 456);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("insets 5 5 0 5", "[grow][][]", "[][grow]"));
		{
			textField = new JTextField();
			contentPanel.add(textField, "cell 0 0,growx");
			textField.setColumns(10);
		}
		{
			JButton btnRun = new JButton("Run");
			btnRun.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Command command = new Command();
					command.command = "qmp command";
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put("instanceName", AdvanceInstanceDialog.this.instanceName);
					parameters.put("commandStr", textField.getText());
					command.parameters.add(parameters);
					ReturnCommand r = CommunicateLib.send(PandoraCommonLib.getCurrentServerIP(), command);
					enhancedTextArea.setText(enhancedTextArea.getText() + "\n" + r.map.get("result"));
				}
			});
			contentPanel.add(btnRun, "cell 1 0");
		}
		{
			contentPanel.add(btnBlockstats, "cell 2 0");
		}
		{
			contentPanel.add(enhancedTextArea, "cell 0 1 3 1,grow");
		}

		CommonLib.centerDialog(this);
	}

}
