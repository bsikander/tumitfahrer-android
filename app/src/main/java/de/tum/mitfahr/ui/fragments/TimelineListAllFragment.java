package de.tum.mitfahr.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.MainActivity;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.TimelineItem;

/**
 * Authored by abhijith on 21/06/14.
 */
public class TimelineListAllFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = TimelineListAllFragment.class.getName();
    @InjectView(R.id.rides_listview)
    ListView timelineListView;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.swipeRefreshLayout_emptyView)
    SwipeRefreshLayout swipeRefreshLayoutEmptyView;
    @InjectView(R.id.button_floating_action)
    FloatingActionButton floatingActionButton;
    private List<TimelineItem> mTimeline = new ArrayList<TimelineItem>();
    private TimelineAdapter mTimelineAdapter;
    private AlphaInAnimationAdapter mAdapter;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TimelineItem clickedItem = mTimeline.get(position);

            if (!TimelineItem.TimelineItemType.RIDE_SEARCHED.equals(clickedItem.getType())) {
                Ride ride = clickedItem.getRide();
                if (ride != null) {
                    Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                    intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, ride);
                    startActivity(intent);
                }
            }
        }
    };

    public TimelineListAllFragment() {
    }

    public static TimelineListAllFragment newInstance() {
        TimelineListAllFragment fragment = new TimelineListAllFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline_list, container, false);
        ButterKnife.inject(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue1,
                R.color.blue2,
                R.color.blue3);

        swipeRefreshLayoutEmptyView.setOnRefreshListener(this);
        swipeRefreshLayoutEmptyView.setColorSchemeResources(R.color.blue1,
                R.color.blue2,
                R.color.blue3);

        floatingActionButton.attachToListView(timelineListView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getNavigationDrawerFragment().selectItem(4);
            }
        });

        timelineListView.setEmptyView(swipeRefreshLayoutEmptyView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTimelineAdapter = new TimelineAdapter(getActivity());
        mAdapter = new AlphaInAnimationAdapter(mTimelineAdapter);
        mAdapter.setAbsListView(timelineListView);
        timelineListView.setAdapter(mAdapter);
        timelineListView.setOnItemClickListener(mItemClickListener);
        setLoading(true);
    }

    public void setTimelineItems(List<TimelineItem> timelineItems) {
        setLoading(false);
        mTimeline = timelineItems;
        Collections.sort(mTimeline);
        refreshList();
    }

    private void refreshList() {
        Log.e(TAG, "In refresh list");
        mTimelineAdapter.clear();
        mTimelineAdapter.addAll(mTimeline);
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
        setLoading(true);
        TUMitfahrApplication.getApplication(getActivity()).getActivitiesService().getActivities();
    }

    private void dump(List<TimelineItem> list) {
        for (TimelineItem item : list) {
            Log.d(TAG, item.getDeparture() + ":" + item.getDestination());
        }
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
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.ic_driver);
                if (item.getRide().isRideRequest())
                    ((TextView) view.findViewById(R.id.timeline_activity_text)).setText(R.string.new_ride_request);
                else
                    ((TextView) view.findViewById(R.id.timeline_activity_text)).setText(R.string.new_ride_offer);

            } else if (item.getType().equals(TimelineItem.TimelineItemType.RIDE_SEARCHED)) {
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.ic_search_white_24dp);
                ((TextView) view.findViewById(R.id.timeline_activity_text)).setText(R.string.user_search_ride);

            } else if (item.getType().equals(TimelineItem.TimelineItemType.RIDE_REQUEST)) {
                ((ImageView) view.findViewById(R.id.timeline_type_image)).setImageResource(R.drawable.ic_passenger);
                ((TextView) view.findViewById(R.id.timeline_activity_text)).setText(R.string.request_received);

            }
            ((TextView) view.findViewById(R.id.timeline_location_text)).setText(item.getDestination());
            ((TextView) view.findViewById(R.id.timeline_time_text)).setText(timeSpanString);
            return view;
        }
    }

}
