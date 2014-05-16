package de.tum.mitfahr.networking.models;

/**
 * Created by abhijith on 16/05/14.
 */
public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String department;
    private String car;
    private boolean isStudent;
    private String apiKey;
    private int ratingAverage;
    private String createdAt;
    private String updatedAt;

    public User(int id, String firstName, String lastName, String email, String phoneNumber, String department, String car, boolean isStudent, String apiKey, int ratingAverage, String createdAt, String updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.car = car;
        this.isStudent = isStudent;
        this.apiKey = apiKey;
        this.ratingAverage = ratingAverage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", department='" + department + '\'' +
                ", car='" + car + '\'' +
                ", isStudent=" + isStudent +
                ", apiKey='" + apiKey + '\'' +
                ", ratingAverage=" + ratingAverage +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
