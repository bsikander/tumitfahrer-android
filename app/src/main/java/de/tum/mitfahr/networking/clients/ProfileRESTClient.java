package de.tum.mitfahr.networking.clients;

import com.squareup.otto.Bus;

import org.json.JSONObject;

import de.tum.mitfahr.networking.BackendUtil;
import de.tum.mitfahr.networking.api.SessionAPIService;
import de.tum.mitfahr.networking.api.UserAPIService;
import de.tum.mitfahr.networking.models.requests.UserRegisterRequestData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by abhijith on 09/05/14.
 */
public class ProfileRESTClient extends AbstractRESTClient {


    public ProfileRESTClient(String mBaseBackendURL, Bus mBus) {
        super(mBaseBackendURL, mBus);
    }

    public void registerUserAccount(final String email,
                                    final String firstName,
                                    final String lastName,
                                    final String department,
                                    final boolean isStudent) {
        UserRegisterRequestData requestData = new UserRegisterRequestData(email, firstName, lastName, department, isStudent);
        UserAPIService userAPIService = mRestAdapter.create(UserAPIService.class);
        userAPIService.registerUser(BackendUtil.getCredentials(), requestData, registerCallback);
    }

    private Callback<JSONObject> registerCallback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            // Post an event based on success on the BUS! :)
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            // Post an error! :)
        }
    };

    public void login(final String email, final String password) {
        SessionAPIService sessionAPIService = mRestAdapter.create(SessionAPIService.class);
        sessionAPIService.loginUser(BackendUtil.getHeader(email, password), loginCallback);
    }

    private Callback<JSONObject> loginCallback = new Callback<JSONObject>() {
        @Override
        public void success(JSONObject jsonObject, Response response) {
            // Post an event based on success on the BUS! :)
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            // Post an error! :)
        }
    };
}
