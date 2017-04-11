package music.abitri.com.euphony;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.Manager.MusicGetter;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.SecondaryFragmentPkg.AllFragment;
import music.abitri.com.euphony.ServcesPkg.PLayerService;

public class SearchActivity extends AppCompatActivity {

    MaterialSearchView searchView;
    List<SongDetail> songList;
    MusicGetter getter;
    QueryAdapter adapter;
    TextView warn;
    RecyclerView recyclerView;
    public static final int GRID_PADDING = 3;
    private int columnWidth;
    private int imageWidth;
    float padding;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.srch_rclr_view);
        searchView = (MaterialSearchView) findViewById(R.id.search_view_id);
        searchView.setFocusable(true);
        warn = (TextView) findViewById(R.id.warn_id_2);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchView.setVoiceIcon(getResources().getDrawable(R.drawable.ic_action_voice_search));
        recyclerView.setVisibility(View.INVISIBLE);
        warn.setVisibility(View.VISIBLE);

        InitilizeGridLayout();
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        recyclerView.addItemDecoration(new GridSpaceItemDecoration(1, dpToPx(3), true));

        songList = new ArrayList<>();
        getter = new MusicGetter();
        new SearchAsync().execute();


        searchView.setVoiceSearch(true);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<SongDetail> filteredModelList = filter(songList, newText);
                adapter.setFilter(filteredModelList);
                if (newText.length() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    warn.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    warn.setVisibility(View.VISIBLE);
                }

                return true;

            }
        });


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                toolbar.setNavigationIcon(null);
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.srch_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        item.expandActionView();


        return true;
    }


    private List<SongDetail> filter(List<SongDetail> models, String query) {
        query = query.toLowerCase();
        final List<SongDetail> filteredModelList = new ArrayList<>();
        for (SongDetail model : models) {
            final String text = model.getTitle().toLowerCase();
            final String artist = model.getArtist().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            } else if (artist.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    class SearchAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            songList = getter.getAlbum(SearchActivity.this, "ALL");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            adapter = new QueryAdapter(SearchActivity.this, songList);
            recyclerView.setAdapter(adapter);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    class QueryAdapter extends RecyclerView.Adapter<queryHolder> {

        Context ctx;
        List<SongDetail> songLst;

        public QueryAdapter(Context context, List<SongDetail> songList) {
            ctx = context;
            this.songLst = songList;
            prefManager = new PrefManager(context);
        }

        @Override
        public queryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.ach_track, parent, false);
            queryHolder holder = new queryHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(queryHolder holder, final int position) {

            if (position % 2 == 0) {
                holder.cardView.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.more_lay.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.cardView.setBackgroundColor(ctx.getResources().getColor(R.color.dark_primary));
                holder.more_lay.setBackgroundColor(ctx.getResources().getColor(R.color.dark_primary));
            }


            holder.imageView.setVisibility(View.GONE);
            holder.bkp_img.setVisibility(View.GONE);
            holder.artist.setText(songLst.get(position).getArtist());
            holder.track.setText(songLst.get(position).getTitle());

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Play(position);
                }
            });
        }

        private void Play(int position) {
            Intent serviceTrack = new Intent(ctx, PLayerService.class);

            prefManager.StoreSongs(songLst);
            prefManager.storePosition(position);
            prefManager.setEndingValue(false);
            prefManager.setCurrentDuration(0);

            ///typeManager.setListType(type_);
            /// typeManager.setRetriveType(retrive_type);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                ctx.startService(serviceTrack);
                Intent activity = new Intent(ctx, PlayActivity.class);
                activity.setFlags(activity.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(activity);
                Activity activity_anim = (Activity) ctx;
                activity_anim.overridePendingTransition(R.anim.bottom_up, 0);
                ((Activity) ctx).finish();

            }
        }

        @Override
        public int getItemCount() {
            return songLst.size();
        }

        public void setFilter(List<SongDetail> mSongList) {
            songLst = new ArrayList<>();
            songLst.addAll(mSongList);
            notifyDataSetChanged();
        }


    }

    class queryHolder extends RecyclerView.ViewHolder {

        TextView track, artist;
        RelativeLayout cardView, more_lay;
        ImageView imageView,bkp_img;

        public queryHolder(View itemView) {
            super(itemView);

            track = (TextView) itemView.findViewById(R.id.track_name);
            artist = (TextView) itemView.findViewById(R.id.artist_name);
            imageView = (ImageView) itemView.findViewById(R.id.track_thumb);
            bkp_img = (ImageView) itemView.findViewById(R.id.bkp_img);
            cardView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            more_lay = (RelativeLayout) itemView.findViewById(R.id.more_lay);

        }

        public void bind(SongDetail mSongDetail) {
            track.setText(mSongDetail.getTitle());
            artist.setText(mSongDetail.getArtist());
        }
    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, GRID_PADDING, r.getDisplayMetrics());
        // Column width
        imageWidth = getScreenWidth();
        columnWidth = (int) ((getScreenWidth() - ((2 + 1) * padding)) / 2);
    }

    public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpaceItemDecoration(int spanCount, int spacing, boolean includeEdge) {

            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }


        }
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
            overridePendingTransition(0,R.anim.right_out);
        }
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError e) {
            point.x = display.getWidth();
            point.y = display.getHeight();

        }

        columnWidth = point.x;

        return columnWidth;
    }


}
