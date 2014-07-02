package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.requests.OfferRideRequest;
import de.tum.mitfahr.networking.models.response.DeleteRideResponse;
import de.tum.mitfahr.networking.models.response.JoinRequestResponse;
import de.tum.mitfahr.networking.models.response.RequestsResponse;
import de.tum.mitfahr.networking.models.response.RidesResponse;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;
import de.tum.mitfahr.networking.models.response.RideResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
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

    @GET("/rides/{id}")
    public void getRide(
            @Header("apiKey") String apiKey,
            @Path("id") int rideId,
            Callback<RideResponse> callback
    );

    @PUT("/users/{userId}/rides/{rideId}")
    public void updateRide(
            @Header("apiKey") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            @Body Ride ride,
            Callback<RideResponse> callback
    );

    @GET("/users/{id}/rides?driver=true")
    public void getMyRidesAsDriver(
            @Header("apiKey") String apiKey,
            @Path("id") int userId,
            Callback<RidesResponse> callback
    );

    @GET("/users/{id}/rides?passenger=true")
    public void getMyRidesAsPassenger(
            @Header("apiKey") String apiKey,
            @Path("id") int userId,
            Callback<RidesResponse> callback
    );

    @GET("/users/{id}/rides?past=true")
    public void getMyRidesPast(
            @Header("apiKey") String apiKey,
            @Path("id") int userId,
            Callback<RidesResponse> callback
    );

    @PUT("/users/{userId}/rides/{rideId}?removed_passenger={removedPassengerId}")
    public void removePassenger(
            @Header("apiKey") String apiKey,
            @Path("userId") int userId,
            @Path("rideId") int rideId,
            @Path("removedPassengerId") int removedPassengerId,
            Callback<RideResponse> callback
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

    @PUT("/rides/{rideId}/requests/{requestId}")
    public void respondToRequest(
            @Header("apiKey") String apiKey,
            @Path("rideId") int userId,
            @Path("requestId") int requestId,
            @Body int passengerId,
            @Body boolean confirmed,
            Callback<JoinRequestResponse> callback
    );

    @GET("/rides/{rideId}/requests")
    public void getRideRequests(
            @Header("apiKey") String apiKey,
            @Path("rideId") int rideId,
            Callback<RequestsResponse> callback
    );

    @GET("/users/{userId}/requests")
    public void getUserRequests(
            @Header("apiKey") String apiKey,
            @Path("userId") int userId,
            Callback<RequestsResponse> callback
    );

    @DELETE("/rides/{rideId}/requests/{requestId}")
    public void deleteRideRequest(
            @Header("apiKey") String apiKey,
            @Path("rideId") int rideId,
            @Path("requestId") int requestId,
            Callback<RequestsResponse> callback
    );

    @GET("/rides?page={pageNo}")
    public void getPage(
            @Header("apiKey") String apiKey,
            @Path("pageNo") int pageNo,
            Callback<RidesResponse> callback
    );

    // Not sure about this call
    @GET("/rides?from_date={fromDate}&ride_type={rideType}")
    public void getRides(
            @Header("apiKey") String apiKey,
            @Path("fromDate")  String fromDate,
            @Path("rideType") int rideType,
            Callback<RidesResponse> callback
    );
}
