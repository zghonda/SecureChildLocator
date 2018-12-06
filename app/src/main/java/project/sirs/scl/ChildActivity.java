package project.sirs.scl;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class ChildActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private final User child = new User("child", "child@gmail.com", "abcd1234");
    private Button btn_sign_in, btn_qr;
    private Cryptography crypto;
    private GpsData gps;
    private static final int BROADCAST_TIME_PERIOD = 5 * 1000;
    private ProgressBar progressBar;
    private TextView text_progress;
    private DatabaseReference reference   ;

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

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
        btn_qr = (Button) findViewById(R.id.btn_qr);
        progressBar = (ProgressBar) findViewById(R.id.progress_infinite);
        text_progress = (TextView) findViewById(R.id.progress_text);
        gps = new GpsData(ChildActivity.this);
        reference = database.getReference("gpsData");

        btn_sign_in.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn();
                    }
                }
        );
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail().equals(child.email())) {
            btn_qr.setVisibility(View.VISIBLE);
        } else {
            btn_sign_in.setVisibility(View.VISIBLE);
        }
        if (getIntent().hasExtra("code")) {
            //String keydata = getIntent().getExtras().getString("code");
            SecretKey keydata = new Cryptography().generateSecretKey();
            //startTransmissionProcess(keydata);
            crypto = new Cryptography();
            crypto.setSharedKey(keydata);

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    String cipherText;
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    String message = new StringBuilder().append(latitude).append(" ").append(longitude).append(" ").append(crypto.incrementSeqNumber()).toString();
                    System.out.println(message);
                    try {
                        cipherText = crypto.encrypt(Base64.encodeToString(crypto.generateHmac(message),0));
                        reference.setValue(cipherText);
                        progressBar.setVisibility(View.VISIBLE);
                        text_progress.setVisibility(View.VISIBLE);
                        text_progress.setText("Sending location ..");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, 0, BROADCAST_TIME_PERIOD);


        }

    }

    private void signIn() {
        final TextView status = (TextView) findViewById(R.id.txt_auth_status);

        mAuth.signInWithEmailAndPassword(child.email(), child.password()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Authentication success !!", Toast.LENGTH_LONG).show();
                    btn_qr.setVisibility(View.VISIBLE);
                } else {
                    status.setText(R.string.error_authentication);
                    status.setVisibility(View.VISIBLE);
                    btn_sign_in.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    private void launchScan() {

        Intent intent = new Intent(getApplicationContext(), ScanActivity2.class);
        startActivity(intent);

    }

    private void startTransmissionProcess(String keydata) {
        crypto = new Cryptography();
        try {
            crypto.setSharedKey(new SecretKeySpec(keydata.getBytes("UTF-8"), "AES"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


}




