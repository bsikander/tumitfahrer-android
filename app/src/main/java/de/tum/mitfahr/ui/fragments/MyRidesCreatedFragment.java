package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetUserRequestsEvent;
import de.tum.mitfahr.events.MyRidesAsDriverEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.RideRequest;
import de.tum.mitfahr.ui.RideDetailsActivity;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Authored by abhijith on 22/06/14.
 */
public class MyRidesCreatedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.list)
    StickyListHeadersListView ridesListView;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;


    public static MyRidesCreatedFragment newInstance() {
        MyRidesCreatedFragment fragment = new MyRidesCreatedFragment();
        return fragment;
    }

    List<MyCreatedRide> myCreatedRideList = new ArrayList<MyCreatedRide>();
    RideAdapterTest mAdapter;

    public MyRidesCreatedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_rides_list, container, false);
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
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new RideAdapterTest(getActivity());
        ridesListView.setAdapter(mAdapter);
        ridesListView.setOnItemClickListener(mItemClickListener);
        fetchRides();
        setLoading(true);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new android.widget.AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MyCreatedRide clickedItem = mAdapter.getItem(position);
            if (clickedItem.ride != null) {
                Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, clickedItem.ride);
                startActivity(intent);
            } else if (clickedItem.request.getRide() != null) {
                Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, clickedItem.request.getRide());
                startActivity(intent);
            }
        }
    };

    private void fetchRides() {
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getMyRidesAsDriver();
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getUserRequests();
    }

    @Subscribe
    public void onGetUserRequestsResult(GetUserRequestsEvent result) {
        setLoading(false);
        if (result.getType() == GetUserRequestsEvent.Type.GET_SUCCESSFUL) {
            if (result.getResponse().getRequests() != null) {
                for (RideRequest request : result.getResponse().getRequests()) {
                    MyCreatedRide createdRide = new MyCreatedRide(MyRideType.MY_RIDE_REQUESTS, null, request);
                    myCreatedRideList.add(createdRide);
                }
                mAdapter.clear();
                mAdapter.addAll(myCreatedRideList);
                mAdapter.notifyDataSetChanged();
            }

        } else if (result.getType() == GetUserRequestsEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Failed to fetch Rides requests", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onGetMyRidesAsDriverResult(MyRidesAsDriverEvent result) {
        setLoading(false);
        if (result.getType() == MyRidesAsDriverEvent.Type.GET_SUCCESSFUL) {
            if (result.getResponse().getRides() != null) {
                for (Ride ride : result.getResponse().getRides()) {
                    MyCreatedRide createdRide = new MyCreatedRide(MyRideType.MY_RIDES_AS_DRIVER, ride, null);
                    myCreatedRideList.add(createdRide);
                }
                mAdapter.clear();
                mAdapter.addAll(myCreatedRideList);
                mAdapter.notifyDataSetChanged();
            }
        } else if (result.getType() == MyRidesAsDriverEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Failed to fetch Rides as Driver", Toast.LENGTH_SHORT).show();
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

    class RideAdapterTest extends ArrayAdapter<MyCreatedRide> implements StickyListHeadersAdapter {

        private final LayoutInflater mInflater;

        public RideAdapterTest(Context context) {
            super(context, 0);
            final float density = context.getResources().getDisplayMetrics().density;
            mInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup view = null;

            if (convertView == null) {
                view = (ViewGroup) mInflater.inflate(R.layout.list_item_my_rides, parent, false);
            } else {
                view = (ViewGroup) convertView;
            }
            MyCreatedRide createdRide = getItem(position);
            Ride ride = null;
            if (createdRide.type == MyRideType.MY_RIDES_AS_DRIVER) {
                ride = createdRide.ride;
            } else {
                ride = createdRide.request.getRide();
            }

            String[] dateTime = ride.getDepartureTime().split("T");

            ((TextView) view.findViewById(R.id.my_rides_from_text)).setText(ride.getDeparturePlace());
            ((TextView) view.findViewById(R.id.my_rides_to_text)).setText(ride.getDestination());
            ((TextView) view.findViewById(R.id.my_rides_time_text)).setText(dateTime[1].substring(0, 5));
            ((TextView) view.findViewById(R.id.my_rides_date_text)).setText(dateTime[0]);

            return view;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup viewGroup) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = mInflater.inflate(R.layout.header_separator, viewGroup, false);
                holder.text = (TextView) convertView.findViewById(R.id.section_name_text);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            //set header text as first char in name
            if (getItem(position).type == MyRideType.MY_RIDES_AS_DRIVER) {
                holder.text.setText("Rides as Driver");
            } else {
                holder.text.setText("Ride Requests");
            }
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            MyCreatedRide ride = getItem(position);
            return ride.type.ordinal();
        }

        class HeaderViewHolder {
            TextView text;
        }
    }

    private class MyCreatedRide {
        MyRideType type;
        Ride ride;
        RideRequest request;

        private MyCreatedRide(MyRideType type, Ride ride, RideRequest request) {
            this.type = type;
            this.ride = ride;
            this.request = request;
        }
    }

    private enum MyRideType {
        MY_RIDES_AS_DRIVER,
        MY_RIDE_REQUESTS
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

    private void setLoading(boolean loading) {
        swipeRefreshLayout.setRefreshing(loading);
        swipeRefreshLayoutEmptyView.setRefreshing(loading);
    }
}
