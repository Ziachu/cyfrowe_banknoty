package Support;

public class Loger {
	static public void println(String msg) {
		System.out.println(msg);
	}
	
	static public void print(String msg) {
		System.out.print(msg);
	}
	
	static public void err(String msg) {
		System.out.println(TerminalColors.ANSI_RED + "\t[err] " + msg + TerminalColors.ANSI_RESET);
	}
	
	static public void debug(String msg) {
		System.out.println(TerminalColors.ANSI_YELLOW + "\t[debug] " + msg + TerminalColors.ANSI_RESET);
	}
	
	static public void warr(String msg) {
		System.out.println(TerminalColors.ANSI_PURPLE + "\t[warning] " + msg + TerminalColors.ANSI_RESET);
	}
}
