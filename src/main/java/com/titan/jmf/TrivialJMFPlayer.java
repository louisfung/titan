package com.c2.pandora.jmf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;

import javax.media.Manager;
import javax.media.Player;
import javax.media.Time;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

public class TrivialJMFPlayer extends JFrame {
	JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);

	// ffmpeg -i jiyeon.mp4 -vcodec mjpeg  -qscale 1 -an  jiyeon.mov
	public static void main(String[] args) {
		try {
			TrivialJMFPlayer f = new TrivialJMFPlayer();
			f.setSize(800, 600);
			f.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TrivialJMFPlayer() throws java.io.IOException, java.net.MalformedURLException, javax.media.MediaException {
		getContentPane().setLayout(new BorderLayout(0, 0));

		FileDialog fd = new FileDialog(this, "TrivialJMFPlayer", FileDialog.LOAD);
		fd.setVisible(true);
		File f = new File(fd.getDirectory(), fd.getFile());

		final Player p = Manager.createRealizedPlayer(f.toURI().toURL());
		Component c = p.getVisualComponent();
		getContentPane().add(c, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.stop();
			}
		});

		JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.start();
			}
		});

		scrollBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				System.out.println("set" + scrollBar.getValue());
				p.setMediaTime(new Time((double) scrollBar.getValue()));
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup().addGap(16).addComponent(playButton).addGap(4).addComponent(btnPause).addGap(4)
						.addComponent(scrollBar, GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE).addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addGap(16).addComponent(playButton))
				.addGroup(gl_panel.createSequentialGroup().addGap(16).addComponent(btnPause))
				.addGroup(gl_panel.createSequentialGroup().addGap(23).addComponent(scrollBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		panel.setLayout(gl_panel);

		scrollBar.setMaximum((int) p.getDuration().getSeconds());
		System.out.println(p.getDuration().getSeconds());

		p.start();
	}
}
