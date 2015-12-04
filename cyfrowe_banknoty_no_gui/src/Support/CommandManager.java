package Support;

import java.io.BufferedReader;
import java.io.PrintWriter;

public interface CommandManager {

	public void setCommand(Command cmd);
	public void setCommandLine(BufferedReader socket_in, PrintWriter socket_out);
	public void respondToCommand(String cmd);
}
