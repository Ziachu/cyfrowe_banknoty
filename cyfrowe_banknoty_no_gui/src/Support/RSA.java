package Support;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {

	public static byte[] exportPublicKey (Key public_key) {

		byte[] public_bytes = public_key.getEncoded();
		return public_bytes;
	}
	
	public static byte[] exportKeyToPrivateKeySpecification (Key private_key) {
		
		byte[] private_bytes = private_key.getEncoded();
		return private_bytes;	
	}
	
	public static PublicKey restorePublicKey (byte[] public_bytes) {
		
		PublicKey public_key;
		try {
			
			public_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(public_bytes));
			return public_key;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {

			Loger.err("Couldn't restore public key.");
			return null;
		}
		
	}
	
	public static PrivateKey restorePrivateKey (byte[] private_bytes) {
		
		PrivateKey private_key;
		try {

			private_key = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(private_bytes));
			return private_key;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			
			Loger.err("Couldn't restore private key.");
			return null;
		}
	}
	
	public static byte[] encrypt(String text, Key key){
        byte[] cipher_text=null;
        try{
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipher_text=cipher.doFinal(text.getBytes());
        } catch (Exception e){
            Loger.err("Couldn't encrypt text.");
        }
        return cipher_text;
    }

    public static String decrypt(byte[] text, Key key){
        byte[] decrypted_text=null;
        try{
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted_text = cipher.doFinal(text);
        } catch (Exception e){
            Loger.err("Couldn't decrypt text");
        }
        return new String(decrypted_text);
    }

}
