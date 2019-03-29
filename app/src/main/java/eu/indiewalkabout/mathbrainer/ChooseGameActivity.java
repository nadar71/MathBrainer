package eu.indiewalkabout.mathbrainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdView;

import eu.indiewalkabout.mathbrainer.aritmetic.DoubleNumberActivity;
import eu.indiewalkabout.mathbrainer.aritmetic.Math_Op_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.Math_Op_Write_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.RandomOperationActivity;
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

    // Button refs
    Button sumChooseBtn;
    Button diffChooseBtn;
    Button multChooseBtn;
    Button divChooseBtn;

    Button sumWriteBtn;
    Button diffWriteBtn;
    Button multWriteBtn;
    Button divWriteBtn;

    Button ResultWriteBtn;
    Button ResultChooseBtn;
    Button quickCountBtn;
    Button doublingBtn;
    Button fallingBtn;
    Button orderBtn;
    Button randomOpsBtn;
    Button soundsSeqBtn;



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
        sumChooseBtn    = findViewById(R.id.sumChoose_Btn);
        diffChooseBtn   = findViewById(R.id.diffChoose_Btn);
        multChooseBtn   = findViewById(R.id.multChoose_Btn);
        divChooseBtn    = findViewById(R.id.divChoose_Btn);

        sumWriteBtn     = findViewById(R.id.sumWrite_Btn);
        diffWriteBtn    = findViewById(R.id.diffWrite_Btn);
        multWriteBtn    = findViewById(R.id.multWrite_Btn);
        divWriteBtn     = findViewById(R.id.divWrite_Btn);


        // --------------------------------------------
        // Mixed op
        // --------------------------------------------
        ResultWriteBtn  = findViewById(R.id.MixedOps_WriteResultBtn);
        ResultChooseBtn = findViewById(R.id.MixedOps_ChooseResultBtn);
        quickCountBtn   = findViewById(R.id.quickCountBtn);
        doublingBtn     = findViewById(R.id.doublingBtn);
        fallingBtn      = findViewById(R.id.fallingBtn);
        orderBtn        = findViewById(R.id.orderBtn);
        randomOpsBtn    = findViewById(R.id.randomOpsBtn);
        soundsSeqBtn    = findViewById(R.id.soundsSeqBtn);


        // END Button management

        // activate clicks on answer buttons
        setBtnPressedListener();

        // make bottom navigation bar and status bar hide
        hideStatusNavBars();

    }



    /**
     * -------------------------------------------------------------------------------------------------
     * Set up the button pressed listener and checking answers
     * -------------------------------------------------------------------------------------------------
     */
    private void setBtnPressedListener() {


        sumChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Sum_Choose_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"+");
                startActivity(intent);
            }
        });

        diffChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Diff_Choose_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"-");
                startActivity(intent);
            }
        });


        multChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Mult_Choose_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"*");
                startActivity(intent);
            }
        });


        divChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Div_Choose_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"/");
                startActivity(intent);
            }
        });

        sumWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Sum_Write_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"+");
                startActivity(intent);
            }
        });


        diffWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Diff_Write_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"-");
                startActivity(intent);
            }
        });


        multWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Mult_Write_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"*");
                startActivity(intent);
            }
        });

        divWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(ChooseGameActivity.this, Div_Write_Result_Activity.class);
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"/");
                startActivity(intent);
            }
        });


        ResultWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Write_Result_Activity.class);
                startActivity(intent);
            }
        });

        ResultChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this, Math_Op_Choose_Result_Activity.class);
                startActivity(intent);
            }
        });

        quickCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,CountObjectsActivity.class);
                startActivity(intent);
            }
        });


        doublingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,DoubleNumberActivity.class);
                startActivity(intent);
            }
        });

        fallingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,FallingOperationsActivity.class);
                startActivity(intent);
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,NumberOrderActivity.class);
                startActivity(intent);
            }
        });

        randomOpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this,RandomOperationActivity.class);
                startActivity(intent);
            }
        });


        soundsSeqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseGameActivity.this, SoundsSeqActivity.class);
                startActivity(intent);
            }
        });

    }




    /**
     * -------------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * -------------------------------------------------------------------------------------------------
     */
    private void hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        View decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     // hide navigation bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // hide navigation bar
                        // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN; // // hide status bar
        decorView.setSystemUiVisibility(uiOptions);
    }



}
