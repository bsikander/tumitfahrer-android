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
import android.text.format.DateUtils;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.networking.models.Activities;
import de.tum.mitfahr.util.LocationUtil;
import de.tum.mitfahr.util.TimelineItem;

/**
 * Authored by abhijith on 21/06/14.
 */
public class TimelineListAroundFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = TimelineListAroundFragment.class.getName();
    private static final int MAX_DISTANCE = 5; //kilometers

    private Activities mTimelineActivities;

    private List<TimelineItem> mTimeline = new ArrayList<TimelineItem>();

    private TimelineAdapter mAdapter;
    @InjectView(R.id.rides_listview)
    ListView timelineList;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;

    private LocationClient mLocationClient;
    private LatLng mCurrentLocation = new LatLng(52.5167, 13.3833);
    private Comparator mLocationComparator = new Comparator<TimelineItem>() {
        @Override
        public int compare(TimelineItem ride1, TimelineItem ride2) {
            Double distance1 = LocationUtil.haversineDistance(mCurrentLocation.latitude,
                    mCurrentLocation.longitude,
                    ride1.getLat(), ride1.getLng());
            Double distance2 = LocationUtil.haversineDistance(mCurrentLocation.latitude,
                    mCurrentLocation.longitude,
                    ride2.getLat(), ride2.getLng());
            return distance1.compareTo(distance2);
        }
    };

    private GetAddressTask mGetAddressTask;
    private Geocoder mGeocoder;

    public static TimelineListAroundFragment newInstance() {
        TimelineListAroundFragment fragment = new TimelineListAroundFragment();
        return fragment;
    }

    public TimelineListAroundFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_timeline_list, container, false);
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

        timelineList.setEmptyView(swipeRefreshLayoutEmptyView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new TimelineAdapter(getActivity());
        timelineList.setAdapter(mAdapter);
        setLoading(true);
    }

    public void setTimelineItems(List<TimelineItem> timelineItems) {
        mTimeline = timelineItems;
        mGetAddressTask = new GetAddressTask();
        mGetAddressTask.execute(mTimeline);
    }

    private void setLoading(boolean loading) {
        swipeRefreshLayout.setRefreshing(loading);
        swipeRefreshLayoutEmptyView.setRefreshing(loading);
    }

    private class TimelineAdapter extends ArrayAdapter<TimelineItem> {

        private final LayoutInflater mInflater;
        private long now;

        public TimelineAdapter(Context context) {
            super(context, 0);
            mInflater = LayoutInflater.from(context);
            now = System.currentTimeMillis();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup view = null;

            if (convertView == null) {
                view = (ViewGroup) mInflater.inflate(R.layout.list_item_timeline, parent, false);
            } else {
                view = (ViewGroup) convertView;
            }
            TimelineItem item = getItem(position);
            long time = item.getTime().getTime();
            String timeSpanString = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.FORMAT_ABBREV_TIME).toString();
            if (item.getType().equals(TimelineItem.TimelineItemType.RIDE_CREATED)) {
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.placeholder);
                ((TextView) view.findViewById(R.id.timeline_activity_text)).setText("New Ride offer to");

            } else if (item.getType().equals(TimelineItem.TimelineItemType.RIDE_SEARCHED)) {
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.placeholder);
                ((TextView) view.findViewById(R.id.timeline_activity_text)).setText("User searched for a Ride to");

            } else if (item.getType().equals(TimelineItem.TimelineItemType.RIDE_REQUEST)) {
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.placeholder);
                ((TextView) view.findViewById(R.id.timeline_activity_text)).setText("Request received for a ride to");

            }
            ((TextView) view.findViewById(R.id.timeline_location_text)).setText(item.getDestination());
            ((TextView) view.findViewById(R.id.timeline_time_text)).setText(timeSpanString);
            return view;
        }
    }

    protected class GetAddressTask extends AsyncTask<List<TimelineItem>, Void, List<TimelineItem>> {

        public GetAddressTask() {
            super();
        }

        @Override
        protected List<TimelineItem> doInBackground(List<TimelineItem>... params) {
            List<TimelineItem> items = params[0];
            List<TimelineItem> nearbyItems = new ArrayList<TimelineItem>();

            if (!isCancelled()) {
                for (TimelineItem item : items) {
                    String locationName = item.getDeparture();
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
                        item.setLat(address.getLatitude());
                        item.setLng(address.getLongitude());

                        if (LocationUtil.haversineDistance(
                                mCurrentLocation.latitude,
                                mCurrentLocation.longitude,
                                item.getLat(),
                                item.getLng()) < MAX_DISTANCE) {
                            nearbyItems.add(item);
                        }
                    }
                }
            }
            return nearbyItems;
        }

        @Override
        protected void onPostExecute(List<TimelineItem> result) {
            setLoading(false);
            mTimeline = result;
            Collections.sort(mTimeline, mLocationComparator);
            refreshList();
        }
    }

    private void refreshList() {
        Log.e(TAG, "In refresh list");
        mAdapter.clear();
        mAdapter.addAll(mTimeline);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGetAddressTask != null)
            mGetAddressTask.cancel(true);
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

    private void dump(List<TimelineItem> list) {
        for (TimelineItem item : list) {
            Log.d(TAG, item.getDeparture() + ":" + item.getDestination());
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setLoading(false);
            }
        }, 5000);
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
}
