package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.GetRideEvent;
import de.tum.mitfahr.events.JoinRequestEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.RideRequest;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.util.StringHelper;
import de.tum.mitfahr.widget.NotifyingScrollView;
import de.tum.mitfahr.widget.PassengerItemView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;

public class RideDetailsFragment extends Fragment {

    private static final String RIDE = "ride";

    @InjectView(R.id.notifyingScrollView)
    NotifyingScrollView notifyingScrollView;

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
    private Map<RideRequest, User> mRequestUserMap = new HashMap<RideRequest, User>();

    private Drawable mActionBarBackgroundDrawable;

    private Handler mActionButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            rideActionButton.setProgress(0);
            rideActionButton.setClickable(true);
            TUMitfahrApplication.getApplication(getActivity()).getRidesService().getRide(mRide.getId());
        }
    };


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
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_detail, container, false);
        ButterKnife.inject(this, view);
        mActionBarBackgroundDrawable = getResources().getDrawable(R.color.blue2);
        mActionBarBackgroundDrawable.setAlpha(0);

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
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
        progressBar.progressiveStart();
        showData();
    }

    @Subscribe
    public void onGetRide(GetRideEvent result) {
        progressBar.progressiveStop();
        if (result.getType() == GetRideEvent.Type.GET_SUCCESSFUL) {
            Log.e("GET", "Success");
            mRide = result.getResponse().getRide();
            showData();
        } else if (result.getType() == GetRideEvent.Type.GET_FAILED) {
            Log.e("GET", "Failure");
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
                    mActionButtonHandler.sendEmptyMessage(0);
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


    private void showData() {
        final TUMitfahrApplication app = TUMitfahrApplication.getApplication(getActivity());
        fromTextView.setText(mRide.getDeparturePlace());
        toTextView.setText(mRide.getDestination());
        infoTextView.setText(mRide.getMeetingPoint());
        if (!StringHelper.isBlank(mRide.getCar())) {
            carTextView.setText(mRide.getCar());
        } else {
            carContainer.setVisibility(View.GONE);
        }
        seatsTextView.setText(Integer.toString(mRide.getFreeSeats()));

        if (null != mRide.getRideOwner()) {
            rideOwnerLayoutContainer.setVisibility(View.VISIBLE);
            driverNameTextView.setText(mRide.getRideOwner()
                    .getFirstName() + " " + mRide.getRideOwner().getLastName());
            if (mCurrentUser.getId() == mRide.getRideOwner().getId()) {
                userIsOwner = true;
                rideActionButton.setVisibility(View.VISIBLE);
                rideActionButton.setText("Delete Ride");
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rideActionButton.setProgress(50);
                        rideActionButton.setClickable(false);
                        app.getRidesService().deleteRide(mRide.getId());
                    }
                });

                // We need to show requests
            } else if (mRide.isRideRequest()) {
                //its a ride request...button is offer ride.
                rideActionButton.setVisibility(View.VISIBLE);
                rideActionButton.setText("Offer Ride");
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO:call activity with some args.
                    }
                });
            } else {
                //not a ride request...button is request ride.
                rideActionButton.setVisibility(View.VISIBLE);
                rideActionButton.setText("Request Ride");
                rideActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rideActionButton.setProgress(50);
                        rideActionButton.setClickable(false);
                        app.getRidesService().joinRequest(mRide.getId());
                    }
                });
            }
        }

        if (null != mRide.getPassengers() && mRide.getPassengers().length > 0) {
            passengersLayoutContainer.setVisibility(View.VISIBLE);
            passengersItemContainer.removeAllViews();
            for (final User passenger : mRide.getPassengers()) {
                if (passenger.getId() == mCurrentUser.getId()) {
                    rideActionButton.setVisibility(View.VISIBLE);
                    rideActionButton.setText("Leave Ride");
                    rideActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: cancel ride request.
                        }
                    });
                }
                PassengerItemView passengerItem = new PassengerItemView(getActivity());
                passengerItem.setPassenger(passenger);
                passengerItem.setItemType(PassengerItemView.TYPE_ACCEPTED);
                passengerItem.setListener(new PassengerItemView.PassengerItemClickListener() {
                    @Override
                    public void onRemoveClicked(User passenger) {
                        app.getRidesService().removePassenger(mRide.getId(), passenger.getId());
                    }

                    @Override
                    public void onActionClicked(User passenger) {
                        //do the action... conversation or accept
                        //TODO: conversations view
                    }

                    @Override
                    public void onUserClicked(User passenger) {
                        //show the userpage
                        //TODO: Users view
                    }
                });
                passengerItem.isOwner(userIsOwner);
                passengersItemContainer.addView(passengerItem);
            }
        }
        if (userIsOwner) {
            if (mRide.getRequests() != null && mRide.getRequests().length > 0) {
                List<RideRequest> rideRequests = Arrays.asList(mRide.getRequests());
                progressBar.progressiveStart();
                new GetUserFromRequestsTask(getActivity()).execute(rideRequests);

            }
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
        protected Boolean doInBackground(List<RideRequest>... params) {
            List<RideRequest> rideRequests = params[0];

            for (RideRequest rideRequest : rideRequests) {
                try {
                    User passenger = TUMitfahrApplication.getApplication(localContext).getProfileService().getUserSynchronous(rideRequest.getPassengerId());
                    if (null != passenger) {
                        mRequestUserMap.put(rideRequest, passenger);
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
            final TUMitfahrApplication app = TUMitfahrApplication.getApplication(getActivity());
            Iterator it = mRequestUserMap.entrySet().iterator();
            if (mRequestUserMap.size() > 0) {
                requestsLayoutContainer.setVisibility(View.VISIBLE);
                requestsItemContainer.removeAllViews();
                while (it.hasNext()) {
                    final Map.Entry pairs = (Map.Entry) it.next();
                    PassengerItemView passengerItem = new PassengerItemView(getActivity());
                    passengerItem.setPassenger((User) pairs.getValue());
                    passengerItem.setItemType(PassengerItemView.TYPE_PENDING);
                    passengerItem.setListener(new PassengerItemView.PassengerItemClickListener() {
                        @Override
                        public void onRemoveClicked(User passenger) {
                            //remove the user
                            app.getRidesService().respondToRequest(mRide.getId(), ((RideRequest) pairs.getKey()).getId(), false);
                        }

                        @Override
                        public void onActionClicked(User passenger) {
                            //do the action... conversation or accept
                            app.getRidesService().respondToRequest(mRide.getId(), ((RideRequest) pairs.getKey()).getId(), true);
                        }

                        @Override
                        public void onUserClicked(User passenger) {
                            //show the userpage
                        }
                    });
                    requestsItemContainer.addView(passengerItem);
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
            progressBar.progressiveStop();
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

}


