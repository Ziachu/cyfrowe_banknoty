package Support;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class CommonCommandManager{

	private PrintWriter socket_out;
	
	private Command cmd;
	private Command last_cmd;
	private String user_input;
	private boolean waiting_for_next_input = false;
	
	// Ustawia kanały komunikacyjne, tak żeby manager mógł przekazywać komunikaty do serwera
	public void setCommandLine(BufferedReader socket_in, PrintWriter socket_out) {
		this.socket_out = socket_out;
	}

	// Musi być tutaj, bo wymaga tego interejs, ale pozostawiam ją pustą,
	// bo dziedziczące klasy np. AliceCommanManager i tak ją nadpiszą
	public void respondToCommand(String cmd) {}
	
	// Dodanie tej metody hołduje podejściu DRY. :D
	// Dzięki niej nie trzeba implementować obsługi podstawych komend (takich jak: usr, exit, commands)
	// a wystarczy zrobić to raz, właśnie tutaj
	public void respondToCommonCommand(String msg) {
		Loger.println("\t[debug] Responding to common user_input: \"" + msg + "\" (" + waiting_for_next_input + ")");
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
				
				default:
					
					Loger.println("[info] Such command (" + cmd.toString() + ") isn't supported yet.");
					break;
				}
			} catch (IllegalArgumentException e) {
				Loger.println("\t[err] Wrong command: " + msg);
			} catch (NullPointerException e) {
				Loger.println("\t[err] Some null in command!\n\t" + e.getMessage());
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
