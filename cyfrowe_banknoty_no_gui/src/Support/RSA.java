package Support;

import javax.crypto.Cipher;
import java.security.Key;

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

}
