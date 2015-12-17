package Support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.PublicKey;

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

	public void sendHiddenBanknote(PrintWriter out) {
		
		/* Wysyłamy w nastepującej kolejności
		 * 		- amount
		 * 		- banknote_id
		 * 		- liczbę ciągów S
		 * 		- każdy ciąg S...
		 * 		- liczbę ciągów U
		 * 		- ...
		 */	
		
		out.println(amount);
		out.println(banknote_id);

		out.println(s_series.length);
		for (BigInteger series : s_series) {
			out.println(series);
		}
		
		out.println(u_series.length);
		for (BigInteger series : u_series) {
			out.println(series);
		}
		
		out.println(t_series.length);
		for (BigInteger series : t_series) {
			out.println(series);
		}
		
		out.println(w_series.length);
		for (BigInteger series : w_series) {
			out.println(series);
		}
	}
	
	public void receiveHiddenBanknote(BufferedReader in) {
		
		try {
			setAmount(new BigInteger(in.readLine()));
			setBanknoteId(banknote_id = new BigInteger(in.readLine()));
			
			int no_s_series = Integer.parseInt(in.readLine());
			BigInteger[] s_series = new BigInteger[no_s_series];
			
			for (int i = 0; i < no_s_series; i++) {
				s_series[i] = new BigInteger(in.readLine());
			}
			
			setSseries(s_series);
			
			int no_u_series = Integer.parseInt(in.readLine());
			BigInteger[] u_series = new BigInteger[no_u_series];
			
			for (int i = 0; i < no_u_series; i++) {
				u_series[i] = new BigInteger(in.readLine());
			}
			
			setUseries(u_series);
			
			int no_t_series = Integer.parseInt(in.readLine());
			BigInteger[] t_series = new BigInteger[no_t_series];
			
			for (int i = 0; i < no_t_series; i++) {
				t_series[i] = new BigInteger(in.readLine());
			}
			
			setTseries(t_series);
			
			int no_w_series = Integer.parseInt(in.readLine());
			BigInteger[] w_series = new BigInteger[no_w_series];
			
			for (int i = 0; i < no_w_series; i++) {
				w_series[i] = new BigInteger(in.readLine());
			}
			
			setWseries(w_series);
		} catch (IOException e) {
			Loger.err("Couldn't parse to BigInteger what was found in socket_in");
		}
	}
	
	public Banknote revealBanknote(PublicKey pub_key, BigInteger secret) {
    	Banknote banknote = new Banknote();
    	
    	int banknote_id = Converter.byteToInt(RSA.revealMessage(getBanknoteId(), pub_key, secret));
    	banknote.setBanknoteId(banknote_id);
    	
    	double amount = Converter.byteToDouble(RSA.revealMessage(getAmount(), pub_key, secret));
    	banknote.setAmount(amount);
    	
    	int no_id_series = this.s_series.length;
    	
    	Series[] s_series = new Series[no_id_series];
    	for (int i = 0; i < no_id_series; i++) {
    		s_series[i] = new Series(RSA.revealMessage(this.s_series[i], pub_key, secret));
    		Loger.print("Odkrywanie banknotów: s_series[" + i + "]: ");
    		s_series[i].visualizeSeries();
    	}
    	
    	banknote.setSseries(s_series);
    	    	
    	Series[] u_series = new Series[no_id_series];
    	for (int i = 0; i < no_id_series; i++) {
    		u_series[i] = new Series(RSA.revealMessage(this.u_series[i], pub_key, secret));
    	}
    	
    	banknote.setUseries(u_series);
    	
    	Series[] t_series = new Series[no_id_series];
    	for (int i = 0; i < no_id_series; i++) {
    		t_series[i] = new Series(RSA.revealMessage(this.t_series[i], pub_key, secret));
    	}
    	
    	banknote.setTseries(t_series);
    	
    	Series[] w_series = new Series[no_id_series];
    	for (int i = 0; i < no_id_series; i++) {
    		w_series[i] = new Series(RSA.revealMessage(this.w_series[i], pub_key, secret));
    	}
    	
    	banknote.setWseries(w_series);
    	
    	return banknote;
    }
}
