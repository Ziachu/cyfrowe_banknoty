package Alice;

import java.io.BufferedReader;
import java.io.PrintWriter;

import Support.Command;
import Support.CommandManager;
import Support.Loger;

public class AliceCommandManager implements CommandManager {

//	private BufferedReader socket_in;
	private PrintWriter socket_out;
	 
	private Command cmd;
	private Command last_cmd;
	private String user_input;
	private boolean waiting_for_next_input;
	
	public AliceCommandManager() {
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
				/* TODO: dodać kolejne obsługiwane przez Alice komendy (case):
					- generowanie bankotów
						- ustalanie kwoty Y
						- losowanie ID banknotu (o określonej # bitów)
						- tworzenie ciągów id_alice (o ustalonej # bitów):
								- losowanie ciągu (I, R, L, T, C, S, B)
								- obliczanie XOR'a na bitach
								- obliczanie hash'a
					- zakrywanie banknotów
						- pobieranie klucza publicznego banku
						- losowanie Z takiego, że (Z, n) = 1
						- obliczanie Y = M * Z^e (mod n)
					- wysyłanie zakrytych banknotów
					- odkrywanie banknotów
						- wysyłanie ciągów potrzebnych do stworzenia hash'a, których nie ma na banknocie
					- odbieranie ślepych podpisów
					- finalizowanie tworzenia bankotów
						- obliczanie SIG = V * Z^{-1} (mod n)
						- przechowywanie jakoś bankotu z podpisem
						- trzymanie przy bankocie ciągów potrzebnych do odkrycia zobowiązań
					- odiberanie ciągów losowych bitów od sprzedawcy
					- ujawnianie zobowiązań odpowiednio lewej/prawe strony ciągu id_alice
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
