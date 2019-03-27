package eu.indiewalkabout.mathbrainer.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import eu.indiewalkabout.mathbrainer.ChooseGameActivity;
import eu.indiewalkabout.mathbrainer.R;

public class GameOverDialog {

    private Context context;
    private IGameFunctions caller;
    private AlertDialog alertDialog;
    private Activity activity;


    public GameOverDialog(Context context, IGameFunctions caller, Activity activity){
        this.context  = context;
        this.caller   = caller;
        this.activity = activity;
        createDialog();
    }



    private void createDialog() {
        // user dialog confirm
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogLayout = LayoutInflater.from(context)
                .inflate(R.layout.dialog_gameover, null);


        // dialogLayout.setBackgroundColor(context.getResources().getColor( R.color.transparent_gray));


        ImageView homeBtn = dialogLayout.findViewById(R.id.home_iv);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(activity, ChooseGameActivity.class);
                activity.startActivity(intent);
            }
        });

        ImageView restartBtn = dialogLayout.findViewById(R.id.restart_iv);

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                activity.finish();
                activity.startActivity(activity.getIntent());
            }
        });

        builder.setView(dialogLayout);

        alertDialog = builder.show();

        // make bottom navigation bar and status bar hide
        // hideStatusNavBars();
    }



    /**
     * -------------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * -------------------------------------------------------------------------------------------------
     */
    private void hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        View decorView = activity.getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     // hide navigation bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // hide navigation bar
                        // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN; // // hide status bar
        decorView.setSystemUiVisibility(uiOptions);
    }



}
