package Alice;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;

import Client.User;
import Support.Command;
import Support.CommonCommandManager;
import Support.Loger;
import Support.RSA;
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
				case test_save_id:

					respondToTestSaveIdCommand();
					break;
				case get_bank_key:
					
					Loger.debug("Wysylanie zapytania do serwera.");
                    break;
				case test_show_bank_key:
					
					respondToTestShowBankKeyCommand();
					break;
				case hide_banknotes:

					respondToHideBanknotesCommand();
					break;
				case send_hidden_banknotes:
					
					respondToSendHiddenBanknotesCommand();
					break;
				case reveal_hidden_banknotes:

					try {
						alice.revealBanknotes(socket_out);
					} catch (UnsupportedEncodingException e) {
						Loger.err("Problem z encodingiem.");
					}					
					
					// TODO: jeżeli trafiam na banknot_i != j to wysyłam jego Z, ciągi do okrycia lewego zobowiązania
					// oraz ciągi do odkrycia prawego zobowiązania
					
					
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

					Loger.println("[info] Komenda (" + cmd.toString() + ") nie jest jeszcze obslugiwana.");
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

	private void respondToSendHiddenBanknotesCommand() {
		if (alice.haveHiddenBanknotes()) {
			// TODO: najpierw przesyłam liczbę banknotów
			int no_banknotes = alice.hidden_banknotes.size();
			Loger.println("[ALICE] Posiadam " + no_banknotes + ". zaslonietych banknotow gotowych do wyslania.\nChyba trzeba sie wziac do roboty!");
			
			socket_out.println(no_banknotes);
			
			// TODO: przesyłam każdy zakryty banknot pokolei
			for (int i = 0; i < no_banknotes; i++) {
				alice.getHiddenBanknote(i).sendHiddenBanknote(socket_out);
				Loger.println("[ALICE] " + i + "-ty banknot wyslany :)");
			}
		} else {
			Loger.warr("Nie mam zadnych ukrytych banknotow :( to smutne, wiecie?.");
		}
	}

	private void respondToHideBanknotesCommand() {
		if (alice.haveBanknotes()) {
			if (alice.havePublicKey()) {
				Loger.mess("Okej, to zaslaniamy te banknoty! :)");
				alice.hideBanknotes();
			} else
				Loger.warr("O kurde! przeciez najpierw trzeba miec klucz publiczny banku! Glupolku :)");
		} else {
			Loger.warr("Moze najpierw poukrywamy jakies banknoty? :)");
		}
	}

	private void respondToTestShowBankKeyCommand() {
		Loger.println("Modulus:\t" + RSA.getModulus(alice.bank_key).toString());
		Loger.println("Exponens:t" + RSA.getPublicExponent((PublicKey) alice.bank_key).toString());		
	}

	private void respondToTestSaveIdCommand() {
		Loger.mess("Ciagi identyfikacyjne przed zaimportowaniem.");
		alice.i_series[0].visualizeSeries();
		alice.i_series[1].visualizeSeries();
		//alice.exportIdToFile();

		alice.importIdFromFile();
		Loger.mess("Ciagi identyfikacyjne po zaimportowaniu: ");
		alice.i_series[0].visualizeSeries();
		alice.i_series[1].visualizeSeries();
		Loger.mess("Sa spoko? :)");
	}

	private void respondToGenerateBanknotesCommand() {
		Loger.println("\ttak testowo tworzymy 5 banknotow :)");
		for (int i = 0; i < 5; i++) {
			alice.generateBanknote(10.1);
		}

		alice.banknotes.get(0).visualizeBanknote();
	}

	private void respondToTestAliceCommand() {
		Alice alice = new Alice(5, 10);
		
		Loger.println("\nCiagi identyfikacyjne Alice(fajne sa :) ):");
		for (int i = 0; i < alice.no_identification_series; i++) {
			Loger.print("nr. " + i + ".: ");
			alice.i_series[i].visualizeSeries();
			Loger.print("\tL: ");
			alice.l_series[i].visualizeSeries();
			Loger.print("\tR: ");
			alice.r_series[i].visualizeSeries();
			Loger.println("\tTeraz Hashe:");
			Loger.print("\tU: ");
			alice.u_series[i].visualizeSeries();
			Loger.print("\tW: ");
			alice.w_series[i].visualizeSeries();
		}
	}

	private void respondToExampleSeriesCommand() {
		Series example1 = new Series(10);
		Loger.println("\nA oto losowy ciag dlugosci: " + example1.getLength() + ".");
		example1.visualizeSeries();
		
		Loger.println("Format bitowy: #" + example1.getValues().toString() + "#");
		
		Series example2 = new Series(10);
		Loger.println("Kolejny losowy ciag dlugosci: " + example2.getLength() + ".");
		example2.visualizeSeries();
		
		Loger.println("Format bitowy: #" + example2.getValues().toString() + "#");
		
		Loger.println("Uda nam sie je dodac? :).");
		Loger.println("Format bitowy dodanych ciagow: #" + (example1.getValues().toString() + example2.getValues().toString()).getBytes() + "#");
		Loger.println("Udalo sie? :)");
	}
}
