package de.tum.mitfahr.networking.api;

import org.json.JSONObject;

import de.tum.mitfahr.networking.models.requests.RegisterRequest;
import de.tum.mitfahr.networking.models.requests.UpdateUserRequest;
import de.tum.mitfahr.networking.models.response.GetUserResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;
import de.tum.mitfahr.networking.models.response.UpdateUserResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by abhijith on 09/05/14.
 */

public interface UserAPIService {

    @POST("/users")
    public void registerUser(
            @Body RegisterRequest user,
            Callback<RegisterResponse> callback
    );

    @POST("/users/{}")
    public void getUser(
            @Header("Authorization") String auth,
            @Body String user,
            Callback<JSONObject> callback
    );

    @GET("/users/{id}")
    public void getSomeUser(
            @Header("apiKey") String apiKey,
            @Path("id") int userId,
            Callback<GetUserResponse> callback
    );

    @PUT("/users/{id}")
    public void updateUser(
            @Header("Authorization") String auth,
            @Path("id") int userId,
            @Body UpdateUserRequest user,
            Callback<UpdateUserResponse> callback
    );

}
