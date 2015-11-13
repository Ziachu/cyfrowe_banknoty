package support;

public class Log {
	
	// "FLUSHING" LINE
	public static void log() {
		System.out.println();
	}
	
	// LOG WITH [log] PROMPT
	public static void log(String msg) {
		System.out.println("[log] " + msg);
	}
	
	public static void log(String msg, boolean new_line) {
		if (new_line)
			System.out.println("[log] " + msg);
		else
			System.out.print("[log] " + msg);
	}
	
	// LOG IN ONE LINE WITHOUT PROMPT
	public static void app(String msg) {
		System.out.print(msg + " ");
	}
	
	// LOG WITH [inp] PROMPT, TO WAIT FOR USER INPUT
	public static void inp() {
		System.out.print("[inp] ");
	}
	
	// LOG WITH [inp] + sth PROMPT, TO WAIT FOR USER INPUT 
	public static void inp(String prompt) {
		System.out.print("[inp] " + prompt + " ");
	}
	
	// SIMPLE ERROR LOG
	public static void err(String msg) {
		System.out.println("[err] " + msg);
	}
	
	// LOG WITH [msg] PROMPT
	public static void msg(String msg) {
		System.out.println("[msg] " + msg);
	}
	
	public static void msg(String msg, boolean new_line) {
		if (new_line)
			System.out.println("[msg] " + msg);
		else
			System.out.print("[msg] " + msg);
	}
}
