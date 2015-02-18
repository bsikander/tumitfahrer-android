package de.tum.mitfahr.networking.models.requests;

import java.util.List;

/**
 * Created by amr on 18/05/14.
 */
public class OfferRideRequest {

    CreateRideRequest ride;

    public OfferRideRequest(String departure, String destination, String meetingPoint,
                            int freeSeats, String dateTime, int rideType, String isDriving, String car,List<String> repeatDates) {
        ride = new CreateRideRequest(departure, destination, meetingPoint, freeSeats, dateTime, rideType, isDriving, car, repeatDates);
    }
}
