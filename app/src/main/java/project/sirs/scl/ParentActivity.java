package project.sirs.scl;

import android.content.Intent;
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
import com.google.firebase.database.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class ParentActivity extends AppCompatActivity {

    private TextView input_txt_email, input_txt_pwd;
    private TextView txt_email, txt_pwd,txt_gps_data,txt_rcv_data;
    private Button btn_signin_parent;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private Set<View> views;
    private Cryptography crypto;
    private Button btn_genQR;
    private SecretKey key;
    private final User mother = new User("mother", "mother@gmail.com", "abcd1234");
    private long motherCounter;
    private long childCounter;
    private DatabaseReference reference;
    private boolean isKeyGenerated;
    private final static String FILENAME = "keyfile.pem";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        views = new HashSet<View>();
        input_txt_email = findViewById(R.id.input_txt_email);
        input_txt_pwd = findViewById(R.id.input_txt_pwd);
        btn_signin_parent = findViewById(R.id.btn_signin_parent);
        txt_email = findViewById(R.id.txt_email);
        txt_pwd = findViewById(R.id.txt_pwd);
        btn_genQR = findViewById(R.id.btn_genQR);
        txt_gps_data = findViewById(R.id.txt_gps_data);
        txt_rcv_data = findViewById(R.id.txt_rcv_data);
        progressBar = findViewById(R.id.progressBar);

        views.add(input_txt_email);
        views.add(input_txt_pwd);
        views.add(btn_signin_parent);
        views.add(txt_email);
        views.add(txt_pwd);

        isKeyGenerated = false;

        btn_signin_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

        btn_genQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQR();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


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
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        crypto = new Cryptography();
        if (currentUser != null && currentUser.getEmail().equals(mother.email())) {
            views.forEach(v -> v.setVisibility(View.INVISIBLE));
            btn_genQR.setVisibility(View.VISIBLE);
            File keyFile = new File(getApplicationContext().getFilesDir(), FILENAME);
            if (keyFile.length() != 0) {
                final byte[] keyBytes;
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(keyFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                DataInputStream dis = new DataInputStream(fis);
                keyBytes = new byte[(int) keyFile.length()];
                try {
                    dis.readFully(keyBytes);
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
                crypto.setSharedKey(secretKey);
                btn_genQR.setVisibility(View.INVISIBLE);
                getGpsData();

            }
        }
    }


    private boolean isInputCredentialsOkay() {
        return input_txt_pwd != null && input_txt_email != null
                && input_txt_email.getText().toString().endsWith("@gmail.com") && input_txt_pwd.getText().length() != 0;

    }

    private void signin() {
        if (!isInputCredentialsOkay())
            Toast.makeText(getApplicationContext(), "Inputs non valid please try again !", Toast.LENGTH_LONG).show();
        else {
            mAuth.signInWithEmailAndPassword(input_txt_email.getText().toString(), input_txt_pwd.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Authentication success !!", Toast.LENGTH_LONG).show();
                        views.forEach(v -> v.setVisibility(View.INVISIBLE));
                        btn_genQR.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(getApplicationContext(), "Authentication failure !!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void getGpsData() {
        String refString = new StringBuilder("gpsData").toString();
        reference = database.getReference(refString);
        txt_rcv_data.setVisibility(View.VISIBLE);
        txt_gps_data.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                motherCounter++;
                try {
                    String[] plainTextIV = value.split("separation");
                    String plainText = crypto.decrypt(plainTextIV[0], new IvParameterSpec(Base64.getDecoder().decode(plainTextIV[1])));
                    String[] macMessage = plainText.split("separation");
                    String macReceived = new String(Base64.getDecoder().decode(macMessage[0].getBytes()));
                    String message = macMessage[1];
                    String myMac = new String(Base64.getDecoder().decode(crypto.generateHmac(message).getBytes()));

                    if (!myMac.equals(macReceived)) {
                        throw new Exception("authentication/integrity problem");
                    }
                    txt_gps_data.setText(message);

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void generateQR() {
        key = crypto.generateSecretKey();
        File keyFile = new File(getApplicationContext().getFilesDir(), FILENAME);
        try {
            FileOutputStream fos = new FileOutputStream(keyFile);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.write(key.getEncoded());
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getApplicationContext(), QRActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();

    }
}
