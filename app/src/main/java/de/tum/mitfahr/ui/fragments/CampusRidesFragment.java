package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tum.mitfahr.R;
import de.tum.mitfahr.ui.MainActivity;

/**
 * Created by abhijith on 22/05/14.
 */
public class CampusRidesFragment extends AbstractNavigationFragment {

    protected static final int SECTION_COLOR = 0xFF9B2335;

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
        changeActionBarColor(getResources().getColor(R.color.blue2));
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
