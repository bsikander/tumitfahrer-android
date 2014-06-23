package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.clients.RatingsRESTClient;

/**
 * Created by amr on 23/06/14.
 */
public class RatingsService {

    private SharedPreferences mSharedPreferences;
    private RatingsRESTClient mRatingsRESTClient;
    private Bus mBus;
    private String userAPIKey;
    private int userId;

    public RatingsService(final Context context) {
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mRatingsRESTClient = new RatingsRESTClient(baseBackendURL);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
    }
}
