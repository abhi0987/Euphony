package music.abitri.com.euphony.SecondaryFragmentPkg;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import music.abitri.com.euphony.AdapterPkg.GenreAdapter;
import music.abitri.com.euphony.AdapterPkg.TrackAdapter;
import music.abitri.com.euphony.Manager.GenreClass;
import music.abitri.com.euphony.Manager.MusicGetter;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenreFragment extends Fragment {

    public static GenreFragment newInstance() {
        GenreFragment f = new GenreFragment();

        return f;
    }


    public static final int GRID_PADDING = 3;
    private int columnWidth;
    private int imageWidth;
    float padding;
    RecyclerView genre_view;
    GenreAdapter adapter;
    List<GenreClass> songlist = null;
    MusicGetter getter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_genre, container, false);
        genre_view = (RecyclerView) view.findViewById(R.id.genre_view);
        InitilizeGridLayout();
        genre_view.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        genre_view.addItemDecoration(new GridSpaceItemDecoration(2, dpToPx(12), true));
        songlist = new ArrayList<>();
        getter = new MusicGetter();

        new GenreAsync().execute();

        return view;
    }


    class GenreAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            songlist = getter.getGenre(getActivity(), "GENRE");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new GenreAdapter(getActivity(), songlist, columnWidth);
            genre_view.setAdapter(adapter);
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


}
