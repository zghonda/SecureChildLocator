package project.sirs.scl;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.crypto.SecretKey;
import java.util.Base64;


public class QRActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        QRCodeWriter writer = new QRCodeWriter();
        if(getIntent().hasExtra("key")){
            try {
                SecretKey key = (SecretKey) getIntent().getExtras().get("key");
                BitMatrix bitMatrix = null;
                try {
                    bitMatrix = writer.encode(new String(Base64.getEncoder().encode(key.getEncoded())), BarcodeFormat.QR_CODE, 512, 512);
                } catch ( NullPointerException e) {
                    e.printStackTrace();
                }
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                ((ImageView) findViewById(R.id.img_qr)).setImageBitmap(bmp);



            } catch (WriterException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Key not received ! ",Toast.LENGTH_LONG).show();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
