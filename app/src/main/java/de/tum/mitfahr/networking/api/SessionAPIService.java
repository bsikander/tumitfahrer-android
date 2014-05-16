package de.tum.mitfahr.networking.api;

import org.json.JSONObject;

import de.tum.mitfahr.networking.models.response.LoginResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;
import retrofit.Callback;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by abhijith on 09/05/14.
 */

public interface SessionAPIService {

    @POST("/sessions")
    void loginUser(@Header("Authorization") String auth, Callback<LoginResponse> callback);

}
