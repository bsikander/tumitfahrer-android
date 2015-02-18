package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.events.SendFeedbackEvent;
import de.tum.mitfahr.networking.api.FeedbackAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.FeedbackRequest;
import de.tum.mitfahr.networking.models.response.FeedbackResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 07.07.14.
 */
public class FeedbackRESTClient extends AbstractRESTClient {

    private FeedbackAPIService feedbackAPIService;
    private Callback sendFeedbackCallback = new Callback<FeedbackResponse>() {

        @Override
        public void success(FeedbackResponse feedbackResponse, Response response) {
            if (response.getStatus() == 201)
                mBus.post(new SendFeedbackEvent(SendFeedbackEvent.Type.SUCCESSFUL, response));
            else
                mBus.post(new SendFeedbackEvent(SendFeedbackEvent.Type.FAILED, response));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public FeedbackRESTClient(String mBaseBackendURL) {
        super(mBaseBackendURL);
        feedbackAPIService = mRestAdapter.create(FeedbackAPIService.class);
    }

    public void sendFeedback(String userAPIKey, FeedbackRequest feedback) {
        feedbackAPIService.sendFeedback(userAPIKey, feedback, sendFeedbackCallback);
    }
}
