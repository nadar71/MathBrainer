package eu.indiewalkabout.mathbrainer.util


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build

import com.unity3d.ads.UnityAds
import java.util.Random
import java.util.concurrent.ThreadLocalRandom

import eu.indiewalkabout.mathbrainer.statistics.GameResult

/**
 * ---------------------------------------------------------------------------------------------
 * Helper class for utilities
 * ---------------------------------------------------------------------------------------------
 */
object MathBrainerUtility {

    /**
     * ---------------------------------------------------------------------------------------------
     * Return a pseudo- random int based on api level, for retrocompat;
     * I return random value in the interval, margins included.
     * @param min
     * @param max
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    fun randRange_ApiCheck(min: Int, max: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(min, max + 1)
        else {
            val rand = Random(System.currentTimeMillis())
            return rand.nextInt(max - min + 1 + min)
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Random answer for sum generator
     * ---------------------------------------------------------------------------------------------
     */
    fun randomSignChooser(): Int {
        val result = MathBrainerUtility.randRange_ApiCheck(1, 2)
        return if (result == 1)
            -1
        else
            1
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return a scaled bitmap resized by scale
     * ---------------------------------------------------------------------------------------------
     */
    fun resizeBitmapByScale(bitmap: Bitmap, scale: Float): Bitmap {


        // dimensions scaled
        val width = Math.round(bitmap.width * scale)
        val height = Math.round(bitmap.height * scale)

        if (width == bitmap.width && height == bitmap.height) {

            return bitmap
        }

        // create a target bitmap where to write on with dimensions scaled, same bitmap param config
        var target = Bitmap.createScaledBitmap(bitmap, width, height, false)
        target = target.copy(Bitmap.Config.ARGB_8888, true)
        return target

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Draw text on a bitmap
     * @param gContext
     * @param bitmap
     * @param gText
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    fun drawTextToBitmap(gContext: Context,
                         bitmap: Bitmap,
                         gText: String): Bitmap {

        val resources = gContext.resources
        val scale = resources.displayMetrics.density
        /*
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, gResId);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);
        */

        val canvas = Canvas(bitmap)

        // set specific font
        val assetManager = gContext.assets
        val plain = Typeface.createFromAsset(assetManager, "font/quicksand.otf")
        val bold = Typeface.create(plain, Typeface.BOLD)

        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // text color
        paint.color = Color.rgb(255, 255, 255)

        // text font
        paint.typeface = bold

        // text size in pixels
        paint.textSize = (80 * scale).toInt().toFloat()

        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK)

        // draw text to the Canvas center
        val bounds = Rect()
        paint.getTextBounds(gText, 0, gText.length, bounds)
        val x = (bitmap.width - bounds.width()) / 2
        val y = (bitmap.height + bounds.height()) / 2

        canvas.drawText(gText, x.toFloat(), y.toFloat(), paint)

        return bitmap
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show unity ads with a defined random frequency
     * ---------------------------------------------------------------------------------------------
     */
    fun showUnityAdsRandom(activity: Activity) {
        val guess = MathBrainerUtility.randRange_ApiCheck(1, 10)
        if (guess <= 4) {
            if (UnityAds.isReady()) {
                UnityAds.show(activity)
            }
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return game result value for a given game result in list
     * ---------------------------------------------------------------------------------------------
     */
    fun getGameResultsFromList(gameResultKey: String, resultsList: List<GameResult>): Int {
        for (g in resultsList) {
            if (g.result_name == gameResultKey) {
                return g.result_value
            }
        }
        return -1
    }



}
