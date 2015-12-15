package Bank;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import Client.User;
import Support.Banknote;
import Support.HiddenBanknote;
import Support.Loger;
import Support.RSA;
import Support.Series;

/**
 * Created by Damian on 2015-12-09.
 */
public class Bank extends User {
    private PublicKey public_key;
    @SuppressWarnings("unused")
	private PrivateKey private_key;
    
    public int picked_banknote;
    public ArrayList<HiddenBanknote> hidden_banknotes;
    public ArrayList<Banknote> revealed_banknotes;
    public BigInteger[] secrets;
    
	private Series[] i_series;
	private Series[] r_series;
	private Series[] l_series;
	private Series[] s_series;
	private Series[] b_series;
	private Series[] t_series;
	private Series[] c_series;
	private Series[] w_series;
	private Series[] u_series;
	
	private int no_identification_series;
    
    public Bank (){
    	
    	hidden_banknotes = new ArrayList<HiddenBanknote>();
    	revealed_banknotes = new ArrayList<Banknote>();
    	
        try {
        	KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);

            KeyPair kp = kpg.generateKeyPair();
            public_key = kp.getPublic();
            private_key = kp.getPrivate();

            Loger.mess("[BANK] Utworzono instancje klasy RSA.");
            
        } catch (NoSuchAlgorithmException e) {
            Loger.err("Couldn't initialize KeyPairGenerator.");
        }
    }

    public User getUser() {
        
    	return this;
    }

    public String getPublicKey() {
       
    	return RSA.exportPublicKey(public_key);
    }

    public void setHiddenBanknotes(ArrayList<HiddenBanknote> hidden_banknotes) {
    	this.hidden_banknotes = hidden_banknotes;
    	Loger.println("Ok, hidden banknotes from Alice received.");
        Loger.mess("BANK: Otrzymano zasloniete banknoty od ALICE.");
    }
    
    public boolean haveHiddenBanknotes() {
    	
    	return hidden_banknotes.isEmpty() ? false : true;
    }
    
    public boolean haveRevealedBanknotes() {
    	
    	return revealed_banknotes.isEmpty() ? false : true;
    }
    
    public int getPickedBanknote() { 
    	
    	return picked_banknote; 
    }
    
    public void setSecrets(BigInteger[] secrets) {
    	
    	this.secrets = new BigInteger[secrets.length];
    	this.secrets = secrets;
    	
    	for (int i = 0; i < secrets.length; i++) {
    		Loger.debug(i + ".: " + secrets[i]);
    	}
    }

    public void revealBanknotes() {
    	
    	// mam tablicę sekretów o długości krótszej w stosunku do liczby banknotów
    	for (int i = 0; i < hidden_banknotes.size(); i++) {
    		if (i < picked_banknote) {
    			Loger.debug("Banknote " + i + ". revelead succesfully\n\twith secret = " + secrets[i] + ".");
    			revealed_banknotes.add(hidden_banknotes.get(i).revealBanknote(public_key, secrets[i]));
    		} else if (i == picked_banknote) {
    			// ...
    		} else {
    			Loger.debug("Banknote " + i + ". revelead succesfully\n\twith secret = " + secrets[i-1] + ".");
    			revealed_banknotes.add(hidden_banknotes.get(i).revealBanknote(public_key, secrets[i-1]));
    		}
    	}
    	
    	Loger.debug("All banknotes revealed (without " + picked_banknote + ").");
    	/*Loger.debug("All banknotes revealed. Simple verificiation:");
    	BigInteger test_value = RSA.hideMessage(Converter.doubleToByte(revealed_banknotes.get(0).getAmount()), public_key, secrets[0]);
    	if (hidden_banknotes.get(0).getAmount().equals(test_value)) {
    		Loger.debug("Index 0., amount is the same.");
    	} else {
    		Loger.debug("Index 0., amount different!");
    	}*/
    	
    }

    public void setAliceSeries(Series[] s_series, Series[] b_series, Series[] l_series, 
    						   Series[] t_series, Series[] c_series, Series[] r_series) {
    	this.s_series = s_series;
    	this.b_series = b_series;
    	this.l_series = l_series;
    	this.t_series = t_series;
    	this.c_series = c_series;
    	this.r_series = r_series;
    	
    	no_identification_series = s_series.length;
    }
    
    public boolean verifyBanknotes() {
    	boolean verified = true;
    	
    	// Jeżeli pojawi się jakaś nieprawidłowość od razu zwracane jest false

    	double amount = revealed_banknotes.get(0).getAmount();
    	ArrayList<Integer> ids = new ArrayList<Integer>();
    	
    	int index = 0;
    	
    	for (Banknote banknote : revealed_banknotes) {
    		// Sprawdzam kwotę
    		if (banknote.getAmount() != amount) {
    			Loger.debug(index + " amount...");
    			return false;
    		}
    		
    		// Sprawdzam unikalność id
    		if (ids.contains(banknote.getBanknoteId())) {
    			Loger.debug(index + " id...");
    			return false;
    		} else {
    			ids.add(banknote.getBanknoteId());
    		}
    		
    		// Sprawdzam ciągi S i T
    		if (s_series != banknote.getSseries() || t_series != banknote.getTseries()) {
    			s_series[0].visualizeSeries();
    			banknote.getSseries()[0].visualizeSeries();
    			
    			Loger.println("");
    			
    			t_series[0].visualizeSeries();
    			banknote.getTseries()[0].visualizeSeries();
    			
    			Loger.debug(index + " S/T...");
    			return false;
    		}
    		
    		// Sprawdzam hash'e U i W
    		w_series = Series.hashSeries(no_identification_series, t_series, c_series, r_series);
    		u_series = Series.hashSeries(no_identification_series, s_series, b_series, l_series);
    		if (w_series != banknote.getWseries() || u_series != banknote.getUseries()) {
    			Loger.debug(index + " W/U...");
    			return false;
    		}
    		
    		index++;
    		Loger.debug("Single banknote verified. It's fine"); 
    	}
    	
    	return verified;
    }
}
