package server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import listeners.CommandListener;

import support.Command;
import support.Pair;
import support.Series;

public class Server extends Thread {
	private int port;
	private static HashMap<String, Pair<BufferedReader, PrintWriter>> users;
	private CommandListener cmd_listener;

/*	// tworzy listener'a i odrębne wątki do obsługi każdego użytkownika
	public static void main(String[] args) throws IOException {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GUI();
			}
		});
		ServerSocket listener = new ServerSocket(port);
		users = new HashMap<String, Pair<BufferedReader, PrintWriter>>();

		System.out.println("Listening at: " + listener.getInetAddress());

		try {
			while (true) {
				new BackgroundServer(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}*/

	public Server(int port) {

		this.port = port;
	}
	
	public void setCommandListener(CommandListener cmd_listener) {

		this.cmd_listener = cmd_listener;
	}
	
	public void run() {

		if (this.port != 0) {
			
			try {
				ServerSocket listener = new ServerSocket(this.port);
				users = new HashMap<String, Pair<BufferedReader, PrintWriter>>();
				
				cmd_listener.CommandEmitted("[srv] Listening at: " + listener.getInetAddress() + ".");
				
				try {
					while (true) {
						new BackgroundServer(listener.accept(), cmd_listener).start();
					}
				} finally {
					listener.close();
					cmd_listener.CommandEmitted("[srv] [!] Server shutted down!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private static class BackgroundServer extends Thread {
		private Socket socket;
		private BufferedReader socket_in;
		private PrintWriter socket_out;
		private String user_role;
		private Pair<BufferedReader, PrintWriter> io_pair;
		private CommandListener cmd_listener;
		
		BackgroundServer(Socket socket, CommandListener cmd_listener) {
			this.socket = socket;
			this.cmd_listener = cmd_listener;
		}

		public void run() {
			try {
				cmd_listener.CommandEmitted("[srv] Connection established with " + socket.getInetAddress() + ".");
						
				socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				socket_out = new PrintWriter(socket.getOutputStream(), true);
				io_pair = new Pair<BufferedReader, PrintWriter>(socket_in, socket_out);

				String user_input;
				Command cmd;

				while (!socket.isClosed()) {
					user_input = socket_in.readLine();
					
					try {
						cmd = Command.valueOf(user_input);
					} catch (NullPointerException e) {
						socket.close();
						cmd_listener.CommandEmitted("[srv] Wrong command - shutting down connection.");
						continue;
					}
								
					switch (cmd) {
					// logowanie użytkownika (przypisanie użytkownikowi roli)
					case role:
						user_input = socket_in.readLine();
						if (user_input.isEmpty())
							break;
						else {
							user_role = user_input;
							users.put(user_role, io_pair);
							cmd_listener.CommandEmitted("[srv] " + user_role + " logged in.");
						}
						
						break;
					// wylistowanie użytkownikami wszystkich zalogowanych
					case usr:
						// najpierw komenda "usr" żeby poinformować klienta czego ma się spodziewać
						socket_out.println("usr");
						socket_out.println(users.size());
						for (Map.Entry<String, Pair<BufferedReader, PrintWriter>> user : users.entrySet()) {
							socket_out.println(user.getKey());
						}

						cmd_listener.CommandEmitted("[srv] " + user_role + " printing users.");
						
						break;
					case exit:
						users.remove(user_role);
								
						if (users.containsKey(user_role)) {
							cmd_listener.CommandEmitted("[srv] " + user_role + " still logged in; server run() switch case statement.");
						} else {
							cmd_listener.CommandEmitted("[srv] " + user_role + " logged out.");
							socket.close();
						}
								
						break;
					case series:
						String receiver = socket_in.readLine();
						cmd_listener.CommandEmitted("[srv] " + user_role + " sends series to " + receiver + ".");
							
						Series series = new Series();
						series.receiveSeries(socket_in);
						series.visualizeSeries();
								
						transferSeries(receiver, series);
								
						break;
					case banknote:
						// TODO: send amount, id, then series
					
						break;
					default:
						socket.close();

						break;
					}
				}

			} catch (IOException e) {
				cmd_listener.CommandEmitted("[srv] in&out problem; BackgroundServer run() method.");
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					cmd_listener.CommandEmitted("[srv] Couldn't close socket; BackgroundServer run() method.");
				}
				
				cmd_listener.CommandEmitted("[srv] Connection with " + socket.getInetAddress() + " shut down.");
			}
		}

		private void transferSeries(String receiver, Series series) throws UnsupportedEncodingException {
			
			if (users.containsKey(receiver)) {
				PrintWriter temp_socket_out = users.get(receiver).getY();
				
				temp_socket_out.println("series");
				series.sendSeries(temp_socket_out);
			} else {
				cmd_listener.CommandEmitted("[srv] There's not receiver with given name (" + receiver + "); Server in transferSeries() method.");
			}
		}
	}
}