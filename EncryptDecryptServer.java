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

public class EncryptDecryptServer implements Runnable {
	
	public static final String myAddress="127/0/0/4";
	public static final String myPort = "8788";
	public Socket newSocket;
	public static Cipher c;
	public KeyGenerator key;
	public SecretKey secretKey;
	public String encryptedMessage;
	public String decryptedMessage;
	
	public EncryptDecryptServer(Socket soc){
		
		this.newSocket = soc;
	}

	public static void main(String[] args) {
		
		
		// registering with the service broker
		Socket soc;
		try {
			soc = new Socket("127.0.1.4", 8797);
			 InputStream inc = soc.getInputStream(); 
			 OutputStream outc = soc.getOutputStream();
			 
			 String[] ip = myAddress.split("/"); 
			 
			 String str = "S/R/"+ip[0]+"."+ip[1]+"."+ip[2]+"."+ip[3]+"/"+myPort+"/N";
			 byte[] bt = str.getBytes(); 
			 outc.write(bt); 
			 soc.close();
			// end of registration
			
			
			//** begin client requests 
			ConnectionModule();
						
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				

	}
	
public static void ConnectionModule() throws IOException{
		
		InetAddress addr;
		try {
			//convert IP address from string to int for InetAddress
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
			
			
			int i=0;
			EncryptDecryptServer[] socketObject = new EncryptDecryptServer[10];
			while (i<2){
			Socket s = ss.accept();
			Socket newS = s;
			 socketObject[i]= new EncryptDecryptServer(newS);
			executorService.execute(socketObject[i]);
			i+=1;
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String inputStreamOperation(Socket socket){
		
		String message = null;
		try {
			
			BufferedReader brs = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//for test
			System.out.println("reading message");
			message = brs.readLine();
			// for test
			System.out.println(message + "received");
			return message;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void outputStreamOperation(Socket socket, String msg){
		
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
		String[] clientRequest = message.split("-");
		
		try {
			
			this.key = KeyGenerator.getInstance("AES");
			this.key.init(128);
			this.secretKey = key.generateKey();
			c = Cipher.getInstance("AES");
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(clientRequest[0].equals("E")){
			
			
			try {
				this.encryptedMessage = encrypt(clientRequest[1], this.secretKey) + "\n";
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.outputStreamOperation(this.newSocket, this.encryptedMessage);
			
		}
		
		else
		{
		try {
			this.decryptedMessage = decrypt(clientRequest[1], this.secretKey) + "\n";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.outputStreamOperation(this.newSocket, this.decryptedMessage);
		}
		
		
		
		
	}
	
	public static synchronized String encrypt(String msg , SecretKey secretKey) throws Exception {
		byte[] Byte = msg.getBytes();
		c.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedByte = c.doFinal(Byte);
		Base64.Encoder encoder = Base64.getEncoder();
		String encryption = encoder.encodeToString(encryptedByte);
		return encryption;
	}
	
	public static synchronized String decrypt(String msg, SecretKey secretKey)throws Exception {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(msg);
		c.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedByte = c.doFinal(encryptedTextByte);
		String decryption = new String(decryptedByte);
		return decryption;
	}


}
