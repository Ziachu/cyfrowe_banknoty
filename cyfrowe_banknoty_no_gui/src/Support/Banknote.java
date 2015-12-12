package Support;


import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Random;

/**
 TODO: musi miec miejsca na IDBanknotu, Kwotę, 4 listy T-W, S-U po 100 obiektow typu series
 Generate_banknote(Y-to podaje ALICE, Xi-losowe(z series), I-liste 100 obiektow typu series(identyfikujacych) - otrzymuje po 1 elemencie
 z listy i sobie zczytuje do swojej listy te 100 banknotow w forze
 ALICE PODAJE Y i CIAGI IDENTYFIKUJACE(sama sobie je generuje z series zeby miec swoj komplet)
 */

public class Banknote {
    private double amount;
    private int banknote_id;
    private Series[] s_series;
    private Series[] u_series;
    private Series[] t_series;
    private Series[] w_series;
    
    private HiddenBanknote hidden_version;

    public Banknote (double amount, int banknote_id, 
    				 Series[] s_series, Series[] u_series,
    				 Series[] t_series, Series[] w_series) {
        this.amount = amount;
        this.banknote_id = banknote_id;
        this.s_series = s_series;
        this.u_series = u_series;
        this.t_series = t_series;
        this.w_series = w_series;
    }

    public int getBanknoteId() { return banknote_id; }

    public void setBanknoteId(int id) { banknote_id = id; }

    public Series[] getSseries() { return s_series; }

    public void setSseries(Series[] series) { u_series = series; }

    public Series[] getUseries() {return u_series;}

    public void setUseries(Series[] series) { t_series = series; }

    public Series[] getTseries() {return t_series;}

    public void setTseries(Series[] series) { s_series = series; }

    public Series[] getWseries() {return w_series;}

    public void setWseries(Series[] series) { w_series = series; }

    public double getAmount() { return amount; }
    
    public void setAmount(double cash) { amount = cash; }

    public void generateBanknoteId(){
        
    	Random rand = new Random();
        int id = rand.nextInt(Integer.MAX_VALUE);
       
        this.banknote_id = id; 
    }

    public void visualizeBanknote() {
    	
    	Loger.println("\t" + TerminalColors.ANSI_GREEN + "*-------------------------------------------------------*");
    	Loger.println("\n\t\t\t\tAmount: " + TerminalColors.ANSI_RESET + getAmount());
    	Loger.println("\t\t\t\t" + TerminalColors.ANSI_GREEN + "ID: " + TerminalColors.ANSI_RESET + getBanknoteId());
    	
    	Loger.println("\n\t" + TerminalColors.ANSI_GREEN + "*------------S - series---------------------------------*" 
    				  + TerminalColors.ANSI_RESET);
    	for (int i = 0; i < 2; i++) {
    		Loger.print("\t");
    		getSseries()[i].visualizeSeries();
    	}
    	Loger.println("\t\t[...]");
    	
    	Loger.println("\t" + TerminalColors.ANSI_GREEN + "*------------U - series---------------------------------*"
    				  + TerminalColors.ANSI_RESET);
    	for (int i = 0; i < 2; i++) {
    		Loger.print("\t");
    		getUseries()[i].visualizeSeries();
        }
    	Loger.println("\t\t[...]");
    	
    	Loger.println("\t" + TerminalColors.ANSI_GREEN + "*------------T - series---------------------------------*"
    				  + TerminalColors.ANSI_RESET);
    	for (int i = 0; i < 2; i++) {
    		Loger.print("\t");
    		getTseries()[i].visualizeSeries();
    	}
    	Loger.println("\t\t[...]");
    	
    	Loger.println("\t" + TerminalColors.ANSI_GREEN + "*------------W - series---------------------------------*"
    				  + TerminalColors.ANSI_RESET);
    	for (int i = 0; i < 2; i++) {
    		Loger.print("\t");
    		getWseries()[i].visualizeSeries();
    	}
    	Loger.println("\t\t[...]");
    	Loger.println("\t" + TerminalColors.ANSI_GREEN + "*-------------------------------------------------------*"  
    				  + TerminalColors.ANSI_RESET);
    }

    public HiddenBanknote hideBanknote(PublicKey pub_key, BigInteger secret){
    	
    	// zakrywamy wszystko osobno w banknocie: kwotę, identyfikator, ciągi
    	HiddenBanknote hidden = new HiddenBanknote();

    	byte[] raw_id = Converter.intToByte(this.banknote_id);
    	hidden.setBanknoteId(RSA.hideMessage(raw_id, pub_key, secret));
    	
    	byte[] raw_amount = Converter.doubleToByte(this.amount);
    	hidden.setAmount(RSA.hideMessage(raw_amount, pub_key, secret));
    	
    	BigInteger[] hidden_s_series = new BigInteger[s_series.length];
    	for (int i = 0; i < s_series.length; i++) {
    		hidden_s_series[i] = RSA.hideMessage(s_series[i].getValues(), pub_key, secret);
    	}
    	hidden.setSseries(hidden_s_series);
    	
    	BigInteger[] hidden_u_series = new BigInteger[u_series.length];
    	for (int i = 0; i < u_series.length; i++) {
    		hidden_u_series[i] = RSA.hideMessage(u_series[i].getValues(), pub_key, secret);
    	}
    	hidden.setUseries(hidden_u_series);

    	BigInteger[] hidden_t_series = new BigInteger[t_series.length];
    	for (int i = 0; i < t_series.length; i++) {
    		hidden_t_series[i] = RSA.hideMessage(t_series[i].getValues(), pub_key, secret);
    	}
    	hidden.setTseries(hidden_t_series);
    	
    	BigInteger[] hidden_w_series = new BigInteger[w_series.length];
    	for (int i = 0; i < w_series.length; i++) {
    		hidden_w_series[i] = RSA.hideMessage(w_series[i].getValues(), pub_key, secret);
    	}
    	hidden.setWseries(hidden_w_series);
    	
    	hidden_version = hidden;
        return hidden;
    }

}
