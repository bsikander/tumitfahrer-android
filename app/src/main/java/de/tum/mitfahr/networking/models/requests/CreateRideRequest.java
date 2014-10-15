package de.tum.mitfahr.networking.models.requests;

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
    int isDriving;
    double departureLongitude;
    double departureLatitude;
    double destinationLongitude;
    double destinationLatitude;
    String[] repeatDates;

    public CreateRideRequest(String departure, String destination, String meetingPoint,
                             int freeSeats, String dateTime, int rideType, int isDriving, String car) {
        this.departurePlace = departure;
        this.destination = destination;
        this.meetingPoint = meetingPoint;
        this.freeSeats = freeSeats;
        this.departureTime = dateTime;
        this.rideType = rideType;
        this.isDriving = isDriving;
        this.repeatDates = new String[0];
        this.departureLatitude = 0.0;
        this.departureLongitude = 0.0;
        this.destinationLatitude = 0.0;
        this.destinationLongitude = 0.0;
    }
}
