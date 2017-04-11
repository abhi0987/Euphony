package music.abitri.com.euphony.SecondaryFragmentPkg.ChildSecFragPkg;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import music.abitri.com.euphony.AdapterPkg.ChildPlaylistAdapter;
import music.abitri.com.euphony.AdapterPkg.TrackAdapter;
import music.abitri.com.euphony.BasemusicActivity;
import music.abitri.com.euphony.FragmentPkg.FragmentDrawer;
import music.abitri.com.euphony.FragmentPkg.LibraryFragment;
import music.abitri.com.euphony.Manager.MusicGetter;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.R;
import music.abitri.com.euphony.SQLiteDataBasePackage.PlayListSQLdatabase;
import music.abitri.com.euphony.SQLiteDataBasePackage.SqliteRecentPlay;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildPlayListFrag extends Fragment {

    String TAG = ChildPlayListFrag.class.getName();
    static String PLAYLIST_KEY_NAME = "PLAYLIST_NAME";
    MusicGetter getter;

    public ChildPlayListFrag() {
        // Required empty public constructor


    }

    RecyclerView playlist_cntr;
    TextView plst_hdr, textWrn;
    List<SongDetail> songList;
    PlayListSQLdatabase dbms;
    SqliteRecentPlay recentDB;
    ChildPlaylistAdapter adapter;
    Toolbar toolbar;
    public static final int GRID_PADDING = 3;
    private int columnWidth;
    private int imageWidth;
    float padding;

    public static ChildPlayListFrag getInstance(String PlayListName) {

        ChildPlayListFrag frag = new ChildPlayListFrag();
        Bundle bundle = new Bundle();
        bundle.putString(PLAYLIST_KEY_NAME, PlayListName);
        frag.setArguments(bundle);

        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_play_list, container, false);
        playlist_cntr = (RecyclerView) view.findViewById(R.id.plst_cntr_view);
        plst_hdr = (TextView) view.findViewById(R.id.plst_hdr_id);
        textWrn = (TextView) view.findViewById(R.id.textWarn);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        UIsetup(true);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);

        getter = new MusicGetter();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        InitilizeGridLayout();
        playlist_cntr.setLayoutManager(new LinearLayoutManager(getActivity()));
        playlist_cntr.addItemDecoration(new GridSpaceItemDecoration(1, dpToPx(3), true));
        songList = new ArrayList<>();
        dbms = new PlayListSQLdatabase(getActivity());
        recentDB = new SqliteRecentPlay(getActivity());
        String name = getArguments().getString(PLAYLIST_KEY_NAME);
        Log.e(TAG, "TABLE :" + name);
        plst_hdr.setText(name);

        if (name.equals("Recently Played")) {
            songList = recentDB.GetRecentSongs();
            Collections.reverse(songList);
            if (!(songList.size() < 6)) {
                songList.subList(6, songList.size()).clear();
            }


        }else if (name.equals("Recently Added")){
            songList = getter.getSongDateWise(getActivity());
        }

        else {
            songList = dbms.getAllSongs(name);
        }


        Log.d(TAG, "songList :" + new Gson().toJson(songList));
        if (!songList.isEmpty()) {

            playlist_cntr.setVisibility(View.VISIBLE);
            textWrn.setVisibility(View.GONE);
            adapter = new ChildPlaylistAdapter(getActivity(), songList, columnWidth, name);
            playlist_cntr.setAdapter(adapter);
        } else {
            playlist_cntr.setVisibility(View.GONE);
            textWrn.setVisibility(View.VISIBLE);
        }


        return view;
    }

    private void onBackPressed() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }

    public void UIsetup(boolean bool) {

        if (bool) {
            //LibraryFragment.tabLayout.setVisibility(View.GONE);
            LibraryFragment.create_plist.setVisibility(View.GONE);
            BasemusicActivity.toolbar.setVisibility(View.GONE);
            FragmentDrawer.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            FragmentDrawer.mDrawerToggle.setDrawerIndicatorEnabled(false);
            FragmentDrawer.mDrawerToggle.syncState();

        } else {
            //LibraryFragment.tabLayout.setVisibility(View.VISIBLE);
            LibraryFragment.create_plist.setVisibility(View.VISIBLE);
            BasemusicActivity.toolbar.setVisibility(View.VISIBLE);
            FragmentDrawer.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            FragmentDrawer.mDrawerToggle.setDrawerIndicatorEnabled(true);
            FragmentDrawer.mDrawerToggle.syncState();

        }

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
