package project.sirs.scl;

import org.junit.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

public class DemoTest {


    @Test
    public void DemoTest() throws Exception{
        Cryptography crypto = new Cryptography();
        SecretKey key = crypto.generateSecretKey();

        String fakeText = "123.123 789.789 0";

        IvParameterSpec iv = Cryptography.generateRandomIv();
        String mac = crypto.generateHmac(fakeText);
        String message = new StringBuilder().append(mac).append("separation").append(fakeText).toString();
        String cipherText = crypto.encrypt(message,iv);
        String gpsData = cipherText.concat("separation").concat(Base64.getEncoder().encodeToString(iv.getIV()));
        System.out.println(gpsData);
        assertEquals(false,true);
    }

}
