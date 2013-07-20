package com.c2.pandora.imagepanel;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DownloadImageTableCellRenderer implements TableCellRenderer {
	DownloadImagePanel panel = new DownloadImagePanel();
	Color c = new Color(240, 240, 255);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		DownloadImage downloadImage = (DownloadImage) value;
		panel.lblHeader.setText(downloadImage.os);
		panel.textArea.setText(downloadImage.description);
		panel.lblDate.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(downloadImage.uploadDate));
		panel.lblLicense.setText(downloadImage.License + ", " + downloadImage.size);
		panel.lblAuthor.setText(downloadImage.author + " " + downloadImage.authorEmail);
		URL url = getClass().getClassLoader().getResource("com/c2/pandora/image/os/" + downloadImage.osType + ".png");
		if (url != null) {
			ImageIcon icon = new ImageIcon(url);
			panel.logoLabel.setIcon(icon);
		}
		if (isSelected) {
			panel.setBackground(c);
			panel.textArea.setBackground(c);
		} else {
			panel.setBackground(null);
			panel.textArea.setBackground(null);
		}
		return panel;
	}
}
