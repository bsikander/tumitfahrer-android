package de.tum.mitfahr.networking.models.response;

import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by amr on 18/05/14.
 */
public class OfferRideResponse {

    private String status;
    private String message;
    private Ride ride;

    public OfferRideResponse(String status, String message, Ride ride) {
        this.status = status;
        this.message = message;
        this.ride = ride;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Ride getRide() {
        return ride;
    }

}
