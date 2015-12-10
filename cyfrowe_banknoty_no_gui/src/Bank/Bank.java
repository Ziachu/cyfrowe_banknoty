package Bank;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import Client.User;
import Support.Loger;
import Support.RSA;

/**
 * Created by Damian on 2015-12-09.
 */
public class Bank extends User {
    private PublicKey public_key;
    @SuppressWarnings("unused")
	private PrivateKey private_key;

    public Bank (){
    	
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

    public byte[] getPublicKey() {
        return RSA.exportPublicKey(public_key);
    }
}
