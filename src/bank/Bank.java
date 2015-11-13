package bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import support.Command;
import support.Log;
import support.Series;

public class Bank {

	public static void main(String[] args) throws IOException, InterruptedException {
		int port = 4444;
		String server_address = "localhost";
		
		Socket socket = new Socket(server_address, port);
		
		Scanner user_in = new Scanner(System.in);
		BufferedReader socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter socket_out = new PrintWriter(socket.getOutputStream(), true);
		
		new ListenerThread(socket_in).start();
		
		String user_input;
		String user_role = "";
		Command cmd;
		
		while (true) {
			Thread.sleep(100);
			Log.inp();
			user_input = user_in.nextLine();
				
			try {
				cmd = Command.valueOf(user_input);
				socket_out.println(cmd);
			} catch (IllegalArgumentException e) {
				Log.log("Wrong command.", true);
				continue;
			}
			
			switch(cmd) {
			case role:
				if (user_role.isEmpty()) {
					Log.inp("Enter your role:");
					user_role = user_in.nextLine();
					
					socket_out.println(user_role);
				} else {
					//wysyłam pustą linijkę, żeby serwer nie rejestrował kolejnego użytkownika z tego samego gniazdka
					socket_out.println("");
				}
				
				break;
			case usr:
				// stąd jedynie wysyłam polecnie do serwera, inny wątek nasłuchuje odpowiedzi
				
				break;
			case exit:
				Log.log("Shutting down the connection.", true);
				socket.close();
				user_in.close();
				System.exit(-1);
				
				break;
			case series:
				socket_out.println("Bob");
				
				Series series = new Series(50);
				series.sendSeries(socket_out);
				series.visualizeSeries();
				
				break;
			case banknote:
				
				break;
			default:
				Log.log("Wrong command, try again.", true);	
				break;
			}
		}
	}
	
	private static class ListenerThread extends Thread {
		BufferedReader socket_in;
		Command cmd;
		String user_input;
		
		ListenerThread(BufferedReader socket_in) {
			this.socket_in = socket_in;
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					if (socket_in.ready()) {
						user_input = socket_in.readLine();
						
						try {
							cmd = Command.valueOf(user_input);
							
							switch(cmd) {
							case usr:
								int no_users = Integer.parseInt(socket_in.readLine());
								
								Log.log("(" + Integer.toString(no_users) + ") ", false);
								for (int i = 0; i < no_users; i++) {
									Log.app(socket_in.readLine());
								}
								
								Log.log();
								
								break;
							case series:								
								Series series = new Series();
								series.receiveSeries(socket_in);
								
								Log.log("Series came from server:");
								series.visualizeSeries();
								
								break;
							default:
								Log.err("Wrong command.");
								break;
							}
						} catch (IllegalArgumentException e) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
							
							Log.log(user_input);
							continue;
						}
					}
				} catch (IOException e) {
					Log.log("IO problem: Bank in ListenerThread's run() method", true);
					e.printStackTrace();
				}
			}
		}
	}
}
