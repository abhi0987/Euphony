package music.abitri.com.euphony.SharedPrefPKg;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abhis on 2/20/2017.
 */

public class TypeStoreManager {

    public String NAME = "LIST_TYPE_SHP";
    public String KEY_TYPE = "KEY_LIST_TYPE";
    public String KEY_RETRIVE_TYPE = "KEY_RETRIVE_TYPE";
    SharedPreferences shp;
    SharedPreferences.Editor editor;

    public TypeStoreManager(Context context){
        shp = context.getSharedPreferences(NAME,Context.MODE_PRIVATE);
    }


    public void setListType(String type){
        editor = shp.edit();
        editor.putString(KEY_TYPE,type);
        editor.apply();
    }  public void setRetriveType(String type){
        editor = shp.edit();
        editor.putString(KEY_RETRIVE_TYPE,type);
        editor.apply();
    }

    public String getListType(){
        return shp.getString(KEY_TYPE,"ALL");
    } public String getRetriveType(){
        return shp.getString(KEY_RETRIVE_TYPE,"ALL");
    }


}
