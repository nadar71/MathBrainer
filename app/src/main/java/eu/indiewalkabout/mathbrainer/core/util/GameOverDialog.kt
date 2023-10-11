package eu.indiewalkabout.mathbrainer.core.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView

import eu.indiewalkabout.mathbrainer.presentation.ui.ChooseGameActivity
import eu.indiewalkabout.mathbrainer.R

class GameOverDialog(private val context: Context, private val caller: IGameFunctions, private val activity: Activity) {
    private var homeBtn :ImageView? = null
    private var restartBtn :ImageView? = null
    private var alertDialog: AlertDialog? = null


    init {
        createDialog()
    }


    private fun createDialog() {
        // user dialog confirm
        val builder = AlertDialog.Builder(context)
        val dialogLayout = LayoutInflater.from(context)
                .inflate(R.layout.dialog_gameover, null)


        // dialogLayout.setBackgroundColor(context.getResources().getColor( R.color.transparent_gray));


        builder.setView(dialogLayout)

        alertDialog = builder.show()

        // Prevent dialog from closing on outside touch
        alertDialog!!.setCancelable(false)


        homeBtn!!.setOnClickListener {
            alertDialog!!.dismiss()
            val intent = Intent(activity, ChooseGameActivity::class.java)
            activity.startActivity(intent)
        }


        restartBtn!!.setOnClickListener {
            alertDialog!!.dismiss()
            activity.finish()
            activity.startActivity(activity.intent)
        }

        // make bottom navigation bar and status bar hide
        hideStatusNavBars()
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        val decorView = alertDialog!!.window!!.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     // hide navigation bar

                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // hide navigation bar

                or View.SYSTEM_UI_FLAG_FULLSCREEN) // // hide status bar
        decorView.systemUiVisibility = uiOptions
    }


}
