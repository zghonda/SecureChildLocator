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
        System.out.println(new String(key.getEncoded(),"UTF-8"));
        String cipherText = crypto.encrypt(text);
        System.out.println(cipherText);
        System.out.println(crypto.decrypt(cipherText));

        assertEquals(crypto.decrypt(crypto.encrypt(text.getBytes())).equals(text.getBytes()),true);

    }




}
