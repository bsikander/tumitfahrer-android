package de.tum.mitfahr.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import java.util.List;

import de.tum.mitfahr.R;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.fragments.SearchResultsFragment;

/**
 * Created by abhijith on 02/10/14.
 */
public class SearchResultsActivity extends Activity {

    public static final String SEARCH_RIDE_RESULT_INTENT_RIDES = "search_ride_results";
    public static final String SEARCH_RIDE_RESULT_INTENT_FROM = "search_ride_from";
    public static final String SEARCH_RIDE_RESULT_INTENT_TO = "search_ride_to";


    private List<Ride> mRides = null;
    private String mTo;
    private String mFrom;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        changeActionBarColor(R.color.blue2);

        Intent intent = getIntent();
        if (intent.hasExtra(SEARCH_RIDE_RESULT_INTENT_RIDES)) {
            mRides = (List<Ride>) intent.getSerializableExtra(SEARCH_RIDE_RESULT_INTENT_RIDES);
            mTo = intent.getStringExtra(SEARCH_RIDE_RESULT_INTENT_TO);
            mFrom = intent.getStringExtra(SEARCH_RIDE_RESULT_INTENT_FROM);
        } else {
            finish();
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, SearchResultsFragment.newInstance(mRides, mFrom, mTo))
                    .commit();
        }
    }

    public void changeActionBarColor(int newColor) {
        Drawable newDrawable = new ColorDrawable((newColor));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newDrawable.setCallback(drawableCallback);
        } else {
            getActionBar().setBackgroundDrawable(newDrawable);
        }
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            mHandler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            mHandler.removeCallbacks(what);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
