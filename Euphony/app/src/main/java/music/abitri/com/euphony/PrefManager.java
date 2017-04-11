package music.abitri.com.euphony;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import music.abitri.com.euphony.Manager.SongDetail;

/**
 * Created by abhis on 2/14/2017.
 */

public class PrefManager {

    ArrayList<SongDetail> songDetails;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public PrefManager(Context context) {

        preferences = context.getSharedPreferences(Constants.STORELIST, Context.MODE_PRIVATE);

    }

    public void StoreSongs(List<SongDetail> songLists) {
        editor = preferences.edit();
        Gson gson = new Gson();
        Log.d("TAG", "SONGS: " + gson.toJson(songLists));

        editor.putString(Constants.KEY_ALBUM, gson.toJson(songLists));
        editor.apply();

    }

    public void storePosition(int pos) {
        editor = preferences.edit();
        editor.putInt(Constants.POSITION, pos);
        editor.apply();
    }

    public int getPosition(String Key) {
        int pos = preferences.getInt(Key, 0);
        return pos;
    }


    public List<SongDetail> getSongs(String key) {
        List<SongDetail> songs = new ArrayList<SongDetail>();
        if (preferences.contains(key)) {
            String json = preferences.getString(key, null);
           // Log.d("TAG", "SONGS GET: " + json);
            Gson gson = new Gson();
            SongDetail[] albumArry = gson.fromJson(json, SongDetail[].class);
            songs = Arrays.asList(albumArry);
            songs = new ArrayList<SongDetail>(songs);
            return songs;

        }

        return songs;
    }


    public void setCurrentDuration(long currentDuration) {

        editor = preferences.edit();
        editor.putLong(Constants.PLAYBACK_TIME, currentDuration);
        editor.apply();
    }

    public long getCurrentDuration() {
        long curr = preferences.getLong(Constants.PLAYBACK_TIME, 0);
        return curr;
    }

    public void setTotalDuration(long totalDuration) {

        editor = preferences.edit();
        editor.putLong(Constants.PLAYBACK_TOTAL_TIME, totalDuration);
        editor.apply();
    }

    public long getTotalDuration() {
        long curr = preferences.getLong(Constants.PLAYBACK_TOTAL_TIME, 0);
        return curr;
    }

    public void setEndingValue(boolean val){
        editor = preferences.edit();
        editor.putBoolean(Constants.PLAYBACK_BOOLEAN_STATE, val);
        editor.apply();
    }

    public boolean getEndingValue(){

         boolean val = preferences.getBoolean(Constants.PLAYBACK_BOOLEAN_STATE,false);
        return val;
    }


    public void ClearSHP(){

        editor = preferences.edit();
        editor.clear();
        editor.apply();
    }


    public void setStringValue(String aTrue) {
        editor = preferences.edit();
        editor.putString("END_VALUE",aTrue);
        editor.apply();
    }
    public String getEnd(){

        String val = preferences.getString("END_VALUE","false");
        return val;
    }


    public void storeShuffleStatus(boolean b){

        editor = preferences.edit();
        editor.putBoolean(Constants.SHUFFLE_STATUS, b);
        editor.apply();
    }

    public boolean getShuffleStatus(){
        boolean b = preferences.getBoolean(Constants.SHUFFLE_STATUS,false);
        return b;
    }
}
