package de.tum.mitfahr.networking.api;

import org.json.JSONObject;

import de.tum.mitfahr.networking.models.requests.UserRegisterRequestData;
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
            @Header("Authorization") String auth,
            @Body UserRegisterRequestData user,
            Callback<JSONObject> callback
    );

    @POST("/users/{hasjhdjas}")
    public void getUser(
            @Header("Authorization") String auth,
            @Body String user,
            Callback<JSONObject> callback
    );


}
