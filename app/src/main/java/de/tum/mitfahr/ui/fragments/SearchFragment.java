package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tum.mitfahr.R;
import de.tum.mitfahr.ui.MainActivity;

/**
 * Created by abhijith on 22/05/14.
 */
public class SearchFragment extends AbstractNavigationFragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        changeActionBarColor(getResources().getColor(R.color.blue3));
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
