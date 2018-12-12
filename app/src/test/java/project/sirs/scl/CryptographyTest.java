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
        String cipherText = crypto.encrypt(message,iv);
        String ReceivedMessage = crypto.decrypt(cipherText,iv);

        String[] macMessage = ReceivedMessage.split("separation");
        String macReceived = new String(Base64.getDecoder().decode(macMessage[0].getBytes()));
        String messageReceived = macMessage[1];


        assertEquals(macReceived.equals(new String(Base64.getDecoder().decode(mac.getBytes())))&&messageReceived.equals(text),true);

    }







}
