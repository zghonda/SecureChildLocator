package project.sirs.scl;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import javax.crypto.spec.SecretKeySpec;
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
            byte[] keydata = getIntent().getExtras().getByteArray("code");
            startTransmissionProcess(keydata);
            gps = new GpsData(ChildActivity.this);
            final DatabaseReference reference = database.getReference("gpsData");
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    byte[] cipherText;
                    Location loc = gps.getLocation();
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();
                    String message = new StringBuilder().append(latitude).append(" ").append(longitude).append(" ").append(crypto.incrementSeqNumber()).toString();
                    try {
                        cipherText = crypto.encrypt(crypto.generateHmac(message));
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

    private void startTransmissionProcess(final byte[] keydata) {
        crypto = new Cryptography();
        crypto.setSharedKey(new SecretKeySpec(keydata, "AES"));


    }


}




