package eu.indiewalkabout.mathbrainer.util;


import android.os.Build;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Helper class for utilities
 */
public class myUtil {

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
            Random rand = new Random();
            return rand.nextInt((max - min +1 ) + min);
        }
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Random answer for sum generator
     * ---------------------------------------------------------------------------------------------
     */
    public static int randomSignChooser(){
        int result = myUtil.randRange_ApiCheck(1, 2);
        if (result == 1)
            return -1;
        else
            return 1;
    }



}
