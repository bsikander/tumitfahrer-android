package de.tum.mitfahr.networking.models;

/**
 * Created by amr on 21/06/14.
 */
public class RideRequest {

    private int id;
    private int passengerId;
    private Ride ride;

    public RideRequest(int id, int passengerId, Ride ride) {
        this.id = id;
        this.passengerId = passengerId;
        this.ride = ride;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
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
