package project.sirs.scl;

import org.junit.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

public class CryptographyTest {
    @Test
    public void encryptDecryptTest() throws Exception{
        Cryptography crypto = new Cryptography();
        SecretKey key = crypto.generateSecretKey();
        IvParameterSpec iv = Cryptography.generateRandomIv();
        String text = "1234.123 4321.123 x";
        String mac = crypto.generateHmac(text);
        String message = new StringBuilder().append(mac).append("separation").append(text).toString();
        System.out.println(message);
        String cipherText = crypto.encrypt(message,iv);
        System.out.println(cipherText);
        String ReceivedMessage = crypto.decrypt(cipherText,iv);
        System.out.println(ReceivedMessage);

        String[] macMessage = ReceivedMessage.split("separation");
        String macReceived = new String(Base64.getDecoder().decode(macMessage[0].getBytes()));
        String messageReceived = macMessage[1];
        System.out.println(macReceived);
        System.out.println(messageReceived);
        System.out.println(new String(Base64.getDecoder().decode(mac.getBytes())));





        assertEquals(false,true);

    }




}
