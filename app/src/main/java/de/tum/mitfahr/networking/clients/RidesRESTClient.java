package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.JoinRequestEvent;
import de.tum.mitfahr.events.MyRidesEvent;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.events.RespondToRequestEvent;
import de.tum.mitfahr.networking.api.RidesAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import de.tum.mitfahr.networking.models.response.MyRidesResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 18/05/14.
 */
public class RidesRESTClient extends AbstractRESTClient{

    private RidesAPIService ridesAPIService;

    public RidesRESTClient(String mBaseBackendURL) {

        super(mBaseBackendURL);
        ridesAPIService = mRestAdapter.create(RidesAPIService.class);
    }

    public void offerRide(final String departure,
                                    final String destination,
                                    final String meetingPoint,
                                    final String freeSeats,
                                    final String dateTime,
                                    final String userAPIKey,
                                    final int rideType,
                                    final int userId) {
        OfferRideRequest requestData = new OfferRideRequest(departure, destination, meetingPoint, freeSeats, dateTime, rideType);
        ridesAPIService.offerRide(userAPIKey, userId, requestData, offerRideCallback);
    }

    private Callback<OfferRideResponse> offerRideCallback = new Callback<OfferRideResponse>() {

        @Override
        public void success(OfferRideResponse offerRideResponse, Response response) {
            mBus.post(new OfferRideEvent(OfferRideEvent.Type.RESULT, offerRideResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getMyRides(final int userId) {
        ridesAPIService.getMyRides(userId, getMyRidesCallback);
    }

    private Callback<MyRidesResponse> getMyRidesCallback = new Callback<MyRidesResponse>() {

        @Override
        public void success(MyRidesResponse myRidesResponse, Response response) {
            mBus.post(new MyRidesEvent(MyRidesEvent.Type.RESULT, myRidesResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };


    public void deleteRide(final String userAPIKey, final int userId, final int rideId) {
        ridesAPIService.deleteRide(userAPIKey, userId, rideId, deleteRideCallback);
    }

    private Callback<DeleteRideResponse> deleteRideCallback = new Callback<DeleteRideResponse>() {

        @Override
        public void success(DeleteRideResponse deleteRideResponse, Response response) {
            mBus.post(new DeleteRideEvent(DeleteRideEvent.Type.RESULT, deleteRideResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void joinRequest(int rideId, int passengerId, String userAPIKey) {
        ridesAPIService.joinRequest(userAPIKey, rideId, passengerId, joinRequestCallback);
    }

    private Callback<JoinRequestResponse> joinRequestCallback = new Callback<JoinRequestResponse>() {
        @Override
        public void success(JoinRequestResponse joinRequestResponse, Response response) {
            mBus.post(new JoinRequestEvent(JoinRequestEvent.Type.RESULT, joinRequestResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void respondToRequest(int rideId, int passengerId, int requestId, boolean confirmed, String userAPIKey) {
        ridesAPIService.respondToRequest(userAPIKey, rideId, requestId, passengerId, confirmed, respondToRequestCallback);
    }

    private Callback respondToRequestCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new RespondToRequestEvent(RespondToRequestEvent.Type.RESULT));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
