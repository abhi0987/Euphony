package music.abitri.com.euphony.AdapterPkg;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import music.abitri.com.euphony.R;
import music.abitri.com.euphony.SQLiteDataBasePackage.PlayListSQLdatabase;
import music.abitri.com.euphony.SecondaryFragmentPkg.ChildSecFragPkg.ChildPlayListFrag;

/**
 * Created by abhis on 2/22/2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlayListHolder> {

    ArrayList<String> playlist;
    Context context;
    PlayListSQLdatabase pdb;

    public PlaylistAdapter(Context context, ArrayList<String> playlist) {
        this.playlist = playlist;
        this.context = context;
        pdb = new PlayListSQLdatabase(context);
    }

    @Override
    public PlaylistAdapter.PlayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ach_track, parent, false);
        PlayListHolder holder = new PlayListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlaylistAdapter.PlayListHolder holder, final int position) {
        if (position % 2 == 0) {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.more_lay.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.dark_primary));
            holder.more_lay.setBackgroundColor(context.getResources().getColor(R.color.dark_primary));
        }
        String playlistnames = playlist.get(position);
        holder.track.setText(playlistnames);
        holder.track.setTextColor(Color.WHITE);

        final PopupMenu popup = new PopupMenu(context, holder.more_lay);
        popup.getMenuInflater().inflate(R.menu.playlist_pop, popup.getMenu());
        if (position<2){
            popup.getMenu().findItem(R.id.dlt_plst_id).setEnabled(false);
            popup.getMenu().findItem(R.id.dlt_plst_id).setVisible(false);

        }else {
            popup.getMenu().findItem(R.id.dlt_plst_id).setVisible(true);
            popup.getMenu().findItem(R.id.dlt_plst_id).setEnabled(true);
        }

        popup.setOnMenuItemClickListener(new menuItemClickListener(position));
        holder.more_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                ChildPlayListFrag albumDetail = ChildPlayListFrag.getInstance(playlist.get(position));
                Log.e("TAG", "GridFragment is creating");
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.right_in,
                        R.anim.right_out, R.anim.right_out);


                fragmentTransaction.replace(R.id.conater, albumDetail);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            }
        });
        holder.artist.setVisibility(View.GONE);
        holder.imageView.setVisibility(View.GONE);
        holder.back_img.setVisibility(View.GONE);
    }


    class menuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        int pos;

        public menuItemClickListener(int position) {
            this.pos = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.play_now_id:
                    return true;
                case R.id.dlt_plst_id:
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Delete " + playlist.get(pos));
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete(playlist.get(pos));
                            playlist.remove(pos);
                            notifyItemRemoved(pos);
                            notifyDataSetChanged();
                            notifyItemRangeChanged(pos, playlist.size());
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

    private void delete(String s) {
        if (!s.equals("Recently played")){
            pdb.DeletePlaylistDB(s);
        }

    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public class PlayListHolder extends RecyclerView.ViewHolder {
        TextView track, artist;
        RelativeLayout cardView, more_lay;
        ImageView imageView,back_img;

        public PlayListHolder(View itemView) {
            super(itemView);
            track = (TextView) itemView.findViewById(R.id.track_name);
            artist = (TextView) itemView.findViewById(R.id.artist_name);
            cardView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            more_lay = (RelativeLayout) itemView.findViewById(R.id.more_lay);
            imageView = (ImageView) itemView.findViewById(R.id.track_thumb);
            back_img = (ImageView) itemView.findViewById(R.id.bkp_img);
        }
    }
}
