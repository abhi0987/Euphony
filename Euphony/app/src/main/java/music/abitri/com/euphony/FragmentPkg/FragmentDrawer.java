package music.abitri.com.euphony.FragmentPkg;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.intrusoft.library.FrissonView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import music.abitri.com.euphony.R;


/**
 * Created by Abhishek on 6/7/2016.
 */
public class FragmentDrawer extends Fragment implements View.OnClickListener {

    RelativeLayout lay1,lay2,lay3,lay4,lay5 ;
    FrissonView frission;

    public static ActionBarDrawerToggle mDrawerToggle;
    public static DrawerLayout mDrawerLayout;
    private View containerView;
    RelativeLayout background;
    private FragmentDrawerListener drawerListener;

    boolean isOpen;

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);



        lay1 = (RelativeLayout) view.findViewById(R.id.lay1);
        lay2 = (RelativeLayout) view.findViewById(R.id.lay2);
        lay3 = (RelativeLayout) view.findViewById(R.id.lay3);
        lay4 = (RelativeLayout) view.findViewById(R.id.lay4);
        lay5 = (RelativeLayout) view.findViewById(R.id.lay5);
        frission = (FrissonView) view.findViewById(R.id.frission);

        background = (RelativeLayout) view.findViewById(R.id.fragment_navigation_drawer);


        lay1.setOnClickListener(this);
        lay2.setOnClickListener(this);
        lay3.setOnClickListener(this);
        lay4.setOnClickListener(this);
        lay5.setOnClickListener(this);



        return view;
    }


    public boolean isDrawerOpen(){

        return isOpen;
    }




    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar, final CoordinatorLayout coordinatorLayout, String color) {

        containerView = getActivity().findViewById(fragmentId);
        frission.setTintColor(Color.parseColor(color));
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
                isOpen = true;

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
                isOpen = false;

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                coordinatorLayout.setAlpha(1 - slideOffset / 2);
                coordinatorLayout.setTranslationX(slideOffset * drawerView.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();

            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }


    public void closeDrawer(){

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()){

            case R.id.lay1 :
                drawerListener.onDrawerItemSelected(view,0);
                break;
            case R.id.lay2:
                drawerListener.onDrawerItemSelected(view,1);
                break;
            case R.id.lay3:
                drawerListener.onDrawerItemSelected(view,2);
                break;
            case R.id.lay4:
                drawerListener.onDrawerItemSelected(view,3);
                break;
            case R.id.lay5:
                drawerListener.onDrawerItemSelected(view,4);
                break;



        }


    }

    public void makeClickable(int pos){

        switch (pos){
            case 0:
                focusable(lay1);
                break;
            case 1:
                focusable(lay2);
                break;
            case 2:
                focusable(lay3);
                break;
            case 3:
                focusable(lay4);
                break;
        }
    }

    public void focusable(RelativeLayout lay) {
            lay1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            lay2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            lay3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            lay4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            lay.setBackgroundColor(getResources().getColor(R.color.dark_primary));
    }


    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }







}
