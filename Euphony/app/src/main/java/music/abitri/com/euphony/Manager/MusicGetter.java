package music.abitri.com.euphony.Manager;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abhis on 2/12/2017.
 */

public class MusicGetter {
    String[] STAR = {"*"};
    Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;


    public List<SongDetail> getAlbum(Context context, String type) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
        List<SongDetail> xxx;
        Cursor cursor = context.getContentResolver().query(allsongsuri, STAR, selection, null,
                MediaStore.Audio.Media.TITLE +" COLLATE NOCASE ASC");
        List<SongDetail> albumlist = new ArrayList<>();
        xxx = new ArrayList<>();

        if (cursor != null) {

            int j = 0;
            int i = 0;
            if (cursor.moveToFirst()) {

                do {

                    int _id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int album_name = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    int display_name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                    int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                    int ID = cursor.getInt(_id);
                    int ALBUM_ID = cursor.getInt(album_id);
                    String ALBUM = cursor.getString(album_name);
                    String ARTIST = cursor.getString(artist);
                    String TITLE = cursor.getString(title);
                    String DISPLAY_NAME = cursor.getString(display_name);
                    String DURATION = cursor.getString(duration);
                    String Path = cursor.getString(data);

                    if (Path!=null || !Path.equals("")){

                        SongDetail mSongDetail = new SongDetail(ID, ALBUM_ID, ALBUM, ARTIST, TITLE, Path, DISPLAY_NAME, DURATION, type);
                        xxx.add(mSongDetail);
                    }



                    if (type.equals("ALBUM")) {
                        if (albumlist.contains(xxx.get(j))) {
                        } else {
                            albumlist.add(xxx.get(j));
                            //Log.d("albm", xxx.get(j).get_album());
                        }
                    } else if (type.equals("ALL")) {
                        albumlist = xxx;
                    }

                    j = j + 1;

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        
       /* Collections.sort(albumlist, new Comparator<SongDetail>() {
            @Override
            public int compare(SongDetail o1, SongDetail o2) {
                return o1.getDisplay_name().compareTo(o2.getDisplay_name());
            }
        });*/
        return albumlist;
    }

    public List<Artistinfo> getArtist_(Context context, String type) {

        String[] proj = {MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS,
                MediaStore.Audio.Artists.ARTIST_KEY
                , MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
        };

        String selection = null;

        String[] selectionArgs = null;

        String sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC";

        Cursor audioCursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, proj, selection, selectionArgs, sortOrder);

        List<Artistinfo> artistList = new ArrayList<>();
        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    int artistname = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
                    int artist_id = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
                    int artist_track = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
                    int artist_key = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST_KEY);

                    String name = (audioCursor.getString(artistname));
                    String key = (audioCursor.getString(artist_key));
                    int no_track = (audioCursor.getInt(artist_track));

                    String id_ = (audioCursor.getString(artist_id));
                    Artistinfo info = new Artistinfo(name, key, id_, no_track);
                    artistList.add(info);




                } while (audioCursor.moveToNext());


            }
        }
       /* Collections.sort(artistList, new Comparator<Artistinfo>() {
            @Override
            public int compare(Artistinfo o1, Artistinfo o2) {
                return o1.getArtistName().compareTo(o2.getArtistName());
            }
        });*/

        return artistList;
    }

    public List<GenreClass> getGenre(Context context, String genreString) {
        Cursor genrecursor;
        List<GenreClass> genreList = new ArrayList<>();
        List<GenreClass> zzz = new ArrayList<>();

        String[] gen_star = {MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID};
        String query = " _id in (select genre_id from audio_genres_map where audio_id in (select _id from audio_meta where is_music != 0))" ;
        genrecursor = context.getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, gen_star, query, null, null);
        int j = 0;
        if (genrecursor != null) {

            if (genrecursor.moveToFirst()) {

                do {

                    String gen_name = genrecursor.getString(genrecursor.getColumnIndex(MediaStore.Audio.Genres.NAME));


                    int gen_id = genrecursor.getInt(genrecursor.getColumnIndex(MediaStore.Audio.Genres._ID));
                    Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", gen_id);
                    GenreClass genre = new GenreClass(gen_name, uri.toString(), gen_id);

                    if (genre.getGenreTitle().equalsIgnoreCase("")) {
                        genre.setGenreTitle("no-genre");
                    }
                    zzz.add(genre);


                    if (genreList.contains(zzz.get(j))) {
                    } else {
                        genreList.add(zzz.get(j));
                        //Log.d("genre", zzz.get(j).getGenreTitle());
                    }
                    j = j + 1;


                } while (genrecursor.moveToNext());
            }
            genrecursor.close();
        }

       /* Collections.sort(genreList, new Comparator<GenreClass>() {
            @Override
            public int compare(GenreClass o1, GenreClass o2) {
                return o1.getGenreTitle().compareTo(o2.getGenreTitle());
            }
        });*/
        return genreList;


    }

    //////////////////////////////////////

    public List<SongDetail> getChildAlbumList(Context context, String name) {
        List<SongDetail> song_list = new ArrayList<>();
        String where = MediaStore.Audio.Media.ALBUM + "=?";
        String whereVal[] = {name};
        String orderBy = MediaStore.Audio.Media.TITLE;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                STAR, where, whereVal, orderBy);

        if (cursor != null) {


            if (cursor.moveToFirst()) {

                do {
                    int _id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int album_name = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    int display_name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                    int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                    int ID = cursor.getInt(_id);
                    int ALBUM_ID = cursor.getInt(album_id);
                    String ALBUM = cursor.getString(album_name);
                    String ARTIST = cursor.getString(artist);
                    String TITLE = cursor.getString(title);
                    String DISPLAY_NAME = cursor.getString(display_name);
                    String DURATION = cursor.getString(duration);
                    String Path = cursor.getString(data);

                    SongDetail mSongDetail = new SongDetail(ID, ALBUM_ID, ALBUM, ARTIST, TITLE, Path, DISPLAY_NAME, DURATION, null);
                    song_list.add(mSongDetail);


                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        /*Collections.sort(song_list, new Comparator<SongDetail>() {
            @Override
            public int compare(SongDetail o1, SongDetail o2) {
                return o1.getDisplay_name().compareTo(o2.getDisplay_name());
            }
        });*/
        return song_list;
    }

    public List<SongDetail> getChildArtistList(Context context, String name) {
        List<SongDetail> song_list = new ArrayList<>();
        String where = MediaStore.Audio.Media.ARTIST + "=?";
        String whereVal[] = {name};
        String orderBy = MediaStore.Audio.Media.TITLE;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                STAR, where, whereVal, orderBy);

        if (cursor != null) {


            if (cursor.moveToFirst()) {

                do {
                    int _id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int album_name = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    int display_name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                    int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                    int ID = cursor.getInt(_id);
                    int ALBUM_ID = cursor.getInt(album_id);
                    String ALBUM = cursor.getString(album_name);
                    String ARTIST = cursor.getString(artist);
                    String TITLE = cursor.getString(title);
                    String DISPLAY_NAME = cursor.getString(display_name);
                    String DURATION = cursor.getString(duration);
                    String Path = cursor.getString(data);

                    SongDetail mSongDetail = new SongDetail(ID, ALBUM_ID, ALBUM, ARTIST, TITLE, Path, DISPLAY_NAME, DURATION, null);
                    song_list.add(mSongDetail);


                } while (cursor.moveToNext());

            }
            cursor.close();
        }

       /* Collections.sort(song_list, new Comparator<SongDetail>() {
            @Override
            public int compare(SongDetail o1, SongDetail o2) {
                return o1.getDisplay_name().compareTo(o2.getDisplay_name());
            }
        });*/
        return song_list;
    }

    public List<SongDetail> getChildGenreList(Context context, Uri uri) {

        List<SongDetail> song_list = new ArrayList<>();
        String orderBy = MediaStore.Audio.Media.TITLE;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
        Cursor cursor = context.getContentResolver().query(uri,
                STAR, selection, null, orderBy);
        if (cursor != null) {


            if (cursor.moveToFirst()) {

                do {
                    int _id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int album_name = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    int display_name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                    int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                    int ID = cursor.getInt(_id);
                    int ALBUM_ID = cursor.getInt(album_id);
                    String ALBUM = cursor.getString(album_name);
                    String ARTIST = cursor.getString(artist);
                    String TITLE = cursor.getString(title);
                   // Log.d("Song_title", TITLE);
                    String DISPLAY_NAME = cursor.getString(display_name);
                    String DURATION = cursor.getString(duration);
                    String Path = cursor.getString(data);

                    SongDetail mSongDetail = new SongDetail(ID, ALBUM_ID, ALBUM, ARTIST, TITLE, Path, DISPLAY_NAME, DURATION, null);
                    song_list.add(mSongDetail);


                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        /*Collections.sort(song_list, new Comparator<SongDetail>() {
            @Override
            public int compare(SongDetail o1, SongDetail o2) {
                return o1.getDisplay_name().compareTo(o2.getDisplay_name());
            }
        });*/
        return song_list;


    }


    ////date added /////

    public List<SongDetail> getSongDateWise(Context context){

        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
        Cursor cursor = context.getContentResolver().query(allsongsuri, STAR, selection, null,
                MediaStore.Audio.Media.DATE_ADDED);
        List<SongDetail> albumlist = new ArrayList<>();

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {

                    int _id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int album_name = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    int display_name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                    int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                    int ID = cursor.getInt(_id);
                    int ALBUM_ID = cursor.getInt(album_id);
                    String ALBUM = cursor.getString(album_name);
                    String ARTIST = cursor.getString(artist);
                    String TITLE = cursor.getString(title);
                    String DISPLAY_NAME = cursor.getString(display_name);
                    String DURATION = cursor.getString(duration);
                    String Path = cursor.getString(data);

                    if (Path!=null || !Path.equals("")){

                        SongDetail mSongDetail = new SongDetail(ID, ALBUM_ID, ALBUM, ARTIST, TITLE, Path, DISPLAY_NAME, DURATION, "ALL");
                        albumlist.add(mSongDetail);
                    }


                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        Collections.reverse(albumlist);

        albumlist = albumlist.subList(0,9);

        return albumlist;
    }




}
