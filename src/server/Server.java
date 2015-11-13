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

import support.*;

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
				Log.log("Connection established with " + socket.getInetAddress() + ".");

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
						Log.err("Wrong command - shutting down connection.");
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
							Log.log(user_role + " logged in.");
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

						Log.log(user_role + " printing users.");
						
						break;
					case exit:
						users.remove(user_role);
						
						if (users.containsKey(user_role)) {
							Log.err(user_role + " still logged in; server run() switch case statement.");
						} else {
							Log.log(user_role + " logged out.");
							socket.close();
						}
						
						break;
					case series:
						// TODO: receive Series
						String receiver = socket_in.readLine();
						Log.log(user_role + " sends series to " + receiver);
						
						Series series = new Series();
						series.receiveSeries(socket_in);
						series.visualizeSeries();
						
						// TODO: send length, then values
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
				Log.err("in&out problem; BackgroundServer run() method.");
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					Log.err("couldn't close socket; BackgroundServer run() method.");
				}

				Log.log("Connection with " + socket.getInetAddress()
						+ " shut down.");
			}
		}

		private void transferSeries(String receiver, Series series) throws UnsupportedEncodingException {
			
			if (users.containsKey(receiver)) {
				PrintWriter temp_socket_out = users.get(receiver).getY();
				
				temp_socket_out.println("series");
				series.sendSeries(temp_socket_out);
			} else {
				Log.err("there's not receiver with given name (" + receiver + "); Server in transferSeries() method.");
			}
		}
	}
}