package music.abitri.com.euphony.FragmentPkg;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.AdapterPkg.FavAdapter;
import music.abitri.com.euphony.AdapterPkg.PlaylistAdapter;
import music.abitri.com.euphony.AdapterPkg.TabPageAdapter;
import music.abitri.com.euphony.AdapterPkg.TrackAdapter;
import music.abitri.com.euphony.Constants;
import music.abitri.com.euphony.Manager.CustomViewPager;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.R;
import music.abitri.com.euphony.SQLiteDataBasePackage.FavoritesDatabase;
import music.abitri.com.euphony.SQLiteDataBasePackage.PlayListSQLdatabase;
import music.abitri.com.euphony.SecondaryFragmentPkg.Albumfragment;
import music.abitri.com.euphony.SecondaryFragmentPkg.AllFragment;
import music.abitri.com.euphony.SecondaryFragmentPkg.ArtistFragment;
import music.abitri.com.euphony.SecondaryFragmentPkg.GenreFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment implements View.OnClickListener {

    Handler handler = new Handler();
    public static TabLayout tabLayout;
    public static RelativeLayout create_plist;
    RecyclerView combinedView;
    TabPageAdapter pageAdapter;
    public static CustomViewPager viewPager;
    public static final int GRID_PADDING = 3;
    private int columnWidth;
    private int imageWidth;
    float padding;
    ArrayList<String> playlistNames;
    PlayListSQLdatabase PdB;
    FavoritesDatabase FdB;
    PlaylistAdapter playlistAdapter;
    FavAdapter Favadapter;
    List<SongDetail> favList;
    TextView textWran;

    String[] tabnames = {"ALBUM", "ARTIST", "ALL", "GENRE"};

    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        combinedView = (RecyclerView) view.findViewById(R.id.combined_view);
        create_plist = (RelativeLayout) view.findViewById(R.id.add_to_list_lay);
        viewPager = (CustomViewPager) view.findViewById(R.id.viewPager);
        textWran = (TextView) view.findViewById(R.id.textWarn);
        pageAdapter = new TabPageAdapter(getFragmentManager());

        Bundle bundle = getArguments();
        int type = bundle.getInt("TYPE", 0);

        switch (type) {

            case 0:
                tabViewsetup();
                break;
            case 1:
                OtherViewGroup(type);
                break;
            case 2:
                OtherViewGroup(type);
                break;
        }


        return view;
    }


    public void OtherViewGroup(int type) {
        combinedView.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        PdB = new PlayListSQLdatabase(getActivity());
        FdB = new FavoritesDatabase(getActivity());
        InitilizeGridLayout();
        playlistNames = new ArrayList<>();
        favList = new ArrayList<>();
        combinedView.setLayoutManager(new LinearLayoutManager(getActivity()));
        combinedView.addItemDecoration(new GridSpaceItemDecoration(1, dpToPx(3), true));

        switch (type) {
            case 1:
                create_plist.setVisibility(View.GONE);
                setFavAdapter();
                break;
            case 2:
                create_plist.setVisibility(View.VISIBLE);
                create_plist.setOnClickListener(this);
                setAdapterData();
                break;
        }


    }

    public void setFavAdapter() {
        favList = FdB.getAllFavSongs();
        if (!favList.isEmpty()){
            combinedView.setVisibility(View.VISIBLE);
            textWran.setVisibility(View.GONE);
            Favadapter = new FavAdapter(getActivity(),favList);
            combinedView.setAdapter(Favadapter);
        }else {
            combinedView.setVisibility(View.GONE);
            textWran.setVisibility(View.VISIBLE);
        }


    }


    public void setAdapterData() {
        playlistNames = PdB.getTablenames();
        playlistNames.remove(0);
        playlistNames.add(0,"Recently Played");
        playlistNames.add(0,"Recently Added");
        if (!playlistNames.isEmpty()) {
            playlistAdapter = new PlaylistAdapter(getActivity(), playlistNames);
            combinedView.setAdapter(playlistAdapter);
        }

    }


    public void tabViewsetup() {
        combinedView.setVisibility(View.GONE);
        create_plist.setVisibility(View.GONE);
        pageAdapter.addFrag(Albumfragment.newInstance(), tabnames[0]);
        pageAdapter.addFrag(ArtistFragment.newInstance(), tabnames[1]);
        pageAdapter.addFrag(AllFragment.newInstance(), tabnames[2]);
        pageAdapter.addFrag(GenreFragment.newInstance(), tabnames[3]);
        viewPager.setAdapter(pageAdapter);
        viewPager.setPageTransformer(true, new ParallaxPageTransformer());
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            View tabOne = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.tab_text, null);
            TextView text = (TextView) tabOne.findViewById(R.id.tab);
            text.setText(tabnames[i]);

            tabLayout.getTabAt(i).setCustomView(tabOne);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_to_list_lay:

                PlaylistBottomSheetWork();
                break;
        }
    }


    public void PlaylistBottomSheetWork() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.playlist_dlg, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.enter_list_nm);
        dialogBuilder.setTitle("New Playlist");

        dialogBuilder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String table_name = edt.getText().toString().trim();
                PdB.Create_Just_DB(table_name);
                Toast.makeText(getActivity(), "Playlist " + table_name + " is Created", Toast.LENGTH_LONG).show();
                setAdapterData();

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

    public class ParallaxPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
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
