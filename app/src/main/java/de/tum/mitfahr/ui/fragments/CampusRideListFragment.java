package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetRidesDateEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.util.LocationUtil;

/**
 * Authored by abhijith on 21/06/14.
 */
public class CampusRideListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {


    private static final String ARG_FRAGMENT_TYPE = "type";

    private List<Ride> mRides;
    private RideAdapterTest mAdapter;
    private int mFragmentType;

    @InjectView(R.id.rides_listview)
    ListView ridesList;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    //A view if the list is empty
    View mEmptyView;
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

    public static CampusRideListFragment newInstance(int type) {
        CampusRideListFragment fragment = new CampusRideListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public CampusRideListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentType = getArguments().getInt(ARG_FRAGMENT_TYPE);
        }
        mLocationClient = new LocationClient(getActivity(), this, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ride_list, container, false);
        mEmptyView = inflater.inflate(R.layout.rides_empty_view, null, false);
        ButterKnife.inject(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        ((ViewGroup)ridesList.getParent()).addView(mEmptyView);
        ridesList.setEmptyView(mEmptyView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        outputFormat.setTimeZone(TimeZone.getDefault());
        String fromDate = outputFormat.format(calendar.getTime());

        mAdapter = new RideAdapterTest(getActivity());
        ridesList.setAdapter(mAdapter);

        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getRides(fromDate, 0);
        swipeRefreshLayout.setRefreshing(true);

        if (mFragmentType == CampusRidesFragment.FRAGMENT_TYPE_AROUND) {

        } else if (mFragmentType == CampusRidesFragment.FRAGMENT_TYPE_MY) {

        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);
    }

    @Subscribe
    public void onCampusRidesEvent(GetRidesDateEvent result) {
        swipeRefreshLayout.setRefreshing(false);
        if (result.getType() == GetRidesDateEvent.Type.GET_SUCCESSFUL) {
            mRides = result.getResponse().getRides();
            if (mFragmentType == CampusRidesFragment.FRAGMENT_TYPE_AROUND) {
                new GetAddressTask(getActivity()).execute(mRides);
            } else {
                mAdapter.clear();
                mAdapter.addAll(mRides);
                mAdapter.notifyDataSetChanged();
            }
        } else if (result.getType() == GetRidesDateEvent.Type.GET_FAILED) {

        } else {

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
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
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

    protected class GetAddressTask extends AsyncTask<List<Ride>, Void, List<Ride>> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public GetAddressTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        @Override
        protected List<Ride> doInBackground(List<Ride>... params) {
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
            List<Ride> rides = params[0];
            for (Ride ride : rides) {
                String locationName = ride.getDeparturePlace();
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(locationName, 2);
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
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    // Return the text
                }
            }
            // If there aren't any addresses, post a message
            return rides;
        }

        /**
         * A method that's called once doInBackground() completes. Set the text of the
         * UI element that displays the address. This method runs on the UI thread.
         */
        @Override
        protected void onPostExecute(List<Ride> result) {
            mRides = result;
            Collections.sort(mRides, mLocationComparator);
            mAdapter.clear();
            //mAdapter.addAll(mRides);
            mAdapter.notifyDataSetChanged();

        }
    }
}
