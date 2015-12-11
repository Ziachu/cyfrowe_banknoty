package Support;

import java.math.BigInteger;

public class HiddenBanknote {

	private BigInteger amount;
    private BigInteger banknote_id;
    private BigInteger[] s_series;
    private BigInteger[] u_series;
    private BigInteger[] t_series;
    private BigInteger[] w_series;
	
	public HiddenBanknote() {}

	public BigInteger getAmount() {
		return amount;
	}

	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}
	
	public BigInteger getBanknoteId() {
		return banknote_id;
	}

	public void setBanknoteId(BigInteger banknote_id) {
		this.banknote_id = banknote_id;
	}
	
	public BigInteger[] getSseries() {
		return s_series;
	}
	
	public void setSseries(BigInteger[] s_series) {
		this.s_series = s_series;
	}
	
	public BigInteger[] getUseries() {
		return u_series;
	}
	
	public void setUseries(BigInteger[] u_series) {
		this.u_series = u_series;
	}

	public BigInteger[] getTseries() {
		return t_series;
	}

	public void setTseries(BigInteger[] t_series) {
		this.t_series = t_series;
	}

	public BigInteger[] getWseries() {
		return w_series;
	}

	public void setWseries(BigInteger[] w_series) {
		this.w_series = w_series;
	}

	public void visualizeHiddenBanknote() {
		Loger.println("\t" + TerminalColors.ANSI_GREEN + "*---------------HIDDEN----------------------------------*");
    	Loger.println("\n\t\t\t\tAmount: " + TerminalColors.ANSI_RESET + getAmount());
    	Loger.println("\t\t\t\t" + TerminalColors.ANSI_GREEN + "ID: " + TerminalColors.ANSI_RESET + getBanknoteId());
    	
    	Loger.println("\n\t" + TerminalColors.ANSI_GREEN + "*------------S - series---------------------------------*" 
    				  + TerminalColors.ANSI_RESET);
    	for (int i = 0; i < 2; i++) {
    		Loger.print("\t");
    		Loger.print(getSseries()[i].toString());
    	}
    	Loger.println("\t\t[...]");
    	
    	Loger.println("\t" + TerminalColors.ANSI_GREEN + "*------------U - series---------------------------------*"
    				  + TerminalColors.ANSI_RESET);
    	for (int i = 0; i < 2; i++) {
    		Loger.print("\t");
    		Loger.print(getUseries()[i].toString());
        }
    	Loger.println("\t\t[...]");
    	
    	Loger.println("\t" + TerminalColors.ANSI_GREEN + "*------------T - series---------------------------------*"
    				  + TerminalColors.ANSI_RESET);
    	for (int i = 0; i < 2; i++) {
    		Loger.print("\t");
    		Loger.print(getTseries()[i].toString());
    	}
    	Loger.println("\t\t[...]");
    	
    	Loger.println("\t" + TerminalColors.ANSI_GREEN + "*------------W - series---------------------------------*"
    				  + TerminalColors.ANSI_RESET);
    	for (int i = 0; i < 2; i++) {
    		Loger.print("\t");
    		Loger.print(getWseries()[i].toString());
    	}
    	Loger.println("\t\t[...]");
    	Loger.println("\t" + TerminalColors.ANSI_GREEN + "*-------------------------------------------------------*"  
    				  + TerminalColors.ANSI_RESET);
	}
}
