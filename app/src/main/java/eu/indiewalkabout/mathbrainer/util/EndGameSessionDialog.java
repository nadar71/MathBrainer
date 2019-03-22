package eu.indiewalkabout.mathbrainer.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import eu.indiewalkabout.mathbrainer.R;

/**
 * ---------------------------------------------------------------------------------------------
 * Create dialog when session game is ended, wrong or ok
 * ---------------------------------------------------------------------------------------------
 */

public class EndGameSessionDialog{

    public enum GAME_SESSION_RESULT {WRONG,OK};

    private Context context;
    private IGameFunctions caller;
    private GAME_SESSION_RESULT result;
    private AlertDialog alertDialog;


    public EndGameSessionDialog(Context context, IGameFunctions caller, GAME_SESSION_RESULT result ){
        this.context = context;
        this.caller  = caller;
        this.result  = result;

        createDialog();
    }



    private void createDialog(){
        if (result == GAME_SESSION_RESULT.WRONG){
            // user dialog confirm
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogLayout = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_game_session_answer, null);

            TextView msg = dialogLayout.findViewById(R.id.result_msg_tv);
            msg.setText(R.string.wrong_str);

            Button nextBtn = dialogLayout.findViewById(R.id.next_btn);

            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    caller.newChallenge();

                }
            });

            builder.setView(dialogLayout);

            alertDialog = builder.show();

        } else if (result == GAME_SESSION_RESULT.OK) {
            // user dialog confirm
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogLayout = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_game_session_answer, null);

            TextView msg = dialogLayout.findViewById(R.id.result_msg_tv);
            msg.setText(R.string.ok_str);

            Button nextBtn = dialogLayout.findViewById(R.id.next_btn);


            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    caller.newChallenge();

                }
            });

            builder.setView(dialogLayout);

            alertDialog = builder.show();

        }
    }


}
