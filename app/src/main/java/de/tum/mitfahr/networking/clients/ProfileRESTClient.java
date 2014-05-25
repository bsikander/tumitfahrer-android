package de.tum.mitfahr.networking.clients;

import android.util.Log;

import com.squareup.otto.Bus;

import org.json.JSONObject;

import de.tum.mitfahr.events.LoginEvent;
import de.tum.mitfahr.events.RegisterEvent;
import de.tum.mitfahr.networking.BackendUtil;
import de.tum.mitfahr.networking.api.SessionAPIService;
import de.tum.mitfahr.networking.api.UserAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.requests.RegisterRequest;
import de.tum.mitfahr.networking.models.response.LoginResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by abhijith on 09/05/14.
 */
public class ProfileRESTClient extends AbstractRESTClient {


    public ProfileRESTClient(String mBaseBackendURL) {
        super(mBaseBackendURL);
    }

    public void registerUserAccount(final String email,
                                    final String firstName,
                                    final String lastName,
                                    final String department,
                                    final boolean isStudent) {
        RegisterRequest requestData = new RegisterRequest(email, firstName, lastName, department, isStudent);
        UserAPIService userAPIService = mRestAdapter.create(UserAPIService.class);
        userAPIService.registerUser(requestData, registerCallback);
    }

    private Callback<RegisterResponse> registerCallback = new Callback<RegisterResponse>() {
        @Override
        public void success(RegisterResponse registerResponse, Response response) {
            // Post an event based on success on the BUS! :)
            mBus.post(new RegisterEvent(RegisterEvent.Type.REGISTER_RESULT, registerResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            // Post an error! :)
        }
    };

    public void login(final String email, final String password) {
        SessionAPIService sessionAPIService = mRestAdapter.create(SessionAPIService.class);
        sessionAPIService.loginUser(BackendUtil.getLoginHeader(email, password), loginCallback);
    }

    private Callback<LoginResponse> loginCallback = new Callback<LoginResponse>() {

        @Override
        public void success(LoginResponse loginResponse, Response response) {
            mBus.post(new LoginEvent(LoginEvent.Type.LOGIN_RESULT, loginResponse));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
