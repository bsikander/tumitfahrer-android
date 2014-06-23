package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.networking.api.RatingsAPIService;

/**
 * Created by amr on 23/06/14.
 */
public class RatingsRESTClient extends AbstractRESTClient {

    private RatingsAPIService ratingsAPIService;

    public RatingsRESTClient(String mBaseBackendURL) {

        super(mBaseBackendURL);
        ratingsAPIService = mRestAdapter.create(RatingsAPIService.class);
    }
}
