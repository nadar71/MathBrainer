package eu.indiewalkabout.mathbrainer.statistics;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "GAMERESULTS_LIST")
public class GameResult {

    @PrimaryKey
    @NonNull
    private String result_name;

    private int result_value;


    public GameResult(String result_name, int result_value) {
        this.result_name = result_name;
        this.result_value = result_value;
    }



    public String getResult_name() {
        return result_name;
    }

    public int getResult_value() {
        return result_value;
    }


    public void setResult_name(String result_name) {
        this.result_name = result_name;
    }

    public void setResult_value(int result_value) {
        this.result_value = result_value;
    }
}

