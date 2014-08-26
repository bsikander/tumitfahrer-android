package de.tum.mitfahr.networking.models.requests;


/**
 * Created by amr on 31/05/14.
 */
public class SearchRequest {
    private String departurePlace;
    private String destination;
    private String departureTime;
    private int departurePlaceThreshold;
    private int destinationThreshold;
    private int rideType;

    public SearchRequest(String from, int departureThreshold, String to, int destinationThreshold,
                         String dateTime, int rideType) {
        this.departurePlace = from;
        this.destination = to;
        this.departureTime = dateTime;
        this.departurePlaceThreshold = departureThreshold;
        this.destinationThreshold = destinationThreshold;
        this.rideType = rideType;
    }
}
