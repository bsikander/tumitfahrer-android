package de.tum.mitfahr.networking.models.requests;

/**
 * Created by amr on 07.07.14.
 */
public class FeedbackRequest {

    private int userId;
    private String title;
    private String content;

    public FeedbackRequest(int userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }
}
