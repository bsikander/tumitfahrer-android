package de.tum.mitfahr.networking.models.requests;



/**
 * Created by amr on 31/05/14.
 */
public class SearchRequest {
    private String startCarpool;
    private String endCarpool;
    private String rideDate;

    public SearchRequest(String from, String to, String dateTime) {
        this.startCarpool = from;
        this.endCarpool = to;
        this.rideDate = dateTime;
    }
}
