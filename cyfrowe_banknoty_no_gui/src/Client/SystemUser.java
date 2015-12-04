package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Alice.AliceCommandManager;
import Support.CommandManager;
import Support.Loger;
import Support.Role;

public class SystemUser {

	private static int port;
	private static String server_address;
	private static Role user_role;
	
	private static Socket socket;
	private static Scanner user_in;
	private static BufferedReader socket_in;
	private static PrintWriter socket_out;
	
	private static CommandManager manager;
	
//	public Alice(String server_address, int port) {
//		
//		this.port = port;
//		this.server_address = server_address;
//	}
	
	public static void main(String args[]) {
		
		port = 4444;
		server_address = "localhost";
		
		try {
			socket = new Socket(server_address, port);
			socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socket_out = new PrintWriter(socket.getOutputStream(), true);
			user_in = new Scanner(System.in);
			
			Loger.println("[info] Connection with " + server_address + ":" + port + " established!");

			getUserRole();
			setCommandManager();

			while (true) {
				managerUserInput();
			}
			
		} catch (UnknownHostException e) {
			Loger.println("[err] Unknown server address.\n\t" + e.getMessage());
		} catch (IOException e) {
			Loger.println("[err] Couldn't establish connection.\n\t" + e.getMessage());
		}
	}
	
	private static void managerUserInput() {
		Loger.print("[usr] ");
		String cmd = user_in.nextLine();
		manager.respondToCommand(cmd);
	}

	private static void setCommandManager() {
		switch(user_role) {
		case Alice:
			manager = new AliceCommandManager();
			break;
		default:
			Loger.println("[err] This error shouldn't occur, what's wrong with " + user_role + " role?");
			break;
		}
	}

	public static void getUserRole() {
		boolean user_role_applied = false;
		while (!user_role_applied) {
			try {
				Loger.print("Enter Your role: ");
				user_role = Role.valueOf(user_in.nextLine());
				
				user_role_applied = true;
			} catch (IllegalArgumentException e) {
				Loger.println("[err] Wrong role applied.");
			}			
		}
	}
	
}
