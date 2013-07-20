package com.c2.pandora.communication;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.swing.JProgressBar;

public class UploadThread implements Runnable {
	public boolean isStop;
	ObjectOutputStream outputStream;
	File file;
	public JProgressBar jProgressBar;

	public UploadThread(ObjectOutputStream outputStream, File file) {
		this.outputStream = outputStream;
		this.file = file;
	}

	public void run() {
		try {
			FileInputStream fileStream = new FileInputStream(file);
			long fileSize = file.length();
			long completed = 0;
			byte[] buffer = new byte[1024];

			jProgressBar.setMaximum(100);
			jProgressBar.setMinimum(0);
			jProgressBar.setStringPainted(true);

			outputStream.writeLong(fileSize);
			long last = new Date().getTime();
			long lastCompleted = 0;
			long now;
			int speed = 0;
			while (completed < fileSize) {
				int x = fileStream.read(buffer);
				outputStream.write(buffer, 0, x);
				outputStream.flush();
				completed += x;
				jProgressBar.setValue((int) (completed * 100 / fileSize));
				now = new Date().getTime();

				double dt = (now - last);
				dt = dt / 1000;
				if (dt > 0) {
					speed = (int) ((completed - lastCompleted) / dt);
				}
				//				jProgressBar.setString("Sending " + file.getName() + " " + (completed * 100 / fileSize) + "% " + String.format("%20s", CommonLib.convertFilesize(speed) + "/s"));
				jProgressBar.setString("Sending " + file.getName() + " " + (completed * 100 / fileSize) + "% ");

				lastCompleted = completed;
				last = now;
			}
			fileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
