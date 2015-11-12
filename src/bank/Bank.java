package bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import support.Command;

public class Bank {

	public static void main(String[] args) throws IOException {
		int port = 4444;
		String server_address = "localhost";
		
		Socket socket = new Socket(server_address, port);
		
		Scanner user_in = new Scanner(System.in);
		BufferedReader socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter socket_out = new PrintWriter(socket.getOutputStream(), true);
		
		String user_input, user_role;
		Command cmd;
		
		while (true) {
			if (socket_in.ready())
			{
				log("Reading from socket: ", false);
				log(socket_in.readLine() + "\n", false);
				continue;
			} else {
				log(">>>", false);
				user_input = user_in.nextLine();
			}
				
			try {
				cmd = Command.valueOf(user_input);
				socket_out.println(cmd);
			} catch (NullPointerException e) {
				log("Wrong command.", true);
				continue;
			}
			
			switch(cmd) {
			case role:
				log("Enter your role: ", false);
				user_role = user_in.nextLine();
				
				socket_out.println(user_role);
				
				break;
			case usr:
				int no_users = Integer.parseInt(socket_in.readLine());
				
				log("(" + Integer.toString(no_users) + ")", false);
				for (int i = 0; i < no_users; i++) {
					log(socket_in.readLine(), false);
				}
				
				
				break;
			case exit:
				log("Shutting down the connection.", true);
				socket.close();
				user_in.close();
				System.exit(-1);
				
				break;
			default:
				log("Wrong command, try again.", true);	
				break;
			}
		}
	}
	
	private static void log(String msg, boolean new_line) {
		if (new_line)
			System.out.println(">>> " + msg);
		else
			System.out.print(msg + " ");
	}

}
