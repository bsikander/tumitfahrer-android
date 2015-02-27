package de.tum.mitfahr.networking.models.response;

/**
 * Created by amr on 15/06/14.
 */
public class DeleteRideResponse {

    private String status;
    private String message;

    public DeleteRideResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
