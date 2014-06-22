package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

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

/**
 * Created by abhijith on 22/05/14.
 */
public class CreateRidesFragment extends AbstractNavigationFragment implements CalendarDatePickerDialog.OnDateSetListener, TimePickerDialogFragment.TimePickerDialogHandler {

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

    private static final String TAG_DATE_PICKER_FRAGMENT = "date_picker_fragment";

    @InjectView(R.id.departureText)
    AutoCompleteTextView departureText;

    @InjectView(R.id.destinationText)
    AutoCompleteTextView destinationText;

    @InjectView(R.id.meetingText)
    AutoCompleteTextView meetingText;

    @InjectView(R.id.seatsText)
    EditText seatsText;

    @InjectView(R.id.rideTypeSpinner)
    Spinner rideTypeSpinner;

    @InjectView(R.id.offerRideButton)
    Button offerRideButton;

    private int mHourOfDeparture;
    private int mMinuteOfDeparture;
    private int mYearOfDeparture;
    private int mMonthOfDeparture;
    private int mDayOfDeparture;
    private int mRideType = 0;
    private ArrayAdapter<CharSequence> mRideTypeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_rides, container, false);
        ButterKnife.inject(this, rootView);
        changeActionBarColor(getResources().getColor(R.color.blue3));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRideTypeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.ride_array, android.R.layout.simple_spinner_item);
        rideTypeSpinner.setAdapter(mRideTypeAdapter);
        rideTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRideType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mRideType = 0;
            }
        });
        final LocationAutoCompleteAdapter adapter = new LocationAutoCompleteAdapter(getActivity());
        departureText.setAdapter(adapter);
        destinationText.setAdapter(adapter);
        meetingText.setAdapter(adapter);
    }

    @OnClick(R.id.offerRideButton)
    public void onOfferRidePressed(Button button) {
        String departure = departureText.getText().toString();
        String destination = destinationText.getText().toString();
        String meetingPoint = meetingText.getText().toString();
        String freeSeats = seatsText.getText().toString();
        String dateTime = getFormattedDate();
        int rideType = rideTypeSpinner.getSelectedItemPosition();
        if (departure != "" && destination != "" && meetingPoint != ""
                && freeSeats != "" && dateTime != "") {
            TUMitfahrApplication.getApplication(getActivity()).getRidesService()
                    .offerRide(departure, destination, meetingPoint, freeSeats, dateTime, rideType);
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
        TimePickerBuilder timePickerBuilder = new TimePickerBuilder()
                .setFragmentManager(getChildFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                .setTargetFragment(CreateRidesFragment.this);
        timePickerBuilder.show();
    }

    @OnClick(R.id.pickDateButton)
    public void showDatePickerDialog() {
        FragmentManager fm = getChildFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(this, now.getYear(), now.getMonthOfYear() - 1,
                        now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, TAG_DATE_PICKER_FRAGMENT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        mYearOfDeparture = year;
        mMonthOfDeparture = monthOfYear;
        mDayOfDeparture = dayOfMonth;
    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        mHourOfDeparture = hourOfDay;
        mMinuteOfDeparture = minute;
    }

    @Subscribe
    public void onRideEvent(OfferRideEvent event) {
        if (event.getType() == OfferRideEvent.Type.RIDE_ADDED) {
            Toast.makeText(getActivity(), "Ride Created", Toast.LENGTH_SHORT).show();
        } else if (event.getType() == OfferRideEvent.Type.OFFER_RIDE_FAILED) {
            Toast.makeText(getActivity(), "Offering Ride Failed! Please check credentials and try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
