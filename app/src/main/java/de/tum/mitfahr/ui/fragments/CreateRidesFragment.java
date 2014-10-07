package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.adapters.LocationAutoCompleteAdapter;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.util.StringHelper;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by abhijith on 22/05/14.
 */
public class CreateRidesFragment extends AbstractNavigationFragment implements CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";

    public static final int RIDE_TYPE_CAMPUS = 0;
    public static final int RIDE_TYPE_ACTIVITY = 1;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CreateRidesFragment newInstance(int sectionNumber) {
        CreateRidesFragment fragment = new CreateRidesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateRidesFragment() {
    }

    @InjectView(R.id.segmentedRequestType)
    SegmentedGroup requestTypeSegmentedGroup;

    @InjectView(R.id.departureText)
    AutoCompleteTextView departureText;

    @InjectView(R.id.destinationText)
    AutoCompleteTextView destinationText;

    @InjectView(R.id.meetingText)
    EditText meetingText;

    @InjectView(R.id.seatsText)
    EditText seatsText;

    @InjectView(R.id.seatsTextContainer)
    View seatsTextContainer;

    @InjectView(R.id.rideTypeSpinner)
    Spinner rideTypeSpinner;

    @InjectView(R.id.offerRideButton)
    CircularProgressButton offerRideButton;

    @InjectView(R.id.pickTimeButton)
    Button pickTimeButton;

    @InjectView(R.id.pickDateButton)
    Button pickDateButton;

    private int mHourOfDeparture;
    private int mMinuteOfDeparture;
    private int mYearOfDeparture;
    private int mMonthOfDeparture;
    private int mDayOfDeparture;
    private int mRideType = RIDE_TYPE_CAMPUS;
    private boolean driver = false;
    private ArrayAdapter<CharSequence> mRideTypeAdapter;
    private Handler mCreateButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            offerRideButton.setProgress(0);
            offerRideButton.setClickable(true);
        }
    };

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
        View rootView = inflater.inflate(R.layout.fragment_create_rides, container, false);
        ButterKnife.inject(this, rootView);
        requestTypeSegmentedGroup.setTintColor(getResources().getColor(R.color.blue3));
        requestTypeSegmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonDriver:
                        driver = true;
                        updateLayoutForDriverAndPassenger();
                        break;
                    case R.id.radioButtonPassenger:
                        driver = false;
                        updateLayoutForDriverAndPassenger();
                        break;
                    default:
                        driver = false;
                        updateLayoutForDriverAndPassenger();
                        break;
                }
            }
        });
        offerRideButton.setIndeterminateProgressMode(true);
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

        mRideTypeAdapter = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getTextArray(R.array.ride_array));
        mRideTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rideTypeSpinner.setAdapter(mRideTypeAdapter);
        rideTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mRideType = RIDE_TYPE_CAMPUS;
                } else {
                    mRideType = RIDE_TYPE_ACTIVITY;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mRideType = RIDE_TYPE_CAMPUS;
            }
        });
        final LocationAutoCompleteAdapter adapter = new LocationAutoCompleteAdapter(getActivity());
        departureText.setAdapter(adapter);
        destinationText.setAdapter(adapter);
    }

    private void updateLayoutForDriverAndPassenger() {
        if (driver) {
            seatsTextContainer.setVisibility(View.VISIBLE);
        } else {
            seatsTextContainer.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.offerRideButton)
    public void onOfferRidePressed(Button button) {
        if (StringHelper.isBlank(departureText.getText().toString())) {
            departureText.setError("Required");
            return;
        } else if (StringHelper.isBlank(destinationText.getText().toString())) {
            destinationText.setError("Required");
            return;
        } else if (StringHelper.isBlank(meetingText.getText().toString())) {
            destinationText.setError("Required");
            return;
        } else if (driver && StringHelper.isBlank(seatsText.getText().toString())) {
            seatsText.setError("Required");
            return;
        }
        offerRideButton.setClickable(false);
        String departure = departureText.getText().toString();
        String destination = destinationText.getText().toString();
        String meetingPoint = meetingText.getText().toString();
        String freeSeats = seatsText.getText().toString();
        String dateTime = getFormattedDate();
        int rideType = rideTypeSpinner.getSelectedItemPosition();
        if (departure != "" && destination != "" && meetingPoint != ""
                && freeSeats != "" && dateTime != "") {
            offerRideButton.setProgress(50);// showing working state
            TUMitfahrApplication.getApplication(getActivity()).getRidesService()
                    .offerRide(departure, destination, meetingPoint, freeSeats, dateTime, rideType, driver);
        }
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


    @Subscribe
    public void onOfferRideEvent(OfferRideEvent event) {
        if (event.getType() == OfferRideEvent.Type.RIDE_ADDED) {
            Toast.makeText(getActivity(), "Ride Created", Toast.LENGTH_SHORT).show();
            offerRideButton.setProgress(100);
        } else if (event.getType() == OfferRideEvent.Type.OFFER_RIDE_FAILED) {
            Toast.makeText(getActivity(), "Offering Ride Failed! Please check credentials and try again.",
                    Toast.LENGTH_SHORT).show();
            offerRideButton.setProgress(-1);
        } else {
            offerRideButton.setProgress(0);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mCreateButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        mYearOfDeparture = year;
        mMonthOfDeparture = monthOfYear + 1;
        mDayOfDeparture = dayOfMonth;
        Log.e("MONTH:", String.valueOf(mMonthOfDeparture));
        String date = String.format("%02d/%02d/" + year, dayOfMonth, monthOfYear + 1);
        pickDateButton.setText(date);

    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
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
