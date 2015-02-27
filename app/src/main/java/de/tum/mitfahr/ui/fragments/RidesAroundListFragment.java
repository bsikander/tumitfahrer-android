package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = RidesAroundListFragment.class.getName();
    private static final int MAX_DISTANCE = 5; //kilometers
    private static final int LIST_ITEM_COLOR_FILTER = 0x5F000000;

    @InjectView(R.id.rides_listview)
    ListView ridesListView;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;
    @InjectView(R.id.button_floating_action)
    FloatingActionButton floatingActionButton;

    private List<Ride> mRides = new ArrayList<Ride>();
    private AlphaInAnimationAdapter mAdapter;
    private RideAdapter mRidesAdapter;
    private int mRideType;

    private GoogleApiClient mGoogleAPIClient;
    private LocationRequest mLocationRequest;
    private static boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";


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

    public RidesAroundListFragment() {
    }

    public static RidesAroundListFragment newInstance(int rideType) {
        RidesAroundListFragment fragment = new RidesAroundListFragment();
        Bundle args = new Bundle();
        args.putInt("ride_type", rideType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRideType = getArguments() != null ? getArguments().getInt("ride_type") : 0;
        mGoogleAPIClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGeocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ride_list, container, false);
        View panoramioAttributionFooter = View.inflate(getActivity(),R.layout.panoramio_attribution,null);
        ButterKnife.inject(this, rootView);
        ridesListView.addFooterView(panoramioAttributionFooter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue1,
                R.color.blue2,
                R.color.blue3);

        swipeRefreshLayoutEmptyView.setOnRefreshListener(this);
        swipeRefreshLayoutEmptyView.setColorSchemeResources(R.color.blue1,
                R.color.blue2,
                R.color.blue3);

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

    private void setLoading(final boolean loading) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(loading);
            }
        });
        swipeRefreshLayoutEmptyView.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayoutEmptyView.setRefreshing(loading);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (mRides.size() == 0) {
            setLoading(true);
            TUMitfahrApplication.getApplication(getActivity()).getRidesService().getAllRides(mRideType);
        }

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

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPIClient);
        if (location != null)
            mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleAPIClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
        Toast.makeText(getActivity(), "Cannot get location.Make sure the location sharing is enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleAPIClient.connect();
        }
    }

    @Override
    public void onStop() {
        mGoogleAPIClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
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
                ((TextView) view.findViewById(R.id.ride_seats_text)).setVisibility(View.GONE);
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

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getChildFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public static void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            onDialogDismissed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleAPIClient.isConnecting() &&
                        !mGoogleAPIClient.isConnected()) {
                    mGoogleAPIClient.connect();
                }
            }
        }
    }
}
