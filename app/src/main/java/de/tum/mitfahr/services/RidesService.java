package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.DeleteRideRequestEvent;
import de.tum.mitfahr.events.GetRideEvent;
import de.tum.mitfahr.events.GetRideRequestsEvent;
import de.tum.mitfahr.events.GetRidesDateEvent;
import de.tum.mitfahr.events.GetRidesPageEvent;
import de.tum.mitfahr.events.GetUserRequestsEvent;
import de.tum.mitfahr.events.JoinRequestEvent;
import de.tum.mitfahr.events.MyRidesAsDriverEvent;
import de.tum.mitfahr.events.MyRidesAsPassengerEvent;
import de.tum.mitfahr.events.MyRidesPastEvent;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.events.RemovePassengerEvent;
import de.tum.mitfahr.events.RespondToRequestEvent;
import de.tum.mitfahr.events.UpdateRideEvent;
import de.tum.mitfahr.networking.clients.RidesRESTClient;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import de.tum.mitfahr.networking.models.response.RequestsResponse;
import de.tum.mitfahr.networking.models.response.RideResponse;
import de.tum.mitfahr.networking.models.response.RidesResponse;
import retrofit.client.Response;

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

    public void offerRide(String departure, String destination, String meetingPoint, String freeSeats, String dateTime, int rideType,boolean isDriving) {
        mRidesRESTClient.offerRide(departure, destination, meetingPoint, freeSeats, dateTime, userAPIKey, rideType, userId, isDriving);
    }

    @Subscribe
    public void onOfferRidesResult(OfferRideEvent result) {
        if (result.getType() == OfferRideEvent.Type.RESULT) {
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
        if (result.getType() == GetRideEvent.Type.RESULT) {
            RideResponse response = result.getResponse();
            if (null == response.getRide()) {
                mBus.post(new GetRideEvent(GetRideEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetRideEvent(GetRideEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public Ride getRideSynchronous(int rideId) {
        return mRidesRESTClient.getRideSynchronous(userAPIKey, rideId);
    }


    public void updateRide(Ride updatedRide) {
        mRidesRESTClient.updateRide(userAPIKey, userId, updatedRide);
    }

    @Subscribe
    public void onUpdateRideResult(UpdateRideEvent result) {
        if (result.getType() == UpdateRideEvent.Type.RESULT) {
            RideResponse response = result.getResponse();
            if (null == response.getRide()) {
                mBus.post(new UpdateRideEvent(UpdateRideEvent.Type.UPDATE_FAILED, response));
            } else {
                mBus.post(new UpdateRideEvent(UpdateRideEvent.Type.RIDE_UPDATED, response));
            }
        }
    }

    public void getMyRidesAsDriver() {
        mRidesRESTClient.getMyRidesAsDriver(userId, userAPIKey);
    }

    @Subscribe
    public void onGetMyRidesAsDriverResult(MyRidesAsDriverEvent result) {
        if (result.getType() == MyRidesAsDriverEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new MyRidesAsDriverEvent(MyRidesAsDriverEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new MyRidesAsDriverEvent(MyRidesAsDriverEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getMyRidesAsPassenger() {
        mRidesRESTClient.getMyRidesAsPassenger(userId, userAPIKey);
    }

    @Subscribe
    public void onGetMyRidesAsPassengerResult(MyRidesAsPassengerEvent result) {
        if (result.getType() == MyRidesAsPassengerEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new MyRidesAsPassengerEvent(MyRidesAsPassengerEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new MyRidesAsPassengerEvent(MyRidesAsPassengerEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getMyRidesPast() {
        mRidesRESTClient.getMyRidesPast(userId, userAPIKey);
    }

    @Subscribe
    public void onGetMyRidesPastResult(MyRidesPastEvent result) {
        if (result.getType() == MyRidesPastEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new MyRidesPastEvent(MyRidesPastEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new MyRidesPastEvent(MyRidesPastEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getPage(int pageNo) {
        mRidesRESTClient.getPage(userAPIKey, pageNo);
    }

    @Subscribe
    public void onGetPageResult(GetRidesPageEvent result) {
        if (result.getType() == GetRidesPageEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void getRides(String fromDate, int rideType) {
        mRidesRESTClient.getRides(userAPIKey, fromDate, rideType);
    }

    @Subscribe
    public void onGetDateResult(GetRidesDateEvent result) {
        if (result.getType() == GetRidesDateEvent.Type.RESULT) {
            RidesResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new GetRidesDateEvent(GetRidesDateEvent.Type.GET_FAILED, response));
            } else {
                mBus.post(new GetRidesDateEvent(GetRidesDateEvent.Type.GET_SUCCESSFUL, response));
            }
        }
    }

    public void removePassenger(int rideId, int removedPassengerId) {
        mRidesRESTClient.removePassenger(userAPIKey, userId, rideId, removedPassengerId);
    }

    @Subscribe
    public void onRemovePassengerResult(RemovePassengerEvent result) {
        if (result.getType() == RemovePassengerEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new RemovePassengerEvent(RemovePassengerEvent.Type.SUCCESSFUL, retrofitResponse));
            } else {
                mBus.post(new RemovePassengerEvent(RemovePassengerEvent.Type.FAILED, retrofitResponse));
            }
        }
    }

    public void deleteRide(int rideId) {
        mRidesRESTClient.deleteRide(userAPIKey, userId, rideId);
    }

    @Subscribe
    public void onDeleteResult(DeleteRideEvent result) {
        if (result.getType() == DeleteRideEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new DeleteRideEvent(DeleteRideEvent.Type.DELETE_SUCCESSFUL, result.getResponse(), retrofitResponse));
            } else {
                mBus.post(new DeleteRideEvent(DeleteRideEvent.Type.DELETE_FAILED, result.getResponse(), retrofitResponse));
            }
        }
    }

    public void joinRequest(int rideId) {
        mRidesRESTClient.joinRequest(rideId, userId, userAPIKey);
    }

    @Subscribe
    public void onRideRequestResult(JoinRequestEvent result) {
        if (result.getType() == JoinRequestEvent.Type.RESULT) {
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
        if (result.getType() == RespondToRequestEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new RespondToRequestEvent(RespondToRequestEvent.Type.RESPOND_SENT, retrofitResponse));
            } else {
                mBus.post(new RespondToRequestEvent(RespondToRequestEvent.Type.RESPOND_FAILED, retrofitResponse));
            }
        }
    }

    public void getRideRequests(int rideId) {
        mRidesRESTClient.getRideRequests(userAPIKey, rideId);
    }

    @Subscribe
    public void onGetRideRequestsResult(GetRideRequestsEvent result) {
        if (result.getType() == GetRideRequestsEvent.Type.RESULT) {
            RequestsResponse requestsResponse = result.getResponse();
            if (null == requestsResponse.getRequests()) {
                mBus.post(new GetRideRequestsEvent(GetRideRequestsEvent.Type.GET_FAILED, requestsResponse));
            } else {
                mBus.post(new GetRideRequestsEvent(GetRideRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
            }
        }
    }

    public void getUserRequests() {
        mRidesRESTClient.getUserRequests(userAPIKey, userId);
    }

    @Subscribe
    public void onGetUserRequestsResult(GetUserRequestsEvent result) {
        if (result.getType() == GetUserRequestsEvent.Type.RESULT) {
            RequestsResponse requestsResponse = result.getResponse();
            if (null == requestsResponse.getRequests()) {
                mBus.post(new GetUserRequestsEvent(GetUserRequestsEvent.Type.GET_FAILED, requestsResponse));
            } else {
                mBus.post(new GetUserRequestsEvent(GetUserRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
            }
        }
    }

    public void deleteRideRequest(int rideId, int requestId) {
        mRidesRESTClient.deleteRideRequest(userAPIKey, rideId, requestId);
    }

    @Subscribe
    public void onDeleteRideRequest(DeleteRideRequestEvent result) {
        if (result.getType() == DeleteRideRequestEvent.Type.RESULT) {
            Response retrofitResponse = result.getRetrofitResponse();
            if (200 == retrofitResponse.getStatus()) {
                mBus.post(new DeleteRideRequestEvent(DeleteRideRequestEvent.Type.SUCCESSFUL, retrofitResponse));
            } else {
                mBus.post(new DeleteRideRequestEvent(DeleteRideRequestEvent.Type.FAILED, retrofitResponse));
            }
        }
    }
}
