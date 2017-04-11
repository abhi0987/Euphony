package music.abitri.com.euphony.AdapterPkg;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import music.abitri.com.euphony.BasemusicActivity;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.PlayActivity;
import music.abitri.com.euphony.PrefManager;
import music.abitri.com.euphony.R;
import music.abitri.com.euphony.SQLiteDataBasePackage.FavoritesDatabase;
import music.abitri.com.euphony.SQLiteDataBasePackage.PlayListSQLdatabase;
import music.abitri.com.euphony.ServcesPkg.PLayerService;

/**
 * Created by abhis on 2/24/2017.
 */

public class ChildPlaylistAdapter extends RecyclerView.Adapter<ChildPlaylistAdapter.ChildHolder> {

    List<SongDetail> songList;
    Context context;
    int imgWidtg;
    PrefManager prefManager;
    PlayListSQLdatabase PDB;
    String tableName;
    FavoritesDatabase fDB;


    public ChildPlaylistAdapter(Context context, List<SongDetail> list, int imgWidth, String name) {

        this.songList = list;
        this.context = context;
        this.imgWidtg = imgWidth;
        this.tableName = name;
        prefManager = new PrefManager(context);
        PDB = new PlayListSQLdatabase(context);
        fDB = new FavoritesDatabase(context);
    }

    @Override
    public ChildPlaylistAdapter.ChildHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ach_track, parent, false);
        ChildHolder holder = new ChildHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ChildPlaylistAdapter.ChildHolder holder, final int position) {

        if (position % 2 == 0) {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.more_lay.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.dark_primary));
            holder.more_lay.setBackgroundColor(context.getResources().getColor(R.color.dark_primary));
        }

        File file = new File(songList.get(position).getPath());
        if (file.exists()) {
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(imgWidtg / 3, ViewGroup.LayoutParams.MATCH_PARENT));
            Glide.with(context).load(songList.get(position).getSmallCover())
                    .crossFade()
                    .override(300,300)
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
            popup.getMenuInflater().inflate(R.menu.child_plst_menu, popup.getMenu());
            if (tableName.equals("Recently Played") || tableName.equals("Recently Added")){
                popup.getMenu().findItem(R.id.dlt_plst_id).setEnabled(false);
                popup.getMenu().findItem(R.id.dlt_plst_id).setVisible(false);

            }else {
                popup.getMenu().findItem(R.id.dlt_plst_id).setVisible(true);
                popup.getMenu().findItem(R.id.dlt_plst_id).setEnabled(true);
            }

            popup.setOnMenuItemClickListener(new menuItemClickListner(position));

            holder.more_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });

            holder.artist.setText(songList.get(position).getArtist());
            holder.track.setText(songList.get(position).getTitle());
        }


    }


    public void play(int position) {
        Intent serviceTrack = new Intent(context, PLayerService.class);

        prefManager.StoreSongs(songList);
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

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ChildHolder extends RecyclerView.ViewHolder {
        TextView track, artist;
        RelativeLayout cardView, more_lay;
        ImageView imageView,back_img;

        public ChildHolder(View itemView) {
            super(itemView);
            track = (TextView) itemView.findViewById(R.id.track_name);
            artist = (TextView) itemView.findViewById(R.id.artist_name);
            imageView = (ImageView) itemView.findViewById(R.id.track_thumb);
            cardView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            more_lay = (RelativeLayout) itemView.findViewById(R.id.more_lay);
            back_img = (ImageView) itemView.findViewById(R.id.bkp_img);
        }
    }




    class menuItemClickListner implements PopupMenu.OnMenuItemClickListener {

        int pos;

        public menuItemClickListner(int position) {
            this.pos = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.play__id:
                    play(pos);
                    return true;
                case R.id.fav_id:
                    boolean stat = fDB.addSongsToFav(songList.get(pos));
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
                case R.id.dlt_plst_id:

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Delete " + songList.get(pos).getTitle());
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete(songList.get(pos));
                            songList.remove(pos);
                            prefManager.StoreSongs(songList);
                            notifyItemRemoved(pos);
                            notifyDataSetChanged();
                            notifyItemRangeChanged(pos, songList.size());
                            Toast.makeText(context, " deleted succesfully", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();


                    return true;
            }


            return false;
        }
    }

    private void delete(SongDetail spngdetail) {

        PDB.DeleteTableData(tableName,spngdetail);

    }

    private void Share(int pos) {

        File audio = new File(songList.get(pos).getPath());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(audio));
        context.startActivity(Intent.createChooser(shareIntent,"Share Using"));
    }

}
