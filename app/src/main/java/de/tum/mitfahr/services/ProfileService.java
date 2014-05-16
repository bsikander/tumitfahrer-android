package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.clients.ProfileRESTClient;
import de.tum.mitfahr.networking.events.LoginResultEvent;
import de.tum.mitfahr.networking.events.RegisterResultEvent;
import de.tum.mitfahr.networking.models.response.LoginResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;

/**
 * Created by abhijith on 09/05/14.
 */
public class ProfileService {

    private SharedPreferences mSharedPreferences;
    private ProfileRESTClient mProfileRESTClient;
    private Bus mBus;

    public ProfileService(final Context context) {
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mProfileRESTClient = new ProfileRESTClient(baseBackendURL, mBus);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public ProfileService() {

    }

    public void login(String email, String password) {
        mProfileRESTClient.login(email, password);
        //TODO : create otto event classes and stuff!
    }

    public void register(String email, String firstName, String lastName, String department) {
        mProfileRESTClient.registerUserAccount(email, firstName, lastName, department, true);
        //TODO : create otto event classes and stuff!
    }

    public boolean isLoggedIn() {
        // TODO : check login using the shared preferences! :)
        return false;
    }

    @Subscribe
    public void onLoginResult(LoginResultEvent result) {
        LoginResponse response = result.getResponse();
    }

    @Subscribe
    public void onRegisterResult(RegisterResultEvent result) {
        RegisterResponse response = result.getResponse();
        if (response.status.equals("created")) {

        } else {

        }
    }

}
