package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.requests.RatingRequest;
import de.tum.mitfahr.networking.models.response.RatingResponse;
import de.tum.mitfahr.networking.models.response.RatingsResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by amr on 23/06/14.
 */
public interface RatingsAPIService {

    @GET("/users/{userId}/ratings")
    public void getUserRatings(
            @Header("apiKey") String apiKey,
            @Path("userId") int userId,
            Callback<RatingsResponse> callback
    );

    @POST("/users/{userId}/ratings")
    public void rateUser(
            @Header("apiKey") String apiKey,
            @Path("userId") int fromUserId,
            @Body RatingRequest ratingRequest,
            Callback<RatingResponse> callback
    );
}
