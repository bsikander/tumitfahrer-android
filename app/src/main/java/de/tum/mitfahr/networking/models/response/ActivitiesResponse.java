package de.tum.mitfahr.networking.models.response;

import java.util.List;

import de.tum.mitfahr.networking.models.Activities;

/**
 * Created by amr on 02/07/14.
 */
public class ActivitiesResponse {

    private String status;
    private String message;
    private Activities activities;

    public ActivitiesResponse(String status, String message, Activities activities) {
        this.status = status;
        this.message = message;
        this.activities = activities;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Activities getActivities() {
        return activities;
    }
}
