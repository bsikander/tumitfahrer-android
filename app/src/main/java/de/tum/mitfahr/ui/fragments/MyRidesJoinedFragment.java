package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.MyRidesAsPassengerEvent;
import de.tum.mitfahr.networking.models.Ride;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Authored by abhijith on 22/06/14.
 */
public class MyRidesJoinedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Ride> mRidesAsDriver;

    @InjectView(R.id.list)
    StickyListHeadersListView ridesList;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public static MyRidesJoinedFragment newInstance() {
        MyRidesJoinedFragment fragment = new MyRidesJoinedFragment();
        return fragment;
    }

    public MyRidesJoinedFragment() {
    }

    private void fetchRides() {
        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getMyRidesAsPassenger();
    }

    @Subscribe
    public void onGetMyRidesAsPassengerResult(MyRidesAsPassengerEvent result) {
        if (result.getType() == MyRidesAsPassengerEvent.Type.GET_SUCCESSFUL) {
            RideAdapterTest adapter = new RideAdapterTest(getActivity());
            adapter.addAll(result.getResponse().getRides());
            ridesList.setAdapter(adapter);
        } else if (result.getType() == MyRidesAsPassengerEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchRides();
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
        return rootView;
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

            ((ImageView) view.findViewById(R.id.my_rides_location_image)).setImageResource(R.drawable.list_image_placeholder);
            ((TextView) view.findViewById(R.id.my_rides_from_text)).setText(ride.getDeparturePlace());
            ((TextView) view.findViewById(R.id.my_rides_to_text)).setText(ride.getDestination());
            ((TextView) view.findViewById(R.id.my_rides_time_text)).setText(ride.getDepartureTime());
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
            String headerText = Integer.toString(position);
            holder.text.setText(headerText);
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return position;
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
}
