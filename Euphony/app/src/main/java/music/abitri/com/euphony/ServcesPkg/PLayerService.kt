package music.abitri.com.euphony.ServcesPkg

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.*
import android.media.AudioManager
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.RemoteViews

import com.bumptech.glide.Glide

import java.io.FileDescriptor
import java.io.IOException
import java.util.ArrayList
import java.util.Random

import music.abitri.com.euphony.BasemusicActivity
import music.abitri.com.euphony.Constants
import music.abitri.com.euphony.Manager.SongDetail
import music.abitri.com.euphony.PrefManager
import music.abitri.com.euphony.QueueActivity
import music.abitri.com.euphony.R
import music.abitri.com.euphony.SQLiteDataBasePackage.SqliteRecentPlay
import music.abitri.com.euphony.SharedPrefPKg.CheckFirstTime
import music.abitri.com.euphony.WelcomePage

class PLayerService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {
    var songPos: Int = 0
    var songList: List<SongDetail>? = null
    private val musicBind = MusicBinder()
    internal var PhoneManager: AudioManager? = null
    internal var prefManager: PrefManager? = null

    internal var curr: Long = 0
    internal var isPlayPaused: Boolean = false
    internal var total: Long = 0
    var isShuffle: Boolean = false
    var isRepeat: Boolean = false

    internal var isReciverRegistered: Boolean = false

    internal var recentDB: SqliteRecentPlay? = null
    internal var notificationManager: NotificationManager? = null
    internal var notification: NotificationCompat.Builder? = null
    internal var simpleContentView: RemoteViews? = null
    internal var reciever: NotificationReciever? = null

    override fun onCreate() {
        super.onCreate()
        songPos = 0
        prefManager = PrefManager(applicationContext)
        recentDB = SqliteRecentPlay(applicationContext)

        songList = ArrayList<SongDetail>()
        PhoneManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        PhoneManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        PhoneManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, PhoneManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
        inItPlayer()


    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        isRepeat = false

        RegisterReciver()

        DataSetup(intent)

        phonestateListen()


        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        return musicBind
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        saveInitialBooleanState()
        this.stopSelf()
    }


    override fun onDestroy() {
        super.onDestroy()
        saveInitialBooleanState()
        if (isReciverRegistered) {
            isReciverRegistered = false
            unregisterReceiver(reciever)
            notificationManager!!.cancelAll()
        }


        try {
            player!!.reset()
            player!!.prepare()
            player!!.stop()
            player!!.release()
            this.stopSelf()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        PhoneManager!!.abandonAudioFocus(this)
        val mgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mgr?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }

    override fun onCompletion(mp: MediaPlayer) {

        if (player!!.currentPosition > 0) {
            NextMusic()
        }

    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Log.v("MUSIC PLAYER", "Playback error")
        onDestroy()
        return false
    }

    override fun onPrepared(mp: MediaPlayer) {

        try {
            mp.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (player != null) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                //phone calling

                if (player!!.isPlaying) {
                    PauseMusic()
                    isPlayPaused = true
                    Log.d("SERVICE", "call recieved or call ongoing ")

                }


            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

                ////notification


                Log.d("SERVICE", "message recieved")

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                //phone call stop


            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {

                ////open another music app

                if (player!!.isPlaying) {
                    PauseMusic()
                    Log.d("SERVICE", "another music player playing ")
                }


            }

        }
    }

    override fun stopService(name: Intent): Boolean {
        saveInitialBooleanState()
        return super.stopService(name)
    }


    inner class MusicBinder : Binder() {
        val service: PLayerService
            get() = this@PLayerService
    }


    ///////// userdefined in methods //////////////////////////////////

    private fun inItPlayer() {
        player = MediaPlayer()
        player!!.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        player!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player!!.setOnPreparedListener(this)
        player!!.setOnCompletionListener(this)
        player!!.setOnErrorListener(this)
        player!!.isLooping = true
    }

    fun setList(list: List<SongDetail>) {
        this.songList = list
    }

    fun setPosition(pos: Int) {
        this.songPos = pos
    }

    fun DataSetup(intent: Intent) {

        songList = prefManager!!.getSongs(Constants.KEY_ALBUM)
        songPos = prefManager!!.getPosition(Constants.POSITION)
        endState = prefManager!!.endingValue
        curr = prefManager!!.currentDuration
        total = prefManager!!.totalDuration
        isShuffle = prefManager!!.shuffleStatus
        setPosition(songPos)


        playMusic(songPos)


    }

    fun playMusic(songPos: Int) {
        CreateNotification(songPos)

        val firstTime = CheckFirstTime(baseContext)
        val song = songList!![songPos]
        val path = song.getPath()

        prefManager!!.StoreSongs(songList)
        prefManager!!.storePosition(songPos)
        recentDB!!.Add_recent_songs(song)

        player!!.stop()
        player!!.reset()
        try {
            player!!.setDataSource(path)

            player!!.prepare()
            player!!.setOnPreparedListener { mp ->
                if (endState == true) {
                    endState = false
                    mp.seekTo(curr.toInt())
                    prefManager!!.endingValue = false
                    mp.start()
                    curr = 0
                } else {
                    mp.start()
                }
            }

            firstTime.first = true
            setBottomBar("PLAYING", songPos)


        } catch (e: Exception) {
            Log.e("MUSIC SERVICE : ", "Error setting song source")
        }

        notification!!.bigContentView.setImageViewResource(R.id.pause_bttn, R.drawable.pause_button)
        notification!!.setCustomBigContentView(simpleContentView)
        notificationManager!!.notify(721, notification!!.build())


    }


    fun NextMusic() {
        if (isRepeat) {
            val pos = songPos

        } else if (isShuffle) {
            var newSong = songPos
            newSong = Random().nextInt(songList!!.size)
            songPos = newSong
        } else {
            songPos++
            if (songPos >= songList!!.size) {
                songPos = 0
            }

        }
        Log.e("NEXT_SERVICE POS : ", "" + songPos)
        playMusic(songPos)

    }


    fun PrevMusic() {

        if (isRepeat) {

        } else if (isShuffle) {
            var newSong = songPos
            newSong = Random().nextInt(songList!!.size)
            songPos = newSong
        } else {
            songPos--
            if (songPos < 0) {
                songPos = songList!!.size - 1
            }
        }
        Log.e("PREV_SERVICE POS : ", "" + songPos)

        playMusic(songPos)


    }


    fun PauseMusic() {
        player!!.pause()
        notification!!.bigContentView.setImageViewResource(R.id.pause_bttn, R.drawable.play_button)
        notification!!.setCustomBigContentView(simpleContentView)
        notificationManager!!.notify(721, notification!!.build())
        BasemusicActivity.btm_btn.setImageResource(R.drawable.play_button)
    }

    fun resumeMusic() {
        player!!.start()
        notification!!.bigContentView.setImageViewResource(R.id.pause_bttn, R.drawable.pause_button)
        notification!!.setCustomBigContentView(simpleContentView)
        notificationManager!!.notify(721, notification!!.build())
        BasemusicActivity.btm_btn.setImageResource(R.drawable.pause_button)
    }

    fun saveInitialBooleanState() {
        prefManager!!.storePosition(songPos)
        prefManager!!.StoreSongs(songList)
        prefManager!!.totalDuration = player!!.duration.toLong()
        prefManager!!.currentDuration = player!!.currentPosition.toLong()
        prefManager!!.endingValue = true
        prefManager!!.setStringValue("TRUE")
        prefManager!!.storeShuffleStatus(isShuffle)


    }


    internal var phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if (player!!.isPlaying) {
                    PauseMusic()
                    isPlayPaused = true

                }
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                //Not in call: Play music

            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold

                if (player!!.isPlaying) {
                    PauseMusic()
                    isPlayPaused = true

                }
            }
            super.onCallStateChanged(state, incomingNumber)
        }
    }


    fun phonestateListen() {

        val mgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mgr?.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }


    /////Base bottom bar setup/////
    fun setBottomBar(playing: String, songPos: Int) {

        BasemusicActivity.info.visibility = View.GONE
        BasemusicActivity.btm_btn.setImageResource(R.drawable.play_button)
        BasemusicActivity.bottomLAY.visibility = View.VISIBLE
        Glide.with(baseContext).load(songList!![songPos].smallCover)
                .thumbnail(0.8f)
                .centerCrop()
                .crossFade()
                .override(250, 250)
                .into(BasemusicActivity.btm_albm_art)
        BasemusicActivity.btm_song_nm.text = songList!![songPos].getTitle()
        BasemusicActivity.btm_artist_nm.text = songList!![songPos].getArtist()

        BasemusicActivity.btm_btn.setImageResource(R.drawable.pause_button)


    }


    fun CreateNotification(songPos: Int) {

        val songName = songList!![songPos].getTitle()
        val artistName = songList!![songPos].getArtist()
        val cover = songList!![songPos].smallCover

        var bm: Bitmap? = null
        val options = BitmapFactory.Options()

        try {
            var pfd = applicationContext.contentResolver.openFileDescriptor(cover, "r")
            if (pfd != null) {
                var fd: FileDescriptor? = pfd.fileDescriptor
                bm = BitmapFactory.decodeFileDescriptor(fd, null, options)
                pfd = null
                fd = null
            }
        } catch (ee: Error) {

        } catch (e: Exception) {
            e.printStackTrace()
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        simpleContentView = RemoteViews(applicationContext.packageName, R.layout.notifi_lay)


        val intent = Intent(applicationContext, WelcomePage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val contentIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        notification = NotificationCompat.Builder(applicationContext)
                .setSmallIcon(R.drawable.euphony_phn)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setContentTitle(songName)
                .setContentText(artistName)
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        NotifEventListener(simpleContentView!!)
        (notification as NotificationCompat.Builder?)!!.setCustomBigContentView(simpleContentView)

        (notification as NotificationCompat.Builder?)!!.bigContentView.setViewVisibility(R.id.pause_bttn, View.VISIBLE)
        (notification as NotificationCompat.Builder?)!!.bigContentView.setViewVisibility(R.id.next_bttn, View.VISIBLE)
        (notification as NotificationCompat.Builder?)!!.bigContentView.setViewVisibility(R.id.prev_bttn, View.VISIBLE)
        (notification as NotificationCompat.Builder?)!!.bigContentView.setViewVisibility(R.id.song_art_notif, View.VISIBLE)


        if (bm != null) {
            (notification as NotificationCompat.Builder?)!!.bigContentView.setViewVisibility(R.id.song_art_notif, View.VISIBLE)
            (notification as NotificationCompat.Builder?)!!.bigContentView.setImageViewBitmap(R.id.song_art_notif, bm)

        } else {
            (notification as NotificationCompat.Builder?)!!.bigContentView.setViewVisibility(R.id.song_art_notif, View.GONE)
        }

        (notification as NotificationCompat.Builder?)!!.bigContentView.setTextViewText(R.id.notif_song_nm, songName)
        (notification as NotificationCompat.Builder?)!!.bigContentView.setTextViewText(R.id.notif_artist_nm, artistName)
        if (player!!.isPlaying) {
            (notification as NotificationCompat.Builder?)!!.bigContentView.setImageViewResource(R.id.pause_bttn, R.drawable.pause_button)
        } else {
            (notification as NotificationCompat.Builder?)!!.bigContentView.setImageViewResource(R.id.pause_bttn, R.drawable.play_button)
        }


        notificationManager!!.notify(721, (notification as NotificationCompat.Builder?)!!.build())
        //  notification.flags |= Notification.FLAG_ONGOING_EVENT;


    }


    fun NotifEventListener(view: RemoteViews) {

        var pendingIntent = PendingIntent.getBroadcast(baseContext, 0, Intent(NOTIFY_PREVIOUS),
                PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.prev_bttn, pendingIntent)

        pendingIntent = PendingIntent.getBroadcast(baseContext, 0, Intent(NOTIFY_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.pause_bttn, pendingIntent)

        pendingIntent = PendingIntent.getBroadcast(baseContext, 0, Intent(NOTIFY_NEXT), PendingIntent.FLAG_UPDATE_CURRENT)
        view.setOnClickPendingIntent(R.id.next_bttn, pendingIntent)


    }


    fun RegisterReciver() {

        if (isReciverRegistered) {
            unregisterReceiver(reciever)
            isReciverRegistered = false
        }
        reciever = NotificationReciever()
        val intentFilter = IntentFilter()
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        // set the custom action
        intentFilter.addAction(NOTIFY_PAUSE)
        intentFilter.addAction(NOTIFY_NEXT)
        intentFilter.addAction(NOTIFY_PREVIOUS)

        registerReceiver(reciever, intentFilter)
        isReciverRegistered = true

    }


    inner class NotificationReciever : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {

            if (intent != null) {


                if (intent.action == PLayerService.NOTIFY_PAUSE) {
                    if (player!!.isPlaying) {
                        PauseMusic()
                        Log.e("PlayerService", "Pause")
                    } else {
                        resumeMusic()
                        Log.e("PlayerService", "PLAY")
                    }

                } else if (intent.action == PLayerService.NOTIFY_NEXT) {

                    NextMusic()


                } else if (intent.action == PLayerService.NOTIFY_PREVIOUS) {

                    PrevMusic()


                }
            }
        }
    }

    companion object {

        val NOTIFY_PREVIOUS = "music.abitri.com.euphony.previous"
        val NOTIFY_PAUSE = "music.abitri.com.euphony.pause"
        val NOTIFY_NEXT = "music.abitri.com.euphony.next"
        internal var endState: Boolean = false
        var player: MediaPlayer? = null
    }


}
