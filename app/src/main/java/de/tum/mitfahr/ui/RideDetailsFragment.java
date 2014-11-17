package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.DeleteRideRequestEvent;
import de.tum.mitfahr.events.GetRideEvent;
import de.tum.mitfahr.events.JoinRequestEvent;
import de.tum.mitfahr.events.RemovePassengerEvent;
import de.tum.mitfahr.events.RespondToRequestEvent;
import de.tum.mitfahr.events.UIActionOfferRideEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.RideRequest;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.networking.panoramio.PanoramioPhoto;
import de.tum.mitfahr.util.StringHelper;
import de.tum.mitfahr.widget.NotifyingScrollView;
import de.tum.mitfahr.widget.PassengerItemView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;

public class RideDetailsFragment extends Fragment {

    private static final String RIDE = "ride";
    private static final int IMAGE_COLOR_FILTER = 0x5F000000;

    @InjectView(R.id.notifyingScrollView)
    NotifyingScrollView notifyingScrollView;

    @InjectView(R.id.details_driver_image_view)
    CircularImageView ownerImageView;

    @InjectView(R.id.details_from_text)
    TextView fromTextView;

    @InjectView(R.id.details_to_text)
    TextView toTextView;

    @InjectView(R.id.details_info)
    TextView infoTextView;

    @InjectView(R.id.ride_location_image)
    ImageView rideLocationImage;

    @InjectView(R.id.details_car)
    TextView carTextView;

    @InjectView(R.id.details_seats)
    TextView seatsTextView;

    @InjectView(R.id.details_ride_owner_name)
    TextView driverNameTextView;

    @InjectView(R.id.details_car_container)
    View carContainer;

    @InjectView(R.id.details_info_container)
    View infoContainer;

    @InjectView(R.id.details_seats_container)
    View seatsContainer;

    @InjectView(R.id.ride_owner_layout_container)
    View rideOwnerLayoutContainer;

    @InjectView(R.id.passengers_layout_container)
    View passengersLayoutContainer;

    @InjectView(R.id.requests_layout_container)
    View requestsLayoutContainer;

    @InjectView(R.id.passengers_item_container)
    LinearLayout passengersItemContainer;

    @InjectView(R.id.requests_item_container)
    LinearLayout requestsItemContainer;


    @InjectView(R.id.details_action_button)
    CircularProgressButton rideActionButton;

    @InjectView(R.id.details_progress_bar)
    SmoothProgressBar progressBar;

    private ProgressDialog mProgressDialog;

    private Ride mRide;
    private User mCurrentUser;
    private boolean userIsOwner;
    private List<RideRequest> mRideRequests = new ArrayList<RideRequest>();
    private Map<Integer, User> mRequestUserMap = new HashMap<Integer, User>();
    private Geocoder mGeocoder;


    private Drawable mActionBarBackgroundDrawable;

    private TUMitfahrApplication mApp;


    private Handler mActionButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            rideActionButton.setProgress(0);
            rideActionButton.setClickable(true);
            TUMitfahrApplication.getApplication(getActivity()).getRidesService().getRide(mRide.getId());
        }
    };
    private int mPendingRequestId;

    private enum ActionButtonState {
        REQUEST_RIDE,
        LEAVE_RIDE,
        PENDING_RIDE,
        DELETE_RIDE,
        OFFER_RIDE,
    }


    public static RideDetailsFragment newInstance(Ride ride) {
        RideDetailsFragment fragment = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(RIDE, ride);
        fragment.setArguments(args);
        return fragment;
    }

    public RideDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRide = (Ride) getArguments().getSerializable(RIDE);
        }
        mApp = TUMitfahrApplication.getApplication(getActivity());
        mGeocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_detail, container, false);
        ButterKnife.inject(this, view);
        mActionBarBackgroundDrawable = getResources().getDrawable(R.color.blue2);
        mActionBarBackgroundDrawable.setAlpha(0);
        rideLocationImage.setColorFilter(IMAGE_COLOR_FILTER);

        getActivity().getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
        notifyingScrollView.setOnScrollChangedListener(mOnScrollChangedListener);

        rideActionButton.setIndeterminateProgressMode(true);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TUMitfahrApplication.getApplication(getActivity()).getRidesService().getRide(mRide.getId());
        progressBar.progressiveStart();
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
        showData();
    }

    @Subscribe
    public void onGetRide(GetRideEvent result) {
        progressBar.progressiveStop();
        if (result.getType() == GetRideEvent.Type.GET_SUCCESSFUL) {
            mRide = result.getResponse().getRide();
            showData();
        } else if (result.getType() == GetRideEvent.Type.GET_FAILED) {
            getActivity().finish();
        }
    }

    @Subscribe
    public void onDeleteResult(DeleteRideEvent result) {
        if (result.getType() == DeleteRideEvent.Type.DELETE_SUCCESSFUL) {
            rideActionButton.setProgress(100);
            rideActionButton.setText("Delete Success");
        } else {
            rideActionButton.setProgress(-1);
            rideActionButton.setText("Delete Failed");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    getActivity().finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Subscribe
    public void onRideRequestResult(JoinRequestEvent result) {
        if (result.getType() == JoinRequestEvent.Type.REQUEST_SENT) {
            rideActionButton.setProgress(100);
            rideActionButton.setText("Request Sent");
        } else {
            rideActionButton.setProgress(-1);
            rideActionButton.setText("Request Failed");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void onRemovePassengerResult(RemovePassengerEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == RemovePassengerEvent.Type.SUCCESSFUL) {
            rideActionButton.setProgress(100);
            rideActionButton.setText("Removed Passenger");
        } else {
            rideActionButton.setProgress(-1);
            rideActionButton.setText("Remove Passenger Failed");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void onRespondToRequestResult(RespondToRequestEvent result) {
        mProgressDialog.dismiss();
        if (result.getType() == RespondToRequestEvent.Type.RESPOND_SENT) {
            rideActionButton.setProgress(100);
            rideActionButton.setText("Response Sent");
        } else {
            rideActionButton.setProgress(100);
            rideActionButton.setText("Response Sending Failed");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe
    public void onDeleteRideRequest(DeleteRideRequestEvent result) {
        if (result.getType() == DeleteRideRequestEvent.Type.RESULT) {
            rideActionButton.setProgress(100);
            rideActionButton.setText("Cancelled Ride Request");
        } else {
            rideActionButton.setProgress(100);
            rideActionButton.setText("Cancel Request Failed");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mActionButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showData() {
        fromTextView.setText(mRide.getDeparturePlace());
        toTextView.setText(mRide.getDestination());
        infoTextView.setText(mRide.getMeetingPoint());
        if (!StringHelper.isBlank(mRide.getCar())) {
            carTextView.setText(mRide.getCar());
        } else {
            carContainer.setVisibility(View.GONE);
        }
        seatsTextView.setText(Integer.toString(mRide.getFreeSeats()));

        if (mRide.getRideImageUrl() != null) {
            Picasso.with(getActivity())
                    .load(mRide.getRideImageUrl())
                    .placeholder(R.drawable.list_image_placeholder)
                    .error(R.drawable.list_image_placeholder)
                    .into(rideLocationImage);
        } else {
            new PanoramioTask().execute(mRide);
        }

        if (null != mRide.getRideOwner()) {
            String profileUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImageURL(mRide.getRideOwner().getId());
            Picasso.with(getActivity())
                    .load(profileUrl)
                    .placeholder(R.drawable.list_image_placeholder)
                    .error(R.drawable.list_image_placeholder)
                    .into(ownerImageView);
            setActionButtonState(ActionButtonState.LEAVE_RIDE);

            rideOwnerLayoutContainer.setVisibility(View.VISIBLE);
            driverNameTextView.setText(mRide.getRideOwner()
                    .getFirstName() + " " + mRide.getRideOwner().getLastName());
            rideOwnerLayoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
                    intent.putExtra(UserDetailsActivity.USER_INTENT_EXTRA, mRide.getRideOwner());
                    startActivity(intent);
                }
            });
            if (mCurrentUser.getId() == mRide.getRideOwner().getId() && !mRide.isRideRequest())
                showDataAsOwnerDriver();
            else if (mCurrentUser.getId() == mRide.getRideOwner().getId())
                showDataAsOwnerRequest();
            else if (isUserPassenger())
                showDataAsPassengerAccepted();
            else if (isUserRequestPending())
                showDataAsPassengerPending();
            else if (mRide.isRideRequest())
                showDataAsRideRequest();
            else
                showDataAsPassenger();
        }

    }

    private boolean isUserRequestPending() {
        if (mRide.getRequests().length == 0)
            return false;
        mRideRequests = Arrays.asList(mRide.getRequests());
        for (RideRequest request : mRideRequests) {
            if (request.getPassengerId() == mCurrentUser.getId()) {
                mPendingRequestId = request.getId();
                return true;
            }
        }
        return false;
    }

    private boolean isUserPassenger() {
        if (null != mRide.getPassengers() && mRide.getPassengers().length > 0) {
            for (User user : mRide.getPassengers()) {
                if (user.getId() == mCurrentUser.getId())
                    return true;
            }
            return false;
        }
        return false;
    }

    private void showDataAsPassenger() {
        userIsOwner = false;
        setActionButtonState(ActionButtonState.REQUEST_RIDE);
        if (null != mRide.getPassengers() && mRide.getPassengers().length > 0) {
            passengersLayoutContainer.setVisibility(View.VISIBLE);
            passengersItemContainer.removeAllViews();
            for (final User passenger : mRide.getPassengers()) {
                PassengerItemView passengerItem = new PassengerItemView(getActivity());
                passengerItem.setPassenger(passenger);
                passengerItem.setListener(new PassengerItemView.PassengerItemClickListener() {
                    @Override
                    public void onRemoveClicked(User passenger) {
                    }

                    @Override
                    public void onActionClicked(User passenger) {
                    }

                    @Override
                    public void onUserClicked(User passenger) {
                        //show the userpage
                        Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
                        intent.putExtra(UserDetailsActivity.USER_INTENT_EXTRA, passenger);
                        startActivity(intent);
                    }
                });
                passengerItem.setItemType(PassengerItemView.TYPE_NONE);
                passengersItemContainer.addView(passengerItem);
            }
        }
    }

    private void showDataAsRideRequest() {
        Log.e("Details", "Showing as Ride request");
        setActionButtonState(ActionButtonState.OFFER_RIDE);
    }

    private void showDataAsPassengerPending() {
        Log.e("Details", "Showing as Passenger pending");
        setActionButtonState(ActionButtonState.PENDING_RIDE);

        requestsLayoutContainer.setVisibility(View.VISIBLE);
        requestsItemContainer.removeAllViews();

        PassengerItemView passengerPendingItem = new PassengerItemView(getActivity());
        passengerPendingItem.setPassenger(mCurrentUser);
        passengerPendingItem.setItemType(PassengerItemView.TYPE_NONE);

        requestsItemContainer.addView(passengerPendingItem);
    }

    private void showDataAsPassengerAccepted() {
        Log.e("Details", "Showing as Passenger accepted");

        if (null != mRide.getPassengers() && mRide.getPassengers().length > 0) {
            passengersLayoutContainer.setVisibility(View.VISIBLE);
            passengersItemContainer.removeAllViews();
            for (final User passenger : mRide.getPassengers()) {
                PassengerItemView passengerItem = new PassengerItemView(getActivity());
                passengerItem.setPassenger(passenger);
                passengerItem.setItemType(PassengerItemView.TYPE_NONE);
                passengerItem.setListener(new PassengerItemView.PassengerItemClickListener() {
                    @Override
                    public void onRemoveClicked(User passenger) {
                    }

                    @Override
                    public void onActionClicked(User passenger) {
                    }

                    @Override
                    public void onUserClicked(User passenger) {
                        Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
                        intent.putExtra(UserDetailsActivity.USER_INTENT_EXTRA, passenger);
                        startActivity(intent);
                    }
                });
                passengersItemContainer.addView(passengerItem);
            }
        }

    }

    private void showDataAsOwnerRequest() {
        Log.e("Details", "Showing as Driver request");
        String profileUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImageURL(getActivity());
        Picasso.with(getActivity())
                .load(profileUrl)
                .placeholder(R.drawable.list_image_placeholder)
                .error(R.drawable.list_image_placeholder)
                .into(ownerImageView);
        setActionButtonState(ActionButtonState.DELETE_RIDE);
    }

    private void showDataAsOwnerDriver() {
        String profileUrl = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getProfileImageURL(getActivity());
        Picasso.with(getActivity())
                .load(profileUrl)
                .placeholder(R.drawable.list_image_placeholder)
                .error(R.drawable.list_image_placeholder)
                .into(ownerImageView);
        Log.e("Details", "Showing as Driver owner");
        userIsOwner = true;
        setActionButtonState(ActionButtonState.DELETE_RIDE);
        //We show all the passenger details and request details

        if (null != mRide.getPassengers() && mRide.getPassengers().length > 0) {
            passengersLayoutContainer.setVisibility(View.VISIBLE);
            passengersItemContainer.removeAllViews();
            for (final User passenger : mRide.getPassengers()) {
                PassengerItemView passengerItem = new PassengerItemView(getActivity());
                passengerItem.setPassenger(passenger);
                passengerItem.setItemType(PassengerItemView.TYPE_PASSENGER);
                passengerItem.setListener(new PassengerItemView.PassengerItemClickListener() {
                    @Override
                    public void onRemoveClicked(User passenger) {
                        mApp.getRidesService().removePassenger(mRide.getId(), passenger.getId());
                        mProgressDialog.show();
                    }

                    @Override
                    public void onActionClicked(User passenger) {
                    }

                    @Override
                    public void onUserClicked(User passenger) {
                        //show the userpage
                        Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
                        intent.putExtra(UserDetailsActivity.USER_INTENT_EXTRA, passenger);
                        startActivity(intent);
                    }
                });
                passengersItemContainer.addView(passengerItem);
            }
        }

        if (mRide.getRequests() != null && mRide.getRequests().length > 0) {
            Log.e("Details", "Has Requests");
            mRideRequests = Arrays.asList(mRide.getRequests());
            new GetUserFromRequestsTask(getActivity()).execute(mRideRequests);

        }
    }

    public void setActionButtonState(ActionButtonState state) {
        rideActionButton.setVisibility(View.VISIBLE);
        switch (state) {

            case REQUEST_RIDE:
                rideActionButton.setText("Request Ride");
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rideActionButton.setProgress(50);
                        rideActionButton.setClickable(false);
                        mApp.getRidesService().joinRequest(mRide.getId());
                    }
                });
                break;
            case LEAVE_RIDE:
                rideActionButton.setText("Leave Ride");
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rideActionButton.setProgress(50);
                        rideActionButton.setClickable(false);
                        mApp.getRidesService().joinRequest(mRide.getId());
                    }
                });
                break;
            case PENDING_RIDE:
                rideActionButton.setText("Cancel Request");
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Cancel Ride request?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mPendingRequestId != 0)
                                            mApp.getRidesService().deleteRideRequest(mRide.getId(), mPendingRequestId);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        // Create the AlertDialog object and return it
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
                break;
            case DELETE_RIDE:
                rideActionButton.setText("Delete Ride");
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rideActionButton.setProgress(50);
                        rideActionButton.setClickable(false);
                        mApp.getRidesService().deleteRide(mRide.getId());
                    }
                });
                break;
            case OFFER_RIDE:
                rideActionButton.setText("Offer Ride");
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BusProvider.getInstance().post(new UIActionOfferRideEvent(mRide));
                    }
                });
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class GetUserFromRequestsTask extends AsyncTask<List<RideRequest>, Void, Boolean> {

        Context localContext;

        public GetUserFromRequestsTask(Context context) {
            this.localContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.progressiveStart();
        }

        @Override
        protected Boolean doInBackground(List<RideRequest>... params) {
            List<RideRequest> rideRequests = params[0];
            for (RideRequest rideRequest : rideRequests) {
                try {
                    User passenger = TUMitfahrApplication.getApplication(localContext).getProfileService().getUserSynchronous(rideRequest.getPassengerId());
                    if (null != passenger) {
                        Log.e("Details", "Got user as ride requestor");
                        mRequestUserMap.put(rideRequest.getId(), passenger);
                    }
                } catch (RetrofitError e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            progressBar.progressiveStop();
            final TUMitfahrApplication app = TUMitfahrApplication.getApplication(localContext);
            Iterator it = mRequestUserMap.entrySet().iterator();
            if (mRequestUserMap.size() > 0) {
                requestsLayoutContainer.setVisibility(View.VISIBLE);
                requestsItemContainer.removeAllViews();
                while (it.hasNext()) {
                    final Map.Entry pairs = (Map.Entry) it.next();
                    PassengerItemView passengerItem = new PassengerItemView(getActivity());
                    passengerItem.setPassenger((User) pairs.getValue());
                    final int requestId = (Integer) pairs.getKey();
                    passengerItem.setItemType(PassengerItemView.TYPE_REQUEST);
                    passengerItem.setListener(new PassengerItemView.PassengerItemClickListener() {
                        @Override
                        public void onRemoveClicked(User passenger) {
                            //remove the user
                            app.getRidesService().respondToRequest(mRide.getId(), requestId, false);
                            mProgressDialog.show();
                        }

                        @Override
                        public void onActionClicked(User passenger) {
                            //do the action... conversation or accept
                            mProgressDialog.show();
                            app.getRidesService().respondToRequest(mRide.getId(), requestId, true);
                        }

                        @Override
                        public void onUserClicked(User passenger) {
                            //show the userpage
                            Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
                            intent.putExtra(UserDetailsActivity.USER_INTENT_EXTRA, passenger);
                            startActivity(intent);
                        }
                    });
                    requestsItemContainer.addView(passengerItem);
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
    }

    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActivity().getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };

    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = rideLocationImage.getHeight() - getActivity().getActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };

    private class PanoramioTask extends AsyncTask<Ride, Void, Ride> {

        public PanoramioTask() {
            super();
        }

        private int PHOTO_AREA = 5;

        @Override
        protected Ride doInBackground(Ride... params) {

            Ride ride = params[0];
            if (!isCancelled()) {
                String locationName = ride.getDestination();
                List<Address> addresses = null;
                try {
                    addresses = mGeocoder.getFromLocationName(locationName, 2);
                } catch (IOException exception1) {
                    exception1.printStackTrace();
                } catch (IllegalArgumentException exception2) {
                    exception2.printStackTrace();
                }
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {
                    // Get the first address
                    Address address = addresses.get(0);
                    ride.setLatitude(address.getLatitude());
                    ride.setLongitude(address.getLongitude());
                    Long longValue = Math.round(address.getLatitude());
                    int lat = Integer.valueOf(longValue.intValue());
                    longValue = Math.round(address.getLongitude());
                    int lng = Integer.valueOf(longValue.intValue());
                    try {
                        PanoramioPhoto photo = TUMitfahrApplication.getApplication(getActivity()).getPanoramioService().getPhoto(lng - PHOTO_AREA, lat - PHOTO_AREA, lng + PHOTO_AREA, lat + PHOTO_AREA);
                        if (photo != null) {
                            ride.setRideImageUrl(photo.getPhotoFileUrl());
                            publishProgress();
                        }
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
            return ride;
        }

        @Override
        protected void onPostExecute(Ride result) {
            Picasso.with(getActivity())
                    .load(mRide.getRideImageUrl())
                    .placeholder(R.drawable.list_image_placeholder)
                    .error(R.drawable.list_image_placeholder)
                    .into(rideLocationImage);
        }
    }
}