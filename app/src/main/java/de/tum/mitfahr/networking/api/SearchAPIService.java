package de.tum.mitfahr.networking.api;

import de.tum.mitfahr.networking.models.requests.SearchRequest;
import de.tum.mitfahr.networking.models.response.SearchResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by amr on 31/05/14.
 */
public interface SearchAPIService {

    @POST("/search")
    public void search(
            @Header("apiKey") String apiKey,
            @Body SearchRequest searchRequest,
            Callback<SearchResponse> callback
    );
}
