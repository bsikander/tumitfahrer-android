package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.events.ForgotPasswordEvent;
import de.tum.mitfahr.events.GetUserEvent;
import de.tum.mitfahr.events.LoginEvent;
import de.tum.mitfahr.events.RegisterEvent;
import de.tum.mitfahr.events.UpdateUserEvent;
import de.tum.mitfahr.networking.BackendUtil;
import de.tum.mitfahr.networking.api.SessionAPIService;
import de.tum.mitfahr.networking.api.UserAPIService;
import de.tum.mitfahr.networking.events.RequestFailedEvent;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.networking.models.requests.ForgotPasswordRequest;
import de.tum.mitfahr.networking.models.requests.RegisterRequest;
import de.tum.mitfahr.networking.models.requests.UpdateUserRequest;
import de.tum.mitfahr.networking.models.response.ForgotPasswordResponse;
import de.tum.mitfahr.networking.models.response.GetUserResponse;
import de.tum.mitfahr.networking.models.response.LoginResponse;
import de.tum.mitfahr.networking.models.response.RegisterResponse;
import de.tum.mitfahr.networking.models.response.UpdateUserResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by abhijith on 09/05/14.
 */
public class ProfileRESTClient extends AbstractRESTClient {

    private UserAPIService userAPIService;

    public ProfileRESTClient(String mBaseBackendURL) {
        super(mBaseBackendURL);
        userAPIService = mRestAdapter.create(UserAPIService.class);
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
            mBus.post(new LoginEvent(LoginEvent.Type.LOGIN_RESULT, loginResponse, response.getStatus()));
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            mBus.post(new LoginEvent(LoginEvent.Type.LOGIN_FAILED, null, 400));
            mBus.post(new RequestFailedEvent());
        }
    };

    public User getUserSynchronous(int someUserId, String userAPIKey) {
        GetUserResponse response = userAPIService.getUserSynchronous(userAPIKey, someUserId);
        if (null != response && null != response.getUser()) {
            return response.getUser();
        }
        return null;
    }

    public void getSomeUser(int someUserId, String userAPIKey) {
        userAPIService.getSomeUser(userAPIKey, someUserId, getUserCallback);
    }

    private Callback<GetUserResponse> getUserCallback = new Callback<GetUserResponse>() {
        @Override
        public void success(GetUserResponse getUserResponse, Response response) {
            mBus.post(new GetUserEvent(GetUserEvent.Type.RESULT, getUserResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void updateUser(int userId, UpdateUserRequest updateUserRequest, String email, String password) {
        userAPIService.updateUser(BackendUtil.getLoginHeader(email, password), userId, updateUserRequest, updateUserCallback);
    }

    private Callback<UpdateUserResponse> updateUserCallback = new Callback<UpdateUserResponse>() {
        @Override
        public void success(UpdateUserResponse updateUserResponse, Response response) {
            mBus.post(new UpdateUserEvent(UpdateUserEvent.Type.RESULT, updateUserResponse, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };

    public void forgotPassword(String email) {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        userAPIService.forgotPassword(request, forgotPasswordCallback);
    }

    private Callback<ForgotPasswordResponse> forgotPasswordCallback = new Callback<ForgotPasswordResponse>() {
        @Override
        public void success(ForgotPasswordResponse o, Response response) {
            mBus.post(new ForgotPasswordEvent(ForgotPasswordEvent.Type.RESULT, response));
        }

        @Override
        public void failure(RetrofitError error) {
            mBus.post(new RequestFailedEvent());
        }
    };
}
