package de.tum.mitfahr.networking.clients;

import java.util.ArrayList;

import de.tum.mitfahr.events.SearchEvent;
import de.tum.mitfahr.networking.api.SearchAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.requests.SearchRequest;
import de.tum.mitfahr.networking.models.response.SearchResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by amr on 31/05/14.
 */
public class SearchRESTClient extends AbstractRESTClient {

    public SearchRESTClient(String mBaseBackendURL) {
        super(mBaseBackendURL);
    }

    public void search(String userAPIKey, String from, int fromThreshold, String to,
                       int toThreshold, String dateTime, int rideType) {
        SearchRequest requestData = new SearchRequest(from, fromThreshold, to, toThreshold,
                dateTime, rideType);
        SearchAPIService searchAPIService = mRestAdapter.create(SearchAPIService.class);
        searchAPIService.search(userAPIKey, requestData, searchCallback);
    }

    private Callback<SearchResponse> searchCallback = new Callback<SearchResponse>() {

        @Override
        public void success(SearchResponse searchResponse, Response response) {
            if (response.getStatus() == 204) {
                mBus.post(new SearchEvent(SearchEvent.Type.RESULT, new SearchResponse("", "", new ArrayList<Ride>())));
            } else {
                mBus.post(new SearchEvent(SearchEvent.Type.RESULT, searchResponse));
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
