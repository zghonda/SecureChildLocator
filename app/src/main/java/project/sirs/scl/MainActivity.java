package project.sirs.scl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private Button btn_child,btn_parent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_child = (Button) findViewById(R.id.btn_child);
        btn_parent= (Button) findViewById(R.id.btn_Parent);

        btn_child.setOnClickListener(
                new View.OnClickListener(){

                    public void onClick(View view){
                        startChild(view);
                    }
                }
        );

        btn_parent.setOnClickListener(
                new View.OnClickListener(){

                    public void onClick(View view){
                        startParent(view);
                    }
                }

        );



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

    public void startChild(View v){
        Intent intent = new Intent(getApplicationContext(),ChildActivity.class);
        startActivity(intent);
    }

    public void startParent(View v){
        Intent intent = new Intent(getApplicationContext(),ParentActivity.class);
        startActivity(intent);
    }



}
