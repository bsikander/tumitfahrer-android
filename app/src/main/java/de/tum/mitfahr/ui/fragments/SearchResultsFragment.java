package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.util.QuickReturnViewHelper;

/**
 * Authored by abhijith on 20/06/14.
 */
public class SearchResultsFragment extends AbstractNavigationFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FROM = "from";
    private static final String ARG_TO = "to";
    private static final String ARG_RIDES = "rides";

    private List<Ride> mRides;
    private String mFrom;
    private String mTo;
    private QuickReturnViewHelper mQuickReturnViewHelper;

    @InjectView(R.id.search_results_listview)
    ListView searchResultsList;

    View mQuickReturnView;
    View mBlankHeader;

    TextView fromText;
    TextView toText;
    TextView dateText;
    ImageButton editButton;


    private static final String[] songs = {"1983... (A Merman I Should Turn to Be)", "51st Anniversary", "All Along The Watchtower", "Angel", "Are You Experienced?", "Bleeding Heart", "Bold As Love", "Burning Of The Midnight Lamp",
            "Castles Made Of Sand", "Crash Landing", "Crosstown Traffic", "Dolly Dagger", "Drifting", "Fire", "Foxy Lady", "Gypsy Eyes", "Hear My Train a Comin'", "Hey Joe", "Highway Chile", "House Burning Down", "Izabella", "Let Me Move You",
            "Little Wing", "Lover Man", "Machine Gun", "Manic Depression", "Mojo Man", "Mr. Bad Luck", "One Rainy Wish", "Purple Haze", "Red House", "She's So Fine", "Ships Passing In The Night", "Somewhere", "Spanish Castle Magic",
            "Stepping Stone", "Still Raining, Still Dreaming", "Stone Free", "The Wind Cries Mary", "Third Stone From The Sun", "Valleys of Neptune", "Voodoo Child (Slight Return)"};

    public static SearchResultsFragment newInstance(List<Ride> rides, String from, String to, int sectionNumber) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FROM, from);
        args.putString(ARG_TO, to);
        args.putSerializable(ARG_RIDES, (java.io.Serializable) rides);
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchResultsFragment newInstance() {
        SearchResultsFragment fragment = new SearchResultsFragment();
        return fragment;
    }

    public SearchResultsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRides = (ArrayList<Ride>) getArguments().getSerializable(ARG_RIDES);
            mFrom = getArguments().getString(ARG_FROM);
            mTo = getArguments().getString(ARG_TO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        mQuickReturnViewHelper = new QuickReturnViewHelper(getActivity(), QuickReturnViewHelper.ViewPosition.TOP, inflater);
        ButterKnife.inject(this, rootView);

        mBlankHeader = inflater.inflate(R.layout.blank_header, null, false);

        mQuickReturnView = mQuickReturnViewHelper.createQuickReturnViewOnListView(searchResultsList, R.layout.quick_return_search_results, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //Log.d("SearchResultsFragment", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.d("SearchResultsFragment", "onScroll");
            }
        });
        changeActionBarColor(getResources().getColor(R.color.blue3));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //searchResultsList.addHeaderView(mBlankHeader, null, false);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_drawer, R.id.menu_title, songs);
        searchResultsList.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

}
