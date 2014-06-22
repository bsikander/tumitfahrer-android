package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.GetUserEvent;
import de.tum.mitfahr.events.LoginEvent;
import de.tum.mitfahr.events.RegisterEvent;
import de.tum.mitfahr.events.UpdateUserEvent;
import de.tum.mitfahr.networking.clients.ProfileRESTClient;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.networking.models.requests.UpdateUserRequest;
import de.tum.mitfahr.networking.models.response.GetUserResponse;
import de.tum.mitfahr.networking.models.response.LoginResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;
import de.tum.mitfahr.networking.models.response.UpdateUserResponse;

/**
 * Created by abhijith on 09/05/14.
 */
public class ProfileService {

    private SharedPreferences mSharedPreferences;
    private ProfileRESTClient mProfileRESTClient;
    private Bus mBus;
    private String userAPIKey;
    private int userId;

    public ProfileService(final Context context) {
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mProfileRESTClient = new ProfileRESTClient(baseBackendURL);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = mSharedPreferences.getInt("id", 0);
        userAPIKey = mSharedPreferences.getString("api_key", null);
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

    public void getSomeUser(int someUserId) {
        mProfileRESTClient.getSomeUser(someUserId, userAPIKey);
    }

    @Subscribe
    public void onGetSomeUserResult(GetUserEvent result) {
        if(result.getType() == GetUserEvent.Type.RESULT) {
            GetUserResponse response = result.getGetUserResponse();
            if (null == response.getUser()) {
                mBus.post(new GetUserEvent(GetUserEvent.Type.GET_FAILED, response, result.getRetrofitResponse()));
            } else {
                mBus.post(new GetUserEvent(GetUserEvent.Type.GET_SUCCESSFUL, response, result.getRetrofitResponse()));
            }
        }
    }

    public void updateUser(User updatedUser, String email, String password, String passwordConfirmation) {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(updatedUser, password, passwordConfirmation);
        mProfileRESTClient.updateUser(userId, updateUserRequest, email, password);
    }

    @Subscribe
    public void onUpdateUserResult(UpdateUserEvent result) {
        if(result.getType() == UpdateUserEvent.Type.RESULT) {
            UpdateUserResponse response = result.getUpdateUserResponse();
            if (null == response.getUser()) {
                mBus.post(new UpdateUserEvent(UpdateUserEvent.Type.UPDATE_FAILED, response, result.getRetrofitResponse()));
            } else {
                mBus.post(new UpdateUserEvent(UpdateUserEvent.Type.USER_UPDATED, response, result.getRetrofitResponse()));
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
