package de.terrarier.lib;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public final class EncryptionUtil {

    // https://github.com/EnumType/HomeSystem/blob/master/src/net/javaexception/homesystem/utils/Crypto.java

   private EncryptionUtil() {}

    public static EncryptionInstance generate(String algorithm, int length) throws NoSuchAlgorithmException {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
            generator.initialize(length);
            KeyPair key = generator.generateKeyPair();
        return new EncryptionInstance(algorithm, length, key.getPrivate(), key.getPublic());
    }

    public static byte[] encrypt(byte[] input, PublicKey key) {
        byte[] out = null;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            out = cipher.doFinal(input);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return out;
    }

    public static byte[] decrypt(byte[] input, PrivateKey key) {
        byte[] out = null;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            out = cipher.doFinal(input);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return out;
    }

    public static PublicKey readPublicKey(byte[] publicKey, String algorithm) {
        PublicKey key = null;
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
            KeyFactory factory = KeyFactory.getInstance(algorithm);
            key = factory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static byte[] publicKeyToBytes(PublicKey key) {
       return key.getEncoded();
    }

}
