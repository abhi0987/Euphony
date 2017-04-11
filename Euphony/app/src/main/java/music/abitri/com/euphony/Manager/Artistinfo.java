package music.abitri.com.euphony.Manager;

import java.io.Serializable;

/**
 * Created by abhis on 2/13/2017.
 */

public class Artistinfo implements Serializable {

    public Artistinfo(String artistName, String artistkey, String artist_id, int artist_track_no) {
        this.artistName = artistName;
        this.artistkey = artistkey;
        this.artist_id = artist_id;
        this.artist_track_no = artist_track_no;
    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistkey() {
        return artistkey;
    }

    public void setArtistkey(String artistkey) {
        this.artistkey = artistkey;
    }

    public int getArtist_track_no() {
        return artist_track_no;
    }

    public void setArtist_track_no(int artist_track_no) {
        this.artist_track_no = artist_track_no;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    String artistName;
    String artistkey;
    int artist_track_no;
    String artist_id;
}
