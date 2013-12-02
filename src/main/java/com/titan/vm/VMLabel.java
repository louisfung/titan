package com.titan.vm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.titan.OS;

public class VMLabel extends JLabel {
	VMPanel vmPanel;
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

	public VMLabel(VMPanel vmPanel) {
		this(vmPanel, OS.none);
	}

	public VMLabel(VMPanel vmPanel, OS os) {
		super();
		this.vmPanel = vmPanel;
		//		setIcon(icon);
		setOS(os);
		setPreferredSize(new Dimension(red.getWidth(null), red.getHeight(null)));
//		setOpaque(false);
	}

	public void setOS(OS os) {
		this.os = os;
	}

	public void paint(Graphics g) {
		super.paint(g);
		int cpu = new Random().nextInt(100);
		//		if (cpu >= 80) {
		//			g.drawImage(red, 0, 0, null);
		//		} else if (cpu >= 60) {
		//			g.drawImage(yellow, 0, 0, null);
		//		} else {
		//			g.drawImage(blue, 0, 0, null);
		//		}

		//		g.drawImage(normal, (getWidth() - normal.getWidth(null)) / 2, (getHeight() - normal.getHeight(null)) / 2, null);
		if (cpu >= 80) {
			g.drawImage(high, 0, 0, getWidth(), getHeight(), null);
		} else if (cpu >= 60) {
			g.drawImage(middle, 0, 0, getWidth(), getHeight(), null);
		} else {
			g.drawImage(low, 0, 0, getWidth(), getHeight(), null);
		}

		int offsetY = 5;
		if (os == OS.redhat) {
			g.drawImage(redhat, (44 - redhat.getWidth(null)) / 2, (44 - redhat.getHeight(null)) / 2 + offsetY, null);
		} else if (os == OS.ubuntu) {
			g.drawImage(ubuntu, (44 - ubuntu.getWidth(null)) / 2, (44 - ubuntu.getHeight(null)) / 2 + offsetY, null);
		} else if (os == OS.suse) {
			g.drawImage(suse, (44 - suse.getWidth(null)) / 2, (44 - suse.getHeight(null)) / 2 + offsetY, null);
		} else if (os == OS.centos) {
			g.drawImage(centos, (44 - centos.getWidth(null)) / 2, (44 - centos.getHeight(null)) / 2 + offsetY, null);
		} else if (os == OS.windows) {
			g.drawImage(windows, (44 - windows.getWidth(null)) / 2, (44 - windows.getHeight(null)) / 2 + offsetY, null);
		} else {
			g.drawImage(noname, (44 - noname.getWidth(null)) / 2, (44 - noname.getHeight(null)) / 2 + offsetY, null);
		}
		g.drawImage(play, (44 - play.getWidth(null)) / 2 + 45, (44 - play.getHeight(null)) / 2 + offsetY, null);

		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.PLAIN, 24));
		g.drawString(String.valueOf(cpu), 10, 80);
		g.drawString(String.valueOf(new Random().nextInt(100)), 55, 80);
	}
}
