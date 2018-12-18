package eu.indiewalkabout.mathbrainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * -------------------------------------------------------------------------------------------------
 * Choose the type of game
 * -------------------------------------------------------------------------------------------------
 */
public class ChooseGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);


        // BEGIN Button management

        final Button ResultWriteBtn = findViewById(R.id.ResultWriteBtn);
        ResultWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this, MathOperationWriteActivity.class);
                startActivity(intent);
            }
        });

        final Button ResultChooseBtn = findViewById(R.id.ResultChooseBtn);
        ResultChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,MathOperationChooseActivity.class);
                startActivity(intent);
            }
        });


        final Button quickCountBtn = findViewById(R.id.quickCountBtn);
        quickCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,CountObjectsActivity.class);
                startActivity(intent);
            }
        });


        final Button doublingBtn = findViewById(R.id.doublingBtn);
        doublingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,DoubleNumberActivity.class);
                startActivity(intent);
            }
        });


        final Button fallingBtn = findViewById(R.id.fallingBtn);
        fallingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,FallingOperationsActivity.class);
                startActivity(intent);
            }
        });


        final Button orderBtn = findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,NumberOrderActivity.class);
                startActivity(intent);
            }
        });


        final Button randomOpsBtn = findViewById(R.id.randomOpsBtn);
        randomOpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,RandomOperationActivity.class);
                startActivity(intent);
            }
        });


        final Button soundsSeqBtn = findViewById(R.id.soundsSeqBtn);
        soundsSeqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,SoundsSeqActivity.class);
                startActivity(intent);
            }
        });

        // END Button management
    }



}
