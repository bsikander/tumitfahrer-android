package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.networking.BackendUtil;
import de.tum.mitfahr.networking.api.RidesAPIService;
import de.tum.mitfahr.networking.api.SessionAPIService;
import de.tum.mitfahr.networking.events.LoginResultEvent;
import de.tum.mitfahr.networking.events.OfferRideResultEvent;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.response.LoginResponse;
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
            mBus.post(new OfferRideResultEvent(offerRideResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
