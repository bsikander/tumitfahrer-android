package de.tum.mitfahr.networking.models;

/**
 * Created by amr on 02/07/14.
 */
public class ActivitiesRideSearch {

    private int id;
    private int userId;
    private String departurePlace;
    private String destination;
    private String departureTime;
    private int rideType;
    private String createdAt;
    private String updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public void setDeparturePlace(String departurePlace) {
        this.departurePlace = departurePlace;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public int getRideType() {
        return rideType;
    }

    public void setRideType(int rideType) {
        this.rideType = rideType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ActivitiesRideSearch(int id, int userId, String departurePlace, String destination,
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
