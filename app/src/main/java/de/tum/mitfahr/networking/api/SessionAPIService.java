package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.response.LoginResponse;
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
