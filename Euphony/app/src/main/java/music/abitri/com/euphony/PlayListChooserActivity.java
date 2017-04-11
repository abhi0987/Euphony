package music.abitri.com.euphony;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.Gson;

import java.util.ArrayList;

import music.abitri.com.euphony.AdapterPkg.Playlist_Db_adapter;
import music.abitri.com.euphony.AdapterPkg.TrackAdapter;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.SQLiteDataBasePackage.FavoritesDatabase;
import music.abitri.com.euphony.SQLiteDataBasePackage.PlayListSQLdatabase;

public class PlayListChooserActivity extends AppCompatActivity implements View.OnClickListener {
    public static RecyclerView listView;
    public static CardView new_playlist_lay;
    PlayListSQLdatabase PDB;
    SongDetail detail;
    Handler handler = new Handler();
    int pos;
    ArrayList<String> tbnms;
    Playlist_Db_adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_chooser);


        listView = (RecyclerView) findViewById(R.id.avlble_playlist);
        new_playlist_lay = (CardView) findViewById(R.id.new_playlist_view);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager();
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        listView.setLayoutManager(layoutManager);

        String data = getIntent().getStringExtra("Songdetail");
        detail = new Gson().fromJson(data,SongDetail.class);
        pos = getIntent().getIntExtra("Pos",0);


        handler.postDelayed(run,200);

        new_playlist_lay.setOnClickListener(this);


        listView.addOnItemTouchListener(new RecyclerTouchListener(getBaseContext(), listView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String table_name = tbnms.get(position);
                Log.d("PLAYLISTCHOOSER",detail.getDisplay_name());
                PDB.AddSongData(table_name, detail);
                Toast.makeText(getBaseContext(), "Added to playlist : " + table_name, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }


    Runnable run = new Runnable() {
        @Override
        public void run() {

            setplstAdapter();
            handler.postDelayed(this,200);
        }
    };

    public void setplstAdapter(){
        PDB = new PlayListSQLdatabase(getBaseContext());
        tbnms = new ArrayList<>();
        tbnms = PDB.getTablenames();
        tbnms.remove(0);
        if (!tbnms.isEmpty()) {
            listView.setVisibility(View.VISIBLE);
            adapter = new Playlist_Db_adapter(getBaseContext(),tbnms);
            listView.setAdapter(adapter);
        }else {
            listView.setVisibility(View.GONE);
        }

    }


    public void PlaylistBottomSheetWork(final int position) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(getBaseContext()).inflate(R.layout.playlist_dlg, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.enter_list_nm);
        dialogBuilder.setTitle("New Playlist");

        dialogBuilder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String table_name = edt.getText().toString().trim();
                if (!table_name.equals("")){
                    PDB.Create_DynamicDB(table_name, detail);
                    Toast.makeText(getBaseContext(), "Added to Playlist " + table_name, Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    edt.setError("Enter table name");
                }


            }
        });

        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.new_playlist_view:
                PlaylistBottomSheetWork(pos);
                break;
        }
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }



        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
