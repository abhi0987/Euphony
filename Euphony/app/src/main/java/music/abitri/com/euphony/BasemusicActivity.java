package music.abitri.com.euphony;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;

import music.abitri.com.euphony.FragmentPkg.FragmentDrawer;
import music.abitri.com.euphony.FragmentPkg.LibraryFragment;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.ServcesPkg.PLayerService;
import music.abitri.com.euphony.SharedPrefPKg.CheckFirstTime;

public class BasemusicActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener {

    CoordinatorLayout coordinatorLayout;

    String TAG = BasemusicActivity.class.getName();
    PrefManager prefManager;
    int REQUEST_EQ = 451;
    ////////////////////////
    Handler handler = new Handler();
    public static FragmentDrawer drawerFragment;
    public static Toolbar toolbar;
    TextView tooltext;
    public static TextView btm_song_nm;
    public static TextView info;
    public static TextView btm_artist_nm;
    public static LinearLayout toucView;
    public static ImageView btm_albm_art;
    public static ImageView btm_btn;
    public static RelativeLayout bottomLAY, btm_play_button;
    private SharedPreferences permissionStatus;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;

    private static PLayerService musicSrv;
    private Intent playIntent;
    private static boolean musicBound = false;

    boolean isbtmShtExpanded;


    CheckFirstTime shp;
    boolean isFirstTime;
    boolean endingState;


    @Override
    protected void onStart() {
        super.onStart();


        if (playIntent == null) {
            try {
                playIntent = new Intent(this, PLayerService.class);
                bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basemusic);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tooltext = (TextView) findViewById(R.id.tooltext);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        prefManager = new PrefManager(getBaseContext());
        shp = new CheckFirstTime(getBaseContext());
        isFirstTime = shp.getFirst();
        setupView();
        checkMarshmallowPermission();
        endingState = prefManager.getEndingValue();
        Log.e(TAG + " :ending state :", endingState + "");


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playIntent == null) {
            try {
                playIntent = new Intent(this, PLayerService.class);
                bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void setupView() {
        String color = getIntent().getStringExtra("color");
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar, coordinatorLayout, color);
        drawerFragment.setDrawerListener(this);

        btm_albm_art = (ImageView) findViewById(R.id.botom_img);
        btm_btn = (ImageView) findViewById(R.id.btm_btn);
        btm_song_nm = (TextView) findViewById(R.id.bottom_sng_nm);
        btm_song_nm.setSelected(true);
        btm_artist_nm = (TextView) findViewById(R.id.bottom_artist_nm);
        info = (TextView) findViewById(R.id.info_text);
        toucView = (LinearLayout) findViewById(R.id.touch_view);
        bottomLAY = (RelativeLayout) findViewById(R.id.main_btm);
        btm_play_button = (RelativeLayout) findViewById(R.id.btm_play_button);


        toucView.setOnClickListener(this);
        btm_play_button.setOnClickListener(this);


    }

    public void setupFragment(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {

            case 0:
                LibraryFragment fragmentlibrary = new LibraryFragment();
                Bundle b = new Bundle();
                b.putInt("TYPE", position);
                fragmentlibrary.setArguments(b);
                fragmentTransaction.replace(R.id.fragment, fragmentlibrary);
                fragmentTransaction.commit();
                tooltext.setText("My Library");
                drawerFragment.makeClickable(0);
                drawerFragment.closeDrawer();
                break;
            case 1:
                LibraryFragment frag = new LibraryFragment();
                Bundle bf = new Bundle();
                bf.putInt("TYPE", position);
                frag.setArguments(bf);
                fragmentTransaction.replace(R.id.fragment, frag);
                fragmentTransaction.commit();
                tooltext.setText("Favorite");
                drawerFragment.makeClickable(1);
                drawerFragment.closeDrawer();
                break;
            case 2:
                LibraryFragment playlistfrag = new LibraryFragment();
                Bundle bp = new Bundle();
                bp.putInt("TYPE", position);
                playlistfrag.setArguments(bp);
                fragmentTransaction.replace(R.id.fragment, playlistfrag);
                fragmentTransaction.commit();
                tooltext.setText("Playlist");
                drawerFragment.makeClickable(2);
                drawerFragment.closeDrawer();
                break;
            case 3:
                Intent intent = new Intent(AudioEffect
                        .ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);

                if ((intent.resolveActivity(getPackageManager()) != null)) {
                    startActivityForResult(intent, REQUEST_EQ);
                } else {

                }
                drawerFragment.makeClickable(3);
                drawerFragment.closeDrawer();

                break;
            case 4:
                break;


        }

        handler.postDelayed(runUi, 200);

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        setupFragment(position);
    }


    private void checkMarshmallowPermission() {
        if (ActivityCompat.checkSelfPermission(BasemusicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(BasemusicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(BasemusicActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(BasemusicActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(BasemusicActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(BasemusicActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            editor.apply();


        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
        //We've got the permission, now we can proceed further
        //Toast.makeText(getBaseContext(), "We got the Storage Permission", Toast.LENGTH_LONG).show();
        setupFragment(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                proceedAfterPermission();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(BasemusicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(BasemusicActivity.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions(BasemusicActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                            setupFragment(0);


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(BasemusicActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerFragment.isDrawerOpen()) {
            drawerFragment.closeDrawer();
        } else {

            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_id:

                Intent intentSrch = new Intent(BasemusicActivity.this, SearchActivity.class);
                startActivity(intentSrch);
                overridePendingTransition(R.anim.right_in, 0);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(BasemusicActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btm_play_button:
                boolean state = prefManager.getEndingValue();
                Log.e(TAG + " ENDING_STATE", String.valueOf(state));
                if (state) {
                    startService(playIntent);
                    play_pause_animation(btm_btn, true);
                } else {

                    if (musicSrv.Companion.getPlayer().isPlaying()) {
                        musicSrv.PauseMusic();
                        btm_btn.setImageResource(R.drawable.play_button);

                    } else {
                        musicSrv.resumeMusic();
                        play_pause_animation(btm_btn, true);

                    }

                }

                break;
            case R.id.touch_view:

                Intent activity = new Intent(BasemusicActivity.this, PlayActivity.class);
                startActivity(activity);
                overridePendingTransition(R.anim.bottom_up, 0);

                break;
        }

    }

    ///////////////BottomSheet behaviour/////


    public void MangeBottomSheet() {
        List<SongDetail> songs = prefManager.getSongs(Constants.KEY_ALBUM);
        int position = prefManager.getPosition(Constants.POSITION);

        if (musicBound == true) {

            if (isFirstTime == true) {
                if (musicSrv.Companion.getPlayer() != null) {

                    if (!songs.isEmpty()) {
                        if (musicSrv.Companion.getPlayer().isPlaying()) {
                            setBottomBar("PLAYING");
                        } else {
                            setBottomBar("PAUSED");
                        }
                    }


                } else {


                    if (endingState == true) {
                        if (!songs.isEmpty()) {
                            getLastPlayedSong(songs, position);
                        }
                    }

                }


            }

        }
    }

    public void setBottomBar(String playing) {

        List<SongDetail> songs = prefManager.getSongs(Constants.KEY_ALBUM);
        int position = prefManager.getPosition(Constants.POSITION);

        info.setVisibility(View.GONE);

        bottomLAY.setVisibility(View.VISIBLE);
        Glide.with(getBaseContext()).load(songs.get(position).getSmallCover())
                .thumbnail(0.8f)
                .centerCrop()
                .crossFade()
                .override(250, 250)
                .into(btm_albm_art);
        btm_song_nm.setText(songs.get(position).getTitle());
        btm_artist_nm.setText(songs.get(position).getArtist());
        if (playing.equals("PLAYING")) {
            BasemusicActivity.btm_btn.setImageResource(R.drawable.pause_button);
        } else if (playing.equals("PAUSE")) {
            BasemusicActivity.btm_btn.setImageResource(R.drawable.play_button);
        }


    }

    public void getLastPlayedSong(List<SongDetail> songs, int position) {

        info.setVisibility(View.GONE);

        bottomLAY.setVisibility(View.VISIBLE);
        Glide.with(getBaseContext()).load(songs.get(position).getSmallCover())
                .thumbnail(0.8f)
                .centerCrop()
                .crossFade()
                .override(200, 200)
                .into(btm_albm_art);
        btm_song_nm.setText(songs.get(position).getTitle());
        btm_artist_nm.setText(songs.get(position).getArtist());
        btm_btn.setImageResource(R.drawable.play_button);

    }

    ////////////////service binding///////////////////

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PLayerService.MusicBinder binder = (PLayerService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass lis
            musicBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    Runnable runUi = new Runnable() {
        @Override
        public void run() {

            MangeBottomSheet();
            handler.postDelayed(this, 200);
        }
    };


    public void play_pause_animation(final ImageView button, final boolean bool) {

        Animation animation = AnimationUtils.loadAnimation(BasemusicActivity.this, R.anim.rotate);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (bool) {
                    button.setImageResource(R.drawable.pause_button);
                } else {
                    button.setImageResource(R.drawable.play_button);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        button.startAnimation(animation);
    }


}
