package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.events.GetUserRatingsEvent;
import de.tum.mitfahr.events.RateUserEvent;
import de.tum.mitfahr.networking.api.RatingsAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.RatingRequest;
import de.tum.mitfahr.networking.models.response.RatingResponse;
import de.tum.mitfahr.networking.models.response.RatingsResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 23/06/14.
 */
public class RatingsRESTClient extends AbstractRESTClient {

    private RatingsAPIService ratingsAPIService;

    public RatingsRESTClient(String mBaseBackendURL) {

        super(mBaseBackendURL);
        ratingsAPIService = mRestAdapter.create(RatingsAPIService.class);
    }

    public void getUserRatings(String userAPIKey, int userId) {
        ratingsAPIService.getUserRatings(userAPIKey, userId, getUserRatingsCallback);
    }

    private Callback<RatingsResponse> getUserRatingsCallback = new Callback<RatingsResponse>() {
        @Override
        public void success(RatingsResponse ratingsResponse, Response response) {
            mBus.post(new GetUserRatingsEvent(GetUserRatingsEvent.Type.GET_SUCCESSFUL, ratingsResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void rateUser(String userAPIKey, int fromUserId, int toUserId, int rideType, int ratingType) {
        ratingsAPIService.rateUser(userAPIKey, fromUserId,
                new RatingRequest(toUserId, rideType, ratingType), rateUserCallback);
    }

    private Callback<RatingResponse> rateUserCallback = new Callback<RatingResponse>() {
        @Override
        public void success(RatingResponse ratingResponse, Response response) {
            mBus.post(new RateUserEvent(RateUserEvent.Type.RESULT, ratingResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
