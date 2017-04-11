package music.abitri.com.euphony.AdapterPkg;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.BasemusicActivity;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.R;
import music.abitri.com.euphony.SecondaryFragmentPkg.Albumfragment;
import music.abitri.com.euphony.SecondaryFragmentPkg.ChildSecFragPkg.ChildDetilalAlbum;


/**
 * Created by abhis on 3/24/2016.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.gridHolder> {
    Context context;
    List<SongDetail> list;
    int imageWidth;
    String type;


    public AlbumAdapter(Context baseContext, List<SongDetail> songlist, int columnWidth,String type) {

        this.context = baseContext;
        this.list = songlist;
        this.imageWidth = columnWidth;
        this.type=type;



    }

    @Override
    public gridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_album, parent, false);
        gridHolder holder = new gridHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(gridHolder holder, int position) {


        //holder.thumbNail.setTransitionName("thumbnail" + position);

        holder.thumbNail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.thumbNail.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, imageWidth));
        //Log.d("album_pic_path", list.get(position).getSmallCover(context).toString());
        Glide.with(context).load(list.get(position).getSmallCover().toString())
                .thumbnail(0.1f)
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbNail);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        holder.thumbNail.setColorFilter(filter);

        holder.albumtitle.setText(list.get(position).get_album());

        final SongDetail detail = list.get(position);
        holder.each_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                ChildDetilalAlbum albumDetail = ChildDetilalAlbum.newInstance(detail);
                Albumfragment first = Albumfragment.newInstance();
                Log.e("TAG", "GridFragment is creating");
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.right_in,
                        R.anim.right_out, R.anim.right_out);


                fragmentTransaction.replace(R.id.album, albumDetail);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class gridHolder extends RecyclerView.ViewHolder {

        ImageView thumbNail;
        TextView albumtitle;
        CardView cardView;
        RelativeLayout each_rel;


        public gridHolder(View itemView) {
            super(itemView);
            thumbNail = (ImageView) itemView.findViewById(R.id.thumbnail);
            albumtitle = (TextView) itemView.findViewById(R.id.textViewtitle);
            cardView = (CardView) itemView.findViewById(R.id.card);
            each_rel = (RelativeLayout) itemView.findViewById(R.id.each);

        }

    }


}
