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
	
	/* Wydobywanie przez użytkoników klucza od banku. */
	get_bank_key,			// "Hey Serwerze, zdobądź dla mnie klucz banku!"
							// komenda klienta ubiegającego się o klucz publiczny banku
	server_publish_key,		// "Hey Banku, daj mi swój klucz!"
							// komenda wysyłana przez Serwer do Banku (który publikuje swój klucz publiczny)
	client_publish_key,		// "Łap serwerze, to mój klucz!"
							// komenda wysyłana przez Bank do Serwera (który przekazuje dalej klucz publiczny banku)
	client_get_key,			// "No siema kliencie, masz to klucz banku!"
							// komenda wysyłana przez Serwer do Klienta
	
	/* Różnorakie testy */
	test_alice,					// Alice (testowanie klasy Alice, tworzenie ciągów, banknotu etc.)
	test_save_id,				// Alice (testowanie eksportowania ID do pliku)
	test_show_bank_key,			// Alice sprawdza czy dobrze zapamietala klucz
	test_hidden_banknotes,		// Bank (sprawdza, czy otrzymał zakryte banknoty od Alice)
	
	/* Etap pierwszy (tworzenie banknotów) */
	generate_banknotes,				// Alice  (testowanie kalsy Banknote, tworzenie banknotu, wizualizacja etc.)
	hide_banknotes,					// Alice  (zakrywanie banknotów)
	send_hidden_banknotes,			// Alice  (przesyła do server zakryte banknoty)
	server_receive_hidden_banknotes,// Server (odbiera od Alice zakryte banknoty)
	server_send_hidden_banknotes,	// Server (przesyła do banku zakryte banknoty
	receive_hidden_banknotes,		// Bank   (odbiera od Alice zakryte banknoty)
	pick_one_banknote,				// Bank   (wybiera, którego banknotu nie sprawdzi)
	server_banknote_picked,			// Bank   (serwer odbiera wybrane przez Bank "J", banknot, który nie będzie odkryty)
	picked_banknote,				// Server (serwer przekazuje Alice wybrany przez Bank banknot)
	reveal_hidden_banknotes,		// Alice  (ujawnia ukryte banknoty Bankowi, poza j-tym)
	uncover_hidden_banknotes,		// Bank   (odkrywa ukryte banknoty)
	verify_banknotes,				// Bank   (weryfikuje odkryte banknoty)
	
}
