package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import Support.Command;
import Support.Loger;
import Support.Pair;

public class Server {

	private static int port;
	private static HashMap<String, Pair<BufferedReader, PrintWriter>> users;
	private static ServerSocket server_socket_listener;
	
	public static void main(String args[]) {
		
		Loger.println("cześć Michał!");
		
		port = 4444;

		try {
			server_socket_listener = new ServerSocket(port);
			users = new HashMap<String, Pair<BufferedReader, PrintWriter>>();
			
			Loger.println("[info] Listening at " + port + "...");			

			try {
				while (true) {
					new BackgroundServer(server_socket_listener.accept()).start();
				}
			} finally {
				server_socket_listener.close();
			}
			
		} catch (IOException e) {
			Loger.println("[err] Couldn't create server socket.\n\t" + e.getMessage());
		}
		
		
	}

	private static class BackgroundServer extends Thread{
		private Socket socket;
		private BufferedReader socket_in;
		private PrintWriter socket_out;
		private Pair<BufferedReader, PrintWriter> io_pair;
		
		private String user_role;
		
		public BackgroundServer(Socket socket) {
			this.socket = socket;
			
			try {
				socket_in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				socket_out = new PrintWriter(this.socket.getOutputStream(), true);
				io_pair = new Pair<BufferedReader, PrintWriter>(socket_in, socket_out);
			} catch (IOException e) {
				Loger.println("[err] Couldn't obtain socket i/o.\n\t" + e.getMessage());
			}
			
			
		}
		
		public void run() {
			Loger.println("[info] Connection with " + socket.getInetAddress() + ":" + socket.getPort() + " established.");
		
			String user_input;
			Command cmd;

			while (!socket.isClosed()) {

				try {
					user_input = socket_in.readLine();				
					cmd = Command.valueOf(user_input);
					
				} catch (NullPointerException e) {
					Loger.println("[err] Unknown command from " + user_role + ".");
					continue;
				} catch (IOException e) {
					Loger.println("[err] Couldn't read from socket.\n\t" + e.getMessage());
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        Loger.println("[err] Socket closed.\n\t");
                    }
                }
							
//				switch (cmd) {
//				case role:
//					user_input = socket_in.readLine();
//					if (user_input.isEmpty())
//						break;
//					else {
//						user_role = user_input;
//						users.put(user_role, io_pair);
//					}
//					
//					break;
//				// wylistowanie użytkownikami wszystkich zalogowanych
//				case usr:
//					// najpierw komenda "usr" żeby poinformować klienta czego ma się spodziewać
//					socket_out.println("usr");
//					socket_out.println(users.size());
//					for (Map.Entry<String, Pair<BufferedReader, PrintWriter>> user : users.entrySet()) {
//						socket_out.println(user.getKey());
//					}
//
//					cmd_listener.CommandEmitted("[srv] " + user_role + " printing users.", true);
//					
//					break;
//				case exit:
//					users.remove(user_role);
//							
//					if (users.containsKey(user_role)) {
//						cmd_listener.CommandEmitted("[srv] " + user_role + " still logged in; server run() switch case statement.", true);
//					} else {
//						cmd_listener.CommandEmitted("[srv] " + user_role + " logged out.", true);
//						socket.close();
//					}
//							
//					break;
//				case series:
//					String receiver = socket_in.readLine();
//					cmd_listener.CommandEmitted("[srv] " + user_role + " sends series to " + receiver + ".", true);
//						
//					Series series = new Series();
//					series.receiveSeries(socket_in);
//					series.visualizeSeries(cmd_listener);
//							
//					transferSeries(receiver, series);
//							
//					break;
//				case banknote:
//					// TODO: send amount, id, then series
//				
//					break;
//				default:
//					socket.close();
//
//					break;
//				}
			}
			
		}
	}
	
}

