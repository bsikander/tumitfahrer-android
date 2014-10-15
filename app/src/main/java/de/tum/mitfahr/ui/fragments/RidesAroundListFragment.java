package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
public class RidesAroundListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = RidesAroundListFragment.class.getName();
    private static final int MAX_DISTANCE = 5; //kilometers

    private List<Ride> mRides;
    private RideAdapterTest mAdapter;

    @InjectView(R.id.rides_listview)
    ListView ridesList;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;

    @InjectView(R.id.button_floating_action)
    FloatingActionButton floatingActionButton;

    private LocationClient mLocationClient;
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

    private GetAddressTask mGetAddressTask;
    private Geocoder mGeocoder;

    public static RidesAroundListFragment newInstance() {
        RidesAroundListFragment fragment = new RidesAroundListFragment();
        return fragment;
    }

    public RidesAroundListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getActivity(), this, this);
        mGeocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
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

        ridesList.setEmptyView(swipeRefreshLayoutEmptyView);

        floatingActionButton.attachToListView(ridesList);
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
        mAdapter = new RideAdapterTest(getActivity());
        ridesList.setAdapter(mAdapter);
        ridesList.setAdapter(mAdapter);
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
    public void onPause() {
        super.onPause();
        if (mGetAddressTask != null)
            mGetAddressTask.cancel(true);
    }

    public void setRides(List<Ride> rides) {
        mRides = rides;
        mGetAddressTask = new GetAddressTask();
        mGetAddressTask.execute(mRides);
    }

    private void refreshList() {
        Log.e(TAG, "In refresh list");
        mAdapter.clear();
        mAdapter.addAll(mRides);
        mAdapter.notifyDataSetChanged();
    }

    private void setLoading(boolean loading) {
        swipeRefreshLayout.setRefreshing(loading);
        swipeRefreshLayoutEmptyView.setRefreshing(loading);
    }

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

    protected class GetAddressTask extends AsyncTask<List<Ride>, Void, List<Ride>> {

        public GetAddressTask() {
            super();
        }

        @Override
        protected List<Ride> doInBackground(List<Ride>... params) {
            List<Ride> rides = params[0];
            List<Ride> nearbyRides = new ArrayList<Ride>();

            if (!isCancelled()) {
                for (Ride ride : rides) {
                    String locationName = ride.getDeparturePlace();
                    List<Address> addresses = null;
                    try {
                        addresses = mGeocoder.getFromLocationName(locationName, 2);
                    } catch (IOException exception1) {
                        exception1.printStackTrace();
                    } catch (IllegalArgumentException exception2) {
                        exception2.printStackTrace();
                    }
                    // If the reverse geocode returned an address
                    if (addresses != null && addresses.size() > 0) {
                        // Get the first address
                        Address address = addresses.get(0);
                        ride.setLatitude(address.getLatitude());
                        ride.setLongitude(address.getLongitude());

                        if (LocationUtil.haversineDistance(
                                mCurrentLocation.latitude,
                                mCurrentLocation.longitude,
                                ride.getLatitude(),
                                ride.getLongitude()) < MAX_DISTANCE) {
                            nearbyRides.add(ride);
                        }
                    }
                }
            }
            // If there aren't any addresses, post a message
            return nearbyRides;
        }

        @Override
        protected void onPostExecute(List<Ride> result) {
            setLoading(false);
            mRides = result;
//            Collections.sort(mRides);
            refreshList();
        }
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
            if (!ride.isRideRequest()) {
                ((TextView) view.findViewById(R.id.ride_seats_text)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.ride_seats_text)).setText(ride.getFreeSeats() + " seats");
                ((ImageView) view.findViewById(R.id.ride_type_image)).setImageResource(R.drawable.ic_driver);
            } else {
                ((ImageView) view.findViewById(R.id.ride_type_image)).setImageResource(R.drawable.ic_passenger);
                ((TextView) view.findViewById(R.id.ride_seats_text)).setVisibility(View.VISIBLE);
            }
            return view;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = mLocationClient.getLastLocation();
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Cannot get location.Make sure the location sharing is enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mLocationClient != null)
            mLocationClient.connect();
    }

    @Override
    public void onStop() {
        if (mLocationClient != null)
            mLocationClient.disconnect();
        super.onStop();
    }

}
