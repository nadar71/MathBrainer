package eu.indiewalkabout.mathbrainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // BEGIN Button management

        final Button playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChooseGameActivity.class);
                startActivity(intent);
            }
        });

        final Button scoreBtn = findViewById(R.id.scoreBtn);
        scoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ChooseGameActivity.class);
                startActivity(intent);
            }
        });


        final Button StatBtn = findViewById(R.id.StatBtn);
        StatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ChooseGameActivity.class);
                startActivity(intent);
            }
        });


        final Button hintsBtn = findViewById(R.id.hintsBtn);
        hintsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ChooseGameActivity.class);
                startActivity(intent);
            }
        });


        // END Button management
    }
}
