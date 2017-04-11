package music.abitri.com.euphony.Manager;

import java.io.Serializable;

/**
 * Created by abhis on 2/13/2017.
 */

public class GenreClass implements Serializable{
    String genreTitle;
    String uri;
    int genid;

    public GenreClass(String genreTitle, String uri, int genid) {
        this.genreTitle = genreTitle;
        this.uri = uri;
        this.genid = genid;
    }

    public String getGenreTitle() {
        return genreTitle;
    }

    public void setGenreTitle(String genreTitle) {
        this.genreTitle = genreTitle;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getGenid() {
        return genid;
    }

    public void setGenid(int genid) {
        this.genid = genid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenreClass)) return false;

        GenreClass that = (GenreClass) o;

        return getGenreTitle().equals(that.getGenreTitle());

    }

    @Override
    public int hashCode() {
        return getGenreTitle().hashCode();
    }
}
