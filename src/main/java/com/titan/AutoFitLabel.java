package com.titan;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class AutoFitLabel extends JLabel {
	protected void paintComponent(Graphics g) {
		ImageIcon icon = (ImageIcon) getIcon();
		if (icon == null) {
			return;
		}
		int iconWidth = icon.getIconWidth();
		int iconHeight = icon.getIconHeight();
		double iconAspect = (double) iconHeight / iconWidth;

		int w = getWidth();
		int h = getHeight();
		double canvasAspect = (double) h / w;

		int x = 0, y = 0;

		// Maintain aspect ratio.
		if (iconAspect < canvasAspect) {
			// Drawing space is taller than image.
			y = h;
			h = (int) (w * iconAspect);
			y = (y - h) / 2; // center it along vertical
		} else {
			// Drawing space is wider than image.
			x = w;
			w = (int) (h / iconAspect);
			x = (x - w) / 2; // center it along horizontal
		}

		Image img = icon.getImage();
		g.drawImage(img, x, y, w + x, h + y, 0, 0, iconWidth, iconHeight, null);
	}
}
