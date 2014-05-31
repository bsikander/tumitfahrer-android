package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.networking.models.Ride;


public class OfferRideFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Context mContext;

    @InjectView(R.id.departureEditText)
    EditText departureText;

    @InjectView(R.id.destinationEditText)
    EditText destinationText;

    @InjectView(R.id.seatsEditText)
    EditText seatsText;

    @InjectView(R.id.meetingEditText)
    EditText meetingText;

    @InjectView(R.id.rideTypeSpinner)
    Spinner rideTypeSpinner;

    //@InjectView(R.id.dateTimeEditText)
    //EditText dateTimeText;

    @InjectView(R.id.offerRideButton)
    Button offerRideButton;

    private OnFragmentInteractionListener mListener;
    private int mHourOfDeparture;
    private int mMinuteOfDeparture;
    private int mYearOfDeparture;
    private int mMonthOfDeparture;
    private int mDayOfDeparture;
    private int mRideType = 0;
    private ArrayAdapter<CharSequence> mRideTypeAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * @return A new instance of fragment OfferRideFragment.
     */
    // TODO: Rename and change types and number of parameters
    /*public static OfferRideFragment newInstance(String param1, String param2) {
        OfferRideFragment fragment = new OfferRideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    public OfferRideFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        ButterKnife.inject(this, view);
        return view;
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

            }
        });
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
            TUMitfahrApplication.getApplication(mContext).getRidesService()
                    .offerRide(departure, destination, meetingPoint, freeSeats, dateTime, rideType);
        }
    }

    @OnClick(R.id.pickTimeButton)
    public void showTimePickerDialog() {
        DialogFragment newFragment = TimePickerFragment.newInstance("OfferRideFragment");
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @OnClick(R.id.pickDateButton)
    public void showDatePickerDialog() {
        DialogFragment newFragment = DatePickerFragment.newInstance("OfferRideFragment");
        newFragment.show(getFragmentManager(), "datePicker");
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

    public void setTime(int hourOfDay, int minute) {
        this.mHourOfDeparture = hourOfDay;
        this.mMinuteOfDeparture = minute;
    }

    public void setDate(int day, int month, int year) {
        this.mDayOfDeparture = day;
        this.mMonthOfDeparture = month;
        this.mYearOfDeparture = year;
    }

    @Subscribe
    public void onRideAdded(OfferRideEvent event) {

        if(event.getType() == OfferRideEvent.Type.RIDE_ADDED) {
            mListener.showRideDetails(event.getRide());
        }
    }

    @Subscribe
    public void onOfferRideFailed(OfferRideEvent event) {

        if(event.getType() == OfferRideEvent.Type.OFFER_RIDE_FAILED) {
            Toast.makeText(mContext, "Offering Ride Failed! Please check credentials and try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void showRideDetails(Ride ride);
    }

}
