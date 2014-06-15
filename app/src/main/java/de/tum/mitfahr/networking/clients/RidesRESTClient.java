package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.events.DeleteRideEvent;
import de.tum.mitfahr.events.MyRidesEvent;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.networking.api.RidesAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import de.tum.mitfahr.networking.models.response.MyRidesResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 18/05/14.
 */
public class RidesRESTClient extends AbstractRESTClient{

    public RidesRESTClient(String mBaseBackendURL) {
        super(mBaseBackendURL);
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
        RidesAPIService ridesAPIService = mRestAdapter.create(RidesAPIService.class);
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
        RidesAPIService ridesAPIService = mRestAdapter.create(RidesAPIService.class);
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
        RidesAPIService ridesAPIService = mRestAdapter.create(RidesAPIService.class);
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
}
