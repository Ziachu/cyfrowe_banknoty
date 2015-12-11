package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import Support.Command;
import Support.HiddenBanknote;
import Support.Loger;
import Support.Pair;
import Support.Role;
import Support.Series;

public class Server {

	private static int port;
	private static HashMap<Role, Pair<BufferedReader, PrintWriter>> users;
	private static ServerSocket server_socket_listener;
	private static LinkedList<Role> waiting_for_response;
	
	public static void main(String args[]) {
		
		port = 4444;

		try {
			server_socket_listener = new ServerSocket(port);
			users = new HashMap<Role, Pair<BufferedReader, PrintWriter>>();
			waiting_for_response = new LinkedList<Role>();
			
			Loger.println("[info] Listening at " + port + "...");			

			try {
				while (true) {
					new BackgroundServer(server_socket_listener.accept()).start();
				}
			} finally {
				server_socket_listener.close();
			}
			
		} catch (IOException e) {
			Loger.err("Couldn't create server socket.\n\t" + e.getMessage());
		}
		
		
	}

	private static class BackgroundServer extends Thread{
		private Socket socket;
		private BufferedReader socket_in;
		private PrintWriter socket_out;
		private Pair<BufferedReader, PrintWriter> io_pair;

		Command cmd;
		String user_input;
		PrintWriter temp_socket;
		
		private Role user_role;
		
		public BackgroundServer(Socket socket) {
			this.socket = socket;
			
			try {
				socket_in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				socket_out = new PrintWriter(this.socket.getOutputStream(), true);
				io_pair = new Pair<BufferedReader, PrintWriter>(socket_in, socket_out);
			} catch (IOException e) {
				Loger.err("Couldn't obtain socket i/o.\n\t" + e.getMessage());
			}
			
			
		}
		
		public void run() {
			Loger.println("[info] Connection with " + socket.getInetAddress() + ":" + socket.getPort() + " established.");

			while (!socket.isClosed()) {

				try {
					user_input = socket_in.readLine();	
//					Loger.println("[srv] from user: " + user_input + ".");
					cmd = Command.valueOf(user_input);
					
					switch (cmd) {
					case role:
						
						respondToRoleCommand();
						break;
					case usr:
						
						respondToUsrCommand();
						break;
					case exit:
						
						respondToExitCommand();
						break;
					case series:
						
						respondToSeriesCommand();
						break;
					case get_bank_key:
						
						respondToGetBankKeyCommand();
						break;
					case server_publish_key:
						
						respondToServerPublishKeyCommand();
						break;
					case send_hidden_banknotes:
						
						if (users.containsKey(Role.Bank)) {
							temp_socket = users.get(Role.Bank).getY();
							temp_socket.println("receive_hidden_banknotes");

							int no_banknotes = Integer.parseInt(socket_in.readLine());
							temp_socket.println(no_banknotes);
							
							for (int i = 0; i < no_banknotes; i++) {
								HiddenBanknote temp_banknote = new HiddenBanknote();
								temp_banknote.receiveHiddenBanknote(socket_in);
								Loger.println("Sending " + i + ". hidden banknote to bank.");
								temp_banknote.sendHiddenBanknote(temp_socket);
							}
						} else {
							Loger.warr("Bank isn't online.");
						}
						break;
					default:
						
						Loger.println("[srv] Such command (" + cmd.toString() + ") isn't supported.");
						break;
					}
				} catch (IOException e) {
					Loger.err("Couldn't read from socket.\n\t" + e.getMessage());
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        Loger.err("Socket closed.\n\t");
                    }
                }
			}
		}

		private void respondToServerPublishKeyCommand() {
			try {
				Loger.debug("I've got public key bytes from Bank.");
				String public_key = socket_in.readLine();
			
				// Pobieram pierwszego użytkownika w kolejce czekającego na odpowiedź
				Role user = waiting_for_response.removeFirst();
			
				temp_socket = users.get(user).getY();
				temp_socket.println("client_get_key");
				temp_socket.println(public_key);
			} catch (IOException e) {
				Loger.err("Couldn't read from socket_in.");
			}
			
		}

		private void respondToGetBankKeyCommand() {
			
			Loger.debug(user_role + " is trying to get Bank's public key.");
			// Dodaję użytkownika do listy użytkowników czekających na odpowiedź
			
			if (users.containsKey(Role.Bank)) {
				temp_socket = users.get(Role.Bank).getY();
				waiting_for_response.add(user_role);
				temp_socket.println("client_publish_key");							
			} else {
				Loger.warr("Bank isn't online.");
			}
		}

		private void respondToSeriesCommand() {
			
			// po stronie klienta jest sprawdzana wartość "receiver" więc tu przyjmuję
			// że nie będzie problemu z funkcją Role.valueOf()

			try {
				Role receiver = Role.valueOf(socket_in.readLine());
				Loger.println("[srv] " + user_role + " sends series to " + receiver + ".");
				
				Series series = new Series();
				series.receiveSeries(socket_in);
				series.visualizeSeries();
					
				if (users.containsKey(receiver)) {
					PrintWriter temp_socket_out = users.get(receiver).getY();
						
					temp_socket_out.println("series");
					series.sendSeries(temp_socket_out);
				} else {
					Loger.err("There's no receiver with given name (" + receiver + ");");
				}

				//transferSeries(receiver, series);
			} catch (IOException e) {
				Loger.err("Couldn't read series receiver from socket_in");
			}
		}

		private void respondToExitCommand() {
			
			users.remove(user_role);
			
			if (users.containsKey(user_role)) {
				Loger.println("[srv] " + user_role + " still logged in. Couldn't remove him from HashMap.");
			} else {
				
				Loger.println("[srv] " + user_role + " logged out.");
				
				try {
					socket.close();
				} catch (IOException e) {
					Loger.err("Couldn't properly close socket after user exit command.");
				}
			}
		}

		private void respondToUsrCommand() {
			// najpierw komenda "usr" żeby poinformować ServerResponseListenera czego ma się spodziewać
			socket_out.println("usr");
			socket_out.println(users.size());
			
			for (Map.Entry<Role, Pair<BufferedReader, PrintWriter>> user : users.entrySet()) {
				socket_out.println(user.getKey().toString());
			}
			
			Loger.println("[srv] " + user_role + " printing users.");
		}

		private void respondToRoleCommand() {
			try {
				user_input = socket_in.readLine();
				if (user_input.isEmpty()) {
					// pass
				} else {
					user_role = Role.valueOf(user_input);
					users.put(user_role, io_pair);
					Loger.println("[srv] " + user_role + " has logged in.");
				}
			} catch (IOException e) {
				Loger.err("Coulnd't read role from socket_in.");
			}
		}
	}
}

