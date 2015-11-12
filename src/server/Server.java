package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import support.Command;
import support.Pair;

public class Server {
	private static int port = 4444;
	private static HashMap<String, Pair<BufferedReader, PrintWriter>> users;

	// tworzy listener'a i odrębne wątki do obsługi każdego użytkownika
	public static void main(String[] args) throws IOException {
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
	}

	private static class BackgroundServer extends Thread {
		private Socket socket;
		private BufferedReader socket_in;
		private PrintWriter socket_out;
		private String user_role;
		private Pair<BufferedReader, PrintWriter> io_pair;
		
		BackgroundServer(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				log("Connection established with " + socket.getInetAddress() + ".");

				socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				socket_out = new PrintWriter(socket.getOutputStream(), true);
				io_pair = new Pair<BufferedReader, PrintWriter>(socket_in, socket_out);

				String user_input;
				Command cmd;

				while (!socket.isClosed()) {
					user_input = socket_in.readLine();
					cmd = Command.valueOf(user_input);

					switch (cmd) {
					// logowanie użytkownika (przypisanie użytkownikowi roli)
					case role:
						user_role = socket_in.readLine();

						users.put(user_role, io_pair);

						log(user_role + " logged in.");
						transferSeries(user_role, "hello You!");

						break;
					// wylistowanie użytkownikami wszystkich zalogowanych
					case usr:
						socket_out.println(users.size());

						for (Map.Entry<String, Pair<BufferedReader, PrintWriter>> user : users.entrySet()) {
							socket_out.println(user.getKey());
						}

						break;
					case exit:
						users.remove(user_role);
						
						if (users.containsKey(user_role)) {
							log("ERROR.005: " + user_role + " still logged in; server run() switch case statement.");
						} else {
							log(user_role + " logged out.");
							socket.close();
						}
						
						break;
					default:
						socket.close();

						break;
					}
				}

			} catch (IOException e) {
				log("ERROR.001: in&out problem; BackgroundServer run() method.");
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					log("ERROR.002: couldn't close socket; BackgroundServer run() method.");
				}

				log("Connection with " + socket.getInetAddress()
						+ " shut down.");
			}
		}

		private void transferSeries(String receiver, String msg) {
			PrintWriter temp_socket_out = users.get(receiver).getY();
			temp_socket_out.println(msg);
		}
		
		private void log(String msg) {
			System.out.println(msg);
		}
	}
}