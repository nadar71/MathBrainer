package eu.indiewalkabout.mathbrainer.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView

import eu.indiewalkabout.mathbrainer.R
import kotlinx.android.synthetic.main.dialog_game_session_answer.view.*

/**
 * ---------------------------------------------------------------------------------------------
 * Create dialog when session game is ended, wrong or ok
 * ---------------------------------------------------------------------------------------------
 */

class EndGameSessionDialog(private val context: Context, private val caller: IGameFunctions, private val result: GAME_SESSION_RESULT) {
    private var alertDialog: AlertDialog? = null

    enum class GAME_SESSION_RESULT {
        WRONG, OK
    }


    init {

        createDialog()
    }


    private fun createDialog() {
        if (result == GAME_SESSION_RESULT.WRONG) {
            // user dialog confirm
            val builder = AlertDialog.Builder(context)
            val dialogLayout = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_game_session_answer, null)

            val msg = dialogLayout.findViewById<TextView>(R.id.result_msg_tv)
            msg.setText(R.string.wrong_str)

            val nextBtn = dialogLayout.findViewById<Button>(R.id.nextBtn)

            nextBtn.setOnClickListener {
                alertDialog!!.dismiss()
                caller.newChallenge()
            }

            builder.setView(dialogLayout)

            alertDialog = builder.show()

            // Prevent dialog from closing on outside touch
            alertDialog!!.setCancelable(false)

        } else if (result == GAME_SESSION_RESULT.OK) {
            // user dialog confirm
            val builder = AlertDialog.Builder(context)
            val dialogLayout = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_game_session_answer, null)

            val msg = dialogLayout.findViewById<TextView>(R.id.result_msg_tv)
            msg.setText(R.string.ok_str)

            val nextBtn = dialogLayout.findViewById<Button>(R.id.nextBtn)


            nextBtn.setOnClickListener {
                alertDialog!!.dismiss()
                caller.newChallenge()
            }

            builder.setView(dialogLayout)

            alertDialog = builder.show()

            // Prevent dialog from closing on outside touch
            alertDialog!!.setCancelable(false)

        }
    }


}
