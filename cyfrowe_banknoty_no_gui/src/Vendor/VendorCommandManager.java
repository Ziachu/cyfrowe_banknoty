package Vendor;

import java.io.BufferedReader;
import java.io.PrintWriter;

import Support.Command;
import Support.CommandManager;
import Support.Loger;

public class VendorCommandManager implements CommandManager {

//	private BufferedReader socket_in;
	private PrintWriter socket_out;
	 
	private Command cmd;
	private Command last_cmd;
	private String user_input;
	private boolean waiting_for_next_input;
	
	public VendorCommandManager() {
		waiting_for_next_input = false;
	}
	
	// Umożliwia ręczne wywołanie odpowiedzi na komendę
	public void setCommand(Command cmd) {
		this.cmd = cmd;
		respondToCommand(this.cmd.toString());
	}
	
	// Ustawia kanały komunikacyjne, tak żeby manager mógł przekazywać komunikaty do serwera
	public void setCommandLine(BufferedReader socket_in, PrintWriter socket_out) {
//		this.socket_in = socket_in;
		this.socket_out = socket_out;
	}

	// Spełnia główne zadanie CommandManager'a, zarządza wprowadzonymi komendami
	public void respondToCommand(String msg) {
		Loger.println("\t[debug] Responding to user_input: " + msg + " (" + waiting_for_next_input + ")");
		user_input = msg;
		
		if (!waiting_for_next_input) {
			// Jeżeli nie oczekuje żadnego konkretnego input'u to próbuje wydobyć komendę
			try {
				cmd = Command.valueOf(msg);
				socket_out.println(cmd);
				
				switch(cmd) {
				case role:
					
					last_cmd = cmd;
					this.waiting_for_next_input = true;
					
					break;
				case exit:
					
					respondToExitCommand();
					break;
				case usr:
					
					// Wysyła polecenie do serwera. Inny wątek nasłuchuje odpowiedzi
					break;
				/* TODO: dodać kolejne obsługiwane przez Sprzedawcę komendy (case)
					- weryfikowanie podpisu:
						- pobieranie klucza publicznego Banku
						- tworzenie podpisu
						- porównywanie podpisu z podpisem na banknocie
					- generowanie ciągu losowych bitów (długości 100)
					- wysyłanie ciągu losowych bitów
					- dalsza weryfikacja podpisu:
						- pobieranie ciągów z banknotu
						- obliczanie hash'u
						- porównywanie hash'y (swoich z tymi na banknocie)
					- akceptacja/ odrzucanie płatności
					- wysyłanie do banku banknotu i ciagów identyfikujących Alice
				
				*/
				default:
					
					Loger.println("[info] Such command (" + cmd.toString() + ") isn't supported yet.");
					break;
				}
			} catch (NullPointerException | IllegalArgumentException e) {
				Loger.println("\t[err] Wrong command: " + msg + ".");
			}
		} else {
			// Jeżeli oczekuje konkretnego input'u, to sprawdza jaką komendę poprzednio obsługiwał
			switch(last_cmd) {
			case role:
				
				respondToRoleCommand(user_input);
				this.waiting_for_next_input = false;
				
				break;
			default:
				
				Loger.println("\t[err] Wrong response for last command (" + last_cmd.toString() + "): " + user_input + ".");
				break;
				
			}
			
		}		
	}
	private void respondToRoleCommand(String user_input) {
		socket_out.println(user_input);
	}
	
	private void respondToExitCommand() {
		System.exit(0);
	}
}
