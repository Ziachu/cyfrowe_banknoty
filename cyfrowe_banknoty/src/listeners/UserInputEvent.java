package listeners;

import java.util.EventObject;

public class UserInputEvent extends EventObject {

	private static final long serialVersionUID = 4825568611147316699L;
	private String user_in;
	
	public UserInputEvent(Object source) {
		super(source);
	}
	
	public UserInputEvent(Object source, String user_in) {
		super(source);
		this.user_in = user_in;
	}
	
	public String getUserIn() {
		return user_in;
	}
}
