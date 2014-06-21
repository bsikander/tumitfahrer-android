package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import de.tum.mitfahr.networking.models.response.MyRidesResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
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

    @GET("/users/{id}/rides")
    public void getMyRides(
            @Path("id") int userId,
            Callback<MyRidesResponse> callback
    );

    @DELETE("/users/{userId}/rides/{rideId}")
    public void deleteRide(
            @Header("apiKey") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            Callback<DeleteRideResponse> callback
    );

    @POST("/rides/{rideId}/requests")
    public void joinRequest(
            @Header("apiKey") String apiKey,
            @Path("rideId") int userId,
            @Body int passengerId,
            Callback<JoinRequestResponse> callback
    );

    @POST("/rides/{rideId}/requests/{requestId}")
    public void respondToRequest(
            @Header("apiKey") String apiKey,
            @Path("rideId") int userId,
            @Path("requestId") int requestId,
            @Body int passengerId,
            @Body boolean confirmed,
            Callback<JoinRequestResponse> callback
    );
}
