package music.abitri.com.euphony.SQLiteDataBasePackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.Manager.SongDetail;

/**
 * Created by abhis on 2/22/2017.
 */

public class PlayListSQLdatabase extends SQLiteOpenHelper {

    private static final int database_version = 1;
    private static final String DATABASE_NAME = "SUMMARY";

    private static final String KEY_ID = "id";
    private static final String SONG_ID = "song_id";
    private static final String ALBUM_ID = "album_id";
    private static final String DURATION = "duration";
    private static final String TITLE = "title";
    private static final String SONG_NAME = "song_name";
    private static final String ALBUM_NAME = "album_name";
    private static final String ARTIST_NAME = "artist_name";
    private static final String SONG_PATH = "song_path";
    SQLiteDatabase dbs;
    Context ctx;


    public PlayListSQLdatabase(Context context) {
        super(context, DATABASE_NAME, null, database_version);
        this.ctx = context;
    }

    public void Create_Just_DB(String TABLE_NAME) {

        if (!getTablenames().contains(TABLE_NAME)) {

            dbs = this.getWritableDatabase();
            String CREATE_PLAYLIST_TABLE = "CREATE TABLE " +
                    TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
                    SONG_ID + " TEXT," +
                    ALBUM_ID + " TEXT," +
                    TITLE + " TEXT," +
                    SONG_NAME + " TEXT," +
                    ALBUM_NAME + " TEXT," +
                    ARTIST_NAME + " TEXT," +
                    SONG_PATH + " TEXT," +
                    DURATION + ")";
            dbs.execSQL(CREATE_PLAYLIST_TABLE);
            dbs.close();
        }else {

            Toast.makeText(ctx,"Playlist already exist",Toast.LENGTH_LONG).show();
        }
    }


    public void Create_DynamicDB(String TABLE_NAME, SongDetail songDetail) {

        if (!getTablenames().contains(TABLE_NAME)) {
            dbs = this.getWritableDatabase();
            String CREATE_PLAYLIST_TABLE = "CREATE TABLE " +
                    TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
                    SONG_ID + " TEXT," +
                    ALBUM_ID + " TEXT," +
                    TITLE + " TEXT," +
                    SONG_NAME + " TEXT," +
                    ALBUM_NAME + " TEXT," +
                    ARTIST_NAME + " TEXT," +
                    SONG_PATH + " TEXT," +
                    DURATION + ")";

            dbs.execSQL(CREATE_PLAYLIST_TABLE);
            ContentValues values = new ContentValues();
            values.put(SONG_ID, songDetail.getId());
            values.put(ALBUM_ID, songDetail.getAlbum_id());
            values.put(TITLE, songDetail.getTitle());
            values.put(SONG_NAME, songDetail.getDisplay_name());
            values.put(ALBUM_NAME, songDetail.get_album());
            values.put(ARTIST_NAME, songDetail.getArtist());
            values.put(SONG_PATH, songDetail.getPath());
            values.put(DURATION, songDetail.getDuration());

            dbs.insert(TABLE_NAME, null, values);
            dbs.close();

            Log.e("TABLE EXISTSTANCE : ", "table created : " + TABLE_NAME);
        } else {

            Log.e("TABLE EXISTSTANCE : ", "table exists : " + TABLE_NAME);
        }

    }

    public void AddSongData(String TABLE_NAME, SongDetail songDetail) {
        if (getTablenames().contains(TABLE_NAME)) {
            SQLiteDatabase adDB = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SONG_ID, songDetail.getId());
            values.put(ALBUM_ID, songDetail.getAlbum_id());
            values.put(TITLE, songDetail.getTitle());
            values.put(SONG_NAME, songDetail.getDisplay_name());
            values.put(ALBUM_NAME, songDetail.get_album());
            values.put(ARTIST_NAME, songDetail.getArtist());
            values.put(SONG_PATH, songDetail.getPath());
            values.put(DURATION, songDetail.getDuration());

            adDB.insert(TABLE_NAME, null, values);
            adDB.close();
        } else {
            Log.e("TABLE EXISTSTANCE : ", "table doesnt exists : " + TABLE_NAME);
        }


    }


    public List<SongDetail> getAllSongs(String TABLE_NAME) {

        List<SongDetail> songList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        dbs = this.getReadableDatabase();
        Cursor cursor = dbs.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SongDetail catagory = new SongDetail();
                catagory.setDbId(Integer.parseInt(cursor.getString(0)));
                catagory.setId(Integer.parseInt(cursor.getString(1)));
                catagory.setAlbum_id(Integer.parseInt(cursor.getString(2)));
                catagory.setTitle(cursor.getString(3));
                catagory.setDisplay_name(cursor.getString(4));
                catagory.set_album(cursor.getString(5));
                catagory.setArtist(cursor.getString(6));
                catagory.setPath(cursor.getString(7));
                catagory.setDuration(cursor.getString(8));
                songList.add(catagory);
            } while (cursor.moveToNext());

        }
        cursor.close();
        dbs.close();

        return songList;
    }


    public void DeleteTableData(String TABLE_NAME, SongDetail songDetail) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(songDetail.getDbId())});
        db.close();
    }

    public ArrayList<String> getTablenames() {

        ArrayList<String> arrTblNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                arrTblNames.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        c.close();
        db.close();

        return arrTblNames;
    }


    public void DeletePlaylistDB(String TABLE_NAME) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
