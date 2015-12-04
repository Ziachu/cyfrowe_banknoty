package Alice;

import java.io.BufferedReader;
import java.io.PrintWriter;

import Support.Command;
import Support.CommandManager;
import Support.Loger;

public class AliceCommandManager implements CommandManager {

	private Command cmd;
	private BufferedReader socket_in;
	private PrintWriter socket_out;
	
	public void setCommand(Command cmd) {
		this.cmd = cmd;
	}
	
	public void setCommandLine(BufferedReader socket_in, PrintWriter socket_out) {
		this.socket_in = socket_in;
		this.socket_out = socket_out;
	}

	public void respondToCommand(String msg) {
			
		try {
			cmd = Command.valueOf(msg);
			socket_out.println(cmd);
			
			switch(cmd) {
			case role:				
				respondToRoleCommand();
				
				break;
			case exit:
				System.exit(0);
				break;
			default:
				
				break;
			}
		} catch (NullPointerException e) {
			Loger.println("[err] Wrong command: " + msg + ".");
		}
	}

	private void respondToRoleCommand() {
		
	}

}
