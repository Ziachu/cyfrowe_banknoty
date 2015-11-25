package vendor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import listeners.CommandListener;

import support.CommonCommandsManager;

public class Vendor extends Thread {
	private int port;
	private String server_address;
	private final String user_role = "Vendor";
	
	private Socket socket;
	
	private BufferedReader socket_in;
	private PrintWriter socket_out;
	private CommandListener cmd_listener;
	
	private CommonCommandsManager cc_manager;
	
	public Vendor(String server_address, int port) {
		
		this.port = port;
		this.server_address = server_address;
	}
	
	public void setCommandListener(CommandListener listener) {
		this.cmd_listener = listener;
	}
	
	public void run() {
		try {
			this.socket = new Socket(this.server_address, this.port);
			
			socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socket_out = new PrintWriter(socket.getOutputStream(), true);
		
			cc_manager = new CommonCommandsManager(user_role, cmd_listener, socket, socket_in, socket_out);
			cc_manager.AnnounceStart();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void manageUserInput(String user_input) {
		cc_manager.ManageUserInput(user_input);
	}
}