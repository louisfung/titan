package com.titan.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.titanserver.InOut;

public class ProxySocketServer implements Runnable {
	InOut inout;
	public boolean started;

	public ProxySocketServer(InOut inout) {
		this.inout = inout;
		new Thread(this).start();
	}

	public void run() {
		try {
			ServerSocket ss = new ServerSocket(inout.port + 1);
			started = true;
			Socket client = ss.accept();
			Socket server;

			final InputStream streamFromClient = client.getInputStream();
			final OutputStream streamToClient = client.getOutputStream();

			final ObjectOutputStream streamToServer = inout.out;
			final ObjectInputStream streamFromServer = inout.in;

			Thread t = new Thread() {
				public void run() {
					int bytesRead;
					try {
						byte bb[] = new byte[1024];
						while ((bytesRead = streamFromClient.read(bb)) > 0) {
							streamToServer.write(bb, 0, bytesRead);
							streamToServer.flush();
						}
					} catch (IOException e) {
					}

					try {
						streamToServer.close();
					} catch (IOException e) {
					}
				}
			};

			t.start();

			int bytesRead;
			try {
				byte bb[] = new byte[1024];
				while ((bytesRead = streamFromServer.read(bb)) > 0) {
					streamToClient.write(bb, 0, bytesRead);
					streamToClient.flush();
				}
			} catch (IOException e) {
			}
			streamToClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
