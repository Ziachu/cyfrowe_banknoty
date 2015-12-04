package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Alice.AliceCommandManager;
import Support.Command;
import Support.CommandManager;
import Support.Loger;
import Support.Role;
import Support.Series;

public class SystemUser {

	private static int port;
	private static String server_address;
	private static Role user_role;
	
	private static Socket socket;
	private static Scanner user_in;
	private static BufferedReader socket_in;
	private static PrintWriter socket_out;
	
	private static ServerResponseListener server_response_listener;
	private static CommandManager manager;
	
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
			Loger.println("\t[err] Unknown server address.\n\t" + e.getMessage());
		} catch (IOException e) {
			Loger.println("\t[err] Couldn't establish connection.\n\t" + e.getMessage());
		}
	}
	
	private static void managerUserInput() {
		Loger.print("[usr] ");
		String cmd = user_in.nextLine();
		manager.respondToCommand(cmd);
	}

	private static void setCommandManager() {
		
		server_response_listener = new ServerResponseListener(socket_in);
		server_response_listener.start();
		
		switch(user_role) {
		case Alice:
			manager = new AliceCommandManager();
			manager.setCommandLine(socket_in, socket_out);
			
			manager.setCommand(Command.role);
			manager.respondToCommand(user_role.toString());
			break;
		default:
			Loger.println("\t[err] This error shouldn't occur, what's wrong with " + user_role + " role?");
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
				Loger.println("\t[err] Wrong role applied.");
			}			
		}
	}
}


// ServerResponseListener to wątek działajacy w tle, nasłuchujący "niespodziewanych"
// wiadomości ze strony serwera. Przykładowa sytuacja:
// 		Bob przesyła ciąg do Alice
// 		Alice w danym momencie wykonuje inne zadanie
//		Dzięki działającemu w tle ServerResponseListener serwer po prostu
//		przesyła otrzymany od Bob'a ciąg 
//		A u Alice jest on wypisywany ;)


class ServerResponseListener extends Thread {
	BufferedReader socket_in;
	Command cmd;
	String user_input;
		
	ServerResponseListener(BufferedReader socket_in) {
		this.socket_in = socket_in;
	}
		
	public void run() {
		while (true) {
			try {
				if (socket_in.ready()) {
					user_input = socket_in.readLine();
						
					try {
						cmd = Command.valueOf(user_input);
							
						switch(cmd) {
						case usr:
							
							displayServerResponseToUsrCommand();
								
							break;
						case series:								
							
							displayServerResponseToSeriesCommand();
								
							break;
						default:
							
							Loger.println("[srv] Wrong command.");
							break;
						}
					} catch (IllegalArgumentException e) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
							
						continue;
					}
				}
			} catch (IOException e) {
				Loger.println("[srv] IO problem: SystemUser in ListenerThread's run() method");
				e.printStackTrace();
			}
		}
	}

	private void displayServerResponseToSeriesCommand() {
		Series series = new Series();
		series.receiveSeries(socket_in);
			
		Loger.println("[srv] Series came from server:");
		series.visualizeSeries();
	}

	private void displayServerResponseToUsrCommand() throws NumberFormatException, IOException {
		int no_users = Integer.parseInt(socket_in.readLine());
		
		String response = "(" + Integer.toString(no_users) + ") ";
		for (int i = 0; i < no_users; i++) {
			response += socket_in.readLine() + " ";
		}
			
		Loger.println("[srv] " + response);
	}
}
