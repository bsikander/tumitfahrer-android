package de.tum.mitfahr.networking.models;

import java.util.ArrayList;

/**
 * Created by amr on 02/07/14.
 */
public class Activity {

    private int id;
    private ArrayList<Ride> rides;
    private ArrayList<ActivityRideRequest> requests;
    private ArrayList<ActivityRideSearch> rideSearches;

    public Activity(int id, ArrayList<Ride> rides, ArrayList<ActivityRideRequest> rideRequests,
                    ArrayList<ActivityRideSearch> rideSearches) {
        this.id = id;
        this.rides = rides;
        this.requests = rideRequests;
        this.rideSearches = rideSearches;
    }
}
