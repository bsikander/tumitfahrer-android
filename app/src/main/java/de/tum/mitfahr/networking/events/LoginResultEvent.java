package de.tum.mitfahr.networking.events;

import de.tum.mitfahr.networking.models.response.LoginResponse;

/**
 * Created by abhijith on 16/05/14.
 */
public class LoginResultEvent {

    LoginResponse response;

    public LoginResultEvent(LoginResponse response) {
        this.response = response;
    }

    public LoginResponse getResponse() {
        return response;
    }
}
