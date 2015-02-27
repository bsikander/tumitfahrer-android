package de.tum.mitfahr.networking.models.response;

import de.tum.mitfahr.networking.models.Activities;

/**
 * Created by amr on 02/07/14.
 */
public class ActivitiesResponse {

    private String status;
    private Activities activities;

    public ActivitiesResponse(String status, String message, Activities activities) {
        this.status = status;
        this.activities = activities;
    }

    public String getStatus() {
        return status;
    }

    public Activities getActivities() {
        return activities;
    }
}
