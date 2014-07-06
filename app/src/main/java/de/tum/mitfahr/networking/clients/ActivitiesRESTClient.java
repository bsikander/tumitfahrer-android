package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.events.GetActivitiesEvent;
import de.tum.mitfahr.events.GetBadgesEvent;
import de.tum.mitfahr.networking.api.ActivitiesAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.response.ActivitiesResponse;
import de.tum.mitfahr.networking.models.response.BadgesResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 02/07/14.
 */
public class ActivitiesRESTClient extends AbstractRESTClient {

    private ActivitiesAPIService activitiesAPIService;

    public ActivitiesRESTClient(String mBaseBackendURL) {

        super(mBaseBackendURL);
        this.activitiesAPIService = mRestAdapter.create(ActivitiesAPIService.class);
    }

    public void getActivities(String userAPIKey) {
        activitiesAPIService.getActivities(userAPIKey, getActivitiesCallback);
    }

    private Callback<ActivitiesResponse> getActivitiesCallback = new Callback<ActivitiesResponse>() {
        @Override
        public void success(ActivitiesResponse activitiesResponse, Response response) {
            mBus.post(new GetActivitiesEvent(GetActivitiesEvent.Type.RESULT,
                    activitiesResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void getBadges(String userAPIKey, String campusUpdatedAt, String activityUpdatedAt,
                          String timelineUpdatedAt, String myRidesUpdatedAt, int userId) {
        activitiesAPIService.getBadges(userAPIKey, campusUpdatedAt, activityUpdatedAt,
                timelineUpdatedAt, myRidesUpdatedAt, userId, getBadgesCallback);
    }

    private Callback<BadgesResponse> getBadgesCallback = new Callback<BadgesResponse>() {
        @Override
        public void success(BadgesResponse badgesResponse, Response response) {
            mBus.post(new GetBadgesEvent(GetBadgesEvent.Type.RESULT, badgesResponse));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
