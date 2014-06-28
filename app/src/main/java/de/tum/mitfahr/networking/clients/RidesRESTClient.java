package de.tum.mitfahr.networking.clients;

import java.text.SimpleDateFormat;

import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.DeleteRideRequestEvent;
import de.tum.mitfahr.events.GetRideEvent;
import de.tum.mitfahr.events.GetRideRequestsEvent;
import de.tum.mitfahr.events.GetRidesDateEvent;
import de.tum.mitfahr.events.GetRidesPageEvent;
import de.tum.mitfahr.events.GetUserRequestsEvent;
import de.tum.mitfahr.events.JoinRequestEvent;
import de.tum.mitfahr.events.MyRidesEvent;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.events.RemovePassengerEvent;
import de.tum.mitfahr.events.RespondToRequestEvent;
import de.tum.mitfahr.events.UpdateRideEvent;
import de.tum.mitfahr.networking.api.RidesAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import de.tum.mitfahr.networking.models.response.RequestsResponse;
import de.tum.mitfahr.networking.models.response.RidesResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import de.tum.mitfahr.networking.models.response.RideResponse;
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

    public void getRide(String userAPIKey, int rideId) {
        ridesAPIService.getRide(userAPIKey, rideId, getRideCallback);
    }

    private Callback<RideResponse> getRideCallback = new Callback<RideResponse>() {
        @Override
        public void success(RideResponse rideResponse, Response response) {
            mBus.post(new GetRideEvent(GetRideEvent.Type.RESULT, rideResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void updateRide(String userAPIKey, int userId, Ride updatedRide) {
        ridesAPIService.updateRide(userAPIKey, userId, updatedRide.getId(), updatedRide, updateRideCallback);
    }

    private Callback<RideResponse> updateRideCallback = new Callback<RideResponse>() {
        @Override
        public void success(RideResponse rideResponse, Response response) {
            mBus.post(new UpdateRideEvent(UpdateRideEvent.Type.RESULT, rideResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getMyRidesAsDriver(final int userId, String userAPIKey) {
        ridesAPIService.getMyRidesAsDriver(userAPIKey, userId, getMyRidesCallback);
    }

    public void getMyRidesAsPassenger(final int userId, String userAPIKey) {
        ridesAPIService.getMyRidesAsPassenger(userAPIKey, userId, getMyRidesCallback);
    }

    public void getMyRidesPast(final int userId, String userAPIKey) {
        ridesAPIService.getMyRidesPast(userAPIKey, userId, getMyRidesCallback);
    }

    private Callback<RidesResponse> getMyRidesCallback = new Callback<RidesResponse>() {

        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new MyRidesEvent(MyRidesEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getPage(String userAPIKey, int pageNo) {
        ridesAPIService.getPage(userAPIKey, pageNo, getPageCallback);
    }

    private Callback<RidesResponse> getPageCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesPageEvent(GetRidesPageEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    // Doc not clear
    public void getRides(String userAPIKey, String fromDate,int rideType) {
        //ridesAPIService.getRides(userAPIKey, fromDate, rideType, getRidesCallback);
    }

    private Callback<RidesResponse> getRidesCallback = new Callback<RidesResponse>() {
        @Override
        public void success(RidesResponse ridesResponse, Response response) {
            mBus.post(new GetRidesDateEvent(GetRidesDateEvent.Type.RESULT, ridesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void removePassenger(String userAPIKey, int userId, int rideId, int removedPassengerId) {
        ridesAPIService.removePassenger(userAPIKey, userId, rideId, removedPassengerId, removePassengerCallback);
    }

    private Callback removePassengerCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new RemovePassengerEvent(RemovePassengerEvent.Type.SUCCESSFUL));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };


    public void deleteRide(final String userAPIKey, final int userId, final int rideId) {
        ridesAPIService.deleteRide(userAPIKey, userId, rideId, deleteRideCallback);
    }

    private Callback<DeleteRideResponse> deleteRideCallback = new Callback<DeleteRideResponse>() {

        @Override
        public void success(DeleteRideResponse deleteRideResponse, Response response) {
            mBus.post(new DeleteRideEvent(DeleteRideEvent.Type.RESULT, deleteRideResponse, response));
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
            mBus.post(new RespondToRequestEvent(RespondToRequestEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getRideRequests(String userAPIKey, int rideId) {
        ridesAPIService.getRideRequests(userAPIKey, rideId, getRideRequestsCallback);
    }

    private Callback<RequestsResponse> getRideRequestsCallback = new Callback<RequestsResponse>() {
        @Override
        public void success(RequestsResponse requestsResponse, Response response) {
            mBus.post(new GetRideRequestsEvent(GetRideRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getUserRequests(String userAPIKey, int userId) {
        ridesAPIService.getUserRequests(userAPIKey, userId, getUserRequestsCallback);
    }

    private Callback<RequestsResponse> getUserRequestsCallback = new Callback<RequestsResponse>() {
        @Override
        public void success(RequestsResponse requestsResponse, Response response) {
            mBus.post(new GetUserRequestsEvent(GetUserRequestsEvent.Type.GET_SUCCESSFUL, requestsResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void deleteRideRequest(String userAPIKey, int rideId, int requestId) {
        ridesAPIService.deleteRideRequest(userAPIKey, rideId, requestId, deleteRideRequestCallback);
    }

    private Callback deleteRideRequestCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new DeleteRideRequestEvent(DeleteRideRequestEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
