package listeners;

import java.util.EventListener;

public interface UserInputListener extends EventListener{
	public void userWrites(UserInputEvent e);
}
