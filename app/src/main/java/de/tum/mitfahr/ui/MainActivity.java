package de.tum.mitfahr.ui;

import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.ui.fragments.ActivityRidesFragment;
import de.tum.mitfahr.ui.fragments.CampusRidesFragment;
import de.tum.mitfahr.ui.fragments.CreateRidesFragment;
import de.tum.mitfahr.ui.fragments.MyRidesFragment;
import de.tum.mitfahr.ui.fragments.SearchFragment;
import de.tum.mitfahr.ui.fragments.SettingsFragment;
import de.tum.mitfahr.ui.fragments.TimelineFragment;
import de.tum.mitfahr.util.ActionBarColorChangeListener;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,ActionBarColorChangeListener {


    private NavigationDrawerFragment mNavigationDrawerFragment;

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
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigationTitleArray = getResources().getStringArray(R.array.navigation_drawer_array);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (position == mCurrentPosition)
            return;
        mCurrentPosition = position;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TimelineFragment.newInstance(position + 1))
                        .commit();
                break;

            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, CampusRidesFragment.newInstance(position + 1))
                        .commit();
                break;

            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ActivityRidesFragment.newInstance(position + 1))
                        .commit();
                break;

            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, CreateRidesFragment.newInstance(position + 1))
                        .commit();
                break;

            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SearchFragment.newInstance(position + 1))
                        .commit();
                break;

            case 5:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MyRidesFragment.newInstance(position + 1))
                        .commit();
                break;

            case 6:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SettingsFragment.newInstance(position + 1))
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        mTitle = mNavigationTitleArray[number - 1];
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

    public int getCurrentActionBarColor(){
        return mCurrentActionBarColor;
    }
}
