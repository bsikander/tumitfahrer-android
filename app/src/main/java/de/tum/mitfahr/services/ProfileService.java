package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.LoginEvent;
import de.tum.mitfahr.events.RegisterEvent;
import de.tum.mitfahr.networking.clients.ProfileRESTClient;
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
    public void onLoginResult(LoginEvent result) {
        if (result.getType() == LoginEvent.Type.LOGIN_RESULT) {
            LoginResponse response = result.getResponse();
            if (null == response.getUser()) {
                mBus.post(new LoginEvent(LoginEvent.Type.LOGIN_FAILED));
            } else {
                addUserToSharedPreferences(response.getUser());
                mBus.post(new LoginEvent(LoginEvent.Type.LOGIN_SUCCESSFUL));
            }
        }
    }

    @Subscribe
    public void onRegisterResult(RegisterEvent result) {
        if (result.getType() == RegisterEvent.Type.REGISTER_RESULT) {
            RegisterResponse response = result.getResponse();
            if (null != response.getStatus() && response.getStatus().equals("bad_request")) {
                mBus.post(new RegisterEvent(RegisterEvent.Type.REGISTER_FAILED));
            } else {
                mBus.post(new RegisterEvent(RegisterEvent.Type.REGISTER_SUCCESSFUL));
            }
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
