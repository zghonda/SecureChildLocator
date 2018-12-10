package project.sirs.scl;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.security.auth.DestroyFailedException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public final class Cryptography {

    private static final String ALGORITHM_METHOD = "AES/CBC/PKCS5Padding";

    private SecretKey sharedKey;
    private Cipher cipher;
    private long seqNumber = 0;
    private static final int KEY_SIZE_BITS = 256;
    private static final int KEY_SIZE_BYTES = KEY_SIZE_BITS / 8;

    /**
     * @throws Exception
     */
    public Cryptography() {
        try {
            cipher = Cipher.getInstance(ALGORITHM_METHOD);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param plainText
     * @return
     * @throws Exception
     */
    public String encrypt(String plainText,IvParameterSpec iv ) throws Exception {
        checkPrecondition();
        cipher.init(Cipher.ENCRYPT_MODE, sharedKey,iv);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));

    }

    /**
     * @param cipherText
     * @return
     * @throws Exception
     */
    public String decrypt(String cipherText,IvParameterSpec iv) throws Exception {
        checkPrecondition();
        cipher.init(Cipher.DECRYPT_MODE, sharedKey, iv);
        return  new String(cipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes())));

    }

    /**
     * Generates a mac using Hmac algorithm with hash function SHA1
     *
     * @param data the input string
     * @return
     * @throws Exception
     */
    public String generateHmac(String data) throws Exception{
        checkPrecondition();
        Mac hmac = null;
        try {
            hmac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            hmac.init(sharedKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return  Base64.getEncoder().encodeToString(hmac.doFinal(data.getBytes()));

    }

    /**
     * Generate AES-
     *
     * @return
     * @throws Exception
     */
    public SecretKey generateSecretKey() {
        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance("AES");
            keygen.init(KEY_SIZE_BITS);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keygen != null;
        sharedKey = keygen.generateKey();
        return sharedKey;
    }


    private void checkPrecondition() {
        if (sharedKey == null) {
            throw new IllegalStateException("Key is not generated yet");
        }
    }

    public void setSharedKey(SecretKey key) {
        if (this.sharedKey != null) {
            try {
                this.sharedKey.destroy();
            } catch (DestroyFailedException e) {
                e.printStackTrace();
            }
        }


        this.sharedKey = key;

    }

    public SecretKey getSharedKey() {
        return sharedKey;
    }

    public long incrementSeqNumber() {
        return seqNumber++;
    }

    public long decrementSeqNumber() {
        return seqNumber--;
    }

    public static IvParameterSpec generateRandomIv() {
        SecureRandom ranGen = new SecureRandom();
        byte[] key = new byte[16];
        ranGen.nextBytes(key);
        return new IvParameterSpec(key);

    }

}
