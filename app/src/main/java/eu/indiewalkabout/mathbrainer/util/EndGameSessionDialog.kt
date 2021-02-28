package eu.indiewalkabout.mathbrainer.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView

import eu.indiewalkabout.mathbrainer.R

/**
 * ---------------------------------------------------------------------------------------------
 * Create dialog when session game is ended, wrong or ok
 * ---------------------------------------------------------------------------------------------
 */

class EndGameSessionDialog(private val context: Context, private val caller: IGameFunctions,
                           private val result: GAME_SESSION_RESULT) {
    var result_msg_tv : TextView? = null
    var nextBtn : Button?         = null
    private var alertDialog: AlertDialog? = null

    enum class GAME_SESSION_RESULT {
        WRONG, OK
    }


    init {
        createDialog()
    }


    private fun createDialog() {
        lateinit var builder : AlertDialog.Builder
        lateinit var dialogLayout : View
        if (result == GAME_SESSION_RESULT.WRONG) {
            // user dialog confirm
            builder = AlertDialog.Builder(context)
            dialogLayout = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_game_session_answer, null)

            result_msg_tv!!.setText(R.string.wrong_str)


        } else if (result == GAME_SESSION_RESULT.OK) {
            // user dialog confirm
            builder = AlertDialog.Builder(context)
            dialogLayout = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_game_session_answer, null)

            result_msg_tv!!.setText(R.string.ok_str)

        }

        nextBtn!!.setOnClickListener {
            alertDialog!!.dismiss()
            caller.newChallenge()
        }

        builder.setView(dialogLayout)

        alertDialog = builder.show()

        // Prevent dialog from closing on outside touch
        alertDialog!!.setCancelable(false)
    }


}
