package eu.indiewalkabout.mathbrainer.util

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.keyboard.*

import eu.indiewalkabout.mathbrainer.R
import kotlinx.android.synthetic.main.keyboard.view.*

class MyKeyboard @JvmOverloads constructor(context: Context,
                                           attrs: AttributeSet? = null,
                                           defStyleAttr: Int = 0) : LinearLayout(context,
                                           attrs, defStyleAttr),
        View.OnClickListener {

    internal var caller: IGameFunctions? = null


    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.
    internal var keyValues = SparseArray<String>()

    // Our communication link to the EditText
    internal var inputConnection: InputConnection? = null

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true)

        
        // set button click listeners
        button_1.setOnClickListener(this)
        button_2.setOnClickListener(this)
        button_3.setOnClickListener(this)
        button_4.setOnClickListener(this)
        button_5.setOnClickListener(this)
        button_6.setOnClickListener(this)
        button_7.setOnClickListener(this)
        button_8.setOnClickListener(this)
        button_9.setOnClickListener(this)
        button_0.setOnClickListener(this)
        button_delete.setOnClickListener(this)
        button_enter.setOnClickListener(this)

        // map buttons IDs to input strings
        keyValues.put(R.id.button_1, "1")
        keyValues.put(R.id.button_2, "2")
        keyValues.put(R.id.button_3, "3")
        keyValues.put(R.id.button_4, "4")
        keyValues.put(R.id.button_5, "5")
        keyValues.put(R.id.button_6, "6")
        keyValues.put(R.id.button_7, "7")
        keyValues.put(R.id.button_8, "8")
        keyValues.put(R.id.button_9, "9")
        keyValues.put(R.id.button_0, "0")
        keyValues.put(R.id.button_enter, "\n")
    }

    override fun onClick(v: View) {

        // do nothing if the InputConnection has not been set yet
        if (inputConnection == null) return

        // Delete text or input key value
        // All communication goes through the InputConnection
        if (v.id == R.id.button_delete) {

            val selectedText = inputConnection!!.getSelectedText(0)
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection!!.deleteSurroundingText(1000, 0)
            } else {
                // delete the selection
                inputConnection!!.commitText("", 1)
            }

            // delete all text
            // inputConnection.commitText("", 1);
        } else if (v.id == R.id.button_enter) {
            caller!!.checkPlayerInput()
        } else {
            val value = keyValues.get(v.id)
            inputConnection!!.commitText(value, 1)
        }
    }

    // The activity (or some parent or controller) must give us
    // a reference to the current EditText's InputConnection
    fun setInputConnection(ic: InputConnection, caller: IGameFunctions) {
        this.inputConnection = ic
        this.caller = caller
    }
}// constructors
