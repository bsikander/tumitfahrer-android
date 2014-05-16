package de.tum.mitfahr.networking.models.requests;

/**
 * Created by abhijith on 09/05/14.
 */
public class RegisterRequest {

    String email;
    String firstName;
    String lastName;
    String department;

    public RegisterRequest(String email, String firstName, String lastName, String department, boolean isStudent) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
    }
}
