package de.tum.mitfahr.networking.events;

import de.tum.mitfahr.networking.models.response.LoginResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;

/**
 * Created by abhijith on 16/05/14.
 */
public class RegisterResultEvent {
    RegisterResponse response;

    public RegisterResultEvent(RegisterResponse response) {
        this.response = response;
    }

    public RegisterResponse getResponse() {
        return response;
    }
}
