package de.tum.mitfahr.networking.events;

import de.tum.mitfahr.networking.models.response.OfferRideResponse;

/**
 * Created by amr on 18/05/14.
 */
public class OfferRideResultEvent {

    OfferRideResponse response;

    public OfferRideResultEvent(OfferRideResponse response) {
        this.response = response;
    }

    public OfferRideResponse getResponse() {
        return response;
    }
}
