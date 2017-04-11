package music.abitri.com.euphony;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.List;


import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.ServcesPkg.PLayerService;

public class QueueActivity extends AppCompatActivity implements View.OnClickListener {


    Handler handler = new Handler();
    Handler hand = new Handler();
    ImageView que_img, qplay_pause;
    TextView que_song, que_artist;
    RecyclerView q_list_view;
    List<SongDetail> songList;
    RelativeLayout q_play_lay;
    SpinKitView spinKitView;

    int pos;
    QueAdapter adapter;
    PrefManager prefManager;
    private boolean musicBound = false;
    private PLayerService musicSrv;
    private Intent playIntent;
    boolean endstate;
    int adpaterPos;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_queue);
        UisetUp();
        getData();


    }

    private void UisetUp() {
        que_img = (ImageView) findViewById(R.id.que_img);
        qplay_pause = (ImageView) findViewById(R.id.q_pl_ps);
        que_song = (TextView) findViewById(R.id.q_song_nm);
        que_artist = (TextView) findViewById(R.id.q_artist_nm);
        q_list_view = (RecyclerView) findViewById(R.id.que_list);
        q_play_lay = (RelativeLayout) findViewById(R.id.q_play);
        q_play_lay.setOnClickListener(this);

    }

    private void getData() {
        prefManager = new PrefManager(getBaseContext());
        songList = prefManager.getSongs(Constants.KEY_ALBUM);
        pos = getIntent().getIntExtra("QPOS", 0);
        endstate = prefManager.getEndingValue();
        SongDetail songDetail = songList.get(pos);
        Log.e("time_length", songDetail.getDuration());
        handler.postDelayed(queRun, 200);
        adapter = new QueAdapter();
        q_list_view.setLayoutManager(new LinearLayoutManager(this));
        q_list_view.addItemDecoration(new VerticalSpaceDecoration(15));
        q_list_view.setAdapter(adapter);
        /**/


    }


    Runnable queRun = new Runnable() {
        @Override
        public void run() {


            if (musicBound == true) {
                endstate = prefManager.getEndingValue();
                if (endstate) {
                    Glide.with(getBaseContext())
                            .load(songList.get(pos).getSmallCover())
                            .override(600, 600)
                            .thumbnail(0.8f)
                            .centerCrop()
                            .into(que_img);
                    que_song.setText(songList.get(pos).getTitle());
                    que_artist.setText(songList.get(pos).getArtist());
                } else {
                    pos = musicSrv.getSongPos();
                    Glide.with(getBaseContext())
                            .load(musicSrv.getSongList().get(pos).getSmallCover())
                            .override(600, 600)
                            .thumbnail(0.8f)
                            .centerCrop()
                            .into(que_img);
                    que_song.setText(musicSrv.getSongList().get(pos).getTitle());
                    que_artist.setText(musicSrv.getSongList().get(pos).getArtist());

                }

                QplayBtn();

            }

            handler.postDelayed(this, 200);
        }
    };


    public void QplayBtn() {

        if (musicSrv.Companion.getPlayer() != null) {
            if (musicSrv.Companion.getPlayer().isPlaying()) {
                qplay_pause.setImageResource(R.drawable.pause_button);
            } else {
                qplay_pause.setImageResource(R.drawable.play_button);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            try {
                playIntent = new Intent(this, PLayerService.class);
                bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PLayerService.MusicBinder binder = (PLayerService.MusicBinder) service;
            musicSrv = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.q_play:

                if (endstate) {
                    startService(playIntent);
                    play_pause_animation(qplay_pause, true);
                    endstate = false;
                } else {
                    if (PLayerService.Companion.getPlayer().isPlaying()) {
                        musicSrv.PauseMusic();
                        BasemusicActivity.btm_btn.setImageResource(R.drawable.play_button);
                        qplay_pause.setImageResource(R.drawable.play_button);
                    } else {
                        musicSrv.resumeMusic();
                        BasemusicActivity.btm_btn.setImageResource(R.drawable.pause_button);
                        play_pause_animation(qplay_pause, true);
                    }
                }

                break;
        }
    }


    public void play_pause_animation(final ImageView button, final boolean bool) {

        Animation animation = AnimationUtils.loadAnimation(QueueActivity.this, R.anim.rotate);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (bool) {
                    PlayActivity.Companion.getPlay_btn().setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_button));
                } else {
                    PlayActivity.Companion.getPlay_btn().setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_button));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        button.startAnimation(animation);
    }


    public class QueAdapter extends RecyclerView.Adapter<QueAdapter.QueHolder> {


        @Override
        public QueAdapter.QueHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.each_que_layout, parent, false);
            QueHolder holder = new QueHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(QueAdapter.QueHolder holder, final int position) {


            Glide.with(getBaseContext())
                    .load(songList.get(position).getSmallCover())
                    .override(200, 200)
                    .crossFade()
                    .centerCrop()
                    .into(holder.q_ic);
            holder.q_song_nm.setText(songList.get(position).getTitle());
            holder.q_artist_nm.setText(songList.get(position).getArtist());


            holder.click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent serviceTrack = new Intent(getBaseContext(), PLayerService.class);

                    prefManager.StoreSongs(songList);
                    prefManager.storePosition(position);
                    prefManager.setEndingValue(false);
                    prefManager.setCurrentDuration(0);


                    startService(serviceTrack);

                }
            });


        }

        @Override
        public int getItemCount() {
            return songList.size();
        }


        class QueHolder extends RecyclerView.ViewHolder {

            ImageView q_ic;
            TextView q_song_nm, q_artist_nm;
            CardView cardView;
            RelativeLayout layout;
            LinearLayout click;


            public QueHolder(View itemView) {
                super(itemView);
                q_song_nm = (TextView) itemView.findViewById(R.id.q_song);
                q_artist_nm = (TextView) itemView.findViewById(R.id.q_artist);
                q_ic = (ImageView) itemView.findViewById(R.id.q_pic);
                cardView = (CardView) itemView.findViewById(R.id.card_view);
                layout = (RelativeLayout) itemView.findViewById(R.id.laayout);
                click = (LinearLayout) itemView.findViewById(R.id.click_lay);


            }

        }


    }

    class VerticalSpaceDecoration extends RecyclerView.ItemDecoration {
        private final int verticalSpaceHeight;

        public VerticalSpaceDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
            outRect.left = verticalSpaceHeight - 2;
            outRect.right = verticalSpaceHeight - 2;
        }
    }

}
