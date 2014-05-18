package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by amr on 18/05/14.
 */
public interface RidesAPIService {

    @POST("/users/{id}/rides")
    public void offerRide(
            @Header("apiKey") String apiKey,
            @Path("id") int userId,
            @Body OfferRideRequest ride,
            Callback<OfferRideResponse> callback
    );
}
