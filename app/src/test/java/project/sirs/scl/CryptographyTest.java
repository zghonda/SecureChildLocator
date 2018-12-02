package project.sirs.scl;

import org.junit.Test;

import javax.crypto.SecretKey;

import static org.junit.Assert.assertEquals;

public class CryptographyTest {
    @Test
    public void encryptDecryptTest() throws Exception{
        Cryptography crypto = new Cryptography();
        SecretKey key = crypto.generateSecretKey();
        String text = "hello world";
        System.out.println(text.getBytes());
        byte[] cipherText = crypto.encrypt(text.getBytes());
        System.out.println(cipherText);
        System.out.println(crypto.decrypt(cipherText));

        assertEquals(crypto.decrypt(crypto.encrypt(text.getBytes())).equals(text.getBytes()),true);

    }




}
