package music.abitri.com.euphony.AdapterPkg;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.Manager.GenreClass;
import music.abitri.com.euphony.R;
import music.abitri.com.euphony.SecondaryFragmentPkg.ChildSecFragPkg.ChildArtistfragment;
import music.abitri.com.euphony.SecondaryFragmentPkg.ChildSecFragPkg.ChildGenreFragment;

/**
 * Created by abhis on 2/13/2017.
 */

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreHolder> {

    Context context;
    int imgwdth;
    List<GenreClass> songlist;

    public GenreAdapter(Context context, List<GenreClass> songlist, int columnWidth) {
        this.context = context;
        this.imgwdth = columnWidth;
        this.songlist = songlist;
    }

    @Override
    public GenreAdapter.GenreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_album, parent, false);
        GenreHolder holder = new GenreHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GenreAdapter.GenreHolder holder, int position) {
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(imgwdth, imgwdth / 2));
        holder.track.setText(songlist.get(position).getGenreTitle());
        holder.imageView.setImageResource(R.drawable.ic_library_music_grey_300_48dp);
        holder.imageView.setVisibility(View.GONE);

        final GenreClass genInfo = songlist.get(position);

        holder.out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                ChildGenreFragment artistDetail = ChildGenreFragment.newInstance(genInfo);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.right_in,
                        R.anim.right_out, R.anim.right_out);
                fragmentTransaction.replace(R.id.container, artistDetail);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                Log.e("TAG", "GenreFrag is created");
            }
        });


    }

    @Override
    public int getItemCount() {
        return songlist.size();
    }

    public class GenreHolder extends RecyclerView.ViewHolder {
        TextView track, artist;
        RelativeLayout out;
        ImageView imageView;

        public GenreHolder(View itemView) {
            super(itemView);
            track = (TextView) itemView.findViewById(R.id.textViewtitle);
            imageView = (ImageView) itemView.findViewById(R.id.thumbnail);
            out = (RelativeLayout) itemView.findViewById(R.id.each);
        }
    }
}
