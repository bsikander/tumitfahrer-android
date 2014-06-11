package de.tum.mitfahr.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import java.util.ArrayList;

import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.fragments.AbstractNavigationFragment;
import de.tum.mitfahr.ui.fragments.ActivityRidesFragment;
import de.tum.mitfahr.ui.fragments.CampusRidesFragment;
import de.tum.mitfahr.ui.fragments.CreateRidesFragment;
import de.tum.mitfahr.ui.fragments.MyRidesFragment;
import de.tum.mitfahr.ui.fragments.SearchFragment;
import de.tum.mitfahr.ui.fragments.SettingsFragment;
import de.tum.mitfahr.ui.fragments.TimelineFragment;
import de.tum.mitfahr.util.ActionBarColorChangeListener;

public class MainActivity extends FragmentActivity
        implements ActionBarColorChangeListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG_TIMELINE_FRAGMENT = "timeline_fragment";
    private static final String TAG_ACTIVITY_RIDES_FRAGMENT = "activity_rides_fragment";
    private static final String TAG_CAMPUS_RIDES_FRAGMENT = "campus_rides_fragment";
    private static final String TAG_CREATE_RIDE_FRAGMENT = "create_ride_fragment";
    private static final String TAG_SEARCH_FRAGMENT = "search_fragment";
    private static final String TAG_MY_RIDES_FRAGMENT = "my_rides_fragment";
    private static final String TAG_SETTINGS_FRAGMENT = "settings_fragment";
    private static final String TAG_PROFILE_FRAGMENT = "profile_fragment";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private AbstractNavigationFragment mCurrentFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String[] mNavigationTitleArray;
    private int mCurrentPosition = 1;

    private int mCurrentActionBarColor = 0xFF0F3750;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TUMitfahrApplication.getApplication(this).getProfileService().isLoggedIn()) {
            Intent intent = new Intent(this, LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        }
        mNavigationTitleArray = getResources().getStringArray(R.array.navigation_drawer_array);
        mTitle = getTitle();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            onNavigationDrawerItemSelected(0);
        } else {
            mTitle = savedInstanceState.getCharSequence("title");
            restoreActionBar();
            findAndAddFragment();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    private void findAndAddFragment() {
        if (getFragmentManager().findFragmentByTag(TAG_TIMELINE_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_TIMELINE_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_CAMPUS_RIDES_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_CAMPUS_RIDES_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_ACTIVITY_RIDES_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_ACTIVITY_RIDES_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_CREATE_RIDE_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_CREATE_RIDE_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_SEARCH_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_SEARCH_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_MY_RIDES_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_MY_RIDES_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_SETTINGS_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_SETTINGS_FRAGMENT);
        } else if (getFragmentManager().findFragmentByTag(TAG_PROFILE_FRAGMENT) != null) {
            mCurrentFragment = (AbstractNavigationFragment) getSupportFragmentManager().findFragmentByTag(TAG_PROFILE_FRAGMENT);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (position == mCurrentPosition)
            return;
        mCurrentPosition = position;
        mTitle = mNavigationTitleArray[position];
        restoreActionBar();
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                mCurrentFragment = TimelineFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mCurrentFragment, TAG_TIMELINE_FRAGMENT)
                        .commit();
                break;
            case 1:
                mCurrentFragment = CampusRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mCurrentFragment, TAG_CAMPUS_RIDES_FRAGMENT)
                        .commit();
                break;

            case 2:
                mCurrentFragment = ActivityRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mCurrentFragment, TAG_ACTIVITY_RIDES_FRAGMENT)
                        .commit();
                break;

            case 3:
                mCurrentFragment = CreateRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mCurrentFragment, TAG_CREATE_RIDE_FRAGMENT)
                        .commit();
                break;

            case 4:
                mCurrentFragment = SearchFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mCurrentFragment, TAG_SEARCH_FRAGMENT)
                        .commit();
                break;

            case 5:
                mCurrentFragment = MyRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mCurrentFragment, TAG_MY_RIDES_FRAGMENT)
                        .commit();
                break;

            case 6:
                mCurrentFragment = SettingsFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mCurrentFragment, TAG_SETTINGS_FRAGMENT)
                        .commit();
                break;
        }
    }

    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActionBarColorChanged(int newColor) {
        mCurrentActionBarColor = newColor;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("title", mTitle);
        outState.putBoolean("isDrawerOpen",
                mNavigationDrawerFragment.isDrawerOpen());
    }

    public void showRideDetails(Ride ride) {
        RideDetailsFragment rideDetailsFragment = RideDetailsFragment.newInstance(ride);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, rideDetailsFragment)
                .commit();
    }

    public int getCurrentActionBarColor() {
        return mCurrentActionBarColor;
    }
}