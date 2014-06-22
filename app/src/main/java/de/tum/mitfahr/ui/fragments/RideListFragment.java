package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.networking.models.Ride;

/**
 * Authored by abhijith on 21/06/14.
 */
public class RideListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_RIDES = "rides";

    private static final String[] songs = {"1983... (A Merman I Should Turn to Be)", "51st Anniversary", "All Along The Watchtower", "Angel", "Are You Experienced?", "Bleeding Heart", "Bold As Love", "Burning Of The Midnight Lamp",
            "Castles Made Of Sand", "Crash Landing", "Crosstown Traffic", "Dolly Dagger", "Drifting", "Fire", "Foxy Lady", "Gypsy Eyes", "Hear My Train a Comin'", "Hey Joe", "Highway Chile", "House Burning Down", "Izabella", "Let Me Move You",
            "Little Wing", "Lover Man", "Machine Gun", "Manic Depression", "Mojo Man", "Mr. Bad Luck", "One Rainy Wish", "Purple Haze", "Red House", "She's So Fine", "Ships Passing In The Night", "Somewhere", "Spanish Castle Magic",
            "Stepping Stone", "Still Raining, Still Dreaming", "Stone Free", "The Wind Cries Mary", "Third Stone From The Sun", "Valleys of Neptune", "Voodoo Child (Slight Return)"};

    private ArrayList<Ride> mRides;

    @InjectView(R.id.rides_listview)
    ListView ridesList;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public static RideListFragment newInstance(List<Ride> rides) {
        RideListFragment fragment = new RideListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RIDES, (java.io.Serializable) rides);
        fragment.setArguments(args);
        return fragment;
    }

    public static RideListFragment newInstance() {
        RideListFragment fragment = new RideListFragment();
        return fragment;
    }

    public RideListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRides = (ArrayList<Ride>) getArguments().getSerializable(ARG_RIDES);
        }
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
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //searchResultsList.addHeaderView(mBlankHeader, null, false);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_rides, R.id.rides_to_text, songs);
        RideAdapterTest adapter = new RideAdapterTest(getActivity());
        adapter.addAll(songs);
        ridesList.setAdapter(adapter);
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

    class RideAdapterTest extends ArrayAdapter<String> {

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
            String text = getItem(position);
            ((ImageView) view.findViewById(R.id.ride_location_image)).setBackgroundResource(R.drawable.list_image_placeholder);
            ((TextView) view.findViewById(R.id.rides_to_text)).setText(text);
            return view;
        }
    }
}
