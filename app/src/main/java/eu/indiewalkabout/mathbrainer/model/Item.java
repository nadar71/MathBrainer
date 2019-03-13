package eu.indiewalkabout.mathbrainer.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

public class Item extends View{
    Context context;
    int x, y;
    int size;
    Bitmap image;



    public Item(Context context, Bitmap bitmap, int x, int y, int size) {
        super(context);
        this.x = x;
        this.y = y;
        this.size = size;
        image     = bitmap;
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
}
