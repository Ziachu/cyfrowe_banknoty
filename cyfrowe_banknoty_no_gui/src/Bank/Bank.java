package Bank;

import Client.User;
import Support.Loger;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Damian on 2015-12-09.
 */
public class Bank extends User {
    private Key public_key;
    private Key private_key;

    public Bank (){
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
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

    public Key getPublicKey(){
        return public_key;
    }

}
