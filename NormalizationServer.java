package com.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class NormalizationServer implements Runnable {

	public static String myAddress = "127/0/0/5";
	public static String myPort = "8789";
	public Socket newSocket;
	public static Cipher c;
	public KeyGenerator key;
	public SecretKey secretKey;
	public String encryptedMessage;
	public String message1;

	public NormalizationServer(Socket soc) {

		this.newSocket = soc;
	}

	public static void main(String[] args) {

		// registering with the service broker
		Socket soc;
		try {
			
			soc = new Socket("127.0.1.4", 8788);
			 InputStream inc = soc.getInputStream(); OutputStream outc = soc.getOutputStream();
			  
			 String str = "S/R/"+myAddress+"/"+myPort+"/ED \n"; byte[] bt =
			 str.getBytes(); outc.write(bt); soc.close();
			 
			// end of registration

			// ** begin client requests
			ConnectionModule();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void ConnectionModule() throws IOException {

		InetAddress addr;
		try {
			// convert IP address from string to int for InetAddress
			System.out.println(myAddress);
			String[] ipAddress = myAddress.split("/");

			int ip1 = Integer.parseUnsignedInt(ipAddress[0]);

			int ip2 = Integer.parseUnsignedInt(ipAddress[1]);
			int ip3 = Integer.parseUnsignedInt(ipAddress[2]);
			int ip4 = Integer.parseUnsignedInt(ipAddress[3]);

			byte[] ipAddr = new byte[] { (byte) ip1, (byte) ip2, (byte) ip3, (byte) ip4 };

			addr = InetAddress.getByAddress(ipAddr);
			System.out.println(addr.getHostAddress());

			ServerSocket ss = new ServerSocket(Integer.parseUnsignedInt(myPort), 0, addr);
			ExecutorService executorService = Executors.newCachedThreadPool();

			int i = 0;
			NormalizationServer[] socketObject = new NormalizationServer[10];
			while (i < 2) {
				Socket s = ss.accept();
				Socket newS = s;
				socketObject[i] = new NormalizationServer(newS);
				executorService.execute(socketObject[i]);
				i += 1;
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String inputStreamOperation(Socket socket) {

		try {

			BufferedReader brs = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// for test
			System.out.println("reading message from client ->"+socket.getInetAddress());
			String message = brs.readLine();
			// for test
			System.out.println(message + " received");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		String message = this.inputStreamOperation(this.newSocket);
		
		String[] msgParts = message.split("-");
		
		if (msgParts[0].equals("U")){
			
			String upperCase = this.upper(msgParts[1]);
			this.outputStreamOperation(this.newSocket, upperCase);
		}
		
		else{
			
			String lowerCase = this.lower(msgParts[1]);
			this.outputStreamOperation(this.newSocket, lowerCase);
		}
		
	}

	public String upper(String msg) {

		String b = msg.toUpperCase();
		System.out.println(b);
		return b;

	}

	public String lower(String msg) {

		String b = msg.toLowerCase();
		System.out.println(b);
		return b;

	}

}
