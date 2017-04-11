package music.abitri.com.euphony.SharedPrefPKg;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abhis on 2/18/2017.
 */

public class CheckFirstTime {

    public String NAME = "FIRST_TIME";
    public String PARAMETER_KEY="IS_FIRST";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public CheckFirstTime(Context context){
        preferences = context.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setFirst(boolean bool){
        editor.putBoolean(PARAMETER_KEY,bool);
        editor.apply();
    }

    public boolean getFirst(){
        return preferences.getBoolean(PARAMETER_KEY,false);
    }
}
