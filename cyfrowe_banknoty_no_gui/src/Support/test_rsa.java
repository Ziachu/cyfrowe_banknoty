package Support;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class test_rsa {

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// stwórz klucz
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048, new SecureRandom());

        KeyPair kp = kpg.generateKeyPair();
        RSAPublicKey public_key = (RSAPublicKey) kp.getPublic();
        RSAPrivateKey private_key = (RSAPrivateKey) kp.getPrivate();
        System.out.println("public_key:\n" + public_key);
        System.out.println("----------------------------");
        System.out.println("private_key:\n" + private_key);
        System.out.println("----------------------------");
        
        // stwórz wiadomość
        String msg = "Gdzie zabrać Martę?";
        byte[] raw_msg = msg.getBytes("UTF-8");
        BigInteger m = new BigInteger(raw_msg);
        
        System.out.println("msg:\n" + msg + "\n" + m);
        System.out.println("----------------------------");

        // zdobądź E, D oraz N and "one"
        BigInteger e = public_key.getPublicExponent();
        BigInteger d = private_key.getPrivateExponent();
        BigInteger n = public_key.getModulus();
        BigInteger one = new BigInteger("1");
        
        System.out.println("e:\t" + e + "\nd:\t" + d + "\nn:\t" + n);
        System.out.println("----------------------------");
        
        // wylosuj sekret
        SecureRandom secure_rand = SecureRandom.getInstance("SHA1PRNG");
        byte[] random_bytes = new byte[10];
        BigInteger z = null;
        BigInteger gdc = null;

        //sprawdź czy sekret jest dobry
        do {
        	secure_rand.nextBytes(random_bytes);
        	z = new BigInteger(1, random_bytes);
        	gdc = z.gcd(n);
        	System.out.println("gdc:\t" + gdc);
        } while (!gdc.equals(one) || z.compareTo(n) >= 0 || z.compareTo(one) <= 0);
        
        System.out.println("----------------------------");
        System.out.println("random_secret:\n" + z);
        System.out.println("----------------------------");
        
        // zakryj wiadomość
        // y = m * z^{e} (mod n)
        BigInteger y = (z.modPow(e, n).multiply(m)).mod(n);
        System.out.println("y:\t" + y);
        System.out.println("----------------------------");
        
        // odkryj wiadomość
        // m = y * z^{-e} (mod n)
        BigInteger _m = (z.modPow(e.negate(), n).multiply(y)).mod(n);
        System.out.println("_m:\t" + _m);
        System.out.println("----------------------------");
        
        // porównaj odkrytą wiadomość z oryginałem
        if (_m.equals(m))
        	System.out.println("Odkryta wiadomość jest taka sama jak oryginalna.");
        else
        	System.out.println("Odkryta wiadomość różni się od oryginału.");
        System.out.println("----------------------------");
        
        // podpisz zakrytą wiadomość
        // v = y^{d} (mod n)
        BigInteger v = y.modPow(d, n);
        System.out.println("v:\t" + v);
        System.out.println("----------------------------");
        
        // odkryj podpis
        // sig = v * z^{-1} (mod N)
//        BigInteger sig = z.modInverse(n).multiply(v).mod(n);
        BigInteger sig = (z.modPow(one.negate(), n).multiply(v)).mod(n);
        System.out.println("sig:\t" + sig);
        System.out.println("----------------------------");
        
        // zweryfikuj podpis
        	// weryfikacja banku: podpis = m^d (mod n)
        BigInteger bank_sig_of_m = m.modPow(d, n);
        System.out.println("bank_sig_of_m:\t" + bank_sig_of_m);
        System.out.println("----------------------------");
        
        if (bank_sig_of_m.equals(sig))
        	System.out.println("Signatures are identical.");
        else
        	System.out.println("There is some difference in signatures.");
        System.out.println("----------------------------");

        	// weryfikacja sprzedawcy: podpis = m^{e} (mod n)
        BigInteger vendor_sig_of_m = m.modPow(e, n);
        System.out.println("vendor_sig_of_m:\t" + vendor_sig_of_m);
        System.out.println("----------------------------");
        
        if (vendor_sig_of_m.equals(sig))
        	System.out.println("Signatures are identical.");
        else
        	System.out.println("There is some difference in signatures.");
        System.out.println("----------------------------");

        	// odzyskanie wiadomości z podpisu?
        BigInteger __m = sig.modPow(e, n);
        System.out.println("__m:\t" + __m);
        System.out.println("----------------------------");
        
        if (m.equals(__m))
        	System.out.println("Messages are identical.");
        else
        	System.out.println("There is some difference in messages.");
        System.out.println("----------------------------");
        
        	// sprawdź, czy podpisy banku i sprzedawcy są takie same
        if (bank_sig_of_m.equals(vendor_sig_of_m))
        	System.out.println("OK, vendor and bank got same signatures.");
        else
        	System.out.println("Nope, vendor and bank still have different signatures.");
        
	}

}
