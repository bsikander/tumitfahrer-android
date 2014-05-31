package de.tum.mitfahr.ui;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.adapters.SearchAdapter;
import de.tum.mitfahr.networking.models.Ride;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
            OfferRideFragment.OnFragmentInteractionListener,
            TimePickerFragment.OnFragmentInteractionListener,
            DatePickerFragment.OnFragmentInteractionListener,
            RideDetailsFragment.OnFragmentInteractionListener,
            SearchFragment.OnFragmentInteractionListener,
            SearchResultsFragment.OnFragmentInteractionListener {

    private static final String OFFER_RIDE_FRAGMENT = "offer_ride_fragment";
    private static final String SEARCH_FRAGMENT = "search_fragment";
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private OfferRideFragment mOfferRideFragment;
    private SearchFragment mSearchFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

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

        if(getFragmentManager().findFragmentByTag(OFFER_RIDE_FRAGMENT) != null) {
            mOfferRideFragment = (OfferRideFragment) getFragmentManager().findFragmentByTag(OFFER_RIDE_FRAGMENT);
        }
        if(getFragmentManager().findFragmentByTag(SEARCH_FRAGMENT) != null) {
            mSearchFragment = (SearchFragment) getFragmentManager().findFragmentByTag(SEARCH_FRAGMENT);
        }

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
        switch(position) {
            case 0:
                mSearchFragment = new SearchFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.container, mSearchFragment, SEARCH_FRAGMENT)
                        .commit();
                break;
            case 1:
                mOfferRideFragment = new OfferRideFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.container, mOfferRideFragment,OFFER_RIDE_FRAGMENT)
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.search);
                break;
            case 2:
                mTitle = getString(R.string.title_offer_ride);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
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

    public void onFragmentInteraction(Uri uri) {

    }

    public void setTime(int hourOfDay, int minute, String callingFragment) {
        if (callingFragment.equals("OfferRideFragment") && mOfferRideFragment != null) {
            mOfferRideFragment.setTime(hourOfDay, minute);
        }

        if (callingFragment.equals("SearchFragment") && mSearchFragment != null) {
            mSearchFragment.setTime(hourOfDay, minute);
        }
    }

    public void setDate(int day, int month, int year, String callingFragment) {
        if (callingFragment.equals("OfferRideFragment") && mOfferRideFragment != null) {
            mOfferRideFragment.setDate(day, month, year);
        }

        if (callingFragment.equals("SearchFragment") && mSearchFragment != null) {
            mSearchFragment.setDate(day, month, year);
        }
    }

    public void showRideDetails(Ride ride) {
        RideDetailsFragment rideDetailsFragment = RideDetailsFragment.newInstance(ride);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, rideDetailsFragment)
                .commit();
    }

    public void showSearchResults(ArrayList<Ride> rides, String from, String to) {
        SearchResultsFragment searchResultsFragment = SearchResultsFragment.newInstance(rides, from, to);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, searchResultsFragment)
                .commit();
    }

    public void onFragmentInteraction(String id) {

    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int sectionNumber = this.getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;
            switch(sectionNumber) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_register, container, false);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_offer_ride, container, false);
                    ButterKnife.inject(this, rootView);
                    break;
            }

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }



    }

}
