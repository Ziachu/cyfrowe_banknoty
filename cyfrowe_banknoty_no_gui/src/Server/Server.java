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
import Support.Role;
import Support.Series;

public class Server {

	private static int port;
	private static HashMap<Role, Pair<BufferedReader, PrintWriter>> users;
	private static ServerSocket server_socket_listener;
	
	public static void main(String args[]) {
		
		port = 4444;

		try {
			server_socket_listener = new ServerSocket(port);
			users = new HashMap<Role, Pair<BufferedReader, PrintWriter>>();
			
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
		
		private Role user_role;
		
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
//					Loger.println("[srv] from user: " + user_input + ".");
					cmd = Command.valueOf(user_input);
					
					switch (cmd) {
					case role:
						
						user_input = socket_in.readLine();
						if (user_input.isEmpty())
							break;
						else {
							user_role = Role.valueOf(user_input);
							users.put(user_role, io_pair);
							Loger.println("[srv] " + user_role + " has logged in.");
						}
						
						break;
					case usr:
						
						// najpierw komenda "usr" żeby poinformować ServerResponseListenera czego ma się spodziewać
						socket_out.println("usr");
						socket_out.println(users.size());
						
						for (Map.Entry<Role, Pair<BufferedReader, PrintWriter>> user : users.entrySet()) {
							socket_out.println(user.getKey().toString());
						}
						
						Loger.println("[srv] " + user_role + " printing users.");
						
						break;
					case exit:
						users.remove(user_role);
						
						if (users.containsKey(user_role)) {
							Loger.println("[srv] " + user_role + " still logged in. Couldn't remove him from HashMap.");
						} else {
							Loger.println("[srv] " + user_role + " logged out.");
							socket.close();
						}
						
						break;
					case series:
						// po stronie klienta jest sprawdzana wartość "receiver" więc tu przyjmuję
						// że nie będzie problemu z funkcją Role.valueOf()
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
							Loger.println("\t[err] There's no receiver with given name (" + receiver + ");");
						}
						
						//transferSeries(receiver, series);
						
						break;
					/*case banknote:
						// TODO: send amount, id, then series
						
						break;*/
					default:
						
						Loger.println("[srv] Such command (" + cmd.toString() + ") isnt' supported.");
						break;
					}
				} catch (NullPointerException e) {
					Loger.println("[err] Unknown command from " + user_role + ".");
					Loger.println("[err] Shutting down connection, \"no_command_loop\".");
					try {
						socket.close();
					} catch (IOException e1) { }
				} catch (IOException e) {
					Loger.println("[err] Couldn't read from socket.\n\t" + e.getMessage());
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        Loger.println("[err] Socket closed.\n\t");
                    }
                }
			}
		}
	}
}

