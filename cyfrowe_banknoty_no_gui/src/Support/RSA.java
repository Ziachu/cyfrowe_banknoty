package Support;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSA {

	// Bank eksportuje swój klucz publiczny w postaci string'a w Base64
	public static String exportPublicKey (Key public_key) {

		String public_string = Base64.getEncoder().encodeToString(public_key.getEncoded());
		return public_string;
	}
	
//	public static byte[] exportKeyToPrivateKeySpecification (Key private_key) {
//
//		byte[] private_bytes = private_key.getEncoded();
//		return private_bytes;
//	}
	
	// Alice / Sprzedawca "odtwarzają" klucz banku z postaci string'a w Base64 
	public static PublicKey restorePublicKey (String public_string) {
		
		PublicKey public_key;
		try {

			byte[] public_bytes = Base64.getDecoder().decode(public_string);
			public_key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(public_bytes));
			return public_key;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {

			Loger.err("[RSA] Nie mozna uzyskac klucza publicznego.");
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
	
	// Szyfrowanie zadanym kluczem
	public static byte[] encrypt(String text, Key key){
        byte[] cipher_text=null;
        try{
			Loger.mess("[RSA] Trwa szyfrowanie...");
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipher_text=cipher.doFinal(text.getBytes());
        } catch (Exception e){
            Loger.err("[RSA] Nie mozna zaszyfrowac tekstu.");
        }
        return cipher_text;
    }

	// Odszyfrowanie zadanym kluczem
    public static String decrypt(byte[] text, Key key){
        byte[] decrypted_text=null;
        try{
			Loger.mess("[RSA] Trwa odszyfrowywanie...");
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted_text = cipher.doFinal(text);
        } catch (Exception e){
            Loger.err("[RSA] Nie udalo sie odszyfrowac tekstu");
        }
		Loger.mess("[RSA] Odszyfrowanie zakonczone powodzeniem.");
        return new String(decrypted_text);
    }

    // Zwrócenie N'ki z zadanego klucza (publicznego, lub prywatnego)
	public static BigInteger getModulus(Key key){
		try {
			RSAPublicKeySpec spec = KeyFactory.getInstance("RSA").getKeySpec(key,RSAPublicKeySpec.class);
			BigInteger modulus = spec.getModulus();
			return modulus;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			Loger.err("[RSA] Proba uzyskania modulusa nieudana.");
			return null;
		}
	}

	// Zwrócenie E z publicznego klucza
	public static BigInteger getPublicExponent(PublicKey pub_key){
		try {
			RSAPublicKeySpec spec = KeyFactory.getInstance("RSA").getKeySpec(pub_key,RSAPublicKeySpec.class);
			BigInteger pub_exp = spec.getPublicExponent();
			return pub_exp;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			Loger.err("[RSA] Proba uzyskania eksponenta(E) nieudana.");
			return null;
		}
	}

	// Zwrócenie D z prywatnego klucza
	public static BigInteger getPrivateExponent(PrivateKey priv_key){
		try {
			RSAPrivateKeySpec spec = KeyFactory.getInstance("RSA").getKeySpec(priv_key, RSAPrivateKeySpec.class);
			BigInteger priv_exp = spec.getModulus();
			return priv_exp;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			Loger.err("[RSA] Proba uzyskania eksponenta(D) nieudana.");
			return null;
		}
	}
	
	// Wylosowanie Z (sekretu) do zakrycia banknotów, używając klucza publicznego
	public static BigInteger drawRandomSecret(PublicKey pub_key) {
		try {
			SecureRandom secure_rand = SecureRandom.getInstance("SHA1PRNG");
			byte[] random_bytes = new byte[10];
			BigInteger n = getModulus(pub_key);
			BigInteger z = null;
			BigInteger gdc = null;
			BigInteger one = new BigInteger("1");
		
			do {
				secure_rand.nextBytes(random_bytes);
				z = new BigInteger(1, random_bytes);
				gdc = z.gcd(n);
			} while (!gdc.equals(one) || z.compareTo(n) >= 0 || z.compareTo(one) <= 0);

			return z;

		} catch (NoSuchAlgorithmException e) {
			Loger.err("[RSA]Nie jestem w stanie stworzyc instancji SHA1PRNG (w trakcie tworzenia losowego sekretu).");
			return null;
		}	
	}
	
	public static BigInteger hideMessage(byte[] raw_msg, PublicKey pub_key, BigInteger secret) {

        BigInteger m = new BigInteger(raw_msg);
		BigInteger n = getModulus(pub_key);
		BigInteger e = getPublicExponent(pub_key);
		
		// y = mz^e (mod n) 
        BigInteger y = (secret.modPow(e, n).multiply(m)).mod(n);
        
        return y;
	}
	
	public static byte[] revealMessage(BigInteger hidden_msg, PublicKey pub_key, BigInteger secret) {

		BigInteger n = getModulus(pub_key);
		BigInteger e = getPublicExponent(pub_key);
	
		// m = yz^{-e} (mod n)
		BigInteger m = (secret.modPow(e.negate(), n).multiply(hidden_msg)).mod(n);
		
		return m.toByteArray();
	}
}
