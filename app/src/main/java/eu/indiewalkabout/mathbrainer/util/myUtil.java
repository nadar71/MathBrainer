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
     * Return a pseudo- random int based on api level, for retrocompat
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




}
