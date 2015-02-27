package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.adapters.LocationAutoCompleteAdapter;
import de.tum.mitfahr.events.SearchEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.SearchResultsActivity;
import de.tum.mitfahr.util.StringHelper;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by abhijith on 22/05/14.
 */
public class SearchFragment extends AbstractNavigationFragment implements CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";


    public static final int RIDE_TYPE_CAMPUS = 0;
    public static final int RIDE_TYPE_ACTIVITY = 1;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private int mRideType = RIDE_TYPE_CAMPUS;
    private int mFromRadius = 15;
    private int mToRadius = 15;
    private int mHourOfDeparture;
    private int mMinuteOfDeparture;
    private int mYearOfDeparture;
    private int mMonthOfDeparture;
    private int mDayOfDeparture;

    private Handler mSearchButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            searchButton.setProgress(0);
            searchButton.setClickable(true);
        }
    };

    @InjectView(R.id.fromSearchEditText)
    AutoCompleteTextView fromText;

    @InjectView(R.id.toSearchEditText)
    AutoCompleteTextView toText;

    @InjectView(R.id.segmentedRideType)
    SegmentedGroup rideTypeSegmentedGroup;

    @InjectView(R.id.fromRadiusSeekBar)
    SeekBar fromRadiusSeekBar;

    @InjectView(R.id.toRadiusSeekBar)
    SeekBar toRadiusSeekBar;

    @InjectView(R.id.fromRadiusTextView)
    TextView fromRadiusTextView;

    @InjectView(R.id.toRadiusTextView)
    TextView toRadiusTextView;

    @InjectView(R.id.pickTimeButton)
    Button pickTimeButton;

    @InjectView(R.id.pickDateButton)
    Button pickDateButton;

    @InjectView(R.id.searchButton)
    CircularProgressButton searchButton;

    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        mHourOfDeparture = calendar.get(Calendar.HOUR_OF_DAY);
        mMinuteOfDeparture = calendar.get(Calendar.MINUTE);
        mYearOfDeparture = calendar.get(Calendar.YEAR);
        mMonthOfDeparture = calendar.get(Calendar.MONTH);
        mDayOfDeparture = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, rootView);
        rideTypeSegmentedGroup.setTintColor(getResources().getColor(R.color.blue3));
        rideTypeSegmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonCampus:
                        mRideType = RIDE_TYPE_CAMPUS;
                        break;
                    case R.id.radioButtonActivity:
                        mRideType = RIDE_TYPE_ACTIVITY;
                        break;
                    default:
                        mRideType = RIDE_TYPE_CAMPUS;
                        break;
                }
            }
        });
        searchButton.setIndeterminateProgressMode(true);
        changeActionBarColor(getResources().getColor(R.color.blue3));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        String dateTimeString = dateFormat.format(Calendar.getInstance().getTime());

        String[] dateTime = dateTimeString.split(",");

        pickTimeButton.setText(dateTime[1]);
        pickDateButton.setText(dateTime[0]);

        fromRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFromRadius = progress;
                fromRadiusTextView.setText(mFromRadius + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        toRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mToRadius = progress;
                toRadiusTextView.setText(mToRadius + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final LocationAutoCompleteAdapter adapter = new LocationAutoCompleteAdapter(getActivity());
        fromText.setAdapter(adapter);
        toText.setAdapter(adapter);
    }


    @OnClick(R.id.searchButton)
    public void onSearchPressed(Button button) {
        if (StringHelper.isBlank(fromText.getText().toString())) {
            fromText.setError("Required");
            return;
        } else if (StringHelper.isBlank(toText.getText().toString())) {
            toText.setError("Required");
            return;
        }
        searchButton.setClickable(false);
        String from = fromText.getText().toString();
        String to = toText.getText().toString();
        String dateTime = getFormattedDate();
        searchButton.setProgress(50);
        TUMitfahrApplication.getApplication(getActivity()).getSearchService()
                .search(from, mFromRadius, to, mToRadius, dateTime, mRideType);
    }

    @OnClick(R.id.pickTimeButton)
    public void showTimePickerDialog() {
        DateTime now = DateTime.now();
        RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(this, now.getHourOfDay(), now.getMinuteOfHour(), false);
        timePickerDialog.show(getChildFragmentManager(), FRAG_TAG_TIME_PICKER);
    }

    @OnClick(R.id.pickDateButton)
    public void showDatePickerDialog() {
        FragmentManager fm = getChildFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(this, now.getYear(), now.getMonthOfYear() - 1,
                        now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    public String getFormattedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        calendar.set(Calendar.HOUR_OF_DAY, mHourOfDeparture);
        calendar.set(Calendar.MINUTE, mMinuteOfDeparture);
        calendar.set(Calendar.SECOND, 0); // I just set them to 0
        calendar.set(Calendar.YEAR, mYearOfDeparture);
        calendar.set(Calendar.MONTH, mMonthOfDeparture);
        calendar.set(Calendar.DAY_OF_MONTH, mDayOfDeparture);
        outputFormat.setTimeZone(TimeZone.getDefault());
        return outputFormat.format(calendar.getTime());
    }

    @Subscribe
    public void onSearchResults(SearchEvent event) {
        if (event.getType() == SearchEvent.Type.SEARCH_SUCCESSFUL) {
            String from = fromText.getText().toString().trim();
            String to = toText.getText().toString().trim();
            searchButton.setProgress(100);

            List<Ride> rideResults = event.getResponse().getRides();
            if (rideResults.size() > 0) {
                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                intent.putExtra(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_RIDES, (java.io.Serializable) rideResults);
                intent.putExtra(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_FROM, from);
                intent.putExtra(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_TO, to);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "No results found!", Toast.LENGTH_SHORT).show();
            }

        } else if (event.getType() == SearchEvent.Type.SEARCH_FAILED) {
            searchButton.setProgress(-1);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mSearchButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {

        String dateString = Integer.toString(dayOfMonth) + "-" + Integer.toString(monthOfYear + 1) + "-" + Integer.toString(year);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");

        LocalDate setDate = formatter.parseLocalDate(dateString);
        LocalDate localDate = LocalDate.fromCalendarFields(Calendar.getInstance());

        if(setDate.isBefore(localDate)){
            Toast.makeText(getActivity(),"Cannot create ride in the past.Select another date.",Toast.LENGTH_SHORT).show();
            return;
        }

        mYearOfDeparture = year;
        mMonthOfDeparture = monthOfYear;
        mDayOfDeparture = dayOfMonth;
        Log.e("MONTH:", String.valueOf(mMonthOfDeparture));
        String date = String.format("%02d/%02d/" + year, dayOfMonth, mMinuteOfDeparture + 1);
        pickDateButton.setText(date);
    }

    @Override
    public void onTimeSet(RadialTimePickerDialog radialPickerLayout, int hourOfDay, int minute) {
        mHourOfDeparture = hourOfDay;
        mMinuteOfDeparture = minute;
        if (hourOfDay > 12) {
            hourOfDay = hourOfDay - 12;
            String time = String.format("%02d:%02d pm", hourOfDay, minute);
            pickTimeButton.setText(time);
        } else {
            String time = String.format("%02d:%02d am", hourOfDay, minute);
            pickTimeButton.setText(time);
        }
    }

}
