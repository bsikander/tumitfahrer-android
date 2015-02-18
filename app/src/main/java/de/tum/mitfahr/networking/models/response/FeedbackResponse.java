package de.tum.mitfahr.networking.models.response;

/**
 * Created by abhijith on 30/11/14.
 */
public class FeedbackResponse {

    private String message;

    public FeedbackResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
