package music.abitri.com.euphony.SQLiteDataBasePackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.Manager.SongDetail;

/**
 * Created by abhis on 3/12/2017.
 */

public class SqliteRecentPlay extends SQLiteOpenHelper {

    private static final int database_version = 1;
    private static final String DATABASE_NAME = "RECENT_PLAY_LIST";

    private static final String TABLE_NAME = "recent_table";
    private static final String KEY_ID = "id";
    private static final String SONG_ID = "song_id";
    private static final String ALBUM_ID = "album_id";
    private static final String DURATION = "duration";
    private static final String TITLE = "title";
    private static final String SONG_NAME = "song_name";
    private static final String ALBUM_NAME = "album_name";
    private static final String ARTIST_NAME = "artist_name";
    private static final String SONG_PATH = "song_path";
    private static final String LASTPLAYEDTIME = "lastplayedtime";

    SQLiteDatabase dbs;
    Context ctx;


    public SqliteRecentPlay(Context context) {
        super(context, DATABASE_NAME, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_RECENT_PLAYLIST_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
                SONG_ID + " TEXT," +
                ALBUM_ID + " TEXT," +
                TITLE + " TEXT," +
                SONG_NAME + " TEXT," +
                ALBUM_NAME + " TEXT," +
                ARTIST_NAME + " TEXT," +
                SONG_PATH + " TEXT," +
                LASTPLAYEDTIME + " TEXT," +
                DURATION + ")";

        db.execSQL(CREATE_RECENT_PLAYLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void Add_recent_songs(SongDetail songDetail) {


            SQLiteDatabase adDB = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SONG_ID, songDetail.getId());
            values.put(ALBUM_ID, songDetail.getAlbum_id());
            values.put(TITLE, songDetail.getTitle());
            values.put(SONG_NAME, songDetail.getDisplay_name());
            values.put(ALBUM_NAME, songDetail.get_album());
            values.put(ARTIST_NAME, songDetail.getArtist());
            values.put(SONG_PATH, songDetail.getPath());
            values.put(LASTPLAYEDTIME,System.currentTimeMillis()+"");
            values.put(DURATION, songDetail.getDuration());

            adDB.insert(TABLE_NAME, null, values);
            adDB.close();

    }


    public List<SongDetail> GetRecentSongs() {

        List<SongDetail> songList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME+ " GROUP BY "+ SONG_NAME + " ORDER BY "+LASTPLAYEDTIME;
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

        return songList;
    }

    public boolean isInRecentList(SongDetail songDetail) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + SONG_NAME + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{songDetail.getDisplay_name()});

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {

            cursor.close();
            return false;
        }
    }
}
