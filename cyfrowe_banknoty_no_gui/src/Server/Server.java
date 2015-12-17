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
			
			Loger.println("[info] Nasluchiwanie na porcie " + port + "...");

			try {
				while (true) {
					new BackgroundServer(server_socket_listener.accept()).start();
				}
			} finally {
				server_socket_listener.close();
			}
			
		} catch (IOException e) {
			Loger.err("Nie mozna stworzyc socketa servera.\n\t" + e.getMessage());
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
				Loger.err("Nie mozna uzyskac socket'a i/o.\n\t" + e.getMessage());
			}
			
			
		}
		
		public void run() {
			Loger.println("[info] Polaczenie z adresem " + socket.getInetAddress() + ":" + socket.getPort() + " zostalo ustanowione.");

			while (!socket.isClosed()) {

				try {
					user_input = socket_in.readLine();	
//					Loger.println("[srv] from user: " + user_input + ".");
					cmd = Command.valueOf(user_input);
					
					switch (cmd) {
					case role:
						
						respondToRoleCommand();
						break;
					case commands:
						
						Loger.println(user_role + " wypisuje komendy.");
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
						
						resnpodToSendHiddenBanknotes();
						break;
					case pick_one_banknote:
						
						respondToPickOneBanknoteCommand();
						break;
					case reveal_hidden_banknotes :
						
						if (users.containsKey(Role.Bank)) {
							
							Loger.mess("[SERVER] Bank jest dostepny.");
							PrintWriter temp_socket = users.get(Role.Bank).getY();
<<<<<<< HEAD

							int no_id_series = Integer.parseInt(socket_in.readLine());
							Loger.mess("[SERVER] Nie otrzymano serii identyfikujacych.");
							temp_socket.println(no_id_series);
							Loger.mess("[SERVER] Nie przeslano serii identyfikujacych do BANKU.");
							
							for (int i = 0; i < no_id_series; i++) {
								Series tmp_s_series = new Series();
								tmp_s_series.receiveSeries(socket_in);
								tmp_s_series.sendSeries(temp_socket);
							}

							Loger.mess("[SERVER] Wyslano wszystkie ciagi s.");
							
							for (int i = 0; i < no_id_series; i++) {
								Series tmp_b_series = new Series();
								tmp_b_series.receiveSeries(socket_in);
								tmp_b_series.sendSeries(temp_socket);
							}
							
							Loger.mess("[SERVER] Wyslano wszystkie ciagi b.");

							for (int i = 0; i < no_id_series; i++) {
								Series tmp_l_series = new Series();
								tmp_l_series.receiveSeries(socket_in);
								tmp_l_series.sendSeries(temp_socket);
							}
							
							Loger.mess("[SERVER] Wyslano wszystkie ciagi l");
							
							for (int i = 0; i < no_id_series; i++) {
								Series tmp_t_series = new Series();
								tmp_t_series.receiveSeries(socket_in);
								tmp_t_series.sendSeries(temp_socket);
							}
							
							Loger.mess("[SERVER] Wyslano wszystkie ciagi t.");

							for (int i = 0; i < no_id_series; i++) {
								Series tmp_c_series = new Series();
								tmp_c_series.receiveSeries(socket_in);
								tmp_c_series.sendSeries(temp_socket);
							}
							
							Loger.mess("[SERVER] Wyslano wszystkie ciagi w");
							
							for (int i = 0; i < no_id_series; i++) {
								Series tmp_w_series = new Series();
								tmp_w_series.receiveSeries(socket_in);
								tmp_w_series.sendSeries(temp_socket);
							}
							
							Loger.mess("[SERVER] Wyslano wszystkie ciagi w.");
							Loger.mess("[SERVER] Wyslano wszystkie ciagi.");
=======
							temp_socket.println("reveal_hidden_banknotes");
>>>>>>> origin/master
							
							int no_secrets = Integer.parseInt(socket_in.readLine());
							temp_socket.println(no_secrets);
							
							for (int i = 0; i < no_secrets; i++) {
								temp_socket.println(socket_in.readLine());
								Loger.debug("Sending " + i + ". secret.");
							}
							
							int no_id_series = Integer.parseInt(socket_in.readLine());
							temp_socket.println(no_id_series);
							
							for (int i = 0; i < no_id_series * 6; i++) {
								Series tmp = new Series();
								tmp.receiveSeries(socket_in);
								tmp.visualizeSeries();
								tmp.sendSeries(temp_socket);
								Loger.debug("Sending " + i + ". series");
							}
						} else {
							Loger.warr("[SERVER] Wyglada na to ze bank sie wylogowal.");
						}
						
						break;
					default:
						
						Loger.mess("[SERVER] Komenda (" + cmd.toString() + ") nie istnieje.");
						break;
					}
				} catch (IOException e) {
					Loger.err("Nie mozna odczytac z socket'a.\n\t" + e.getMessage());
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        Loger.err("Zamknieto socket.\n\t");
                    }
                }
			}
		}

		private void respondToPickOneBanknoteCommand() throws NumberFormatException, IOException {
			int picked_banknote = Integer.parseInt(socket_in.readLine());
			
			if (users.containsKey(Role.Alice)){
				PrintWriter temp_socket = users.get(Role.Alice).getY();
				temp_socket.println("picked_banknote");
				temp_socket.println(picked_banknote);
				
			} else {
				Loger.warr("Alice nie jest online.");
			}
		}

		private void resnpodToSendHiddenBanknotes() throws NumberFormatException, IOException {
			if (users.containsKey(Role.Bank)) {
				temp_socket = users.get(Role.Bank).getY();
				temp_socket.println("receive_hidden_banknotes");

				int no_banknotes = Integer.parseInt(socket_in.readLine());
				temp_socket.println(no_banknotes);
				
				for (int i = 0; i < no_banknotes; i++) {
					HiddenBanknote temp_banknote = new HiddenBanknote();
					temp_banknote.receiveHiddenBanknote(socket_in);
					Loger.println("Wysylanie banknotu numer " + i + ". do banku.");
					temp_banknote.sendHiddenBanknote(temp_socket);
				}
			} else {
				Loger.warr("Bank isn't online.");
			}
		}

		private void respondToServerPublishKeyCommand() {
			try {
				Loger.debug("[SERVER] Otrzymalem klucz publiczny w bajtach od BANKU.");
				String public_key = socket_in.readLine();
			
				// Pobieram pierwszego użytkownika w kolejce czekającego na odpowiedź
				Role user = waiting_for_response.removeFirst();
			
				temp_socket = users.get(user).getY();
				temp_socket.println("client_get_key");
				temp_socket.println(public_key);
			} catch (IOException e) {
				Loger.err("Nie mozna odczytac z socket_in.");
			}
			
		}

		private void respondToGetBankKeyCommand() {
			
			Loger.mess("[SERVER]" + user_role + " probuje uzyskac klucz publiczny BANKU.");
			// Dodaję użytkownika do listy użytkowników czekających na odpowiedź
			
			if (users.containsKey(Role.Bank)) {
				temp_socket = users.get(Role.Bank).getY();
				waiting_for_response.add(user_role);
				temp_socket.println("client_publish_key");							
			} else {
				Loger.warr("[SERVER] Bank nie jest dostepny.");
			}
		}

		private void respondToSeriesCommand() {
			
			// po stronie klienta jest sprawdzana wartość "receiver" więc tu przyjmuję
			// że nie będzie problemu z funkcją Role.valueOf()

			try {
				Role receiver = Role.valueOf(socket_in.readLine());
				Loger.mess("[SERVER] " + user_role + " wysyla ciagi do " + receiver + ".");
				
				Series series = new Series();
				series.receiveSeries(socket_in);
				series.visualizeSeries();
					
				if (users.containsKey(receiver)) {
					PrintWriter temp_socket_out = users.get(receiver).getY();
						
					temp_socket_out.println("series");
					series.sendSeries(temp_socket_out);
				} else {
					Loger.err("[SERVER] Nie ma takiego mozliwego odbiorcy jak (" + receiver + ");");
				}

				//transferSeries(receiver, series);
			} catch (IOException e) {
				Loger.err("[SERVER] Nie mozna odczytac odbiorcy ciagow z socket_in");
			}
		}

		private void respondToExitCommand() {
			
			users.remove(user_role);
			
			if (users.containsKey(user_role)) {
				Loger.println("[SERVER] " + user_role + " jest nadal zalogowany. Nie jestem w stanie usunac go z hashmapy.");
			} else {
				
				Loger.println("[SERVER] " + user_role + " wylogowany.");
				
				try {
					socket.close();
				} catch (IOException e) {
					Loger.err("[SERVER] Nie mozna zamknac socketa po komendzie exit.");
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
			
			Loger.println("[SERVER] " + user_role + " wyswietlam obecnych uzykownikow.");
		}

		private void respondToRoleCommand() {
			try {
				user_input = socket_in.readLine();
				if (user_input.isEmpty()) {
					// pass
				} else {
					user_role = Role.valueOf(user_input);
					users.put(user_role, io_pair);
					Loger.println("[SERVER] " + user_role + " zalogowany.");
				}
			} catch (IOException e) {
				Loger.err("Nie mozna odczytac roli uzytkownika z socket_in.");
			}
		}
	}
}

