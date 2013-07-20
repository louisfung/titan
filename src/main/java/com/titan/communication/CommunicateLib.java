package com.c2.pandora.communication;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;

import javax.net.SocketFactory;
import javax.swing.JDialog;

import com.c2.pandora.Global;
import com.c2.pandoraserver.Command;
import com.c2.pandoraserver.InOut;
import com.c2.pandoraserver.ReturnCommand;
import com.peterswing.advancedswing.jprogressbardialog.JProgressBarDialog;

public class CommunicateLib {
	public static ReturnCommand send(Command command) {
		return send(command, null, null);
	}

	public static ReturnCommand send(String remoteAddr, Command command) {
		if (remoteAddr.contains(":")) {
			return send(remoteAddr.split(":")[0], Integer.parseInt(remoteAddr.split(":")[1]), command);
		} else {
			return send(remoteAddr, 4444, command);
		}
	}

	public static ReturnCommand send(String ip, int port, Command command) {
		return send(ip, port, command, null, null);
	}

	public static ReturnCommand send(Command command, File file, JDialog dialog) {
		if (Global.primaryServerIP.contains(":")) {
			return send(Global.primaryServerIP.split(":")[0], Integer.parseInt(Global.primaryServerIP.split(":")[1]), command, file, dialog);
		} else {
			return send(Global.primaryServerIP, 4444, command, file, dialog);
		}
	}

	public static ReturnCommand send(String remoteAddr, Command command, File file, JDialog dialog) {
		if (remoteAddr.contains(":")) {
			return send(remoteAddr.split(":")[0], Integer.parseInt(remoteAddr.split(":")[1]), command, file, dialog);
		} else {
			return send(remoteAddr, 4444, command, file, dialog);
		}
	}

	public static ReturnCommand send(String ip, int port, Command command, File file, JDialog dialog) {
		ObjectInputStream in = null;
		Socket socket = null;
		SocketAddress remoteaddr;
		try {
			socket = SocketFactory.getDefault().createSocket();

			remoteaddr = new InetSocketAddress(ip, port);
			socket.connect(remoteaddr, 3000);

			final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());

			command.date = new Date();
			if (Command.id == -1) {
				Command.id = (int) (Math.random() * 1000000);
			}
			if (file != null) {
				command.filename = file.getName();
			}
			out.writeObject(command);
			out.flush();
			if (file != null) {
				UploadThread t1 = new UploadThread(out, file);
				Thread t = new Thread(t1);

				JProgressBarDialog d = new JProgressBarDialog(dialog, true);
				t1.jProgressBar = d.jProgressBar;
				d.thread = t;
				d.setVisible(true);
			}

			ReturnCommand r = (ReturnCommand) in.readObject();
			return r;
		} catch (Exception e) {
			System.out.println("Can't send to " + ip + ":" + port);
			return null;
		}
	}

	public static InOut requestProxy(String remoteAddr, int port) {
		Command command = new Command();
		command.command = "proxy";
		command.parameters.add(port);
		Socket socket = null;
		SocketAddress remoteaddr;
		try {
			socket = SocketFactory.getDefault().createSocket();

			if (remoteAddr.contains(":")) {
				remoteaddr = new InetSocketAddress(remoteAddr.split(":")[0], Integer.parseInt(remoteAddr.split(":")[1]));
			} else {
				remoteaddr = new InetSocketAddress(remoteAddr, 4444);
			}
			socket.connect(remoteaddr, 3000);

			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			command.date = new Date();
			out.writeObject(command);
			out.flush();
			return new InOut(in, out, socket.getLocalPort());
		} catch (Exception e) {
			System.out.println("Can't send to " + remoteAddr + ":" + port);
			return null;
		}
	}
}
