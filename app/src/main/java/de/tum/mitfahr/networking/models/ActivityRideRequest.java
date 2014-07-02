package de.tum.mitfahr.networking.models;

/**
 * Created by amr on 02/07/14.
 */
public class ActivityRideRequest {

    private int id;
    private int passengerId;
    private int rideId;
    private String createdAt;
    private String updatedAt;

    public ActivityRideRequest(int id, int passengerId, int rideId) {
        this.id = id;
        this.passengerId = passengerId;
        this.rideId = rideId;
    }

    public ActivityRideRequest(int id, int passengerId, int rideId, String createdAt, String updatedAt) {
        this.id = id;
        this.passengerId = passengerId;
        this.rideId = rideId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getRideId() {
        return rideId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
