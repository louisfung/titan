package com.titan.vm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.peterswing.advancedswing.searchtextfield.JSearchTextField;
import com.titan.OS;
import com.titan.TitanCommonLib;
import com.titan.TitanSetting;
import com.titan.communication.CommunicateLib;
import com.titan.mainframe.MainFrame;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class VMMainPanel extends JPanel {
	MainFrame mainFrame;
	JSlider slider = new JSlider();
	JPanel mainPanel = new JPanel();
	JSONArray servers;
	JLabel colLabel = new JLabel("");
	ButtonGroup group1 = new ButtonGroup();
	Vector<VMPanel> panels = new Vector<VMPanel>();
	private JToggleButton ganttViewButton;
	private JToggleButton deltailViewButton;
	JScrollPane scrollPane = new JScrollPane();
	VMGanttPanel vmGanttPanel;

	public VMMainPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JToolBar toolBar = new JToolBar();
		panel.add(toolBar);

		JToggleButton iconViewButton = new JToggleButton("");
		iconViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrollPane.setViewportView(mainPanel);
			}
		});
		iconViewButton.setSelected(true);
		iconViewButton.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/color_swatch.png")));
		toolBar.add(iconViewButton);
		group1.add(iconViewButton);

		ganttViewButton = new JToggleButton("");
		ganttViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (vmGanttPanel == null) {
					vmGanttPanel = new VMGanttPanel();
				}
				scrollPane.setViewportView(vmGanttPanel);
			}
		});
		ganttViewButton.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/text_list_bullets.png")));
		toolBar.add(ganttViewButton);
		group1.add(ganttViewButton);

		deltailViewButton = new JToggleButton("");
		deltailViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		deltailViewButton.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/text_align_justify.png")));
		toolBar.add(deltailViewButton);
		group1.add(deltailViewButton);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/arrow_rotate_clockwise.png")));
		toolBar.add(btnRefresh);

		JSearchTextField searchTextField = new JSearchTextField();
		searchTextField.setMaximumSize(new Dimension(100, 20));
		toolBar.add(searchTextField);

		slider.setMaximumSize(new Dimension(120, 20));
		slider.setMinimum(5);
		slider.setMaximum(20);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//				if (!slider.getValueIsAdjusting()) {
				int maxVMColumnCount = (int) slider.getValue();
				TitanSetting.getInstance().maxVMColumnCount = maxVMColumnCount;
				TitanSetting.getInstance().save();
				Command command = new Command();
				command.command = "from titan: nova list";
				if (servers == null) {
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
					servers = JSONObject.fromObject(r.map.get("result")).getJSONArray("servers");
				}
				colLabel.setText(TitanSetting.getInstance().maxVMColumnCount + " columns");
				int noOfVM = servers.size();
				int maxCol = maxVMColumnCount;
				int maxRow = (int) Math.ceil(((float) noOfVM) / maxVMColumnCount);
				String colStr = StringUtils.repeat("[]", maxCol);
				String rowStr = StringUtils.repeat("[]", maxRow);
				//				System.out.println(noOfVM + "," + maxRow + "," + maxCol);
				mainPanel.setLayout(new MigLayout("", colStr, rowStr));
				int row = 0;
				int col = 0;
				mainPanel.removeAll();
				panels.clear();
				for (int x = 0; x < servers.size(); x++) {
					JSONObject obj = servers.getJSONObject(x);
					VMLabel label = new VMLabel();
					label.setOS(OS.values()[new Random().nextInt(OS.values().length)]);
					VMPanel panel = new VMPanel(label);
					panels.add(panel);
					if (TitanCommonLib.getJSONString(obj, "name", "No name").length() <= 10) {
						panel.label.setText(TitanCommonLib.getJSONString(obj, "name", "No name"));
					} else {
						panel.label.setText(TitanCommonLib.getJSONString(obj, "name", "No name").substring(0, 10));
					}
					panel.addMouseListener(new MouseListener() {
						@Override
						public void mouseClicked(MouseEvent e) {
							clearAllPanelsSelection();
							VMPanel panel = (VMPanel) e.getSource();
							panel.setSelected(!panel.isSelected);
						}

						private void clearAllPanelsSelection() {
							for (VMPanel panel : panels) {
								panel.setSelected(false);
							}
						}

						@Override
						public void mousePressed(MouseEvent e) {
						}

						@Override
						public void mouseReleased(MouseEvent e) {
						}

						@Override
						public void mouseEntered(MouseEvent e) {
						}

						@Override
						public void mouseExited(MouseEvent e) {
						}
					});
					mainPanel.add(panel, "cell " + col + " " + row);
					col++;
					if (col == maxCol) {
						col = 0;
						row++;
					}
				}
				mainPanel.updateUI();
				//				}
			}
		});
		toolBar.add(slider);

		slider.setValue(TitanSetting.getInstance().maxVMColumnCount);

		colLabel.setText(TitanSetting.getInstance().maxVMColumnCount + " columns");
		toolBar.add(colLabel);

		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(mainPanel);
	}
}
