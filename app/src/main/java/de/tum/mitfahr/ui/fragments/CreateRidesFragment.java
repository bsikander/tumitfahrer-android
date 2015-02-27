package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
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
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.adapters.LocationAutoCompleteAdapter;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.MultiDatePickerDialog;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.StringHelper;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by Abhijith on 22/05/14.
 */
public class CreateRidesFragment extends AbstractNavigationFragment implements
        CalendarDatePickerDialog.OnDateSetListener,
        RadialTimePickerDialog.OnTimeSetListener,
        MultiDatePickerDialog.MultiDatePickerDialogListener {

    public static final String ARG_OFFER_RIDE = "offerRide";

    public static final int RIDE_TYPE_CAMPUS = 0;
    private int mRideType = RIDE_TYPE_CAMPUS;
    public static final int RIDE_TYPE_ACTIVITY = 1;
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_REPEAT_DATE_PICKER = "repeatPickerDialogFragment";

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
    @InjectView(R.id.repeatButton)
    Button repeatButton;


    private User mCurrentUser;
    private boolean offerRideFlag;
    private Ride mOfferRideExtra;
    private int mHourOfDeparture;
    private int mMinuteOfDeparture;
    private int mYearOfDeparture;
    private int mMonthOfDeparture;
    private int mDayOfDeparture;
    private boolean driver = false;
    private List<Date> mRepeatDates = null;
    private ArrayAdapter<CharSequence> mRideTypeAdapter;
    private Handler mCreateButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            offerRideButton.setProgress(0);
            offerRideButton.setClickable(true);
        }
    };

    public CreateRidesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CreateRidesFragment newInstance(int sectionNumber, Ride offerRideExtra) {
        CreateRidesFragment fragment = new CreateRidesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        if (offerRideExtra != null) {
            Log.d("Create:newInstance: ", offerRideExtra.toString());
            args.putSerializable(ARG_OFFER_RIDE, offerRideExtra);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOfferRideExtra = (Ride) bundle.getSerializable(ARG_OFFER_RIDE);
            if (mOfferRideExtra != null)
                Log.d("Create:onCreate: ", mOfferRideExtra.toString());
        }
        mHourOfDeparture = calendar.get(Calendar.HOUR_OF_DAY);
        mMinuteOfDeparture = calendar.get(Calendar.MINUTE);
        mYearOfDeparture = calendar.get(Calendar.YEAR);
        mMonthOfDeparture = calendar.get(Calendar.MONTH);
        mDayOfDeparture = calendar.get(Calendar.DAY_OF_MONTH);
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
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
                        mRepeatDates = null;
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

        if (mOfferRideExtra != null) {
            populateUI();
        }
    }

    private void populateUI() {
        destinationText.setText(mOfferRideExtra.getDestination());
        departureText.setText(mOfferRideExtra.getDeparturePlace());
        meetingText.setText(mOfferRideExtra.getMeetingPoint());
        if (mOfferRideExtra.isRideRequest()) {
            requestTypeSegmentedGroup.check(R.id.radioButtonPassenger);
        } else {
            requestTypeSegmentedGroup.check(R.id.radioButtonDriver);
            seatsText.setText(mOfferRideExtra.getFreeSeats());
        }
        mRideType = mOfferRideExtra.getRideType();
        rideTypeSpinner.setSelection(mRideType);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date departureTime = new Date();
        try {
            departureTime = outputFormat.parse(mOfferRideExtra.getDepartureTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(departureTime);
        mHourOfDeparture = calendar.get(Calendar.HOUR_OF_DAY);
        mMinuteOfDeparture = calendar.get(Calendar.MINUTE);
        mYearOfDeparture = calendar.get(Calendar.YEAR);
        mMonthOfDeparture = calendar.get(Calendar.MONTH);
        mDayOfDeparture = calendar.get(Calendar.DAY_OF_MONTH);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        String dateTimeString = dateFormat.format(calendar.getTime());

        String[] dateTime = dateTimeString.split(",");

        pickTimeButton.setText(dateTime[1]);
        pickDateButton.setText(dateTime[0]);

        requestTypeSegmentedGroup.check(R.id.radioButtonDriver);
        seatsText.setFocusableInTouchMode(true);
        seatsText.requestFocus();

    }

    private void updateLayoutForDriverAndPassenger() {
        if (driver) {
            seatsTextContainer.setVisibility(View.VISIBLE);
            repeatButton.setVisibility(View.VISIBLE);
        } else {
            seatsTextContainer.setVisibility(View.GONE);
            repeatButton.setVisibility(View.GONE);
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
            meetingText.setError("Required");
            return;
        } else if (driver && StringHelper.isBlank(seatsText.getText().toString())) {
            seatsText.setError("Required");
            return;
        }
        offerRideButton.setClickable(false);
        String departure = departureText.getText().toString();
        String destination = destinationText.getText().toString();
        String meetingPoint = meetingText.getText().toString();
        int freeSeats = 0;
        if (!StringHelper.isBlank(seatsText.getText().toString()))
            freeSeats = Integer.parseInt(seatsText.getText().toString());
        String dateTime = getFormattedDate();
        int rideType = rideTypeSpinner.getSelectedItemPosition();
        String isDriver = (driver) ? "1" : "0";

        if (!StringHelper.isBlank(departure) && !StringHelper.isBlank(destination) && !StringHelper.isBlank(meetingPoint) && !StringHelper.isBlank(dateTime)) {
            offerRideButton.setProgress(50);

            TUMitfahrApplication.getApplication(getActivity()).getRidesService()
                    .offerRide(departure, destination, meetingPoint,
                            freeSeats, dateTime, rideType,
                            isDriver, mCurrentUser.getCar(), getRepeatDates());
        }
    }

    @OnClick(R.id.repeatButton)
    public void showRepeatDatePicker() {
        MultiDatePickerDialog multiDatePickerDialog = MultiDatePickerDialog.newInstance(this);
        multiDatePickerDialog.show(getChildFragmentManager(), FRAG_REPEAT_DATE_PICKER);
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

    public List<String> getRepeatDates() {
        if (mRepeatDates == null) {
            return null;
        }
        List<String> repeatDateString = new ArrayList<String>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:mm:yyyy");
        for (Date date : mRepeatDates) {
            String[] dateTime = simpleDateFormat.format(date).split(":");
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            calendar.set(Calendar.HOUR_OF_DAY, mHourOfDeparture);
            calendar.set(Calendar.MINUTE, mMinuteOfDeparture);
            calendar.set(Calendar.SECOND, 0); // I just set them to 0
            calendar.set(Calendar.YEAR, Integer.parseInt(dateTime[2]));
            calendar.set(Calendar.MONTH, Integer.parseInt(dateTime[1]));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateTime[0]));
            outputFormat.setTimeZone(TimeZone.getDefault());
            repeatDateString.add(outputFormat.format(calendar.getTime()));
        }
        return repeatDateString.size() > 0 ? repeatDateString : null;
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
            if (event.getRide() != null) {
                Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, event.getRide());
                startActivity(intent);
            }
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
        String dateString = Integer.toString(dayOfMonth) + "-" + Integer.toString(monthOfYear + 1) + "-" + Integer.toString(year);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");

        LocalDate setDate = formatter.parseLocalDate(dateString);
        LocalDate localDate = LocalDate.fromCalendarFields(Calendar.getInstance());

        if (setDate.isBefore(localDate)) {
            Toast.makeText(getActivity(), "Cannot create ride in the past.Select another date.", Toast.LENGTH_SHORT).show();
            return;
        }

        mYearOfDeparture = year;
        mMonthOfDeparture = monthOfYear;
        mDayOfDeparture = dayOfMonth;
        Log.e("MONTH:", String.valueOf(mMonthOfDeparture));

        String date = String.format("%02d/%02d/" + year, dayOfMonth, mMonthOfDeparture + 1);
        pickDateButton.setText(date);

    }

    @Override
    public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute) {
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

    @Override
    public void onMultiDatePicked(DialogFragment dialog, List<Date> selectedDates) {
        if (selectedDates != null && selectedDates.size() > 1) {
            mRepeatDates = selectedDates;
            SimpleDateFormat dt = new SimpleDateFormat("dd/mm/yyyy");
            String repeatString = "";
            for (Date date : selectedDates) {
                String dateStr = dt.format(date);
                repeatString = repeatString + dateStr + " ; ";
            }
            repeatButton.setText("Repeat on : " + repeatString);
        }
    }
}
