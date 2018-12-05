package project.sirs.scl;

import android.util.Base64;

import javax.crypto.*;
import javax.security.auth.DestroyFailedException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class Cryptography {

    private static final String ALGORITHM_METHOD = "AES";

    private SecretKey sharedKey;
    private  Cipher cipher;
    private long seqNumber = 0;

    /**
     *
     * @throws Exception
     */
    public Cryptography()  {
        try {
            cipher = Cipher.getInstance(ALGORITHM_METHOD);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param plainText
     * @return
     * @throws Exception
     */
    public String encrypt(String plainText ) throws Exception {
        checkPrecondition();
        cipher.init(Cipher.ENCRYPT_MODE, sharedKey);
        return Base64.encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")),0);

    }

    /**
     *
     * @param cipherText
     * @return
     * @throws Exception
     */
    public String decrypt(String cipherText)  throws Exception{
        checkPrecondition();
        cipher.init(Cipher.DECRYPT_MODE, sharedKey);
        return new String(cipher.doFinal(Base64.decode(cipherText,0)),"UTF-8");

    }

    /**
     * Generates a mac using Hmac algorithm with hash function SHA1
     *
     * @param data the input string
     * @return
     * @throws Exception
     */
    public byte[] generateHmac(String data)  {
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
        return hmac.doFinal(data.getBytes());

    }

    /**
     * Generate AES-
     * @return
     * @throws Exception
     */
    public SecretKey generateSecretKey() {
        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance("AES");
            SecureRandom rand = new SecureRandom();
            keygen.init(256,rand);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        sharedKey = keygen.generateKey();
        return sharedKey;
    }


    private void checkPrecondition() {
        if (sharedKey == null) {
            throw new IllegalStateException("Key is not generated yet");
        }
    }

    public void setSharedKey(SecretKey key) {
        if(this.sharedKey != null){
            try {
                this.sharedKey.destroy();
            } catch (DestroyFailedException e) {
                e.printStackTrace();
            }
        }


        this.sharedKey = key;

    }

    public SecretKey getSharedKey(){
        return sharedKey;
    }

    public long incrementSeqNumber(){
        return seqNumber++;
    }
    public long decrementSeqNumber(){
        return seqNumber--;
    }


}
