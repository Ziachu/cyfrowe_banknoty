package bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import listeners.CommandListener;
import support.Command;
import support.Series;

public class Bank extends Thread {
	private int port;
	private String server_address;
	private String user_role;
	
	private Socket socket;
	
	private BufferedReader socket_in;
	private PrintWriter socket_out;
	private CommandListener cmd_listener;

	private boolean waiting_for_next_input;
	private Command last_command;
	
	public Bank(String server_address, int port) {
		
		this.port = port;
		this.server_address = server_address;
		user_role = "";
		waiting_for_next_input = false;
	}
	
	public void setCommandListener(CommandListener listener) {
		this.cmd_listener = listener;
	}
	
	public void run() {
		
		try {
			this.socket = new Socket(this.server_address, this.port);
			
			socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socket_out = new PrintWriter(socket.getOutputStream(), true);
			
			new ListenerThread(socket_in, cmd_listener).start();
			
			cmd_listener.CommandEmitted("[bnk] Bank up and running.");

			// Bank loguje się na serwerze
			
			user_role = "Bank";
			socket_out.println(Command.valueOf("role"));
			socket_out.println(user_role);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void manageUserInput(String user_input) {
		Command cmd;
		
		if (!waiting_for_next_input) {
			try {
				cmd = Command.valueOf(user_input);
				System.out.println(user_input);
				socket_out.println(cmd);
				
				switch(cmd) {
				case role:
					if (user_role.isEmpty()) {
						cmd_listener.CommandEmitted("[bnk] Enter your role:");
						last_command = cmd;
						waiting_for_next_input = true;							
					} else {
						//wysyłam pustą linijkę, żeby serwer nie rejestrował kolejnego użytkownika z tego samego gniazdka
						socket_out.println("");
						last_command = cmd;
						waiting_for_next_input = false;
					}
						
					break;
				case usr:
					// stąd jedynie wysyłam polecnie do serwera, inny wątek nasłuchuje odpowiedzi
					last_command = cmd;
					waiting_for_next_input = false;	
					
					break;
				case exit:
					cmd_listener.CommandEmitted("[bnk] Shutting down the connection.");
						
					try {
						socket.close();
					} catch (IOException e1) { e1.printStackTrace(); }

					last_command = cmd;
					waiting_for_next_input = false;
					
					//TODO: Shut down the connection;
						
					break;
				case series:
					last_command = cmd;
					waiting_for_next_input = true;
					socket_out.println("Bob");
						
					Series series = new Series(50);
					try {
						series.sendSeries(socket_out);
					} catch (UnsupportedEncodingException e) { e.printStackTrace(); }
						
					series.visualizeSeries();
						
					break;
				case banknote:
						
					break;
				default:
					cmd_listener.CommandEmitted("[bnk] Wrong command, try again.");	
						
					break;
				}
			} catch (IllegalArgumentException e) {
				cmd_listener.CommandEmitted("[bnk] Wrong command.");
			}
		} else {
			switch (last_command) {
			case role:
				user_role = user_input;
				socket_out.println(user_role);
					
				break;
			case series:
				String receiver = user_input;
				socket_out.println(receiver);
					
				Series series = new Series(50);
				try {
					series.sendSeries(socket_out);
				} catch (UnsupportedEncodingException e) { e.printStackTrace(); }
					
				series.visualizeSeries();
					
				break;
			case banknote:
					
				break;
			default:
				cmd_listener.CommandEmitted("[bnk] Wrong command, try again.");	
					
				break;
			}
		}
	}
}
	
//	public static void main(String[] args) throws IOException, InterruptedException {
//		
//		Scanner user_in = new Scanner(System.in);
//		BufferedReader socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		PrintWriter socket_out = new PrintWriter(socket.getOutputStream(), true);
//		
//		new ListenerThread(socket_in).start();
		
//		String user_input;
//		String user_role = "";
//		Command cmd;
//		
//		while (true) {
//			Thread.sleep(100);
//			Log.inp();
//			user_input = user_in.nextLine();
//				
//			try {
//				cmd = Command.valueOf(user_input);
//				socket_out.println(cmd);
//			} catch (IllegalArgumentException e) {
//				Log.log("Wrong command.", true);
//				continue;
//			}
//			
//			switch(cmd) {
//			case role:
//				if (user_role.isEmpty()) {
//					Log.inp("Enter your role:");
//					user_role = user_in.nextLine();
//					
//					socket_out.println(user_role);
//				} else {
//					//wysyłam pustą linijkę, żeby serwer nie rejestrował kolejnego użytkownika z tego samego gniazdka
//					socket_out.println("");
//				}
//				
//				break;
//			case usr:
//				// stąd jedynie wysyłam polecnie do serwera, inny wątek nasłuchuje odpowiedzi
//				
//				break;
//			case exit:
//				Log.log("Shutting down the connection.", true);
//				socket.close();
//				user_in.close();
//				System.exit(-1);
//				
//				break;
//			case series:
//				socket_out.println("Bob");
//				
//				Series series = new Series(50);
//				series.sendSeries(socket_out);
//				series.visualizeSeries();
//				
//				break;
//			case banknote:
//				
//				break;
//			default:
//				Log.log("Wrong command, try again.", true);	
//				break;
//			}
//		}
//	}
	
class ListenerThread extends Thread {
	BufferedReader socket_in;
	Command cmd;
	String user_input;
	CommandListener cmd_listener;
		
	ListenerThread(BufferedReader socket_in, CommandListener cmd_listener) {
		this.socket_in = socket_in;
		this.cmd_listener = cmd_listener;
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
								
							String response = "(" + Integer.toString(no_users) + ") ";
							for (int i = 0; i < no_users; i++) {
								response += socket_in.readLine() + " ";
							}
								
							cmd_listener.CommandEmitted("[bnk] " + response);
								
							break;
						case series:								
							Series series = new Series();
							series.receiveSeries(socket_in);
								
							cmd_listener.CommandEmitted("[bnk] Series came from server:");
							series.visualizeSeries();
								
							break;
						default:
							cmd_listener.CommandEmitted("[bnk] Wrong command.");
							break;
						}
					} catch (IllegalArgumentException e) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
							
						cmd_listener.CommandEmitted("[bnk] " + user_input);
						continue;
					}
				}
			} catch (IOException e) {
				cmd_listener.CommandEmitted("[bnk] IO problem: Bank in ListenerThread's run() method");
				e.printStackTrace();
			}
		}
	}
}
