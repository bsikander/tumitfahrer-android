package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;
import java.util.List;

import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by amr on 31/05/14.
 */
public class SearchResponse {

    private String status;
    private String message;
    private List<Ride> rides;

    public SearchResponse(String status, String message, ArrayList<Ride> rides) {
        this.status = status;
        this.message = message;
        this.rides = rides;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Ride> getRides() {
        return rides;
    }
}
