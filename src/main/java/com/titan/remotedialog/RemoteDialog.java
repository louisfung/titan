package com.titan.remotedialog;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.peter.tightvncpanel.TightVNC;
import com.titan.Global;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.InOut;

public class RemoteDialog extends JDialog implements WindowListener {
	private final JPanel contentPanel = new JPanel();
	JPanel remotePanel = new JPanel();

	public static void main(String[] args) {
		try {
			RemoteDialog dialog = new RemoteDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RemoteDialog() {
		setBounds(100, 100, 711, 545);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			contentPanel.add(remotePanel, BorderLayout.CENTER);
		}

		Global.primaryServerIP = "210.5.164.14:4444";
		final InOut inout = CommunicateLib.requestProxy(TitanCommonLib.getCurrentServerIP(), 5905);

		final SocketServer socketServer = new SocketServer(inout);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new Thread() {
			public void run() {
				System.out.println("vnc connect to " + (inout.port + 1));
				System.out.println();
				TightVNC.initVNCPanel(RemoteDialog.this, remotePanel, "localhost", (inout.port + 1), null);
				//				TightVNC.initVNCPanel(RemoteDialog.this, remotePanel, "210.5.164.14", 5901, null);
			}
		}.start();

	}

	class SocketServer implements Runnable {
		InOut inout;

		public SocketServer(InOut inout) {
			this.inout = inout;
			new Thread(this).start();
		}

		public void run() {
			try {
				ServerSocket ss = new ServerSocket(inout.port + 1);

				final byte[] request = new byte[1024];
				byte[] reply = new byte[1024];

				Socket client = ss.accept();
				final InputStream streamFromServer = client.getInputStream();
				final OutputStream streamToServer = client.getOutputStream();

				final ObjectInputStream streamFromClient = new ObjectInputStream(client.getInputStream());
				final ObjectOutputStream streamToClient = new ObjectOutputStream(client.getOutputStream());

				Thread t = new Thread() {
					public void run() {
						int bytesRead;
						try {
							while ((bytesRead = streamFromClient.read(request)) != -1) {
								streamToServer.write(request, 0, bytesRead);
								streamToServer.flush();
							}
						} catch (IOException e) {
						}

						// the client closed the connection to us, so close our
						// connection to the server.
						try {
							streamToServer.close();
						} catch (IOException e) {
						}
					}
				};
				t.start();

				int bytesRead;
				try {
					while ((bytesRead = streamFromServer.read(reply)) != -1) {
						streamToClient.write(reply, 0, bytesRead);
						streamToClient.flush();
					}
				} catch (IOException e) {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
