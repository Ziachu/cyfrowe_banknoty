package Support;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSA {

	public static void main(String[] args) {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			
			KeyPair kp = kpg.genKeyPair();
			Key publicKey = kp.getPublic();
			Key privateKey = kp.getPrivate();
			
			System.out.println(publicKey);
			System.out.println(privateKey);
			
			KeyFactory factory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = factory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			RSAPrivateKeySpec priv = factory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
		
			System.out.println(pub.getModulus().bitLength());
		
		} catch (NoSuchAlgorithmException e) {
			Loger.err("There's no such algorithm!");
		} catch (InvalidKeySpecException e) {
			Loger.err("Couldn't get key speciifications.");
		}
	}

}
