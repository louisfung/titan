package com.titan.vdipanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.peterswing.advancedswing.onoffbutton.OnOffButton;

public class VDISettingDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();

	private JLabel lblScreenMonitoring;
	private OnOffButton onOffButton;

	public VDISettingDialog(final Frame frame, String instanceName) {
		super(frame, "VDI setting : ", true);
		setBounds(100, 100, 253, 135);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblScreenMonitoring = new JLabel("Screen monitoring");
		}
		{
			onOffButton = new OnOffButton();
		}
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_contentPanel.createSequentialGroup().addGap(16).addComponent(lblScreenMonitoring).addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(onOffButton, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE).addGap(47)));
		gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_contentPanel
						.createSequentialGroup()
						.addGap(23)
						.addGroup(
								gl_contentPanel.createParallelGroup(Alignment.TRAILING, false).addComponent(onOffButton, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
										.addComponent(lblScreenMonitoring, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap(26, Short.MAX_VALUE)));
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton saveButton = new JButton("Save");
				saveButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				saveButton.setIcon(new ImageIcon(VDISettingDialog.class.getResource("/com/titan/image/famfamfam/disk.png")));
				saveButton.setActionCommand("OK");
				buttonPane.add(saveButton);
				getRootPane().setDefaultButton(saveButton);
			}
		}
	}

}
