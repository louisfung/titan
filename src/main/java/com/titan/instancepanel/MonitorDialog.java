package com.titan.instancepanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.DecimalFormat;

import javax.media.Manager;
import javax.media.Player;
import javax.media.Time;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.peter.tightvncpanel.TightVNC;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titan.communication.ProxySocketServer;
import com.titanserver.Command;
import com.titanserver.InOut;
import com.titanserver.ReturnCommand;
import com.toedter.calendar.JCalendar;

public class MonitorDialog extends JFrame implements WindowListener, Runnable {
	private final JPanel contentPanel = new JPanel();
	JPanel remotePanel = new JPanel();
	JPanel playbackPanel = new JPanel();
	Player player;
	JSlider slider = new JSlider();
	JLabel timeLabel = new JLabel();

	static boolean test;

	public static void main(String args[]) {
		test = true;
		new MonitorDialog("A").setVisible(true);
	}

	public MonitorDialog(String instanceName) {
		setTitle(instanceName);
		setBounds(100, 100, 732, 542);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane, BorderLayout.CENTER);
			tabbedPane.addTab("Remote", null, remotePanel, null);
			{
				tabbedPane.addTab("Playback", null, playbackPanel, null);
				playbackPanel.setLayout(new BorderLayout(0, 0));
				{
					JPanel panel = new JPanel();
					playbackPanel.add(panel, BorderLayout.NORTH);

					JCalendar calendar = new JCalendar();

					slider.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							if (player != null) {
								player.setMediaTime(new Time((double) slider.getValue()));
							}
						}
					});

					timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

					JButton btnPlay = new JButton("Play");
					btnPlay.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (player != null) {
								player.start();
								System.out.println("player.start()");
							}
						}
					});
					btnPlay.setIcon(new ImageIcon(MonitorDialog.class.getResource("/com/titan/image/famfamfam/control_play.png")));

					JButton pauseButton = new JButton("Pause");
					pauseButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (player != null) {
								player.stop();
							}
						}
					});
					pauseButton.setIcon(new ImageIcon(MonitorDialog.class.getResource("/com/titan/image/famfamfam/control_pause.png")));

					JButton screenCaptureButton = new JButton("Screen capture");
					screenCaptureButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
						}
					});
					screenCaptureButton.setIcon(new ImageIcon(MonitorDialog.class.getResource("/com/titan/image/famfamfam/disk.png")));
					GroupLayout gl_panel = new GroupLayout(panel);
					gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
							gl_panel.createSequentialGroup()
									.addContainerGap()
									.addComponent(calendar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(
											gl_panel.createParallelGroup(Alignment.LEADING)
													.addComponent(slider, GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
													.addGroup(
															gl_panel.createSequentialGroup().addGap(156).addComponent(timeLabel, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
																	.addGap(165))
													.addGroup(
															gl_panel.createSequentialGroup().addGap(6)
																	.addComponent(btnPlay, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addComponent(pauseButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addComponent(screenCaptureButton, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)))
									.addContainerGap()));
					gl_panel.setVerticalGroup(gl_panel
							.createParallelGroup(Alignment.TRAILING)
							.addGroup(
									gl_panel.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(calendar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap())
							.addGroup(
									Alignment.LEADING,
									gl_panel.createSequentialGroup()
											.addGap(44)
											.addComponent(timeLabel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(
													gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(btnPlay).addComponent(pauseButton)
															.addComponent(screenCaptureButton)).addContainerGap(49, Short.MAX_VALUE)));
					panel.setLayout(gl_panel);
				}
			}
		}

		if (!test) {
			Command command = new Command();
			command.command = "get vnc port";
			command.parameters.add(instanceName);
			ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
			int port = (Integer) r.map.get("port");
			setTitle(getTitle() + ", port=" + port);
			final InOut inout = CommunicateLib.requestProxy(TitanCommonLib.getCurrentServerIP(), 5900 + port);
			ProxySocketServer p = new ProxySocketServer(inout);
			while (!p.started) {
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			new Thread() {
				public void run() {
					TightVNC.initVNCPanel(MonitorDialog.this, remotePanel, "localhost", (inout.port + 1), null);
				}
			}.start();
		}

		Component c = initVideo(new File("/Users/peter/Movies/1.avi"));
		if (c != null) {
			playbackPanel.add(c, BorderLayout.CENTER);
			System.out.println("added");
		} else {
			System.err.println("Can add video component");
		}

		new Thread(this).start();
	}

	private Component initVideo(File file) {
		Component c = null;
		try {
			player = Manager.createRealizedPlayer(file.toURI().toURL());
			System.out.println("player=" + player);
			c = player.getVisualComponent();
			System.out.println("c=" + c);
			slider.setMaximum((int) player.getDuration().getSeconds());
			slider.setValue(0);
		} catch (Exception e) {
			//			e.printStackTrace();
		}
		return c;
	}

	public void run() {
		while (true) {
			if (player != null) {
				timeLabel.setText(roundTwoDecimals(player.getMediaTime().getSeconds()) + " / " + roundTwoDecimals(player.getDuration().getSeconds()) + " seconds");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.#");
		return Double.valueOf(twoDForm.format(d));
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}
}
