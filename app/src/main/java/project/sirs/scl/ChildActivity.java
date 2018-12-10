package project.sirs.scl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

public class ChildActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private final User child = new User("child", "child@gmail.com", "abcd1234");
    private Button btn_qr;
    private Cryptography crypto;
    private GpsData gps;
    private static final int BROADCAST_TIME_PERIOD = 20 * 1000;
    private ProgressBar progressBar;
    private TextView text_progress;
    private DatabaseReference referenceJournal;
    private long counter;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_child);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        btn_qr = (Button) findViewById(R.id.btn_qr);
        progressBar = (ProgressBar) findViewById(R.id.progress_infinite);
        text_progress = (TextView) findViewById(R.id.progress_text);
        gps = new GpsData(ChildActivity.this);


        btn_qr.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        launchScan();
                    }
                }
        );


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent().hasExtra("code")) {
            String keydata = getIntent().getExtras().getString("code");
            startTransmissionProcess(keydata);
            progressBar.setVisibility(View.VISIBLE);
            text_progress.setVisibility(View.VISIBLE);
            btn_qr.setVisibility(View.INVISIBLE);
            text_progress.setText("Sending location ..");
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    sendData();

                }
            }, 0, BROADCAST_TIME_PERIOD);


        }

    }


    private void launchScan() {

        Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
        this.finish();
        startActivity(intent);

    }

    private void startTransmissionProcess(String keydata) {
        crypto = new Cryptography();
        crypto.setSharedKey(new SecretKeySpec(Base64.getDecoder().decode(keydata.getBytes()), "AES"));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void sendData() {
        String cipherText;
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        counter = crypto.incrementSeqNumber();
        String refStringJournal = new StringBuilder("gpsData_").append(counter).toString();
        referenceJournal = database.getReference(refStringJournal);
        String refString = "gpsData";
        reference = database.getReference(refString);
        IvParameterSpec iv = Cryptography.generateRandomIv();
        String message = new StringBuilder().append(latitude).append(" ").append(longitude).append(" ").append(counter).toString();
        System.out.println(message);
        try {
            cipherText = crypto.encrypt(new StringBuilder().append(crypto.generateHmac(message)).append("separation").append(message).toString(),iv);
            referenceJournal.setValue(cipherText);
            reference.setValue(cipherText.concat("separation").concat(Base64.getEncoder().encodeToString(iv.getIV())));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




