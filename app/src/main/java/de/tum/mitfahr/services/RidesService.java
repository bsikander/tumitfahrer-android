package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.LoginFailedEvent;
import de.tum.mitfahr.events.LoginSuccessfulEvent;
import de.tum.mitfahr.events.OfferRideFailedEvent;
import de.tum.mitfahr.events.RideAddedEvent;
import de.tum.mitfahr.networking.clients.ProfileRESTClient;
import de.tum.mitfahr.networking.clients.RidesRESTClient;
import de.tum.mitfahr.networking.events.LoginResultEvent;
import de.tum.mitfahr.networking.events.OfferRideResultEvent;
import de.tum.mitfahr.networking.models.response.LoginResponse;
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

    public void offerRide(String departure, String destination, String meetingPoint, String freeSeats, String dateTime) {
        int userId = mSharedPreferences.getInt("id", 0);
        String userAPIKey = mSharedPreferences.getString("api_key", null);
        mRidesRESTClient.offerRide(departure, destination, meetingPoint, freeSeats, dateTime, userAPIKey, userId);
        //TODO : create otto event classes and stuff!
    }

    @Subscribe
    public void onOfferRidesResult(OfferRideResultEvent result) {
        OfferRideResponse response = result.getResponse();
        if (null == response.getRide()) {
            mBus.post(new OfferRideFailedEvent());
        } else {
            mBus.post(new RideAddedEvent());
        }
    }
}
