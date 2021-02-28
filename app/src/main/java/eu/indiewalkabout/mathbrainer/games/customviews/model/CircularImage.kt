package eu.indiewalkabout.mathbrainer.games.customviews.model

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatImageView

class CircularImage : AppCompatImageView {
    internal lateinit var context: Context
    var _x: Int = 0
    var _y: Int = 0
    var size: Int = 0
    lateinit var _bitmap: Bitmap
    var _number: Int = 0   //number associated with image/bitmap
    var _touched = false


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, x: Int, y: Int, size: Int) : super(context) {
        this._x = x
        this._y = y
        this.size = size
    }

    private fun init(context: Context) {
        this.context = context

    }

}
