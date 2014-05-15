package de.tum.mitfahr.networking.models.requests;

/**
 * Created by abhijith on 09/05/14.
 */
public class UserRegisterRequestData {

    String email;
    String firstName;
    String lastName;
    String department;
    boolean isStudent;

    public UserRegisterRequestData(String email, String firstName, String lastName, String department, boolean isStudent) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.isStudent = isStudent;
    }
}
