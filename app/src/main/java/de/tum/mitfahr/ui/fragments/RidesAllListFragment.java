package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.LocationUtil;
import de.tum.mitfahr.widget.FloatingActionButton;

/**
 * Authored by abhijith on 21/06/14.
 */
public class RidesAllListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RidesAllListFragment.class.getName();

    private List<Ride> mRides;
    private AlphaInAnimationAdapter mAdapter;
    private RideAdapter mRidesAdapter;

    @InjectView(R.id.rides_listview)
    ListView ridesListView;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;

    @InjectView(R.id.button_floating_action)
    FloatingActionButton floatingActionButton;

    private LatLng mCurrentLocation = new LatLng(52.5167, 13.3833);
    private Comparator mLocationComparator = new Comparator<Ride>() {
        @Override
        public int compare(Ride ride1, Ride ride2) {
            Double distance1 = LocationUtil.haversineDistance(mCurrentLocation.latitude,
                    mCurrentLocation.longitude,
                    ride1.getLatitude(), ride1.getLongitude());
            Double distance2 = LocationUtil.haversineDistance(mCurrentLocation.latitude,
                    mCurrentLocation.longitude,
                    ride2.getLatitude(), ride2.getLongitude());
            return distance1.compareTo(distance2);
        }
    };

    public static RidesAllListFragment newInstance() {
        RidesAllListFragment fragment = new RidesAllListFragment();
        return fragment;
    }

    public RidesAllListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ride_list, container, false);
        ButterKnife.inject(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayoutEmptyView.setOnRefreshListener(this);
        swipeRefreshLayoutEmptyView.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ridesListView.setEmptyView(swipeRefreshLayoutEmptyView);

        floatingActionButton.attachToListView(ridesListView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(4);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRidesAdapter = new RideAdapter(getActivity());
        mAdapter = new AlphaInAnimationAdapter(mRidesAdapter);
        mAdapter.setAbsListView(ridesListView);
        ridesListView.setAdapter(mAdapter);
        ridesListView.setAdapter(mAdapter);
        ridesListView.setOnItemClickListener(mItemClickListener);
        setLoading(true);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new android.widget.AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Ride clickedItem = mRides.get(position);
            if (clickedItem != null) {
                Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, clickedItem);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayoutEmptyView.setRefreshing(false);
            }
        }, 5000);
    }

    public void setRides(List<Ride> rides) {
        setLoading(false);
        mRides = rides;
//        Collections.sort(mRides);
        refreshList();
    }

    private void refreshList() {
        Log.e(TAG, "In refresh list");
        mRidesAdapter.clear();
        mRidesAdapter.addAll(mRides);
        mAdapter.notifyDataSetChanged();
    }

    private void setLoading(boolean loading) {
        swipeRefreshLayout.setRefreshing(loading);
        swipeRefreshLayoutEmptyView.setRefreshing(loading);
    }

    class RideAdapter extends ArrayAdapter<Ride> {

        private final LayoutInflater mInflater;

        public RideAdapter(Context context) {
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
            if (!ride.isRideRequest()) {
                ((TextView) view.findViewById(R.id.ride_seats_text)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.ride_seats_text)).setText(ride.getFreeSeats() + " seats available");
                ((ImageView) view.findViewById(R.id.ride_type_image)).setImageResource(R.drawable.ic_driver);
            } else {
                ((ImageView) view.findViewById(R.id.ride_type_image)).setImageResource(R.drawable.ic_passenger);
                ((TextView) view.findViewById(R.id.ride_seats_text)).setVisibility(View.GONE);
            }
            return view;
        }
    }
}
