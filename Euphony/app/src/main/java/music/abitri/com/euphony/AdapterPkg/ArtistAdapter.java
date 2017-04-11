package music.abitri.com.euphony.AdapterPkg;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.Manager.Artistinfo;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.R;
import music.abitri.com.euphony.SecondaryFragmentPkg.Albumfragment;
import music.abitri.com.euphony.SecondaryFragmentPkg.ChildSecFragPkg.ChildArtistfragment;
import music.abitri.com.euphony.SecondaryFragmentPkg.ChildSecFragPkg.ChildDetilalAlbum;


/**
 * Created by abhis on 3/25/2016.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ListHolder> {
    Context context;
    List<Artistinfo> artist;
    int imgWidth;

    public ArtistAdapter(Context context, List<Artistinfo> songlist, int columnWidth) {
        this.context = context;
        this.artist = songlist;
        this.imgWidth = columnWidth;

    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_artist, parent, false);
        ListHolder holder = new ListHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(ListHolder holder, int position) {

        final Artistinfo info = artist.get(position);

        holder.artisttitle.setText(info.getArtistName());

        if (position % 2 == 0) {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.dark_primary));
        }

        if (info.getArtist_track_no() > 1) {
            holder.song_no.setText(String.valueOf(artist.get(position).getArtist_track_no()) + " Tracks");
        } else {
            holder.song_no.setText(String.valueOf(artist.get(position).getArtist_track_no()) + " Track");
        }


        holder.each_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                ChildArtistfragment artistDetail = ChildArtistfragment.newInstance(info);
                Log.e("TAG", "GridFragment is creating");
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.right_in,
                        R.anim.right_out, R.anim.right_out);


                fragmentTransaction.replace(R.id.artist, artistDetail);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);


            }
        });

    }

    @Override
    public int getItemCount() {
        return artist.size();
    }


    public class ListHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        RelativeLayout each_rel;

        TextView artisttitle;
        TextView song_no;


        public ListHolder(View itemView) {
            super(itemView);
            artisttitle = (TextView) itemView.findViewById(R.id.textViewArt);
            song_no = (TextView) itemView.findViewById(R.id.song_no_id);
            cardView = (CardView) itemView.findViewById(R.id.card);
            each_rel = (RelativeLayout) itemView.findViewById(R.id.each_real);


        }

    }
}
