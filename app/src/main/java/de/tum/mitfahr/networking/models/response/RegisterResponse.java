package de.tum.mitfahr.networking.models.response;

/**
 * Created by abhijith on 16/05/14.
 */
public class RegisterResponse {
    public String status;
    public String message;

    public RegisterResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}
