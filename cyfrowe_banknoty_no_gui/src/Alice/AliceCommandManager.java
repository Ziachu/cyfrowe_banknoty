package Alice;

import Support.Command;
import Support.CommandManager;
import Support.Loger;

public class AliceCommandManager implements CommandManager {

	private Command cmd;
	
	public void setCommand(Command cmd) {
		this.cmd = cmd;
	}
	
	public void respondToCommand(String msg) {
			
		try {
			cmd = Command.valueOf(msg);
			
			switch(cmd) {
			case role:
				respondToRoleCommand();
				break;
			default:
					
				break;
			}
		} catch (NullPointerException e) {
			Loger.println("[err] Wrong command: " + msg + ".");
		}
	}

	private void respondToRoleCommand() {
		// TODO Auto-generated method stub
		
	}
}
