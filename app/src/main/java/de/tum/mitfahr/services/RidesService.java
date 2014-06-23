package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.GetRideEvent;
import de.tum.mitfahr.events.JoinRequestEvent;
import de.tum.mitfahr.events.MyRidesEvent;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.events.RespondToRequestEvent;
import de.tum.mitfahr.events.UpdateRideEvent;
import de.tum.mitfahr.networking.clients.RidesRESTClient;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import de.tum.mitfahr.networking.models.response.MyRidesResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import de.tum.mitfahr.networking.models.response.RideResponse;

/**
 * Created by amr on 18/05/14.
 */
public class RidesService {

    private SharedPreferences mSharedPreferences;
    private RidesRESTClient mRidesRESTClient;
    private Bus mBus;
    private String userAPIKey;
    private int userId;

    public RidesService(final Context context) {
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mRidesRESTClient = new RidesRESTClient(baseBackendURL);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
    }

    public void offerRide(String departure, String destination, String meetingPoint, String freeSeats, String dateTime, int rideType) {
        mRidesRESTClient.offerRide(departure, destination, meetingPoint, freeSeats, dateTime, userAPIKey, rideType, userId);
        //TODO : create otto event classes and stuff!
    }

    @Subscribe
    public void onOfferRidesResult(OfferRideEvent result) {
        if(result.getType() == OfferRideEvent.Type.RESULT) {
            OfferRideResponse response = result.getResponse();
            if (null == response.getRide()) {
                mBus.post(new OfferRideEvent(OfferRideEvent.Type.OFFER_RIDE_FAILED));
            } else {
                mBus.post(new OfferRideEvent(OfferRideEvent.Type.RIDE_ADDED, response.getRide()));
            }
        }
    }

    public void getRide(int rideId) {
        mRidesRESTClient.getRide(userAPIKey, rideId);
    }

    @Subscribe
    public void onGetRideResult(GetRideEvent result) {
        if(result.getType() == GetRideEvent.Type.RESULT) {
            RideResponse response = result.getResponse();
            if (null == response.getRide()) {
                mBus.post(new GetRideEvent(GetRideEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetRideEvent(GetRideEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void updateRide(Ride updatedRide) {
        mRidesRESTClient.updateRide(userAPIKey, userId, updatedRide);
    }

    @Subscribe
    public void onUpdateRideResult(UpdateRideEvent result) {
        if(result.getType() == UpdateRideEvent.Type.RESULT) {
            RideResponse response = result.getResponse();
            if (null == response.getRide()) {
                mBus.post(new UpdateRideEvent(UpdateRideEvent.Type.UPDATE_FAILED, response));
            } else {
                mBus.post(new UpdateRideEvent(UpdateRideEvent.Type.RIDE_UPDATED, response));
            }
        }
    }

    public void getMyRides() {
        mRidesRESTClient.getMyRides(userId, userAPIKey);
    }

    @Subscribe
    public void onGetMyRidesResult(MyRidesEvent result) {
        if(result.getType() == OfferRideEvent.Type.RESULT) {
            MyRidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new MyRidesEvent(MyRidesEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new MyRidesEvent(MyRidesEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void deleteRide(int rideId) {
        mRidesRESTClient.deleteRide(userAPIKey, userId, rideId);
    }

    @Subscribe
    public void onDeleteResult(DeleteRideEvent result) {
        //TODO post events
    }

    public void joinRequest(int rideId) {
        mRidesRESTClient.joinRequest(rideId, userId, userAPIKey);
    }

    @Subscribe
    public void onRideRequestResult(JoinRequestEvent result) {
        if(result.getType() == JoinRequestEvent.Type.RESULT) {
            JoinRequestResponse joinRequestResponse = result.getJoinRequestResponse();
            if (null == joinRequestResponse.getRideRequest()) {
                mBus.post(new JoinRequestEvent(JoinRequestEvent.Type.REQUEST_FAILED,
                        joinRequestResponse, result.getRetrofitResponse()));
            } else {
                mBus.post(new JoinRequestEvent(JoinRequestEvent.Type.REQUEST_SENT,
                        joinRequestResponse, result.getRetrofitResponse()));
            }
        }
    }

    public void respondToRequest(int rideId, int requestId, boolean confirmed) {
        mRidesRESTClient.respondToRequest(rideId, userId, requestId, confirmed, userAPIKey);
    }

    @Subscribe
    public void onRespondToRequestResult(RespondToRequestEvent result) {
        if(result.getType() == RespondToRequestEvent.Type.RESULT) {
            // TODO handle events
        }
    }
}
