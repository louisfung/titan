package com.titan.dashboard;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.titan.OS;

public class VMLabel extends JLabel {
	OS os;
	Icon icon = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/vmIcon.png"));
	Image redhat = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/redhat.png")).getImage();
	Image ubuntu = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/ubuntu.png")).getImage();
	Image suse = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/suse.png")).getImage();
	Image centos = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/centos.png")).getImage();
	Image windows = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/vmPanel/os/windows.png")).getImage();

	public VMLabel() {
		this(OS.none);
	}

	public VMLabel(OS os) {
		super();
		setIcon(icon);
		setOS(os);
	}

	public void setOS(OS os) {
		this.os = os;
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (os == OS.redhat) {
			g.drawImage(redhat, (44 - redhat.getWidth(null)) / 2, (44 - redhat.getHeight(null)) / 2, null);
		} else if (os == OS.ubuntu) {
			g.drawImage(ubuntu, (44 - ubuntu.getWidth(null)) / 2, (44 - ubuntu.getHeight(null)) / 2, null);
		} else if (os == OS.suse) {
			g.drawImage(suse, (44 - suse.getWidth(null)) / 2, (44 - suse.getHeight(null)) / 2, null);
		} else if (os == OS.centos) {
			g.drawImage(centos, (44 - centos.getWidth(null)) / 2, (44 - centos.getHeight(null)) / 2, null);
		} else if (os == OS.windows) {
			g.drawImage(windows, (44 - windows.getWidth(null)) / 2, (44 - windows.getHeight(null)) / 2, null);
		}
	}
}
