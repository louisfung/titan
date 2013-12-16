package com.titan.vm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.sf.json.JSONObject;

import com.titan.OS;
import com.titan.TitanCommonLib;

public class VMIcon extends JPanel implements Comparable {
	JLabel label = new JLabel("");
	public boolean clicked;
	public boolean selected;
	LineBorder border = new LineBorder(new Color(0, 0, 0), 1);
	EmptyBorder emptyBorder = new EmptyBorder(1, 1, 1, 1);
	JSONObject json;
	Image selectedImage = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/selected.png")).getImage();
	public String vmName;
	public int maxVMNameLength = 15;

	public VMIcon(JSONObject json) {
		this.json = json;
		VMLabel iconLabel = new VMLabel(this);
		iconLabel.setOS(OS.values()[new Random().nextInt(OS.values().length)]);
		setLayout(new BorderLayout(0, 0));

		add(iconLabel, BorderLayout.CENTER);
		vmName = TitanCommonLib.getJSONString(json, "name", "No name");
		label.setText("<html><body><center>" + vmName.replaceAll("(.{" + maxVMNameLength + "})", "$1<br>") + "</center></body></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);

		add(label, BorderLayout.SOUTH);
		setBorder(emptyBorder);
		setMaximumSize(new Dimension(100, 200));
		setOpaque(false);
	}

	public void paint(Graphics g) {
		if (selected) {
			g.drawImage(selectedImage, 0, 0, null);
		}

		super.paint(g);
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		repaint();
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
		repaint();
	}

	@Override
	public int compareTo(Object o) {
		return vmName.compareToIgnoreCase(((VMIcon) o).vmName);
	}
}
