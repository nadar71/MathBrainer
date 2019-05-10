package eu.indiewalkabout.mathbrainer.util;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;

import com.unity3d.ads.UnityAds;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import eu.indiewalkabout.mathbrainer.statistics.GameResult;

/**
 * ---------------------------------------------------------------------------------------------
 * Helper class for utilities
 * ---------------------------------------------------------------------------------------------
 */
public class MathBrainerUtility {

    /**
     * ---------------------------------------------------------------------------------------------
     * Return a pseudo- random int based on api level, for retrocompat;
     * I return random value in the interval, margins included.
     * @param min
     * @param max
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public static int randRange_ApiCheck(int min, int max){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        else {
            Random rand = new Random(System.currentTimeMillis());
            return rand.nextInt((max - min +1 ) + min);
        }
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Random answer for sum generator
     * ---------------------------------------------------------------------------------------------
     */
    public static int randomSignChooser(){
        int result = MathBrainerUtility.randRange_ApiCheck(1, 2);
        if (result == 1)
            return -1;
        else
            return 1;
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Return a scaled bitmap resized by scale
     * ---------------------------------------------------------------------------------------------
     */
    public static Bitmap resizeBitmapByScale(Bitmap bitmap, float scale) {


        // dimensions scaled
        int width  = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);

        if (width == bitmap.getWidth() && height == bitmap.getHeight()){

            return bitmap;
        }

        // create a target bitmap where to write on with dimensions scaled, same bitmap param config
        Bitmap target = Bitmap.createScaledBitmap(bitmap, width, height, false);
        target = target.copy(Bitmap.Config.ARGB_8888, true);
        return target;

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
    public static Bitmap drawTextToBitmap(Context gContext,
                                   Bitmap bitmap,
                                   String gText) {

        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
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

        Canvas canvas = new Canvas(bitmap);

        // set specific font
        AssetManager assetManager = gContext.getAssets();
        Typeface plain = Typeface.createFromAsset(assetManager, "font/quicksand.otf");
        Typeface bold = Typeface.create(plain, Typeface.BOLD);

        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // text color
        paint.setColor(Color.rgb(255, 255, 255));

        // text font
        paint.setTypeface(bold);

        // text size in pixels
        paint.setTextSize((int) (80 * scale));

        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;

        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }




    /**
     * ---------------------------------------------------------------------------------------------
     * Show unity ads with a defined random frequency
     * ---------------------------------------------------------------------------------------------
     */
    public static void showUnityAdsRandom(Activity activity){
        int guess = MathBrainerUtility.randRange_ApiCheck(1,10);
        if (guess <= 4) {
            if (UnityAds.isReady()){
                UnityAds.show(activity);
            }
        }
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Return game result value for a given game result in list
     * ---------------------------------------------------------------------------------------------
     */
    public static int getGameResultsFromList(String gameResultKey, List<GameResult> resultsList){
        for(GameResult g : resultsList){
            if (g.getResult_name().equals(gameResultKey)) {
                return g.getResult_value();
            }
        }
        return -1;
    }
}
