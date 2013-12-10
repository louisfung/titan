package com.titan.vm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.titan.OS;
import com.titan.TitanCommonLib;

public class VMLabel extends JLabel {
	VMIcon vmPanel;
	OS os;
	//	Icon icon = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/vmIcon.png"));
	Image normal = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/normal.png")).getImage();

	Image gray = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/gray.png")).getImage();
	Image blue = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/blue.png")).getImage();
	Image yellow = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/yellow.png")).getImage();
	Image red = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/red.png")).getImage();
	Image noname = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/noname.png")).getImage();
	Image redhat = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/redhat.png")).getImage();
	Image ubuntu = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/ubuntu.png")).getImage();
	Image suse = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/suse.png")).getImage();
	Image centos = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/centos.png")).getImage();
	Image windows = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/windows.png")).getImage();

	Image play = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/status/play.png")).getImage();
	Image pause = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/status/pause.png")).getImage();
	Image stop = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/status/stop.png")).getImage();

	Image low = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/low.png")).getImage();
	Image middle = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/middle.png")).getImage();
	Image high = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/high.png")).getImage();

	Image shutoff = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/shutoff.png")).getImage();
	Image tick = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/tick.png")).getImage();
	Color normalTextColor = new Color(0, 162, 222);
	Color grayTextColor = new Color(193, 193, 193);

	public VMLabel(VMIcon vmPanel) {
		this(vmPanel, OS.none);
	}

	public VMLabel(VMIcon vmPanel, OS os) {
		super();
		this.vmPanel = vmPanel;
		setOS(os);
		setPreferredSize(new Dimension(red.getWidth(null), red.getHeight(null)));
		setOpaque(false);
	}

	public void setOS(OS os) {
		this.os = os;
	}

	public void paint(Graphics g) {
		super.paint(g);
		int cpu = new Random().nextInt(100);
		int memory = new Random().nextInt(100);
		String status = TitanCommonLib.getJSONString(vmPanel.json, "status", "SHUTOFF");
		g.drawImage(normal, 0, 0, null);
		if (status.equals("SHUTOFF")) {
			int offsetY = 4;
			int col1OffsetX = 6;
			int col2OffsetX = 51;
			if (os == OS.redhat) {
				g.drawImage(redhat, (44 - redhat.getWidth(null)) / 2 + col1OffsetX, (44 - redhat.getHeight(null)) / 2 + offsetY, null);
			} else if (os == OS.ubuntu) {
				g.drawImage(ubuntu, (44 - ubuntu.getWidth(null)) / 2 + col1OffsetX, (44 - ubuntu.getHeight(null)) / 2 + offsetY, null);
			} else if (os == OS.suse) {
				g.drawImage(suse, (44 - suse.getWidth(null)) / 2 + col1OffsetX, (44 - suse.getHeight(null)) / 2 + offsetY, null);
			} else if (os == OS.centos) {
				g.drawImage(centos, (44 - centos.getWidth(null)) / 2 + col1OffsetX, (44 - centos.getHeight(null)) / 2 + offsetY, null);
			} else if (os == OS.windows) {
				g.drawImage(windows, (44 - windows.getWidth(null)) / 2 + col1OffsetX, (44 - windows.getHeight(null)) / 2 + offsetY, null);
			} else {
				g.drawImage(noname, (44 - noname.getWidth(null)) / 2 + col1OffsetX, (44 - noname.getHeight(null)) / 2 + offsetY, null);
			}
			g.drawImage(stop, 57, 7, null);
			g.setColor(grayTextColor);
			Font font = new Font("Arial", Font.PLAIN, 24);
			g.setFont(font);
			FontMetrics metrics = g.getFontMetrics(font);
			int cpuFontWidth = metrics.stringWidth("0");
			int memoryFontWidth = metrics.stringWidth("0");
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.drawString("0", (44 - cpuFontWidth) / 2 + col1OffsetX, 77);
			g.drawString("0", (44 - memoryFontWidth) / 2 + col2OffsetX, 77);
		} else {
			int offsetY = 4;
			int col1OffsetX = 6;
			int col2OffsetX = 51;
			if (os == OS.redhat) {
				g.drawImage(redhat, (44 - redhat.getWidth(null)) / 2 + col1OffsetX, (44 - redhat.getHeight(null)) / 2 + offsetY, null);
			} else if (os == OS.ubuntu) {
				g.drawImage(ubuntu, (44 - ubuntu.getWidth(null)) / 2 + col1OffsetX, (44 - ubuntu.getHeight(null)) / 2 + offsetY, null);
			} else if (os == OS.suse) {
				g.drawImage(suse, (44 - suse.getWidth(null)) / 2 + col1OffsetX, (44 - suse.getHeight(null)) / 2 + offsetY, null);
			} else if (os == OS.centos) {
				g.drawImage(centos, (44 - centos.getWidth(null)) / 2 + col1OffsetX, (44 - centos.getHeight(null)) / 2 + offsetY, null);
			} else if (os == OS.windows) {
				g.drawImage(windows, (44 - windows.getWidth(null)) / 2 + col1OffsetX, (44 - windows.getHeight(null)) / 2 + offsetY, null);
			} else {
				g.drawImage(noname, (44 - noname.getWidth(null)) / 2 + col1OffsetX, (44 - noname.getHeight(null)) / 2 + offsetY, null);
			}
			g.drawImage(play, 57, 7, null);
			g.setColor(normalTextColor);
			Font font = new Font("Arial", Font.PLAIN, 24);
			g.setFont(font);
			FontMetrics metrics = g.getFontMetrics(font);
			int cpuFontWidth = metrics.stringWidth(String.valueOf(cpu));
			int memoryFontWidth = metrics.stringWidth(String.valueOf(memory));
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.drawString(String.valueOf(cpu), (44 - cpuFontWidth) / 2 + col1OffsetX, 77);
			g.drawString(String.valueOf(memory), (44 - memoryFontWidth) / 2 + col2OffsetX, 77);
		}

		if (vmPanel.clicked) {
			g.drawImage(tick, 70, 0, null);
		}
	}
}
