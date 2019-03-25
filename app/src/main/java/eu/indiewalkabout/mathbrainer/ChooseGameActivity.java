package eu.indiewalkabout.mathbrainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdView;

import eu.indiewalkabout.mathbrainer.aritmetic.DoubleNumberActivity;
import eu.indiewalkabout.mathbrainer.aritmetic.MixedOp_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.MixedOp_Write_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.RandomOperationActivity;
import eu.indiewalkabout.mathbrainer.aritmetic.singleop.Diff_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.singleop.Diff_Write_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.singleop.Div_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.singleop.Div_Write_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.singleop.Mult_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.singleop.Mult_Write_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.singleop.Sum_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.singleop.Sum_Write_Result_Activity;
import eu.indiewalkabout.mathbrainer.othergames.CountObjectsActivity;
import eu.indiewalkabout.mathbrainer.othergames.FallingOperationsActivity;
import eu.indiewalkabout.mathbrainer.othergames.NumberOrderActivity;
import eu.indiewalkabout.mathbrainer.othergames.SoundsSeqActivity;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;

/**
 * -------------------------------------------------------------------------------------------------
 * Choose the type of game
 * -------------------------------------------------------------------------------------------------
 */
public class ChooseGameActivity extends AppCompatActivity {

    // admob banner ref
    private AdView mAdView;

    public static final String OPERATION_KEY = "operation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(ChooseGameActivity.this));

        // -----------------------------------------------------------------------------------------
        // Aritmetic operation button
        // -----------------------------------------------------------------------------------------

        // --------------------------------------------
        // Basic op
        // --------------------------------------------
        final Button sumChooseBtn = findViewById(R.id.sumChoose_Btn);
        sumChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Sum_Choose_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this,MixedOp_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"+");
                startActivity(intent);
            }
        });

        final Button diffChooseBtn = findViewById(R.id.diffChoose_Btn);
        diffChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Diff_Choose_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this,MixedOp_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"-");
                startActivity(intent);
            }
        });


        final Button multChooseBtn = findViewById(R.id.multChoose_Btn);
        multChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Mult_Choose_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this,MixedOp_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"*");
                startActivity(intent);
            }
        });


        final Button divChooseBtn = findViewById(R.id.divChoose_Btn);
        divChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this, Div_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"/");
                startActivity(intent);
            }
        });


        // --------------------------------------------


        final Button sumWriteBtn = findViewById(R.id.sumWrite_Btn);
        sumWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Sum_Write_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, MixedOp_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"+");
                startActivity(intent);
            }
        });

        final Button diffWriteBtn = findViewById(R.id.diffWrite_Btn);
        diffWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Diff_Write_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, MixedOp_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"-");
                startActivity(intent);
            }
        });


        final Button multWriteBtn = findViewById(R.id.multWrite_Btn);
        multWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Mult_Write_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, MixedOp_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"*");
                startActivity(intent);
            }
        });


        final Button divWriteBtn = findViewById(R.id.divWrite_Btn);
        divWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Div_Write_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, MixedOp_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"/");
                startActivity(intent);
            }
        });


        // --------------------------------------------
        // Mixed op
        // --------------------------------------------

        final Button ResultWriteBtn = findViewById(R.id.MixedOps_WriteResultBtn);
        ResultWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this, MixedOp_Write_Result_Activity.class);
                startActivity(intent);
            }
        });

        final Button ResultChooseBtn = findViewById(R.id.MixedOps_ChooseResultBtn);
        ResultChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,MixedOp_Choose_Result_Activity.class);
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
