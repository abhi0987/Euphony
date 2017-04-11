package music.abitri.com.euphony.SecondaryFragmentPkg.ChildSecFragPkg;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrusoft.library.FrissonView;

import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.AdapterPkg.TrackAdapter;
import music.abitri.com.euphony.BasemusicActivity;
import music.abitri.com.euphony.Constants;
import music.abitri.com.euphony.FragmentPkg.FragmentDrawer;
import music.abitri.com.euphony.FragmentPkg.LibraryFragment;
import music.abitri.com.euphony.Manager.GenreClass;
import music.abitri.com.euphony.Manager.MusicGetter;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildGenreFragment extends Fragment {


    FrissonView frissonView;
    RecyclerView child_dtl_view;
    ImageView img;
    TextView albumname,warn;
    public static final int GRID_PADDING = 3;
    private int columnWidth;
    private int imageWidth;
    float padding;
    TrackAdapter adapter;
    Toolbar toolbar;
    List<SongDetail> songList;
    MusicGetter getter;
    GenreClass detail;
    String type,retrive_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_child_genre, container, false);

        Log.e("Gnere", "frag view started");
        UIsetup(true);
        setImage(view);

        new GenreAsyncTask().execute();

        return view;
    }

    public static ChildGenreFragment newInstance(GenreClass genInfo) {

        Bundle bundle = new Bundle();
        if (genInfo != null) {
            bundle.putSerializable(Constants.GENRE_KEY, genInfo);

        } else {
            Log.d("error", "null value");
        }
        ChildGenreFragment f = new ChildGenreFragment();
        f.setArguments(bundle);
        return f;

    }

    public void UIsetup(boolean bool) {

        if (bool) {
            LibraryFragment.tabLayout.setVisibility(View.GONE);
            LibraryFragment.viewPager.setPagingEnabled(false);
            BasemusicActivity.toolbar.setVisibility(View.GONE);
            FragmentDrawer.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            FragmentDrawer.mDrawerToggle.setDrawerIndicatorEnabled(false);
            FragmentDrawer.mDrawerToggle.syncState();

        } else {
            LibraryFragment.tabLayout.setVisibility(View.VISIBLE);
            LibraryFragment.viewPager.setPagingEnabled(true);
            BasemusicActivity.toolbar.setVisibility(View.VISIBLE);
            FragmentDrawer.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            FragmentDrawer.mDrawerToggle.setDrawerIndicatorEnabled(true);
            FragmentDrawer.mDrawerToggle.syncState();

        }

    }


    private void setImage(View view) {
        frissonView = (FrissonView) view.findViewById(R.id.artist_gen_view);
        child_dtl_view = (RecyclerView) view.findViewById(R.id.child_gen_view);
        albumname = (TextView) view.findViewById(R.id.gen_nm_id);
        warn = (TextView) view.findViewById(R.id.warn_id);
        warn.setVisibility(View.GONE);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        InitilizeGridLayout();
        child_dtl_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        child_dtl_view.addItemDecoration(new GridSpaceItemDecoration(1, dpToPx(3), true));

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle bundle = getArguments();
        detail = (GenreClass) bundle.getSerializable(Constants.GENRE_KEY);
        albumname.setText(detail.getGenreTitle());
        songList = new ArrayList<>();

        getter = new MusicGetter();


    }

    public void onBackPressed() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void onDetach() {
        UIsetup(false);

        super.onDetach();
    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                GRID_PADDING, r.getDisplayMetrics());

        // Column width
        imageWidth = getScreenWidth();

        columnWidth = (int) ((getScreenWidth() - ((
                2 + 1) * padding)) /
                2);

    }


    class GenreAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            songList = getter.getChildGenreList(getActivity(), Uri.parse(detail.getUri()));
            type = "GENRE";
            retrive_type = detail.getUri();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!songList.isEmpty()){
                adapter = new TrackAdapter(getActivity(), songList, columnWidth, "TRACK_AND_ART",type,retrive_type);
                child_dtl_view.setAdapter(adapter);
            }else {
                warn.setVisibility(View.VISIBLE);
            }

        }
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) getActivity().getBaseContext().getSystemService(Context.WINDOW_SERVICE);
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

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
}
