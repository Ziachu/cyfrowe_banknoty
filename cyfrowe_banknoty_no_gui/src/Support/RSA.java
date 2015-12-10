package Support;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.*;
import java.util.Base64;


import javax.crypto.Cipher;

public class RSA {

	public static String exportPublicKey (Key public_key) {

		String public_string = Base64.getEncoder().encodeToString(public_key.getEncoded());
		return public_string;
	}
	
//	public static byte[] exportKeyToPrivateKeySpecification (Key private_key) {
//
//		byte[] private_bytes = private_key.getEncoded();
//		return private_bytes;
//	}
	
	public static PublicKey restorePublicKey (String public_string) {
		
		PublicKey public_key;
		try {

			byte[] public_bytes = Base64.getDecoder().decode(public_string);
			public_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(public_bytes));
			return public_key;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {

			Loger.err("Couldn't restore public key.");
			return null;
		}
		
	}
	
//	public static PrivateKey restorePrivateKey (byte[] private_bytes) {
//
//		PrivateKey private_key;
//		try {
//
//			private_key = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(private_bytes));
//			return private_key;
//		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
//
//			Loger.err("Couldn't restore private key.");
//			return null;
//		}
//	}
	
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

	public static BigInteger getModulus(Key key){
		try {
			RSAPublicKeySpec spec = KeyFactory.getInstance("RSA").getKeySpec(key,RSAPublicKeySpec.class);
			BigInteger modulus = spec.getModulus();
			return modulus;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			Loger.err("Couldn't get RSA specification in getModulus");
			return null;
		}
	}

	public static BigInteger getPublicExponent(PublicKey pub_key){
		try {
			RSAPublicKeySpec spec = KeyFactory.getInstance("RSA").getKeySpec(pub_key,RSAPublicKeySpec.class);
			BigInteger pub_exp = spec.getPublicExponent();
			return pub_exp;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			Loger.err("Couldn't get RSA specification in getPublicExponent");
			return null;
		}
	}

	public static BigInteger getPrivateExponent(PrivateKey priv_key){
		try {
			RSAPrivateKeySpec spec = KeyFactory.getInstance("RSA").getKeySpec(priv_key, RSAPrivateKeySpec.class);
			BigInteger priv_exp = spec.getModulus();
			return priv_exp;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			Loger.err("Couldn't get RSA specification in getPrivateExponent");
			return null;
		}
	}

}
