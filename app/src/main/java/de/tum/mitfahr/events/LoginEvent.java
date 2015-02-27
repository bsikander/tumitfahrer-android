package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.response.LoginResponse;

/**
 * Created by amr on 25/05/14.
 */
public class LoginEvent extends AbstractEvent {

    LoginResponse response;
    int statusCode;

    public LoginEvent(Type type) {
        super(type);
    }

    public LoginEvent(Type type, LoginResponse response, int statusCode) {

        super(type);
        this.response = response;
        this.statusCode = statusCode;
    }

    public LoginResponse getResponse() {
        return response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public enum Type {
        LOGIN_FAILED,
        LOGIN_SUCCESSFUL,
        LOGIN_RESULT
    }
}
