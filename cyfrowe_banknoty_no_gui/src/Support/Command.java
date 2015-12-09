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
	test_alice,			// Alice (testowanie klasy Alice, tworzenie ciągów, banknotu etc.)
	generate_banknotes,	// Alice (testowanie kalsy Banknote, tworzenie banknotu, wizualizacja etc.)
	save_id				// Alice (testowanie eksportowania ID do pliku)
}