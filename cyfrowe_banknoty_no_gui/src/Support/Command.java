package Support;

// Jeżeli dodajesz komendę uniwersalną, pamiętaj żeby dodać ją również
// do tablicy "common_commands" w pliku "SystemUser.java"

public enum Command {
	role,
	usr,
	exit,
	series,
	commands,
	example_series,
	
	server_get_bank_key,		// "Hey Serwerze, zdobądź dla mnie klucz banku!"
								// komenda klienta ubiegającego się o klucz publiczny banku
	client_publish_key,			// "Hey Banku, daj mi swój klucz!"
								// komenda wysyłana przez Serwer do Banku (który publikuje swój klucz publiczny)
	server_publish_key,			// "Łap serwerze, to mój klucz!"
								// komenda wysyłana przez Bank do Serwera (który przekazuje dalej klucz publiczny banku)
	client_get_key,		// "No siema kliencie, masz to klucz banku!"
								// komenda wysyłana przez Serwer do Klienta
	
	test_alice,					// Alice (testowanie klasy Alice, tworzenie ciągów, banknotu etc.)
	generate_banknotes,			// Alice (testowanie kalsy Banknote, tworzenie banknotu, wizualizacja etc.)
	save_id,					// Alice (testowanie eksportowania ID do pliku)
}