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
    private String password;
    private String passwordConfirmation;

    public UpdateUserRequest(User user, String password, String passwordConfirmation) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.department = "departmentNo(" + user.getDepartment() + ")";
        this.car = user.getCar();
        this.isStudent = user.isStudent();
        this.apiKey = user.getApiKey();
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
}
