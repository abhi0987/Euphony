package music.abitri.com.euphony;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.ServcesPkg.PLayerService;
import music.abitri.com.euphony.SharedPrefPKg.CheckFirstTime;

public class SongPickerActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    Handler handler = new Handler();
    SeekBarCompat seekBar;
    FloatingActionButton play_btn;
    TextView Song_nm, artist_nm;
    MediaPlayer player;
    android.media.AudioManager PhoneManager;
    boolean isPlayPaused;
    MediaMetadataRetriever metaRetriver;
    String path;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        handler.removeCallbacks(seek);
        player.stop();
        player.release();
        player = null;



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_picker);
        Song_nm = (TextView) findViewById(R.id.sng_nm_id);
        artist_nm = (TextView) findViewById(R.id.artst_nm_id);
        play_btn = (FloatingActionButton) findViewById(R.id.play_btn_pick);
        seekBar = (SeekBarCompat) findViewById(R.id.seekbar_pick);
        metaRetriver = new MediaMetadataRetriever();
        getIntentData();

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.start();
                }
            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(seek);

        player = null;

    }

    private void inItPlayer() {
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setLooping(true);
    }

    public void getIntentData() {
        PhoneManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        PhoneManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        PhoneManager.setStreamVolume(AudioManager.STREAM_MUSIC, PhoneManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        inItPlayer();

        Uri data = getIntent().getData();
        if (data != null) {

            if (data.getScheme().equalsIgnoreCase("file")) {
                path = data.getPath().toString();
                if (!TextUtils.isEmpty(path)) {

                    metaRetriver.setDataSource(path);

                    Song_nm.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                    artist_nm.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    playMusic(path);
                    handler.postDelayed(seek, 200);
                    seekOperation();


                }
            }

        }
    }


    Runnable seek = new Runnable() {
        @Override
        public void run() {
            long currentPosition = player.getCurrentPosition();
            long total = player.getDuration();
            seekBar.setMax((int) total);
            seekBar.setProgress((int) currentPosition);
            handler.postDelayed(this, 200);

            if (player != null) {
                if (player.isPlaying()) {
                    play_btn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_button));
                } else {
                    play_btn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.play_button));
                }
            }
        }
    };


    public void seekOperation() {


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean stat;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player != null) {

                    if (player.isPlaying() && stat) {
                        player.seekTo(progress);
                    } else if (stat) {

                        player.start();
                        play_btn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_button));
                        player.seekTo(progress);

                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                stat = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                stat = false;
            }
        });


    }

    public void playMusic(String path) {

        player.stop();
        player.reset();
        try {
            player.setDataSource(path);

            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    play_btn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_button));
                }
            });


        } catch (Exception e) {
            Log.e("MUSIC SERVICE : ", "Error setting song source");
        }


    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (player != null) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                //phone calling

                if (player.isPlaying()) {
                    player.pause();
                    play_btn.setImageDrawable(ContextCompat.
                            getDrawable(getApplicationContext(), R.drawable.play_button));
                    isPlayPaused = true;

                }


            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

                ////notification

                PhoneManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, 0);

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                //phone call stop

                if (isPlayPaused) {
                    isPlayPaused = false;
                    player.start();
                    PhoneManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, 0);

                    play_btn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_button));

                } else {

                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

                ////open another music app
                PhoneManager.abandonAudioFocus(this);
                player.stop();

            }

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {


        playMusic(path);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
