package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.events.SendFeedbackEvent;
import de.tum.mitfahr.networking.api.FeedbackAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.FeedbackRequest;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 07.07.14.
 */
public class FeedbackRESTClient extends AbstractRESTClient {

    private FeedbackAPIService feedbackAPIService;

    public FeedbackRESTClient(String mBaseBackendURL) {

        super(mBaseBackendURL);
        feedbackAPIService = mRestAdapter.create(FeedbackAPIService.class);
    }

    public void sendFeedback(String userAPIKey, FeedbackRequest feedback) {
        feedbackAPIService.sendFeedback(userAPIKey, feedback, sendFeedbackCallback);
    }

    private Callback sendFeedbackCallback = new Callback() {
        @Override
        public void success(Object o, Response response) {
            mBus.post(new SendFeedbackEvent(SendFeedbackEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
