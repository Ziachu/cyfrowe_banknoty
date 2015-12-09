package Support;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSA {

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

//	public static void main(String[] args) {
//		try {
//			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//			kpg.initialize(2048);
//
//			KeyPair kp = kpg.genKeyPair();
//			Key publicKey = kp.getPublic();
//			Key privateKey = kp.getPrivate();
//
//			System.out.println(publicKey);
//			System.out.println(privateKey);
//
//			KeyFactory factory = KeyFactory.getInstance("RSA");
//			RSAPublicKeySpec pub = factory.getKeySpec(publicKey, RSAPublicKeySpec.class);
//			RSAPrivateKeySpec priv = factory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
//
//			System.out.println(pub.getModulus().bitLength());
//
//		} catch (NoSuchAlgorithmException e) {
//			Loger.err("There's no such algorithm!");
//		} catch (InvalidKeySpecException e) {
//			Loger.err("Couldn't get key speciifications.");
//		}
//	}

}
