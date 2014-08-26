package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;

/**
 * Created by abhijith on 22/05/14.
 */
public class CampusRidesFragment extends AbstractNavigationFragment {

    public static final int FRAGMENT_TYPE_ALL = 0;
    public static final int FRAGMENT_TYPE_AROUND = 1;
    public static final int FRAGMENT_TYPE_MY = 2;


    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @InjectView(R.id.pager)
    ViewPager pager;

    private CampusPagerAdapter adapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CampusRidesFragment newInstance(int sectionNumber) {
        CampusRidesFragment fragment = new CampusRidesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public CampusRidesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campus_rides, container, false);
        ButterKnife.inject(this, rootView);
        adapter = new CampusPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        tabs.setViewPager(pager);
        changeActionBarColor(getResources().getColor(R.color.blue2));
        //showTabs();
        return rootView;
    }

    /**
     * Show the label using an animation
     */
    private void showTabs() {
        tabs.setVisibility(View.VISIBLE);
        tabs.setTranslationY(-getActivity().getActionBar().getHeight());
        tabs.animate().translationY(0f).setDuration(100).start();
    }

    public class CampusPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"All", "Around Me", "Recent"};

        public CampusPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return CampusRideListFragment.newInstance(FRAGMENT_TYPE_ALL);
            else if (position == 1)
                return CampusRideListFragment.newInstance(FRAGMENT_TYPE_AROUND);
            else
                return CampusRideListFragment.newInstance(FRAGMENT_TYPE_MY);
        }
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
    }
}
