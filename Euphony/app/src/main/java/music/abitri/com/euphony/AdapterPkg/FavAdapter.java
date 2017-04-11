package music.abitri.com.euphony.AdapterPkg;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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
import music.abitri.com.euphony.SQLiteDataBasePackage.PlayListSQLdatabase;
import music.abitri.com.euphony.ServcesPkg.PLayerService;

/**
 * Created by abhis on 3/10/2017.
 */

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavitemHolder> {
    Handler handler = new Handler();
    Context context;
    List<SongDetail> favList;
    PrefManager prefManager;

    PlayListSQLdatabase PDB;

    ArrayList<String> tbnms;
    Playlist_Db_adapter adapter;

    public FavAdapter(Context context, List<SongDetail> favList) {

        this.context = context;
        this.favList = favList;
        prefManager = new PrefManager(context);
        PDB = new PlayListSQLdatabase(context);
    }

    @Override
    public FavitemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ach_track, parent, false);
        FavitemHolder holder = new FavitemHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(FavitemHolder holder, final int position) {

        if (position % 2 == 0) {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.more_lay.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.dark_primary));
            holder.more_lay.setBackgroundColor(context.getResources().getColor(R.color.dark_primary));
        }

        File file = new File(favList.get(position).getPath());
        if (file.exists()) {
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(imgWidtg / 3, ViewGroup.LayoutParams.MATCH_PARENT));
            Glide.with(context).load(favList.get(position).getSmallCover())
                    .crossFade()
                    .override(300, 300)
                    .into(holder.imageView);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            holder.imageView.setColorFilter(filter);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(position);
                }
            });

            final PopupMenu popup = new PopupMenu(context, holder.more_lay);
            popup.getMenuInflater().inflate(R.menu.fav_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new menuItemClickListner(position, favList));
            holder.more_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });

            holder.artist.setText(favList.get(position).getArtist());
            holder.track.setText(favList.get(position).getTitle());
        }

    }

    @Override
    public int getItemCount() {
        return favList.size();
    }

    public class FavitemHolder extends RecyclerView.ViewHolder {

        TextView track, artist;
        RelativeLayout cardView, more_lay;
        ImageView imageView;

        public FavitemHolder(View itemView) {
            super(itemView);
            track = (TextView) itemView.findViewById(R.id.track_name);
            artist = (TextView) itemView.findViewById(R.id.artist_name);
            imageView = (ImageView) itemView.findViewById(R.id.track_thumb);
            cardView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            more_lay = (RelativeLayout) itemView.findViewById(R.id.more_lay);
        }
    }


    class menuItemClickListner implements PopupMenu.OnMenuItemClickListener {

        int pos;
        List<SongDetail> myList;

        public menuItemClickListner(int position, List<SongDetail> myList) {
            this.pos = position;
            this.myList = myList;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.play_id:
                    play(pos);
                    return true;
                case R.id.plst_id:
                    Intent intent = new Intent(context, PlayListChooserActivity.class);
                    intent.putExtra("Pos", pos);
                    String str = new Gson().toJson(myList.get(pos));
                    intent.putExtra("Songdetail", str);
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
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

    public void play(int position) {
        Intent serviceTrack = new Intent(context, PLayerService.class);

        prefManager.StoreSongs(favList);
        prefManager.storePosition(position);
        prefManager.setEndingValue(false);
        prefManager.setCurrentDuration(0);

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


    private void Share(int pos) {

        File audio = new File(favList.get(pos).getPath());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(audio));
        context.startActivity(Intent.createChooser(shareIntent, "Share Using"));
    }


}
