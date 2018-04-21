package com.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class client_test {

	public static String Server_Lookup(String a) throws UnknownHostException, IOException {
		String A = a;
		Socket s = new Socket("127.0.1.4", 8706);
		Scanner sc4 = new Scanner(s.getInputStream());
		System.out.println("for server look up");
		PrintStream p = new PrintStream(s.getOutputStream());
		p.println(A);
		String B = sc4.nextLine();
		System.out.println(B);
		return B;

	}

	public static void Normalizer_Process() throws UnknownHostException, IOException {

		int choice = 0;
		while (choice != 3) {
			System.out.println("option 1: to convert in upper string\n");
			System.out.println("option 2: to convert in lower string\n");
			System.out.println("option 3: quit enter 3\n");
			Scanner sc2 = new Scanner(System.in);
			choice = sc2.nextInt();
			switch (choice) {
			case 1:
				System.out.println("Enter string to convert in Upper Case");
				Scanner s2 = new Scanner(System.in);
				String a = s2.nextLine();
				a = a + "\n";
				System.out.println(a + " sent");
				String s1 = Server_Lookup("C/N\n");
				String[] parts = s1.split("/");
				String part1 = parts[0];
				int part2 = Integer.parseInt(parts[1]);
				Socket s = new Socket(part1, part2);// server address
				Scanner sc1 = new Scanner(s.getInputStream());
				PrintStream p = new PrintStream(s.getOutputStream());
				p.println(a);
				String temp = sc1.nextLine();
				System.out.println(temp);
				break;

			case 2:
				System.out.println("Enter string to convert in lower Case");
				Scanner s3 = new Scanner(System.in);
				String a1 = s3.nextLine();
				a = a1 + "\n";
				System.out.println(a + " sent");
				String ss = Server_Lookup("C/N\n");
				String[] parts1 = ss.split("/");
				String part11 = parts1[0];
				int part21 = Integer.parseInt(parts1[1]);
				Socket sss = new Socket(part11, part21);// server address
				Scanner sc11 = new Scanner(sss.getInputStream());
				PrintStream pp = new PrintStream(sss.getOutputStream());
				pp.println(a);
				String temp1 = sc11.nextLine();
				System.out.println(temp1);
				break;

			default:
				System.out.println("You Choose to quit");
				break;

			}

		}
	}

	public static void ED_Process() throws UnknownHostException, IOException {
		int choice = 0;
		Scanner s2 = new Scanner(System.in);
		String s1 = Server_Lookup("C/ED\n");
		String[] parts = s1.split("/");
		String part1 = parts[0];
		int part2 = Integer.parseInt(parts[1]);
		Socket s = new Socket(part1, part2);

		PrintStream p = new PrintStream(s.getOutputStream());
		BufferedReader brs = new BufferedReader(new InputStreamReader(s.getInputStream()));
		Scanner sMessage1 = new Scanner(System.in);
		Scanner sMessage2 = new Scanner(System.in);

		while (choice != 3) {
			System.out.println("option 1: For Encryption\n");
			System.out.println("option 2: For Decryption\n");
			System.out.println("option 3: quit enter 3\n");

			choice = s2.nextInt();

			System.out.println("printing the choice ->" + choice);
			switch (choice) {

			case 1:

				System.out.println("Enter string to Encrypt");

				String a = sMessage1.nextLine();

				System.out.println("Encrypting..");
				p.println(a);
				String temp = brs.readLine();
				System.out.println(temp);

				break;
			case 2:
				System.out.println("Enter string to Decrypt");

				String a1 = sMessage2.nextLine();

				System.out.println("message read ->" + a1);
				p.println(a1);
				String temp1 = brs.readLine();
				System.out.println(temp1);

				break;
			default:
				System.out.println("You Choose to quit");
				p.println("exit");
				s2.close();
				sMessage1.close();
				sMessage2.close();
				s.close();
				break;

			}
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub

		int choice;
		System.out.println("option 1: normalizer Server enter C/N\n");
		System.out.println("option 2: encryption/decryption Server C/ED\n");
		System.out.println("option 3: quit enter 3\n");
		Scanner sc2 = new Scanner(System.in);
		choice = sc2.nextInt();
		switch (choice) {
		case 1:
			// Server_Lookup("C/N\n");
			Normalizer_Process();
			break;

		case 2:
			// Server_Lookup("C/ED\n");
			ED_Process();
			break;

		default:
			System.out.println("You Choose to quit");
			break;

		}

	}

}
