package Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;

import Support.Command;
import Support.CommonCommandManager;
import Support.Loger;

public class BankCommandManager extends CommonCommandManager {

//	private BufferedReader socket_in;
	private PrintWriter socket_out;
	 
	private Command cmd;
	private Command last_cmd;
	private String user_input;
	private boolean waiting_for_next_input;
	
	public BankCommandManager() {
		waiting_for_next_input = false;
	}
	
	// Umożliwia ręczne wywołanie odpowiedzi na komendę
	public void setCommand(Command cmd) {
		this.cmd = cmd;
	}
	
	// Ustawia kanały komunikacyjne, tak żeby manager mógł przekazywać komunikaty do serwera
	public void setCommandLine(BufferedReader socket_in, PrintWriter socket_out) {
		this.socket_out = socket_out;
		super.setCommandLine(socket_in, socket_out);
	}

	// Spełnia główne zadanie CommandManager'a, zarządza wprowadzonymi komendami
	public void respondToCommand(String msg) {
		Loger.debug("Responding to user_input: " + msg + " (" + waiting_for_next_input + ")");
		user_input = msg;
		
		if (!waiting_for_next_input) {
			// Jeżeli nie oczekuje żadnego konkretnego input'u to próbuje wydobyć komendę
			try {
				cmd = Command.valueOf(msg);
				socket_out.println(cmd);
				
				switch(cmd) {
				
				/* TODO: dodać kolejne obsługiwane przez Bank komendy (case):
					- losowanie j (numeru bankotu do podpisu)
					- udostępnianie klucza publicznego
					- odbieranie zakrytych banknotów od Alice
					- sprawdzanie poprawności banknotów:
						- odbieranie ciągów potrzebnych do hash'u
						- sprawdzanie kwoty na banknotach
						- sprawdzanie czy każde ID jest inne
						- obliczanie hash'y
						- porównywanie has'y (swoich z tymi na banknocie)
						- obliczanie ciągów id_alice
						- sprawdzanie czy ciągi identyfikując Alice
					- podpisywanie banknotów (RSA)
						- obliczanie V = Y^d (mod n)
						- wysyłanie V do Alice
					- wysyłanie podpisanych banknotów
					- odbieranie banknotow od sprzedawcy
					- weryfikacja podpisu pod banknotem
						- obliczanie sig = M^e (mod n)
						- porównywanie sig z tym na banknocie
					- sprawdzanie ważności banknotu
						- prowadzenie księgowości (historia banknotów otrzymanych)
						- sprawdzanie, czy ID_banknotu już się pojawiło
						- odnotowanie ID oraz ciągów id_alice
				*/	
				default:
					
					Loger.println("[info] Such command (" + cmd.toString() + ") isn't supported yet.");
					break;
				}
			} catch (NullPointerException | IllegalArgumentException e) {
				Loger.err("Wrong command: " + msg + ".");
			}
		} else {
			// Jeżeli oczekuje konkretnego input'u, to sprawdza jaką komendę poprzednio obsługiwał
			switch(last_cmd) {
			default:
				
				Loger.err("Wrong response for last command (" + last_cmd.toString() + "): " + user_input + ".");
				break;
			}
		}		
	}
}
