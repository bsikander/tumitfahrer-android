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
import de.tum.mitfahr.events.MyRidesPastEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.RideDetailsActivity;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Authored by abhijith on 22/06/14.
 */
public class MyRidesPastFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.list)
    StickyListHeadersListView ridesListView;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;

    RideAdapterTest mAdapter;
    List<Ride> mPastRides = new ArrayList<Ride>();

    public static MyRidesPastFragment newInstance() {
        MyRidesPastFragment fragment = new MyRidesPastFragment();
        return fragment;
    }

    public MyRidesPastFragment() {
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
            Ride clickedItem = mAdapter.getItem(position);
            if (clickedItem != null) {
                Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, clickedItem);
                startActivity(intent);
            }
        }
    };

    private void fetchRides() {
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getMyRidesPast();
    }

    @Subscribe
    public void onGetMyRidesPastResult(MyRidesPastEvent result) {
        setLoading(false);
        if (result.getType() == MyRidesPastEvent.Type.GET_SUCCESSFUL) {
            mPastRides.addAll(result.getResponse().getRides());
            mAdapter.clear();
            mAdapter.addAll(result.getResponse().getRides());
            mAdapter.notifyDataSetChanged();
        } else if (result.getType() == MyRidesPastEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Failed to fetch Past rides", Toast.LENGTH_SHORT).show();
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

    class RideAdapterTest extends ArrayAdapter<Ride> implements StickyListHeadersAdapter {

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
            Ride ride = getItem(position);
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
            String headerText = "My Past Rides";
            holder.text.setText(headerText);
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return 0;
        }

        class HeaderViewHolder {
            TextView text;
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

    private void setLoading(boolean loading) {
        swipeRefreshLayout.setRefreshing(loading);
        swipeRefreshLayoutEmptyView.setRefreshing(loading);
    }
}
