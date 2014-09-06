package de.tum.mitfahr;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;

import de.tum.mitfahr.services.ActivitiesService;
import de.tum.mitfahr.services.ConversationsService;
import de.tum.mitfahr.services.FeedbackService;
import de.tum.mitfahr.services.MessagesService;
import de.tum.mitfahr.services.ProfileService;
import de.tum.mitfahr.services.RatingsService;
import de.tum.mitfahr.services.RidesService;
import de.tum.mitfahr.services.SearchService;

/**
 * Created by abhijith on 09/05/14.
 */
public class TUMitfahrApplication extends Application {

    private static final String BASE_BACKEND_URL = "http://tumitfahrer-staging.herokuapp.com/api/v2";

    private Bus mBus = BusProvider.getInstance();

    private ProfileService mProfileService;
    private RidesService mRidesService;
    private SearchService mSearchService;

    private ActivitiesService mActivitiesService;
    private MessagesService mMessagesService;
    private ConversationsService mConversationsService;
    private FeedbackService mFeedbackService;
    private RatingsService mRatingsService;

    @Override
    public void onCreate() {
        super.onCreate();
        mBus.register(this);
        mProfileService = new ProfileService(this);
        mRidesService = new RidesService(this);
        mSearchService = new SearchService(this);
        mActivitiesService = new ActivitiesService(this);
    }

    public ProfileService getProfileService() {
        return mProfileService;
    }

    public RidesService getRidesService() {
        return mRidesService;
    }

    public SearchService getSearchService() {
        return mSearchService;
    }

    public ActivitiesService getActivitiesService() {
        return mActivitiesService;
    }

    public MessagesService getMessagesService(){
        return mMessagesService;
    }

    public ConversationsService getConversationsService(){
        return mConversationsService;
    }

    public FeedbackService getFeedbackService(){
        return mFeedbackService;
    }

    public RatingsService getRatingsService(){
        return mRatingsService;
    }

    public String getBaseURLBackend() {
        return BASE_BACKEND_URL;
    }

    public static TUMitfahrApplication getApplication(final Context context) {
        return (TUMitfahrApplication) context.getApplicationContext();
    }
}
