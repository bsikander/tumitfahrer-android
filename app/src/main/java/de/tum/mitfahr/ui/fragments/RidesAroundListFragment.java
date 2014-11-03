package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetRidesDateEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.panoramio.PanoramioPhoto;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.LocationUtil;

/**
 * Authored by abhijith on 21/06/14.
 */
public class RidesAroundListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = RidesAroundListFragment.class.getName();
    private static final int MAX_DISTANCE = 5; //kilometers
    private static final int LIST_ITEM_COLOR_FILTER = 0x5F000000;

    private List<Ride> mRides = new ArrayList<Ride>();
    private AlphaInAnimationAdapter mAdapter;
    private RideAdapter mRidesAdapter;
    private int mRideType;

    @InjectView(R.id.rides_listview)
    ListView ridesListView;

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

    public static RidesAroundListFragment newInstance(int rideType) {
        RidesAroundListFragment fragment = new RidesAroundListFragment();
        Bundle args = new Bundle();
        args.putInt("ride_type", rideType);
        fragment.setArguments(args);
        return fragment;
    }

    public RidesAroundListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRideType = getArguments() != null ? getArguments().getInt("ride_type") : 0;
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
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        if (mGetAddressTask != null)
            mGetAddressTask.cancel(true);
    }

    public void setRides(List<Ride> rides) {
        mRides = rides;
        mGetAddressTask = new GetAddressTask();
        mGetAddressTask.execute(mRides);
    }

    public void setRefreshedRides(List<Ride> rides) {
        setLoading(false);
        mRides.addAll(0, rides);
        new GetAddressTask().execute(rides);
        refreshList();
    }

    private void refreshList() {
        Log.e(TAG, "In refresh list");
        mRidesAdapter.clear();
        if (mRides != null)
            mRidesAdapter.addAll(mRides);
        mAdapter.notifyDataSetChanged();
    }

    private void setLoading(boolean loading) {
        swipeRefreshLayout.setRefreshing(loading);
        swipeRefreshLayoutEmptyView.setRefreshing(loading);
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String time = outputFormat.format(calendar.getTime());
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getRides(time, mRideType);
    }

    @Subscribe
    public void onGetDateResult(GetRidesDateEvent result) {
        setLoading(false);
        if (result.getType() == GetRidesDateEvent.Type.GET_SUCCESSFUL) {
            List<Ride> refreshedRides = result.getResponse().getRides();
            if (refreshedRides == null || refreshedRides.size() > 1) {
                Toast.makeText(getActivity(), "No new rides", Toast.LENGTH_SHORT).show();
            } else {
                mRides.addAll(refreshedRides);
                setRides(mRides);
            }
        } else if (result.getType() == GetRidesDateEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Get new rides failed", Toast.LENGTH_SHORT).show();
        }
    }

    protected class GetAddressTask extends AsyncTask<List<Ride>, Void, List<Ride>> {
        private int PHOTO_AREA = 5;

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
                            Long longValue = Math.round(address.getLatitude());
                            int lat = Integer.valueOf(longValue.intValue());
                            longValue = Math.round(address.getLongitude());
                            int lng = Integer.valueOf(longValue.intValue());
                            try {
                                PanoramioPhoto photo = TUMitfahrApplication.getApplication(getActivity()).getPanoramioService().getPhoto(lng - PHOTO_AREA, lat - PHOTO_AREA, lng + PHOTO_AREA, lat + PHOTO_AREA);
                                if (photo != null) {
                                    ride.setRideImageUrl(photo.getPhotoFileUrl());
                                }
                            } catch (Exception e) {
                                return null;
                            }
                            nearbyRides.add(ride);
                        }
                    }
                }
            }
            return nearbyRides;
        }

        @Override
        protected void onPostExecute(List<Ride> result) {
            setLoading(false);
            mRides.clear();
            mRides = result;
            Collections.reverse(mRides);
            refreshList();
        }
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
                ((TextView) view.findViewById(R.id.ride_seats_text)).setText(ride.getFreeSeats() + " seats free");
                ((ImageView) view.findViewById(R.id.ride_type_image)).setImageResource(R.drawable.ic_driver);
            } else {
                ((ImageView) view.findViewById(R.id.ride_type_image)).setImageResource(R.drawable.ic_passenger);
                ((TextView) view.findViewById(R.id.ride_seats_text)).setVisibility(View.VISIBLE);
            }

            ImageView locationImage = ((ImageView) view.findViewById(R.id.ride_location_image));
            locationImage.setColorFilter(LIST_ITEM_COLOR_FILTER);
            Picasso.with(getActivity())
                    .load(ride.getRideImageUrl())
                    .placeholder(R.drawable.list_image_placeholder)
                    .error(R.drawable.list_image_placeholder)
                    .into(locationImage);

            return view;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = mLocationClient.getLastLocation();
        if (location != null)
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

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

}
