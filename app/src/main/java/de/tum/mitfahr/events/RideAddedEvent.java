package de.tum.mitfahr.events;

import de.tum.mitfahr.networking.models.Ride;

/**
 * Created by amr on 18/05/14.
 */
public class RideAddedEvent {
    private Ride ride;

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public RideAddedEvent(Ride ride) {
        this.ride = ride;

    }
}
