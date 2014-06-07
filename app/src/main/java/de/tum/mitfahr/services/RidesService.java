package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.networking.clients.RidesRESTClient;
import de.tum.mitfahr.networking.models.response.OfferRideResponse;

/**
 * Created by amr on 18/05/14.
 */
public class RidesService {

    private SharedPreferences mSharedPreferences;
    private RidesRESTClient mRidesRESTClient;
    private Bus mBus;

    public RidesService(final Context context) {
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mRidesRESTClient = new RidesRESTClient(baseBackendURL);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void offerRide(String departure, String destination, String meetingPoint, String freeSeats, String dateTime, int rideType) {
        int userId = mSharedPreferences.getInt("id", 0);
        String userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.offerRide(departure, destination, meetingPoint, freeSeats, dateTime, userAPIKey, rideType, userId);
        //TODO : create otto event classes and stuff!
    }

    @Subscribe
    public void onOfferRidesResult(OfferRideEvent result) {
        if(result.getType() == OfferRideEvent.Type.RESULT) {
            OfferRideResponse response = result.getResponse();
            if (null == response.getRide()) {
                mBus.post(new OfferRideEvent(OfferRideEvent.Type.OFFER_RIDE_FAILED));
            } else {
                mBus.post(new OfferRideEvent(OfferRideEvent.Type.RIDE_ADDED, response.getRide()));
            }
        }
    }
}
