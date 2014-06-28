package de.tum.mitfahr.networking.models.requests;



/**
 * Created by amr on 31/05/14.
 */
public class SearchRequest {
    private String startCarpool;
    private String endCarpool;
    private String rideDate;
    private int departureThreshold;
    private int destinationThreshold;
    private int rideType;

    public SearchRequest(String from, int departureThreshold, String to, int destinationThreshold,
                         String dateTime, int rideType) {
        this.startCarpool = from;
        this.endCarpool = to;
        this.rideDate = dateTime;
        this.departureThreshold = departureThreshold;
        this.destinationThreshold = destinationThreshold;
        this.rideType = rideType;
    }
}
