package Alice;

import java.io.BufferedReader;
import java.io.PrintWriter;

import Client.User;
import Support.Command;
import Support.CommonCommandManager;
import Support.Loger;
import Support.Series;

public class AliceCommandManager extends CommonCommandManager {

//	private BufferedReader socket_in;
	private PrintWriter socket_out;
	 
	private Command cmd;
	private Command last_cmd;
	private String user_input;
	private boolean waiting_for_next_input;
	
	private Alice alice;
	
	public AliceCommandManager() {
		super();
		
		alice = new Alice(100, 32);
		waiting_for_next_input = false;
	}

    public User getUser() {
        return alice;
    }

	// Ustawia kanały komunikacyjne, tak żeby manager mógł przekazywać komunikaty do serwera
	public void setCommandLine(BufferedReader socket_in, PrintWriter socket_out) {
//		this.socket_in = socket_in;
		this.socket_out = socket_out;
		
		super.setCommandLine(socket_in, socket_out);
	}

	// Spełnia główne zadanie CommandManager'a, zarządza wprowadzonymi komendami
	public void respondToCommand(String msg) {
		Loger.debug("Responding to uncommon user_input: " + msg + " (" + waiting_for_next_input + ")");
		user_input = msg;

		if (!waiting_for_next_input) {
			// Jeżeli nie oczekuje żadnego konkretnego input'u to próbuje wydobyć komendę
			try {
				cmd = Command.valueOf(msg);
				socket_out.println(cmd);

				switch(cmd) {
				case example_series:

					respondToExampleSeriesCommand();
					break;
				case test_alice:

					respondToTestAliceCommand();
					break;
				case generate_banknotes:

					respondToGenerateBanknotesCommand();
					break;
				case save_id:

					respondToSaveIdCommand();
					break;
				case server_get_bank_key:
					Loger.debug("Sending request to server.");
					//sending request to server
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
			} catch (IllegalArgumentException e) {
				Loger.err("Wrong command: " + msg + ".");
			} catch (NullPointerException e) {
				Loger.err("Null value somewhere.");
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

	private void respondToSaveIdCommand() {
		Loger.debug("id_series before import...");
		alice.i_series[0].visualizeSeries();
		alice.i_series[1].visualizeSeries();
		//alice.exportIdToFile();

		alice.importIdFromFile();
		Loger.debug("id_series after export...");
		alice.i_series[0].visualizeSeries();
		alice.i_series[1].visualizeSeries();
	}

	private void respondToGenerateBanknotesCommand() {
		Loger.println("100 banknotes are being generated...");
		for (int i = 0; i < 100; i++) {
			alice.generateBanknote(10.1);
		}

		alice.banknotes.get(0).visualizeBanknote();
	}

	private void respondToTestAliceCommand() {
		Alice alice = new Alice(5, 10);
		
		Loger.println("\nAlice's identification series:");
		for (int i = 0; i < alice.no_identification_series; i++) {
			Loger.print("no. " + i + ".: ");
			alice.i_series[i].visualizeSeries();
			Loger.print("\tL: ");
			alice.l_series[i].visualizeSeries();
			Loger.print("\tR: ");
			alice.r_series[i].visualizeSeries();
			Loger.println("\tHashes:");
			Loger.print("\tU: ");
			alice.u_series[i].visualizeSeries();
			Loger.print("\tW: ");
			alice.w_series[i].visualizeSeries();
		}
	}

	private void respondToExampleSeriesCommand() {
		Series example1 = new Series(10);
		Loger.println("\nRandom series of length: " + example1.getLength() + ".");
		example1.visualizeSeries();
		
		Loger.println("Byte format: #" + example1.getValues().toString() + "#");
		
		Series example2 = new Series(10);
		Loger.println("Another random series of length: " + example2.getLength() + ".");
		example2.visualizeSeries();
		
		Loger.println("Byte format: #" + example2.getValues().toString() + "#");
		
		Loger.println("Let's try to add them.");
		Loger.println("Byte format: #" + (example1.getValues().toString() + example2.getValues().toString()).getBytes() + "#");
	}
}
