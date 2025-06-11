package paketami;
import java.math.*;
import java.security.*;

public class RSA {
    private BigInteger n, d, e;
    private int bitlen = 2048;

    public RSA() {
        SecureRandom r = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitlen / 2, r);
        BigInteger q = BigInteger.probablePrime(bitlen / 2, r);
        n = p.multiply(q);

        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        e = BigInteger.valueOf(65537);
        while (phi.gcd(e).intValue() > 1) {
            e = e.add(BigInteger.TWO);
        }

        d = e.modInverse(phi);
    }


    public BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n);
    }


    public BigInteger decrypt(BigInteger ciphertext) {
        return ciphertext.modPow(d, n);
    }


    public BigInteger getPublicKey() {
        return e;
    }

    public BigInteger getModulus() {
        return n;
    }


    public static BigInteger stringToBigInt(String message) {
        return new BigInteger(message.getBytes());
    }

    public static String bigIntToString(BigInteger message) {
        return new String(message.toByteArray());
    }


    public static void main(String[] args) {
        RSA rsa = new RSA();

        String plaintext = "Best Kuka nai dobrata kuka na sveta! 😀";
        System.out.println("Original message: " + plaintext);

        BigInteger message = stringToBigInt(plaintext);

        BigInteger encrypted = rsa.encrypt(message);
        System.out.println("Encrypted: " + encrypted);

        BigInteger decrypted = rsa.decrypt(encrypted);
        String result = bigIntToString(decrypted);
        System.out.println("Decrypted message: " + result);
    }
}

