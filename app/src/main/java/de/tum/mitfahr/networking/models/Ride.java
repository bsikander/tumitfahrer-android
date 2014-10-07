package de.tum.mitfahr.networking.models;

import java.io.Serializable;

/**
 * Created by amr on 18/05/14.
 */
public class Ride implements Serializable {

    private int id;
    private String departurePlace;
    private String destination;
    private String meetingPoint;
    private int freeSeats;
    private String departureTime;
    private double price;
    private String realtimeDepartureTime;
    private double realtimeKm;
    private User rideOwner;
    private boolean isRideRequest;
    private int rideType;
    private String createdAt;
    private String updatedAt;
    private double latitude;

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public User[] getPassengers() {
        return passengers;
    }

    public void setPassengers(User[] passengers) {
        this.passengers = passengers;
    }

    public RideRequest[] getRequests() {
        return requests;
    }

    public void setRequests(RideRequest[] requests) {
        this.requests = requests;
    }

    public Conversation[] getConversations() {
        return conversations;
    }

    public void setConversations(Conversation[] conversations) {
        this.conversations = conversations;
    }

    private String car;
    private User passengers[];
    private RideRequest requests[];
    private Conversation conversations[];


    private double longitude;

    public Ride(int id,
                String departurePlace,
                String destination,
                String meetingPoint,
                int freeSeats,
                String departureTime,
                double price,
                String realtimeDepartureTime,
                double realtimeKm,
                User driver,
                int rideType,
                String createdAt,
                String updatedAt) {
        this.id = id;
        this.departurePlace = departurePlace;
        this.destination = destination;
        this.meetingPoint = meetingPoint;
        this.freeSeats = freeSeats;
        this.departureTime = departureTime;
        this.price = price;
        this.realtimeDepartureTime = realtimeDepartureTime;
        this.realtimeKm = realtimeKm;
        this.rideOwner = driver;
        this.rideType = rideType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public Ride(ActivitiesRideSearch searchedRide) {
        this.createdAt = searchedRide.getCreatedAt();
        this.departurePlace = searchedRide.getDeparturePlace();
        this.departureTime = searchedRide.getDepartureTime();
        this.destination = searchedRide.getDestination();
        this.rideType = searchedRide.getRideType();
        this.updatedAt = searchedRide.getUpdatedAt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(int freeSeats) {
        this.freeSeats = freeSeats;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRealtimeDepartureTime() {
        return realtimeDepartureTime;
    }

    public void setRealtimeDepartureTime(String realtimeDepartureTime) {
        this.realtimeDepartureTime = realtimeDepartureTime;
    }

    public double getRealtimeKm() {
        return realtimeKm;
    }

    public void setRealtimeKm(double realtimeKm) {
        this.realtimeKm = realtimeKm;
    }

    public User getRideOwner() {
        return rideOwner;
    }

    public void setRideOwner(User driver) {
        this.rideOwner = driver;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isRideRequest() {
        return isRideRequest;
    }

    public void setRideRequest(boolean isRideRequest) {
        this.isRideRequest = isRideRequest;
    }

}
