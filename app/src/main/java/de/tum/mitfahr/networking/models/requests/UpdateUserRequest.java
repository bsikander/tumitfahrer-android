package de.tum.mitfahr.networking.models.requests;

import de.tum.mitfahr.networking.models.User;

/**
 * Created by amr on 22/06/14.
 */
public class UpdateUserRequest {
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
    private String password;
    private String passwordConfirmation;

    public UpdateUserRequest(User user, String password, String passwordConfirmation) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.department = user.getDepartment();
        this.car = user.getCar();
        this.isStudent = user.isStudent();
        this.apiKey = user.getApiKey();
        this.ratingAverage = user.getRatingAverage();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
}
