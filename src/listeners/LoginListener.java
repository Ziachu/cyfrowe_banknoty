package listeners;

import java.util.EventListener;



public interface LoginListener extends EventListener {
	public void loginEventOccured(LoginEvent e);
}
