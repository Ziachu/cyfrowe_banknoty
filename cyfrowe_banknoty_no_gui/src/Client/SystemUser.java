package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Alice.AliceCommandManager;
import Bank.BankCommandManager;
import Support.Command;
import Support.CommonCommandManager;
import Support.HiddenBanknote;
import Support.Loger;
import Support.RSA;
import Support.Role;
import Support.Series;
import Vendor.VendorCommandManager;

public class SystemUser {

	private static int port;
	private static String server_address;
	private static Role user_role;
	
	private static Socket socket;
	private static Scanner user_in;
	private static BufferedReader socket_in;
	private static PrintWriter socket_out;
	
	private static ServerResponseListener server_response_listener;
	private static CommonCommandManager manager;
    private static User user;
	
	public static Command[] common_commands;
	private static Command last_cmd;
	
	public static void main(String args[]) {
		
		port = 4444;
		server_address = "localhost";
		
		try {
			socket = new Socket(server_address, port);
			socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socket_out = new PrintWriter(socket.getOutputStream(), true);
			user_in = new Scanner(System.in);
			
			Loger.println("[info] Connection with " + server_address + ":" + port + " established!");

			getUserRole();
			setCommandManager();
			
			while (true) {
				managerUserInput();
			}
			
		} catch (UnknownHostException e) {
			Loger.err("Unknown server address.\n\t" + e.getMessage());
		} catch (IOException e) {
			Loger.err("Couldn't establish connection.\n\t" + e.getMessage());
		}
	}
	
	private static void managerUserInput() {
		// Opóźniony prompt o 50ms, żeby nie nakładał się z wątkiem słuchającym
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Loger.err("Troubles with Thread.sleep() function.");
		}
		
		String user_input;
		
		do {
			Loger.print("[usr] ");
			user_input = user_in.nextLine();
		} while (user_input.equals(""));
		
		
		common_commands = new Command[] {Command.role, Command.exit, Command.usr, Command.series, Command.commands};
		Command cmd;
		
		// Próbuje wydobyć komendę od użytkownika
		// Jeżeli się uda, to przekazuje ją do odp. manager'a
		try {
			cmd = Command.valueOf(user_input); 

			// Jeżeli to wspólna komenda
			if (Arrays.asList(common_commands).contains(cmd)) {
				manager.respondToCommonCommand(user_input);
			// Jeżeli nie (tzn. że jest unikatowa)
			} else {
				manager.respondToCommand(user_input);
			}
			
			last_cmd = cmd;
		// Jeżeli się nie uda, to traktuje ją jako odpowiedź na wcześniejszą komendę
		// i również przekazuje do odpowiedniego manager'a, tam może być odrzucona
		} catch (IllegalArgumentException e) {
			Loger.err("Such command (" + user_input + ") doesn't exist.");
			
			if (Arrays.asList(common_commands).contains(last_cmd)) {
				manager.respondToCommonCommand(user_input);
			} else {
				manager.respondToCommand(user_input);
			}
		}
	}

	private static void setCommandManager() {
		
		switch(user_role) {
		case Alice:
			
			manager = new AliceCommandManager();
			break;
		case Bank:
			
			manager = new BankCommandManager();
			break;
		case Vendor:
			
			manager = new VendorCommandManager();
			break;
		default:
			Loger.err("This error shouldn't occur, what's wrong with " + user_role + " role?");
			break;
		}

        user = manager.getUser();
        Loger.debug("User: " + user);

        server_response_listener = new ServerResponseListener(socket_in, socket_out, user);
        server_response_listener.start();

		manager.setCommandLine(socket_in, socket_out);
		
		manager.respondToCommonCommand(Command.role.toString());
		manager.respondToCommonCommand(user_role.toString());
	}

	public static void getUserRole() {
		boolean user_role_applied = false;
		while (!user_role_applied) {
			try {
				Loger.print("Enter Your role: ");
				user_role = Role.valueOf(user_in.nextLine());
				
				user_role_applied = true;
			} catch (IllegalArgumentException e) {
				Loger.err("Wrong role applied.");
			}			
		}
	}
}


// ServerResponseListener to wątek działajacy w tle, nasłuchujący "niespodziewanych"
// wiadomości ze strony serwera. Przykładowa sytuacja:
// 		Bob przesyła ciąg do Alice
// 		Alice w danym momencie wykonuje inne zadanie
//		Dzięki działającemu w tle ServerResponseListener serwer po prostu
//		przesyła otrzymany od Bob'a ciąg 
//		A u Alice jest on wypisywany ;)


class ServerResponseListener extends Thread {
	BufferedReader socket_in;
	PrintWriter socket_out;
	Command cmd;
	String user_input;
    User user;
		
	ServerResponseListener(BufferedReader socket_in, PrintWriter socket_out, User user) {

        this.socket_in = socket_in;
        this.socket_out = socket_out;
       	this.user = user;
   	}
		
	public void run() {
		while (true) {
			try {
				if (socket_in.ready()) {
					user_input = socket_in.readLine();
						
					try {
						cmd = Command.valueOf(user_input);
							
						switch(cmd) {
						case usr:
							
							displayServerResponseToUsrCommand();
							break;
						case series:								
							
							displayServerResponseToSeriesCommand();
							break;
						case client_publish_key:

							displayServerResponseToClientPublishKeyCommand();
							break;
						case client_get_key:

							displayServerResponseToClientGetKeyCommand();
							break;
						case receive_hidden_banknotes:
							
							respondToReceiveHiddenBanknotes();
							break;
						case picked_banknote:
							
							int picked_banknote = Integer.parseInt(socket_in.readLine());
							user.setPickedBanknote(picked_banknote);
							break;
						case reveal_hidden_banknotes:
							
							Loger.println("Oh, I got secrets from Alice.");
							int no_revealed_banknotes = Integer.parseInt(socket_in.readLine());
							int no_id_series = Integer.parseInt(socket_in.readLine());
							
							Series[] s_series = new Series[no_id_series];
							Series[] b_series = new Series[no_id_series];
							Series[] l_series = new Series[no_id_series];
							
							Series[] t_series = new Series[no_id_series];
							Series[] c_series = new Series[no_id_series];
							Series[] w_series = new Series[no_id_series];

							ArrayList<BigInteger> secrets = new ArrayList<BigInteger>();
							
							// NIEDOKOŃCZONE!
							// NIEPRZETESTOWANE PRZESYŁANIE "j"
							
							for (int i = 0; i < no_revealed_banknotes; i++) {
								BigInteger secret = new BigInteger(socket_in.readLine());
								
								// następnie ciągi
								for (int k = 0; k < no_id_series; k++) {
									// lewa część ciągów id
									s_series[k] = new Series();
									s_series[k].receiveSeries(socket_in);
									b_series[k] = new Series();
									b_series[k].receiveSeries(socket_in);
									l_series[k] = new Series();
									l_series[k].receiveSeries(socket_in);
									
									// prawa część ciagów id
									t_series[k] = new Series();
									t_series[k].receiveSeries(socket_in);
									c_series[k] = new Series();
									c_series[k].receiveSeries(socket_in);
									w_series[k] = new Series();
									w_series[k].receiveSeries(socket_in);
								}
							}

							Loger.println("Oh, and also other stuff to uncover banknotes.");
							
							break;
						default:
							
							Loger.println("[srv] Wrong command.");
							break;
						}
					} catch (IllegalArgumentException e) {
						Loger.err("IllegalArgExc");
						continue;
					}
				}
			} catch (IOException e) {
				Loger.println("[srv] IO problem: SystemUser in ListenerThread's run() method");
				e.printStackTrace();
			}
		}
	}

	private void respondToReceiveHiddenBanknotes() throws NumberFormatException, IOException {
		Loger.println("I see some hidden banknotes comming...!");
		// TODO: odbieram liczbę banknotów
		int no_banknotes = Integer.parseInt(socket_in.readLine());
		ArrayList<HiddenBanknote> temp_arr = new ArrayList<HiddenBanknote>();
		
		Loger.println("There is " + no_banknotes + " of them.");
		
		// TODO: w odpowiedniej pętli odbieram wszystkie banknoty
		for (int i = 0; i < no_banknotes; i++) {
			HiddenBanknote temp_banknote = new HiddenBanknote();
			temp_banknote.receiveHiddenBanknote(socket_in);
			Loger.println(i + ". hidden banknote received from server.");
			temp_arr.add(temp_banknote);
		}
		
		// TODO: przekazuje banknoty do klasy banku
		user.setHiddenBanknotes(temp_arr);		
	}

	private void displayServerResponseToClientGetKeyCommand() {
		
		try {
			String bank_key = socket_in.readLine();
			Loger.debug("I've received public key from bank!");
			
			PublicKey pub_key = RSA.restorePublicKey(bank_key);
			
			// dany użytkownik (Alice/Sprzedawca) będzie miał teraz dostęp do klucza banku
			user.setPublicKey(pub_key);		

		} catch (IOException e) {
			Loger.err("Couldn't get key from server.");
		}
	}

	private void displayServerResponseToClientPublishKeyCommand() {
		
		Loger.debug("I'm publishing my public key.");
    	String public_key = user.getPublicKey();

    	socket_out.println("server_publish_key");
    	socket_out.println(public_key);
	}

	private void displayServerResponseToSeriesCommand() {
		Series series = new Series();
		series.receiveSeries(socket_in);
			
		Loger.println("[srv] Series came from server:");
		series.visualizeSeries();
	}

	private void displayServerResponseToUsrCommand() throws NumberFormatException, IOException {
		int no_users = Integer.parseInt(socket_in.readLine());
		
		String response = "(" + Integer.toString(no_users) + ") ";
		for (int i = 0; i < no_users; i++) {
			response += socket_in.readLine() + " ";
		}
			
		Loger.println("[srv] " + response);
	}
}
