package eu.indiewalkabout.mathbrainer.customviews.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;

public class CircularImage extends AppCompatImageView {
    Context context;
    int x, y;
    int size;
    Bitmap image;
    int number;   //number associated with image/bitmap
    boolean touched = false;



    public CircularImage(Context context) {
        super(context);

    }

    public CircularImage(Context context, int x, int y, int size) {
        super(context);
        this.x = x;
        this.y = y;
        this.size = size;
    }

    private void init(Context context) {
        this.context = context;

    }


    public int get_x() {
        return x;
    }

    public void set_x(int x) {
        this.x = x;
    }

    public int get_y() {
        return y;
    }

    public void set_y(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int get_number() {
        return number;
    }

    public void set_number(int number) {
        this.number = number;
    }

    public boolean get_touched() {
        return touched;
    }

    public void set_touched(boolean touched) {
        this.touched = touched;
    }

    public Bitmap get_bitmap() {
        return image;
    }

    public void set_bitmap(Bitmap image) {
        this.image = image;
    }

}
