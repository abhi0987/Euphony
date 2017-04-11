package music.abitri.com.euphony;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.intrusoft.library.FrissonView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import music.abitri.com.euphony.Manager.MusicGetter;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.SharedPrefPKg.CheckFirstTime;
import music.abitri.com.euphony.SharedPrefPKg.TypeStoreManager;

public class WelcomePage extends AppCompatActivity {
    String TAG = WelcomePage.class.getName();
    String[] quotes = {"music is safe kind of high"
            , "My music fights against the system that teaches to live and die"
            , "One good thing about music, when it hits you, you feel no pain"
            , "Music is the movement of sound to reach the soul for the education of its virtue"};
    String[] colors = {"#ef8300", "#ff0000", "#00ced1", "#8a2be2"};

    private SharedPreferences permissionStatus;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;

    MusicGetter getter;
    PrefManager manager;
    TypeStoreManager typeManager;
    List<SongDetail> songList, retriveList;
    int retrivePos;

    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_page);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        getter = new MusicGetter();
        manager = new PrefManager(getBaseContext());
        typeManager = new TypeStoreManager(getBaseContext());
        retriveList = new ArrayList<>();
        songList = new ArrayList<>();

        FrissonView frissonView = (FrissonView) findViewById(R.id.wav_view);
        TextView qtext = (TextView) findViewById(R.id.sub_text);
        Random r = new Random();
        num = r.nextInt(quotes.length);

        frissonView.setTintColor(Color.parseColor(colors[num]));
        qtext.setText(quotes[num]);
        checkMarshmallowPermission();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(WelcomePage.this, BasemusicActivity.class);
                intent.putExtra("color", colors[num]);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.activity_close_scale);
                finish();
            }
        }, 4000);

    }


    class SDsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            retrivePos = manager.getPosition(Constants.POSITION);
            retriveList = manager.getSongs(Constants.KEY_ALBUM);
            CheckFirstTime firstTime = new CheckFirstTime(getBaseContext());
            boolean endSTate = manager.getEndingValue();
            Log.e("ENDING VALUE :",endSTate+"");
            String path = "";
                if (!retriveList.isEmpty()) {
                    path = retriveList.get(retrivePos).getPath();
                    File file = new File(path);
                    if (file.exists()) {
                        Log.d(TAG, "file exists");
                    } else {
                        manager.setEndingValue(false);
                        firstTime.setFirst(false);
                    }
                }else {
                    manager.setEndingValue(false);
                    firstTime.setFirst(false);

                }






       /*if (!songList.isEmpty() && !retriveList.isEmpty()) {

                if (songList.size() != retriveList.size()) {

                    manager.StoreSongs(songList);
                    manager.setEndingValue(false);
                    firstTime.setFirst(false);
                }
            }*/


        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    @Override
    protected void onResume() {
        super.onResume();


    }


    private void checkMarshmallowPermission() {
        if (ActivityCompat.checkSelfPermission(WelcomePage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(WelcomePage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomePage.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(WelcomePage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomePage.this);
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
                ActivityCompat.requestPermissions(WelcomePage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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
        new SDsync().execute();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                proceedAfterPermission();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(WelcomePage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(WelcomePage.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions(WelcomePage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);


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
            if (ActivityCompat.checkSelfPermission(WelcomePage.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }


}



