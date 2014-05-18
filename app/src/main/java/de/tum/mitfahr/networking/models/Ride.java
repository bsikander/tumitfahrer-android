package de.tum.mitfahr.networking.models;

/**
 * Created by amr on 18/05/14.
 */
public class Ride {

    String departurePlace;
    String destination;
    String meetingPoint;
    String freeSeats;
    String departureTime;
    int driverId;

    public Ride(String departure, String destination, String meetingPoint,
                            String freeSeats, String dateTime, int driverId) {
        this.departurePlace = departure;
        this.destination = destination;
        this.meetingPoint = meetingPoint;
        this.freeSeats = freeSeats;
        this.departureTime = dateTime;
        this.driverId = driverId;
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

    public String getMeetingPoint() {
        return meetingPoint;
    }

    public void setMeetingPoint(String meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    public String getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(String freeSeats) {
        this.freeSeats = freeSeats;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
}
