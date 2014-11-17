package de.tum.mitfahr.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.io.IOException;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.UIActionOfferRideEvent;
import de.tum.mitfahr.gcm.PushNotificationHelper;
import de.tum.mitfahr.gcm.PushNotificationInterface;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.fragments.AbstractNavigationFragment;
import de.tum.mitfahr.ui.fragments.ActivityRidesFragment;
import de.tum.mitfahr.ui.fragments.CampusRidesFragment;
import de.tum.mitfahr.ui.fragments.CreateRidesFragment;
import de.tum.mitfahr.ui.fragments.MyRidesFragment;
import de.tum.mitfahr.ui.fragments.ProfileFragment;
import de.tum.mitfahr.ui.fragments.SearchFragment;
import de.tum.mitfahr.ui.fragments.SettingsFragment;
import de.tum.mitfahr.ui.fragments.TimelineFragment;
import de.tum.mitfahr.util.ActionBarColorChangeListener;

public class MainActivity extends ActionBarActivity
        implements ActionBarColorChangeListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks, PushNotificationInterface {
    private static final String TAG_TIMELINE_FRAGMENT = "timeline_fragment";
    private static final String TAG_ACTIVITY_RIDES_FRAGMENT = "activity_rides_fragment";
    private static final String TAG_CAMPUS_RIDES_FRAGMENT = "campus_rides_fragment";
    private static final String TAG_CREATE_RIDE_FRAGMENT = "create_ride_fragment";
    private static final String TAG_SEARCH_FRAGMENT = "search_fragment";
    private static final String TAG_MY_RIDES_FRAGMENT = "my_rides_fragment";
    private static final String TAG_SETTINGS_FRAGMENT = "settings_fragment";
    private static final String TAG_PROFILE_FRAGMENT = "profile_fragment";

    private static final String TAG_SEARCH_RESULTS_FRAGMENT = "search_results_fragment";

    private Toolbar toolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private AbstractNavigationFragment mCurrentFragment;
    private boolean offerRideFlag;
    private Ride mOfferedRide = null;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String[] mNavigationTitleArray;
    private int mCurrentPosition = -1;

    private int mCurrentActionBarColor = 0xFF0F3750;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TUMitfahrApplication.getApplication(this).getProfileService().isLoggedIn()) {
            Intent intent = new Intent(this, LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("first_run", true)) {
            prefs.edit().putBoolean("first_run", false).commit();
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        mTitle = getTitle();

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            onNavigationDrawerItemSelected(1, "Timeline");
        } else {
            mTitle = savedInstanceState.getCharSequence("title");
            restoreActionBar();
            findAndAddFragment();
        }

        PushNotificationHelper helper = new PushNotificationHelper();
        try {
            helper.getRegistrationID(this);
        } catch (IOException e) {
            e.printStackTrace();
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
    public void onNavigationDrawerItemSelected(int position, String title) {
        // update the main content by replacing fragments
        if (position == mCurrentPosition)
            return;
        mCurrentPosition = position;
        mTitle = title;
        restoreActionBar();
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                mCurrentFragment = ProfileFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_TIMELINE_FRAGMENT)
                        .commit();
                break;

            case 1:
                mCurrentFragment = TimelineFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_TIMELINE_FRAGMENT)
                        .commit();
                break;
            case 2:
                mCurrentFragment = CampusRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_CAMPUS_RIDES_FRAGMENT)
                        .commit();
                break;

            case 3:
                mCurrentFragment = ActivityRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_ACTIVITY_RIDES_FRAGMENT)
                        .commit();
                break;

            case 4:
                mCurrentFragment = CreateRidesFragment.newInstance(position + 1);
                if (offerRideFlag) {
                    Bundle extras = new Bundle();
                    extras.putSerializable(CreateRidesFragment.ARG_OFFER_RIDE, mOfferedRide);
                    mCurrentFragment.setArguments(extras);
                    offerRideFlag = false;
                    mOfferedRide = null;
                }
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_CREATE_RIDE_FRAGMENT)
                        .commit();
                break;

            case 5:
                mCurrentFragment = SearchFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_SEARCH_FRAGMENT)
                        .commit();
                break;

            case 6:
                mCurrentFragment = MyRidesFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_MY_RIDES_FRAGMENT)
                        .commit();
                break;

            case 7:
                mCurrentFragment = SettingsFragment.newInstance(position + 1);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_enter, R.anim.fade_exit)
                        .replace(R.id.container, mCurrentFragment, TAG_SETTINGS_FRAGMENT)
                        .commit();
                break;
        }
    }

    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    public int getCurrentActionBarColor() {
        return mCurrentActionBarColor;
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    public NavigationDrawerFragment getNavigationDrawerFragment() {
        return mNavigationDrawerFragment;
    }

    @Override
    public void onPlayServiceRegistrationComplete(String id) {
        if (id == null) {
            Toast.makeText(this, "There is some problem with registration. Please try again!", Toast.LENGTH_LONG).show();
        } else {
            // Use this id for your purposes
            //Toast.makeText(this, id, Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void onOfferRideUIAction(UIActionOfferRideEvent event) {
        mNavigationDrawerFragment.selectItem(4);
        offerRideFlag = true;
        mOfferedRide = event.getRide();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

}