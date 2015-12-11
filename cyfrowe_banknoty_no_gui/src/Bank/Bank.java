package Bank;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import Client.User;
import Support.HiddenBanknote;
import Support.Loger;
import Support.RSA;

/**
 * Created by Damian on 2015-12-09.
 */
public class Bank extends User {
    private PublicKey public_key;
    @SuppressWarnings("unused")
	private PrivateKey private_key;
    
    public ArrayList<HiddenBanknote> hidden_banknotes;
    
    public Bank (){
    	
    	hidden_banknotes = new ArrayList<HiddenBanknote>();
    	
        try {
        	KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);

            KeyPair kp = kpg.generateKeyPair();
            public_key = kp.getPublic();
            private_key = kp.getPrivate();
            
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
    }
    
    public boolean haveHiddenBanknotes() {
    	
    	return hidden_banknotes.isEmpty() ? false : true;
    }
}
