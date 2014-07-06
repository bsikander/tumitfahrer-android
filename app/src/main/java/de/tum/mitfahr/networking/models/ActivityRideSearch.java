package de.tum.mitfahr.networking.models;

/**
 * Created by amr on 02/07/14.
 */
public class ActivityRideSearch {

    private int id;
    private int userId;
    private String departurePlace;
    private String destination;
    private String departureTime;
    private int rideType;
    private String createdAt;
    private String updatedAt;

    public ActivityRideSearch(int id, int userId, String departurePlace, String destination,
                              String departureTime, int rideType, String createdAt, String updatedAt) {

        this.createdAt = createdAt;
        this.departurePlace = departurePlace;
        this.departureTime = departureTime;
        this.destination = destination;
        this.id = id;
        this.rideType = rideType;
        this.userId = userId;
        this.updatedAt = updatedAt;
    }
}
