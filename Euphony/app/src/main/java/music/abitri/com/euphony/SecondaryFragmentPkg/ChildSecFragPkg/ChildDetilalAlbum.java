package music.abitri.com.euphony.SecondaryFragmentPkg.ChildSecFragPkg;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.intrusoft.library.FrissonView;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import music.abitri.com.euphony.AdapterPkg.TrackAdapter;
import music.abitri.com.euphony.BasemusicActivity;
import music.abitri.com.euphony.Constants;
import music.abitri.com.euphony.FragmentPkg.FragmentDrawer;
import music.abitri.com.euphony.FragmentPkg.LibraryFragment;
import music.abitri.com.euphony.Manager.MusicGetter;
import music.abitri.com.euphony.Manager.SongDetail;
import music.abitri.com.euphony.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildDetilalAlbum extends Fragment {


    public static ChildDetilalAlbum newInstance(SongDetail detail) {
        Bundle bundle = new Bundle();
        if (detail != null) {
            bundle.putSerializable(Constants.ALBUM_KEY, detail);
            bundle.putString("TYPE_KEY","ALBUM");
        } else {
            Log.d("error", "null value");
        }
        ChildDetilalAlbum f = new ChildDetilalAlbum();
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


    FrissonView frissonView;
    RecyclerView child_dtl_view;
    ImageView img;
    TextView albumname;
    public static final int GRID_PADDING = 3;
    private int columnWidth;
    private int imageWidth;
    float padding;
    TrackAdapter adapter;
    Toolbar toolbar;
    List<SongDetail> songList;
    MusicGetter getter;
    SongDetail detail;
    String type;
    String retrive_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_detilal_album, container, false);
        UIsetup(true);
        setImage(view);

        new ChildAsynctask().execute();

        return view;
    }


    private void setImage(View view) {
        frissonView = (FrissonView) view.findViewById(R.id.album_art_view);
        child_dtl_view = (RecyclerView) view.findViewById(R.id.child_albm_view);
        albumname = (TextView) view.findViewById(R.id.album_nm_id);
        img = (ImageView) view.findViewById(R.id.albm_img_id);
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
        detail = (SongDetail) bundle.getSerializable(Constants.ALBUM_KEY);
        albumname.setText(detail.get_album());
        songList = new ArrayList<>();

        getter = new MusicGetter();
        Uri coverPath = detail.getSmallCover();
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(coverPath, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                frissonView.setBitmap(bm);
                pfd = null;
                fd = null;
            }
        } catch (Error ee) {

        } catch (Exception e) {
            e.printStackTrace();
        }


        Glide.with(getActivity())
                .load(detail.getSmallCover())
                .centerCrop()
                .thumbnail(0.8f)
                .override(600, 600)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        img.setColorFilter(filter);
    }

    public void onBackPressed() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ftransaction = fm.beginTransaction();
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


    private class ChildAsynctask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            songList = getter.getChildAlbumList(getActivity(), detail.get_album());
            type = "ALBUM";
            retrive_type = detail.get_album();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new TrackAdapter(getActivity(), songList, columnWidth, "CHILD_ALBUM",type,retrive_type);
            child_dtl_view.setAdapter(adapter);

        }
    }
}
