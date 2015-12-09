package Support;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
	public void respondToCommonCommand(String user_input) {
		Loger.debug("Responding to common user_input: \"" + user_input + "\" (" + waiting_for_next_input + ")");
		
		if (!waiting_for_next_input) {
			// Jeżeli nie oczekuje żadnego konkretnego input'u to próbuje wydobyć komendę
			try {
				cmd = Command.valueOf(user_input);
				socket_out.println(cmd);
				
				switch(cmd) {
				case role:
					
					last_cmd = cmd;
					waiting_for_next_input = true;
					break;
				case exit:
					
					respondToExitCommand();
					break;
				case usr:
					
					// Wysyła polecenie do serwera. Inny wątek nasłuchuje odpowiedzi
					break;
				case series:
					
					respondToSeriesCommand(true);
					break;
				case commands:
					
					respondToCommandsCommand();
					break;
				default:
					Loger.println("[info] Such command (" + cmd.toString() + ") isn't supported yet.");
					break;
				}
			} catch (IllegalArgumentException e) {
				if (user_input != " ")
					Loger.err("Wrong command: " + user_input);
			} catch (NullPointerException e) {
				Loger.err("Some null in command!\n\t" + e.getMessage());
			}
		} else {
			// Jeżeli oczekuje konkretnego input'u, to sprawdza jaką komendę poprzednio obsługiwał
			
			this.user_input = user_input;
			switch(last_cmd) {
			case role:
				
				respondToRoleCommand(user_input);
				this.waiting_for_next_input = false;
				
				break;
			case series:
				
				respondToSeriesCommand(false);
				break;
			default:
				
				Loger.err("Wrong response for last command (" + last_cmd.toString() + "): " + user_input + ".");
				break;
			}
		}
	}

	private void respondToCommandsCommand() {
		Loger.println("[cmd] Available commands:");
		for (Command cmd : Command.values()) {
			Loger.println("\t" + TerminalColors.ANSI_CYAN + cmd + TerminalColors.ANSI_RESET);
		}
	}

	private void respondToSeriesCommand(boolean wait_for_next) {
		if (wait_for_next) {
			Loger.println("[cmd] Enter receiver's name.");
			last_cmd = cmd;
			waiting_for_next_input = true;
		} else {
			try {
				Role receiver = Role.valueOf(user_input);
				socket_out.println(receiver);
				
				Series series = new Series(50);
				series.visualizeSeries();
				series.sendSeries(socket_out);
			} catch (IllegalArgumentException e) {
				Loger.err("There is no user with name: " + user_input);
			} catch (UnsupportedEncodingException e) {
				Loger.err("Couldn't send series through socket.\n\t" + e.getMessage());
			}
			
			waiting_for_next_input = false;
		}
	}

	private void respondToRoleCommand(String user_input) {
		socket_out.println(user_input);
	}
	
	private void respondToExitCommand() {
		System.exit(0);
	}
}
