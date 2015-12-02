package support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import listeners.CommandListener;

public class CommonCommandsManager {
	private Roles role;
	private String role_prompt;

	private Socket socket;
	private BufferedReader socket_in;
	private PrintWriter socket_out;
	private CommandListener cmd_listener;

	private ListenerThread listener_thread;
	
	private boolean waiting_for_next_input;
	private Commands last_command;

	public CommonCommandsManager(String role, CommandListener cmd_listener,
			Socket socket, BufferedReader socket_in, PrintWriter socket_out) {
		try {
			this.role = Roles.valueOf(role);
		} catch (IllegalArgumentException err) {
			this.role = Roles.Vendor;
		}

		switch (this.role) {
		case Alice:
			role_prompt = "[alc]";
			break;
		case Bank:
			role_prompt = "[bnk]";
			break;
		case Vendor:
			role_prompt = "[vdr]";
			break;
		default:
			role_prompt = "[vdr]";
			break;
		}

		this.cmd_listener = cmd_listener;
		this.socket = socket;
		this.socket_in = socket_in;
		this.socket_out = socket_out;

		listener_thread = new ListenerThread(this.socket_in, this.cmd_listener);
		
		waiting_for_next_input = false;
	}

	public void AnnounceStart() {
		listener_thread.start();
		
		// Bank loguje się na serwerze
		socket_out.println("role");
		socket_out.println(role);
		
		cmd_listener.CommandEmitted(role_prompt + " " + role + " up and running.", true);
	}
	
	public void ManageUserInput(String user_input) {
		Commands cmd;

		if (!waiting_for_next_input) {
			try {
				cmd = Commands.valueOf(user_input);
				socket_out.println(cmd);

				switch (cmd) {
				case usr:
					// stąd jedynie wysyłam polecnie do serwera, inny wątek
					// nasłuchuje odpowiedzi
					last_command = cmd;
					waiting_for_next_input = false;

					break;
				case exit:
					cmd_listener.CommandEmitted(role_prompt
							+ " Shutting down the connection.", true);

					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					last_command = cmd;
					waiting_for_next_input = false;

					// TODO: Shut down the connection;

					break;
				case series:
					cmd_listener.CommandEmitted(role_prompt + " Enter receiver's name:", false);

					last_command = cmd;
					waiting_for_next_input = true;

					break;
				case banknote:

					break;
				default:
					cmd_listener.CommandEmitted(role_prompt
							+ " Wrong command, try again.", true);

					break;
				}
			} catch (IllegalArgumentException e) {
				cmd_listener.CommandEmitted(role_prompt + " Wrong command.",
						true);
			}
		} else {
			switch (last_command) {
			case series:
				String receiver = user_input;
				socket_out.println(receiver);

				Series series = new Series(50);
				try {
					series.sendSeries(socket_out);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				series.visualizeSeries(cmd_listener);

				break;
			case banknote:

				break;
			default:
				cmd_listener.CommandEmitted(role_prompt
						+ " Wrong command, try again.", true);

				break;
			}
		}
	}
}

class ListenerThread extends Thread {
	BufferedReader socket_in;
	Commands cmd;
	String user_input;
	CommandListener cmd_listener;
		
	ListenerThread(BufferedReader socket_in, CommandListener cmd_listener) {
		this.socket_in = socket_in;
		this.cmd_listener = cmd_listener;
	}
		
	@Override
	public void run() {
		while (true) {
			try {
				if (socket_in.ready()) {
					user_input = socket_in.readLine();
						
					try {
						cmd = Commands.valueOf(user_input);
							
						switch(cmd) {
						case usr:
							int no_users = Integer.parseInt(socket_in.readLine());
								
							String response = "(" + Integer.toString(no_users) + ") ";
							for (int i = 0; i < no_users; i++) {
								response += socket_in.readLine() + " ";
							}
								
							cmd_listener.CommandEmitted("[bnk] " + response, true);
								
							break;
						case series:								
							Series series = new Series();
							series.receiveSeries(socket_in);
								
							cmd_listener.CommandEmitted("[bnk] Series came from server:", true);
							series.visualizeSeries(cmd_listener);
								
							break;
						default:
							cmd_listener.CommandEmitted("[bnk] Wrong command.", true);
							break;
						}
					} catch (IllegalArgumentException e) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
							
						cmd_listener.CommandEmitted("[bnk] " + user_input, true);
						continue;
					}
				}
			} catch (IOException e) {
				cmd_listener.CommandEmitted("[bnk] IO problem: Bank in ListenerThread's run() method", true);
				e.printStackTrace();
			}
		}
	}
}