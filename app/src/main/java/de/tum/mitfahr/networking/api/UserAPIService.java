package de.tum.mitfahr.networking.api;

import org.json.JSONObject;

import de.tum.mitfahr.networking.models.requests.RegisterRequest;
import de.tum.mitfahr.networking.models.response.RegisterResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

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


}
