package eu.indiewalkabout.mathbrainer.core.util

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import eu.indiewalkabout.mathbrainer.R

class MyKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(
    context,
    attrs, defStyleAttr
),
    View.OnClickListener {

    private var caller: IGameFunctions? = null

    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.
    private var keyValues = SparseArray<String>()

    // Our communication link to the EditText
    private var inputConnection: InputConnection? = null

    lateinit var button_1 : Button
    lateinit var button_2 : Button
    lateinit var button_3 : Button
    lateinit var button_4 : Button
    lateinit var button_5 : Button
    lateinit var button_6 : Button
    lateinit var button_7 : Button
    lateinit var button_8 : Button
    lateinit var button_9 : Button
    lateinit var button_0 : Button
    lateinit var button_delete : Button
    lateinit var button_enter : Button

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true)

        button_1 = findViewById(R.id.button_1)
        button_2 = findViewById(R.id.button_2)
        button_3 = findViewById(R.id.button_3)
        button_4 = findViewById(R.id.button_4)
        button_5 = findViewById(R.id.button_5)
        button_6 = findViewById(R.id.button_6)
        button_7 = findViewById(R.id.button_7)
        button_8 = findViewById(R.id.button_8)
        button_9 = findViewById(R.id.button_9)
        button_0 = findViewById(R.id.button_0)
        button_delete = findViewById(R.id.button_delete)
        button_enter = findViewById(R.id.button_enter)

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
} // constructors
