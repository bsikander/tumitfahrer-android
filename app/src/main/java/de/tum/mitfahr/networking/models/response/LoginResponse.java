package de.tum.mitfahr.networking.models.response;

import com.google.gson.annotations.SerializedName;

import de.tum.mitfahr.networking.models.User;

/**
 * Created by abhijith on 16/05/14.
 */

public class LoginResponse {

    String status;
    String message;
    User user;


    public LoginResponse(String status, String message, User user) {
        this.status = status;
        this.message = message;
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", user=" + user +
                '}';
    }
}
