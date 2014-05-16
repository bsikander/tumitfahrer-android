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
import de.tum.mitfahr.events.RegisterFailedEvent;
import de.tum.mitfahr.events.RegisterSuccessfulEvent;
import de.tum.mitfahr.networking.clients.ProfileRESTClient;
import de.tum.mitfahr.networking.events.LoginResultEvent;
import de.tum.mitfahr.networking.events.RegisterResultEvent;
import de.tum.mitfahr.networking.models.User;
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
        mProfileRESTClient = new ProfileRESTClient(baseBackendURL);
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
        String apiKey = mSharedPreferences.getString("api_key", null);
        return apiKey != null;
    }

    @Subscribe
    public void onLoginResult(LoginResultEvent result) {
        LoginResponse response = result.getResponse();
        if (null == response.getUser()) {
            mBus.post(new LoginFailedEvent());
        } else {
            addUserToSharedPreferences(response.getUser());
            mBus.post(new LoginSuccessfulEvent());
        }
    }

    @Subscribe
    public void onRegisterResult(RegisterResultEvent result) {
        RegisterResponse response = result.getResponse();
        if (null != response.getStatus() && response.getStatus().equals("bad_request")) {
            mBus.post(new RegisterFailedEvent());
        } else {
            mBus.post(new RegisterSuccessfulEvent());
        }
    }

    private void addUserToSharedPreferences(User user) {
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        prefEditor.putInt("id", user.getId());
        prefEditor.putString("name", user.getFirstName() + " " + user.getLastName());
        prefEditor.putString("email", user.getEmail());
        prefEditor.putString("api_key", user.getApiKey());
        prefEditor.commit();
    }

}
