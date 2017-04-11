package music.abitri.com.euphony

import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity

import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson


import java.io.File
import java.util.ArrayList
import java.util.Collections
import java.util.Locale


import app.minimize.com.seek_bar_compat.SeekBarCompat

import music.abitri.com.euphony.AdapterPkg.Playlist_Db_adapter
import music.abitri.com.euphony.Manager.SongDetail

import music.abitri.com.euphony.SQLiteDataBasePackage.FavoritesDatabase
import music.abitri.com.euphony.SQLiteDataBasePackage.PlayListSQLdatabase
import music.abitri.com.euphony.ServcesPkg.PLayerService

class PlayActivity : AppCompatActivity(), View.OnClickListener {
    internal var TAG = PlayActivity::class.java.name
    internal var handler: Handler? = null
    internal var back_press: RelativeLayout? = null
    internal var shuffle: RelativeLayout? = null
    internal var repeat: RelativeLayout? = null
    internal var favort: RelativeLayout? = null
    internal var playlist_q: RelativeLayout? = null
    internal var setting: RelativeLayout? = null
    internal var manager: PrefManager? = null
    internal var songList: List<SongDetail>? = null
    internal var songPos: Int = 0
    internal var current_time: Long = 0
    internal var total_time: Long = 0
    internal var endState: Boolean = false
    var pager: ViewPager? = null
    internal var pagerAdapater: MyPagerAdapater? = null
    internal var songInfo: SongDetail? = null
    private var musicSrv: PLayerService? = null
    private var playIntent: Intent? = null
    private var musicBound = false
    internal var prefManager: PrefManager? = null
    internal var pDB: PlayListSQLdatabase? = null
    internal var fDB: FavoritesDatabase? = null
    internal var isFav: Boolean = false
    internal var list_of_playlist_names: ArrayList<String>? = null
    internal var popup: PopupMenu? = null

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(0, R.anim.bottom_down)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_play)
        prefManager = PrefManager(baseContext)
        handler = Handler()
        pDB = PlayListSQLdatabase(baseContext)
        fDB = FavoritesDatabase(baseContext)
        list_of_playlist_names = ArrayList<String>()

        UIsetup()
        StartMusic()

    }

    private fun StartMusic() {
        getSongInfo()

        pager?.addOnPageChangeListener(CircularViewPagerHandler(pager!!))
        pagerAdapater = MyPagerAdapater()
        pager?.adapter = pagerAdapater
        setCurrentItem(songInfo, songPos)
        seekOperation()
        handler!!.postDelayed(Time, 200)


    }


    override fun onStart() {
        super.onStart()
        if (playIntent == null) {
            try {
                playIntent = Intent(this, PLayerService::class.java)
                bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private val musicConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as PLayerService.MusicBinder
            musicSrv = binder.service
            musicBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            musicBound = false
        }
    }

    private fun getSongInfo() {
        manager = PrefManager(applicationContext)
        songList = ArrayList<SongDetail>()
        songList = manager!!.getSongs(Constants.KEY_ALBUM)
        songPos = manager!!.getPosition(Constants.POSITION)
        current_time = manager!!.currentDuration
        total_time = manager!!.totalDuration
        endState = manager!!.endingValue

        songInfo = (songList as MutableList<SongDetail>?)!![songPos]


    }


    private fun CheckPlayPauseButton() {
        if (PLayerService!!.player != null) {
            if (PLayerService!!.player!!.isPlaying()) {
                play_btn!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.pause_button))
            } else {
                play_btn!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.play_button))
            }
        }


    }






    override fun onResume() {
        super.onResume()
        if (playIntent == null) {
            try {
                playIntent = Intent(this, PLayerService::class.java)
                bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    private fun checkShuffle() {


        if (musicSrv!!.isShuffle) {
            sh_ic!!.setImageResource(R.drawable.shuffle)

        } else {
            sh_ic!!.setImageResource(R.drawable.shuffle_white)

        }

    }

    private fun checkRepeat() {


        if (musicSrv!!.isRepeat) {
            rpt_ic!!.setImageResource(R.drawable.repeat)
        } else {
            rpt_ic!!.setImageResource(R.drawable.repeat_white)
        }
    }


    fun setCurrentItem(songInfo: SongDetail?, position: Int) {
        pager!!.currentItem = position
        song_nm!!.text = songInfo!!.getTitle()
        artist_nm!!.text = songInfo.getArtist()
    }


    fun UIsetup() {
        sh_ic = findViewById(R.id.sh_ic) as ImageView
        rpt_ic = findViewById(R.id.rpt_ic) as ImageView
        fav_ic = findViewById(R.id.fav_ic) as ImageView
        song_nm = findViewById(R.id.tooltext) as TextView
        song_nm!!.isSelected = true
        artist_nm = findViewById(R.id.artist_nm) as TextView
        startTm = findViewById(R.id.startTm) as TextView
        endTm = findViewById(R.id.endTm) as TextView
        pager = findViewById(R.id.view_pager) as ViewPager
        back_press = findViewById(R.id.back_btn) as RelativeLayout
        shuffle = findViewById(R.id.shfl_ic_lay) as RelativeLayout
        repeat = findViewById(R.id.rpt_ic_lay) as RelativeLayout
        favort = findViewById(R.id.fav_view) as RelativeLayout
        setting = findViewById(R.id.setting) as RelativeLayout
        next = findViewById(R.id.skip_next) as RelativeLayout
        prev = findViewById(R.id.skip_prev) as RelativeLayout
        popup = PopupMenu(this@PlayActivity, setting!!)
        popup!!.menuInflater.inflate(R.menu.play_activity_menu, popup!!.menu)
        popup!!.setOnMenuItemClickListener(menuItemClickListner())
        playlist_q = findViewById(R.id.play_list_q) as RelativeLayout
        /////bottomsheet/////


        val layoutManager = FlexboxLayoutManager()
        layoutManager.flexWrap = FlexWrap.WRAP


        //////bottomsheet///
        play_btn = findViewById(R.id.play_btn) as FloatingActionButton
        seekBar = findViewById(R.id.seekbar) as SeekBarCompat


        back_press!!.setOnClickListener(this)
        shuffle!!.setOnClickListener(this)
        repeat!!.setOnClickListener(this)
        favort!!.setOnClickListener(this)
        setting!!.setOnClickListener(this)
        play_btn!!.setOnClickListener(this)
        prev!!.setOnClickListener(this)
        next!!.setOnClickListener(this)
        playlist_q!!.setOnClickListener(this)
        setting!!.setOnClickListener(this)


    }


    internal inner class menuItemClickListner : PopupMenu.OnMenuItemClickListener {

        override fun onMenuItemClick(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.add_to_plst_id -> {
                    val intent = Intent(baseContext, PlayListChooserActivity::class.java)
                    intent.putExtra("Pos", songPos)
                    val str = Gson().toJson(songList!![songPos])
                    intent.putExtra("Songdetail", str)
                    startActivity(intent)
                    return true
                }
                R.id.edit_id -> return true
                R.id.shr_id -> {
                    Share(songPos)
                    return true
                }
            }


            return false
        }
    }


    private fun Share(pos: Int) {

        val audio = File(songList!![pos].getPath())
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "audio/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(audio))
        startActivity(Intent.createChooser(shareIntent, "Share Using"))
    }


    inner class CircularViewPagerHandler(private val mViewPager: ViewPager) : ViewPager.OnPageChangeListener {
        private var mCurrentPosition: Int = 0
        private var mScrollState: Int = 0

        override fun onPageSelected(position: Int) {
            mCurrentPosition = position

            Log.e("size info : ", "" + songList!!.size)
            Log.e("position info : ", "postion :$position, songPos :$songPos")

            if (position > songPos) {
                if (position == songList!!.size - 1) {
                    songPos = songList!!.size - 1
                    onClick(prev!!)
                }

                onClick(next!!)


            } else if (position < songPos) {

                if (position == 0) {
                    songPos = 0
                    onClick(next!!)
                }

                onClick(prev!!)


            }


        }

        override fun onPageScrollStateChanged(state: Int) {
            handleScrollState(state)
            mScrollState = state
        }

        private fun handleScrollState(state: Int) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                setNextItemIfNeeded()
            }
        }

        private fun setNextItemIfNeeded() {
            if (!isScrollStateSettling) {
                handleSetNextItem()
            }
        }

        private val isScrollStateSettling: Boolean
            get() = mScrollState == ViewPager.SCROLL_STATE_SETTLING

        private fun handleSetNextItem() {
            val lastPosition = mViewPager.adapter.count - 1
            if (mCurrentPosition == 0) {

                mViewPager.setCurrentItem(lastPosition, false)


            } else if (mCurrentPosition == lastPosition) {
                mViewPager.setCurrentItem(0, false)


            }

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    }


    inner class MyPagerAdapater : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null

        override fun getCount(): Int {
            return songList!!.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            layoutInflater = baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater!!.inflate(R.layout.custom_img_view, container, false)
            imagePreview = view.findViewById(R.id.song_main_art) as ImageView
            blur_art = view.findViewById(R.id.blur_main_art) as ImageView
            val song = songList!![position]
            Log.d("SONG POS IN SLIDE :", position.toString())
            Log.d("SONG POS IN SLIDE :", songList!![position].display_name.toString())

            Glide.with(baseContext).load(song.smallCover)
                    .centerCrop()
                    .thumbnail(0.8f)
                    .override(500, 500)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imagePreview)
            Glide.with(baseContext).load(song.smallCover)
                    .centerCrop()
                    .override(700, 700)
                    .thumbnail(0.8f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(blur_art)


            container.addView(view)


            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

            container.removeView(`object` as View)

        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

    }


    internal var Time: Runnable = object : Runnable {
        override fun run() {

            if (musicBound == true) {
                endState = prefManager!!.endingValue
                if (endState == true) {
                    seekBar!!.progress = current_time.toInt()
                    seekBar!!.max = total_time.toInt()
                    startTm!!.text = "" + milliSecondTotimer(current_time)
                    endTm!!.text = "" + milliSecondTotimer(total_time)

                    seekBar!!.setOnTouchListener { v, event -> true }
                    pagerAdapater!!.notifyDataSetChanged()
                    setCurrentItem(songList!![songPos], songPos)
                    checkIsfav(songList!!, songPos)

                } else {
                    val currentPosition = PLayerService!!.player!!.getCurrentPosition().toLong()
                    val total = PLayerService!!.player!!.getDuration().toLong()
                    startTm!!.text = "" + milliSecondTotimer(currentPosition)
                    endTm!!.text = "" + milliSecondTotimer(total)
                    seekBar!!.setOnTouchListener { v, event -> false }
                    seekBar!!.max = total.toInt()
                    seekBar!!.progress = currentPosition.toInt()
                    songPos = musicSrv!!.songPos
                    songList = musicSrv!!.songList
                    pagerAdapater!!.notifyDataSetChanged()
                    setCurrentItem((songList as MutableList<SongDetail>?)!![songPos], songPos)
                    checkIsfav((songList as MutableList<SongDetail>?)!!, songPos)
                }


                CheckPlayPauseButton()
                checkShuffle()
                checkRepeat()


                pagerAdapater!!.notifyDataSetChanged()


            } else if (musicBound == false) { // if service is not bounded log it
                Log.v("Still waiting to bound", java.lang.Boolean.toString(musicBound))
            }

            handler!!.postDelayed(this, 200)


        }
    }

    private fun checkIsfav(songList: List<SongDetail>, songPos: Int) {

        isFav = fDB!!.isFavorite(songList[songPos])

        if (isFav) {
            fav_ic!!.setImageResource(R.drawable.ic_favorite_red_300_48dp)

        } else {
            fav_ic!!.setImageResource(R.drawable.ic_favorite_border_red_300_48dp)
        }

    }


    fun milliSecondTotimer(milliseconds: Long): String {

        var finalTimerString = ""
        var secondsString = ""

        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        if (hours > 0) {
            finalTimerString = hours.toString() + ":"
        }

        if (seconds < 10) {
            secondsString = "0" + seconds
        } else {
            secondsString = "" + seconds
        }

        finalTimerString = finalTimerString + minutes + ": " + secondsString
        return finalTimerString
    }


    fun seekOperation() {


        seekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            internal var stat: Boolean = false

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (PLayerService!!.player != null) {

                    if (PLayerService!!.player!!.isPlaying() && stat) {
                        PLayerService!!.player!!.seekTo(progress)
                    } else if (stat) {

                        PLayerService!!.player!!.start()
                        play_pause_animation(play_btn!!, true)
                        PLayerService!!.player!!.seekTo(progress)

                    }
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

                stat = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                stat = false
            }
        })


    }

    fun FavRoation(img: ImageView) {


        val animationScaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val animationScaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        val growShrink = AnimationSet(true)
        growShrink.addAnimation(animationScaleUp)
        growShrink.addAnimation(animationScaleDown)
        img.startAnimation(growShrink)
        img.setImageResource(R.drawable.ic_favorite_red_300_48dp)

    }

    fun play_pause_animation(button: FloatingActionButton, bool: Boolean) {

        val animation = AnimationUtils.loadAnimation(this@PlayActivity, R.anim.rotate)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                if (bool) {
                    play_btn!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.pause_button))
                } else {
                    play_btn!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.play_button))
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        button.startAnimation(animation)
    }


    override fun onClick(v: View) {
        when (v.id) {

            R.id.play_btn -> if (endState) {
                startService(playIntent)
                play_pause_animation(play_btn!!, true)
                endState = false
            } else {
                if (PLayerService.player!!.isPlaying) {
                    musicSrv!!.PauseMusic()
                    BasemusicActivity.btm_btn.setImageResource(R.drawable.play_button)
                    play_btn!!.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.play_button))
                } else {
                    musicSrv!!.resumeMusic()
                    BasemusicActivity.btm_btn.setImageResource(R.drawable.pause_button)
                    play_pause_animation(play_btn!!, true)
                }
            }
            R.id.skip_next -> if (endState) {
            } else {
                musicSrv!!.setPosition(songPos)
                musicSrv!!.NextMusic()
                songPos = musicSrv!!.songPos
                // setCurrentItem(musicSrv.songList.get(musicSrv.songPos), musicSrv.songPos);
            }
            R.id.skip_prev -> if (endState) {

            } else {
                musicSrv!!.setPosition(songPos)
                musicSrv!!.PrevMusic()
                songPos = musicSrv!!.songPos
                //setCurrentItem(musicSrv.songList.get(musicSrv.songPos), musicSrv.songPos);
            }
            R.id.shfl_ic_lay -> musicSrv!!.isShuffle = musicSrv!!.isShuffle != true
            R.id.rpt_ic_lay -> musicSrv!!.isRepeat = musicSrv!!.isRepeat != true
        /* case R.id.playlist_view:

                break;*/
            R.id.setting -> popup!!.show()
            R.id.fav_view -> if (endState) {

                val stat = fDB!!.addSongsToFav(songList!![songPos])
                if (stat) {
                    FavRoation(fav_ic!!)
                    Toast.makeText(baseContext, "Added to favorites", Toast.LENGTH_SHORT).show()
                } else {
                    fDB!!.DeleteFavTableData(songList!![songPos])
                    fav_ic!!.setImageResource(R.drawable.ic_favorite_border_red_300_48dp)
                }
            } else {
                val stat = fDB!!.addSongsToFav(musicSrv!!.songList!![musicSrv!!.songPos])
                if (stat) {
                    FavRoation(fav_ic!!)
                    Toast.makeText(baseContext, "Added to favorites", Toast.LENGTH_SHORT).show()
                } else {
                    fDB!!.DeleteFavTableData(musicSrv!!.songList!![musicSrv!!.songPos])
                    fav_ic!!.setImageResource(R.drawable.ic_favorite_border_red_300_48dp)
                }
            }
            R.id.back_btn -> onBackPressed()

            R.id.play_list_q -> {
                val intentQ = Intent(this@PlayActivity, QueueActivity::class.java)
                intentQ.putExtra("QPOS", songPos)
                startActivity(intentQ)
                overridePendingTransition(R.anim.right_in, 0)
            }
        }
    }

    companion object {
        var sh_ic: ImageView? = null
        var rpt_ic: ImageView? = null
        var fav_ic: ImageView? = null
        var plst_ic: ImageView? = null
        var imagePreview: ImageView? = null
        var blur_art: ImageView? = null
        var song_nm: TextView? = null
        var artist_nm: TextView? = null
        var startTm: TextView? = null
        var endTm: TextView? = null
        var next: RelativeLayout? = null
        var prev: RelativeLayout? = null
        var play_btn: FloatingActionButton? = null
        var seekBar: SeekBarCompat? = null
    }


    ////////////////////////////////////////////////////////////////////////////


}
