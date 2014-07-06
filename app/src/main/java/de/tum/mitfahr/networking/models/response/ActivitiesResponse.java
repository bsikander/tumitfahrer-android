package de.tum.mitfahr.networking.models.response;

import java.util.ArrayList;

import de.tum.mitfahr.networking.models.Activity;

/**
 * Created by amr on 02/07/14.
 */
public class ActivitiesResponse {

    private String status;
    private String message;
    private ArrayList<Activity> activities;

    public ActivitiesResponse(String status, String message, ArrayList<Activity> activities) {
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

    public ArrayList<Activity> getActivities() {
        return activities;
    }
}
