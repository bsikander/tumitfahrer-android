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
    private User driver;
    private int rideType;
    private String createdAt;
    private String updatedAt;

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
        this.driver = driver;
        this.rideType = rideType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
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
}
