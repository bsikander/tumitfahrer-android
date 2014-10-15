package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.SearchResultsActivity;
import de.tum.mitfahr.util.QuickReturnViewHelper;

/**
 * Authored by abhijith on 20/06/14.
 */
public class SearchResultsFragment extends Fragment {

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

    public static SearchResultsFragment newInstance(List<Ride> rides, String from, String to) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_FROM, from);
        args.putString(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_TO, to);
        args.putSerializable(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_RIDES, (java.io.Serializable) rides);
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
            mRides = (ArrayList<Ride>) getArguments().getSerializable(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_RIDES);
            mFrom = getArguments().getString(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_FROM);
            mTo = getArguments().getString(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_TO);
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
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RideAdapterTest adapter = new RideAdapterTest(getActivity());
        adapter.addAll(mRides);
        searchResultsList.setAdapter(adapter);
    }

    class RideAdapterTest extends ArrayAdapter<Ride> {

        private final LayoutInflater mInflater;

        public RideAdapterTest(Context context) {
            super(context, 0);
            mInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup view = null;

            if (convertView == null) {
                view = (ViewGroup) mInflater.inflate(R.layout.list_item_rides, parent, false);
            } else {
                view = (ViewGroup) convertView;
            }
            Ride ride = getItem(position);

            String[] dateTime = ride.getDepartureTime().split("T");
            ((ImageView) view.findViewById(R.id.ride_location_image)).setBackgroundResource(R.drawable.list_image_placeholder);
            ((TextView) view.findViewById(R.id.rides_from_text)).setText(ride.getDeparturePlace().split(",")[0]);
            ((TextView) view.findViewById(R.id.rides_to_text)).setText(ride.getDestination().split(",")[0]);
            ((TextView) view.findViewById(R.id.rides_date_text)).setText(dateTime[0]);
            ((TextView) view.findViewById(R.id.rides_time_text)).setText(dateTime[1].substring(0, 5));

            return view;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

}
