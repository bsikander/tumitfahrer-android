package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import com.google.android.gms.maps.model.LatLng;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.panoramio.PanoramioPhoto;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.RideDetailsActivity;

/**
 * Authored by abhijith on 21/06/14.
 */
public class RidesAllListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RidesAllListFragment.class.getName();
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
    private Geocoder mGeocoder;
    private LatLng mCurrentLocation = new LatLng(52.5167, 13.3833);
    private PanoramioTask mPanoramioTask;

    private Comparator mTimeComparator = new Comparator<Ride>() {
        @Override
        public int compare(Ride ride1, Ride ride2) {
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = outputFormat.parse(ride1.getDepartureTime());
                date2 = outputFormat.parse(ride1.getDepartureTime());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date2.compareTo(date1);
        }
    };
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

    public RidesAllListFragment() {
    }

    public static RidesAllListFragment newInstance(int rideType) {
        RidesAllListFragment fragment = new RidesAllListFragment();
        Bundle args = new Bundle();
        args.putInt("ride_type", rideType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRideType = getArguments() != null ? getArguments().getInt("ride_type") : 0;
        mGeocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ride_list, container, false);
        ButterKnife.inject(this, rootView);
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


    public void setRides(List<Ride> rides) {
        setLoading(false);
        mRides.clear();
        mRides.addAll(rides);
        Collections.reverse(mRides);
        mPanoramioTask = new PanoramioTask(mRidesAdapter);
        mPanoramioTask.execute(mRides);
        refreshList();
    }

    public void setRefreshedRides(List<Ride> rides) {
        setLoading(false);
        new PanoramioTask(mRidesAdapter).execute(rides);
        mRides.addAll(0, rides);
        refreshList();
    }

    private void refreshList() {
        Log.e(TAG, "In refresh list");
        mRidesAdapter.clear();
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
    public void onStop() {
        super.onStop();
        if (mPanoramioTask != null && mPanoramioTask.getStatus() == AsyncTask.Status.RUNNING) {
            mPanoramioTask.cancel(true);
            mPanoramioTask = null;
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
            ((TextView) view.findViewById(R.id.rides_from_text)).setText(ride.getDeparturePlace());
            ((TextView) view.findViewById(R.id.rides_to_text)).setText(ride.getDestination());
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

    private class PanoramioTask extends AsyncTask<List<Ride>, Void, List<Ride>> {

        RideAdapter adapter;
        private int PHOTO_AREA = 5;

        public PanoramioTask(RideAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected List<Ride> doInBackground(List<Ride>... params) {
            List<Ride> rides = params[0];
            if (!isCancelled()) {
                for (Ride ride : rides) {
                    String locationName = ride.getDestination();
                    List<Address> addresses = null;
                    if (ride.getLatitude() != 0 && ride.getLongitude() != 0)
                        continue;
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
                        Long longValue = Math.round(address.getLatitude());
                        int lat = Integer.valueOf(longValue.intValue());
                        longValue = Math.round(address.getLongitude());
                        int lng = Integer.valueOf(longValue.intValue());
                        try {
                            PanoramioPhoto photo = TUMitfahrApplication.getApplication(getActivity()).getPanoramioService().getPhoto(lng - PHOTO_AREA, lat - PHOTO_AREA, lng + PHOTO_AREA, lat + PHOTO_AREA);
                            if (photo != null) {
                                ride.setRideImageUrl(photo.getPhotoFileUrl());
                                publishProgress();
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }
                }
            }
            return rides;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(List<Ride> result) {
            mRidesAdapter.notifyDataSetChanged();
        }
    }

}