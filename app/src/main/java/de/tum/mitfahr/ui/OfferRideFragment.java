package de.tum.mitfahr.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.OfferRideFailedEvent;
import de.tum.mitfahr.events.RideAddedEvent;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfferRideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfferRideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

    @InjectView(R.id.dateTimeEditText)
    EditText dateTimeText;

    @InjectView(R.id.offerRideButton)
    Button offerRideButton;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick(R.id.offerRideButton)
    public void onOfferRidePressed(Button button) {
        Log.w("********", "Btn Pressed");
        /*String departure = departureText.getText().toString();
        String destination = destinationText.getText().toString();
        String meetingPoint = meetingText.getText().toString();
        String freeSeats = seatsText.getText().toString();
        String dateTime = dateTimeText.getText().toString();
        if (departure != "" && destination != "" && meetingPoint != ""
                && freeSeats != "" && dateTime != "") {
            TUMitfahrApplication.getApplication(mContext).getRidesService()
                    .offerRide(departure, destination, meetingPoint, freeSeats, dateTime);
        }*/
    }

    @Subscribe
    public void onRideAdded(RideAddedEvent event) {
        Log.w("*********", "RIDE ADDED.....");
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void onOfferRideFailed(OfferRideFailedEvent event) {
        Toast.makeText(mContext, "Offering Ride Failed! Please check credentials and try again.",
                Toast.LENGTH_SHORT).show();
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
    }

}
