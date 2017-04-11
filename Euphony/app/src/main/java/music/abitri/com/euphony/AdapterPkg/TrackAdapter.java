package music.abitri.com.euphony.AdapterPkg;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.BasemusicActivity;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.PlayActivity;
import music.abitri.com.euphony.PlayListChooserActivity;
import music.abitri.com.euphony.PrefManager;
import music.abitri.com.euphony.R;
import music.abitri.com.euphony.SQLiteDataBasePackage.FavoritesDatabase;
import music.abitri.com.euphony.SQLiteDataBasePackage.PlayListSQLdatabase;
import music.abitri.com.euphony.ServcesPkg.PLayerService;
import music.abitri.com.euphony.SharedPrefPKg.TypeStoreManager;


/**
 * Created by abhis on 3/25/2016.
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ListHolder> {

    Handler handler;
    Context context;
    List<SongDetail> songlist;
    int imgwdth;
    String type;
    PrefManager prefManager;
    TypeStoreManager typeManager;
    String type_, retrive_type;

    PlayListSQLdatabase PDB;

    ArrayList<String> tbnms;
    Playlist_Db_adapter adapter;
    FavoritesDatabase fDB;

    public TrackAdapter(Context context, List<SongDetail> songlist, int columnWidth, String s, String type_, String retrive_type) {

        this.context = context;
        this.imgwdth = columnWidth;
        this.songlist = songlist;
        this.type_ = type_;
        this.retrive_type = retrive_type;
        this.type = s;
        prefManager = new PrefManager(context);
        typeManager = new TypeStoreManager(context);
        PDB = new PlayListSQLdatabase(context);
        fDB = new FavoritesDatabase(context);
        handler = new Handler();
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ach_track, parent, false);
        ListHolder holder = new ListHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ListHolder holder, final int position) {

        if (position % 2 == 0) {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.more_lay.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.dark_primary));
            holder.more_lay.setBackgroundColor(context.getResources().getColor(R.color.dark_primary));
        }
          File file = new File(songlist.get(position).getPath());


        if (file.exists()){
            if (type.equals("TRACK_AND_ART")) {
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(imgwdth / 3, ViewGroup.LayoutParams.MATCH_PARENT));
                Glide.with(context).load(songlist.get(position).getSmallCover())
                        .crossFade()
                        .override(300,300)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageView);
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                holder.imageView.setColorFilter(filter);
            } else if (type.equals("CHILD_ALBUM")) {
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
               // holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(imgwdth / 3, ViewGroup.LayoutParams.MATCH_PARENT));
                Glide.with(context).load(songlist.get(position).getSmallCover())
                        .thumbnail(0.5f)
                        .crossFade()
                        .override(200,200)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageView);
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                holder.imageView.setColorFilter(filter);
            }



            holder.artist.setText(songlist.get(position).getArtist());
            holder.track.setText(songlist.get(position).getTitle());

            final PopupMenu popup = new PopupMenu(context, holder.more_lay);
            popup.getMenuInflater().inflate(R.menu.pop_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new menuItemClickListner(position,songlist));
            holder.more_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(position);

                }
            });
        }






    }


    @Override
    public int getItemCount() {
        return songlist.size();
    }


    class menuItemClickListner implements PopupMenu.OnMenuItemClickListener {

        int pos;
        List<SongDetail> myList;

        public menuItemClickListner(int position, List<SongDetail> songlist) {
            this.pos = position;
            this.myList = songlist;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.play_id:
                    play(pos);
                    return true;
                case R.id.plst_id:
                    Intent intent = new Intent(context, PlayListChooserActivity.class);
                    intent.putExtra("Pos",pos);
                    String str = new Gson().toJson(myList.get(pos));
                    intent.putExtra("Songdetail",str);
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return true;
                case R.id.fav_id:
                    boolean stat = fDB.addSongsToFav(songlist.get(pos));
                    if (stat) {

                        Toast.makeText(context,"Added to favorites",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"Already exists in favorites",Toast.LENGTH_SHORT).show();

                    }
                    return true;
                case R.id.edit_id:
                    return true;
                case R.id.shr_id:
                    Share(pos);
                    return true;

            }


            return false;
        }
    }

    private void Share(int pos) {

        File audio = new File(songlist.get(pos).getPath());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(audio));
        context.startActivity(Intent.createChooser(shareIntent,"Share Using"));
    }


    public class ListHolder extends RecyclerView.ViewHolder {
        TextView track, artist;
        RelativeLayout cardView, more_lay;
        ImageView imageView,backup_img;

        public ListHolder(View itemView) {
            super(itemView);
            track = (TextView) itemView.findViewById(R.id.track_name);
            artist = (TextView) itemView.findViewById(R.id.artist_name);
            imageView = (ImageView) itemView.findViewById(R.id.track_thumb);
            backup_img = (ImageView) itemView.findViewById(R.id.bkp_img);
            cardView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            more_lay = (RelativeLayout) itemView.findViewById(R.id.more_lay);

        }
    }


    public void play(int position) {
        Intent serviceTrack = new Intent(context, PLayerService.class);

        prefManager.StoreSongs(songlist);
        prefManager.storePosition(position);
        prefManager.setEndingValue(false);
        prefManager.setCurrentDuration(0);
        //////////////////////////////////
       // typeManager.setListType(type_);
       // typeManager.setRetriveType(retrive_type);


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            context.startService(serviceTrack);
            Intent activity = new Intent(context, PlayActivity.class);
            activity.setFlags(activity.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activity);
            Activity activity_anim = (Activity) context;
            activity_anim.overridePendingTransition(R.anim.bottom_up, 0);


        }

    }
















}
