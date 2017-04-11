/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package music.abitri.com.euphony.Manager;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

public class SongDetail implements Serializable {
    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public int dbId;
    public int id;
    public int album_id;
    public String artist;
    public String title;
    public String _album;
    public String display_name;
    public String duration;
    public String path;
    public float audioProgress = 0.0f;
    public int audioProgressSec = 0;
    String type;

    public String get_album() {
        return _album;
    }

    public void set_album(String _album) {
        this._album = _album;
    }

    public SongDetail(){

    }

    public SongDetail(int _id, int aLBUM_ID, String _album, String _artist, String _title, String _path, String _display_name, String _duration, String type_) {
        this.id = _id;
        this.album_id = aLBUM_ID;
        this._album = _album;
        this.artist = _artist;
        this.title = _title;
        this.path = _path;
        this.type = type_;
        this.display_name = _display_name;
        this.duration = TextUtils.isEmpty(_duration) ? "0" : String.valueOf((Long.valueOf(_duration) / 1000));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getSmallCover() {

        Uri uri = null;
        try {


            Uri artUri = Uri.parse("content://media/external/audio/albumart");
            uri = ContentUris.withAppendedId(artUri, getAlbum_id());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        switch (type) {
            case "ALBUM":
                if (object != null && object instanceof SongDetail) {
                    sameSame = this._album.equals(((SongDetail) object)._album);

                }
                break;
            case "ALL":
                break;
            case "ARTIST":
                if (object != null && object instanceof SongDetail) {
                    sameSame = this.artist.equals(((SongDetail) object).artist);
                }
                break;
            case "CHECK_SD":
                if (object != null && object instanceof SongDetail) {
                    sameSame = this.display_name.equals(((SongDetail) object).display_name);
                }
            case "GENRE":
                break;
            default:
                break;
        }


        return sameSame;
    }
}
