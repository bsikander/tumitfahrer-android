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
import de.tum.mitfahr.events.GetRidesPageEvent;
import de.tum.mitfahr.events.MyRidesAsDriverEvent;
import de.tum.mitfahr.events.MyRidesAsPassengerEvent;
import de.tum.mitfahr.events.MyRidesEvent;
import de.tum.mitfahr.events.MyRidesPastEvent;
import de.tum.mitfahr.networking.models.Ride;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Authored by abhijith on 22/06/14.
 */
public class MyRidesListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_TYPE = "type";


    private static final String[] songs = {"1983... (A Merman I Should Turn to Be)", "51st Anniversary", "All Along The Watchtower", "Angel", "Are You Experienced?", "Bleeding Heart", "Bold As Love", "Burning Of The Midnight Lamp",
            "Castles Made Of Sand", "Crash Landing", "Crosstown Traffic", "Dolly Dagger", "Drifting", "Fire", "Foxy Lady", "Gypsy Eyes", "Hear My Train a Comin'", "Hey Joe", "Highway Chile", "House Burning Down", "Izabella", "Let Me Move You",
            "Little Wing", "Lover Man", "Machine Gun", "Manic Depression", "Mojo Man", "Mr. Bad Luck", "One Rainy Wish", "Purple Haze", "Red House", "She's So Fine", "Ships Passing In The Night", "Somewhere", "Spanish Castle Magic",
            "Stepping Stone", "Still Raining, Still Dreaming", "Stone Free", "The Wind Cries Mary", "Third Stone From The Sun", "Valleys of Neptune", "Voodoo Child (Slight Return)"};

    private ArrayList<Ride> mRides;
    private Type mType;

    public enum Type {
        MY_RIDES_PAST,
        MY_RIDES_JOINED,
        MY_RIDES_CREATED
    }

    @InjectView(R.id.list)
    StickyListHeadersListView ridesList;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public static MyRidesListFragment newInstance(Type type) {
        MyRidesListFragment fragment = new MyRidesListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, (java.io.Serializable) type);
        fragment.setArguments(args);
        return fragment;
    }

    public MyRidesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = (Type) getArguments().getSerializable(ARG_TYPE);
        }
        fetchRides();
    }

    private void fetchRides() {
        if (mType == Type.MY_RIDES_PAST)
            TUMitfahrApplication.getApplication(getActivity()).getRidesService().getMyRidesPast();
        else if (mType == Type.MY_RIDES_CREATED)
            TUMitfahrApplication.getApplication(getActivity()).getRidesService().getMyRidesAsDriver();
        else if (mType == Type.MY_RIDES_JOINED)
            TUMitfahrApplication.getApplication(getActivity()).getRidesService().getMyRidesAsPassenger();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //searchResultsList.addHeaderView(mBlankHeader, null, false);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_rides, R.id.rides_to_text, songs);
//        RideAdapterTest adapter = new RideAdapterTest(getActivity());
//        //adapter.addAll(songs);
//        ridesList.setAdapter(adapter);
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
    public void onGetMyRidesPageResult(GetRidesPageEvent result){
        if (result.getType() == GetRidesPageEvent.Type.GET_SUCCESSFUL) {
            RideAdapterTest adapter = new RideAdapterTest(getActivity());
            adapter.addAll(result.getResponse().getRides());
            ridesList.setAdapter(adapter);
        } else if (result.getType() == GetRidesPageEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onGetMyRidesPastResult(MyRidesPastEvent result){
        if (result.getType() == MyRidesPastEvent.Type.GET_SUCCESSFUL) {
            RideAdapterTest adapter = new RideAdapterTest(getActivity());
            adapter.addAll(result.getResponse().getRides());
            Log.e("PAST RIDE OBJECT",result.getResponse().getRides().toString());
            ridesList.setAdapter(adapter);
        } else if (result.getType() == MyRidesPastEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onGetMyRidesAsDriverResult(MyRidesAsDriverEvent result){
        if (result.getType() == MyRidesAsDriverEvent.Type.GET_SUCCESSFUL) {
            RideAdapterTest adapter = new RideAdapterTest(getActivity());
            Log.e("DRIVER RIDE OBJECT",result.getResponse().getRides().toString());
            adapter.addAll(result.getResponse().getRides());
            ridesList.setAdapter(adapter);
        } else if (result.getType() == MyRidesAsDriverEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onGetMyRidesAsPassengerResult(MyRidesAsPassengerEvent result){
        if (result.getType() == MyRidesAsPassengerEvent.Type.GET_SUCCESSFUL) {
            RideAdapterTest adapter = new RideAdapterTest(getActivity());
            Log.e("PASSENGERS RIDE OBJECT",result.getResponse().getRides().toString());
            adapter.addAll(result.getResponse().getRides());
            ridesList.setAdapter(adapter);
        } else if (result.getType() == MyRidesAsPassengerEvent.Type.GET_FAILED) {
            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
        }
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
            String headerText = "" + songs[position].subSequence(0, 1).charAt(0);
            holder.text.setText(headerText);
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return songs[position].subSequence(0, 1).charAt(0);
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
