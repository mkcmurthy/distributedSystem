package com.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceBroker implements Runnable {

	public static LinkedList<ServerDetails> inventory = new LinkedList<ServerDetails>();
	private Socket newSocket;

	public ServiceBroker(Socket newSocket) {

		this.newSocket = newSocket;
	}

	public static void main(String[] args) {

		/*
		 * code for the connection, receive message from server or client will
		 * be here
		 * 
		 */
		try {
			ConnectionModule();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void ConnectionModule() throws IOException {

		InetAddress addr;
		try {
			byte[] ipAddr = new byte[] { 127, 0, 1, 4 };
			addr = InetAddress.getByAddress(ipAddr);
			System.out.println("Service Broker having IP -> " + addr.getHostAddress() + " started");

			ServerSocket ss = new ServerSocket(8706, 40, addr);
			ExecutorService executorService = Executors.newCachedThreadPool();

			int i = 0;
			ServiceBroker[] socketObject = new ServiceBroker[10];
			while (i < 10) {
				Socket s = ss.accept();
				Socket newS = s;
				socketObject[i] = new ServiceBroker(newS);
				executorService.execute(socketObject[i]);
				i += 1;
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String inputStreamOperation(Socket socket) {

		String message = null;
		try {

			BufferedReader brs = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// for test
			System.out.println("reading message from -> " + socket.getInetAddress());
			message = brs.readLine();
			// for test
			System.out.println("Message -> " + message + " received");
			return message;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void outputStreamOperation(Socket socket, String msg) {

		try {
			OutputStream out = socket.getOutputStream();
			byte[] byteMessage = msg.getBytes();
			out.write(byteMessage);
			socket.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		String msg = this.inputStreamOperation(this.newSocket);

		String message = this.serviceDelegation(msg);

		if (!message.equals("null")) {
			this.outputStreamOperation(this.newSocket, message);
		}

	}

	public String serviceDelegation(String message) {

		String[] msgParts = message.split("/");

		// check if the requesting process is client
		if (msgParts[0].equals("C")) {

			// client requesting for Normalised server
			if (msgParts[1].equals("N")) {
				// for test

				String foundServer = getServer("N");
				System.out.println(foundServer + " - Normalised server found");

				return foundServer;

			}

			// client requesting for Encrypt/Decrypt
			else {

				// for test
				String foundServer = getServer("ED");
				System.out.println(foundServer + " Encrypt/Decrypt server found");
				return foundServer;
			}

		}

		if (msgParts[0].equals("S")) {

			if (msgParts[1].equals("R")) {
				int port = Integer.parseInt(msgParts[3]);
				boolean status = registration(msgParts[2], port, msgParts[4]);
				// for test and required

				if (status) {
					System.out.println("server having IP " + msgParts[2] + " is registered");
					return "registered";
				} else
					System.out.println("server having IP " + msgParts[2] + " cannot be registered");
				return "not registered";
			}

			else {
				boolean status = unRegistration(msgParts[2]);
				// for test and required
				if (status) {
					System.out.println("Server having IP " + msgParts[2] + " is unregistered");
					return "unregistered";
				} else {
					System.out.println("Server having IP " + msgParts[2] + " cannot be unregistered");
					return "not unregistered";
				}
			}
		}
		return "null";
	}

	// server registration along with its service
	public synchronized boolean registration(String ipAddress, int port, String service) {

		if (inventory.add(new ServerDetails(ipAddress, service, port))) {
			return true;
		}

		else
			return false;

	}

	// server unregistration
	public synchronized boolean unRegistration(String ipAddress) {

		int i = 0;
		for (ServerDetails e : inventory) {

			if (e.getIpAddress().equals(ipAddress)) {

				inventory.remove(i);
				return true;
			}
			i += 1;

		}

		return false;
	}

	// retrieve server details for client
	public static String getServer(String service) {

		String serverDetailsToClient;
		System.out.println("for Service ->" + service);

		// fetch the requested server details to client
		int i = 0;
		for (ServerDetails e : inventory) {

			if (e.getService().equals(service)) {

				serverDetailsToClient = inventory.get(i).getIpAddress() + "/" + inventory.get(i).getPort();

				return serverDetailsToClient;
			}
			i += 1;

		}
		return null;

	}

}
