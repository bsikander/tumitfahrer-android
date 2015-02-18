package de.tum.mitfahr.networking.models.requests;

import java.util.List;

/**
 * Created by abhijith on 08/10/14.
 */
public class CreateRideRequest {

    String departurePlace;
    String destination;
    String meetingPoint;
    int freeSeats;
    String departureTime;
    int rideType;
    String isDriving;
    double departureLongitude;
    double departureLatitude;
    double destinationLongitude;
    double destinationLatitude;
    List<String> repeatDates;

    public CreateRideRequest(String departure, String destination, String meetingPoint,
                             int freeSeats, String dateTime, int rideType, String isDriving, String car, List<String> repeatDates) {
        this.departurePlace = departure;
        this.destination = destination;
        this.meetingPoint = meetingPoint;
        this.freeSeats = freeSeats;
        this.departureTime = dateTime;
        this.rideType = rideType;
        this.isDriving = isDriving;
        this.departureLatitude = 0.0;
        this.departureLongitude = 0.0;
        this.destinationLatitude = 0.0;
        this.destinationLongitude = 0.0;
        if (Integer.parseInt(isDriving) == 0) {
            this.repeatDates = null;
        } else {
            this.repeatDates = repeatDates;
        }
    }
}
