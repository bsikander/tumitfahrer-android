package de.tum.mitfahr.networking.clients;

import org.json.JSONObject;

import de.tum.mitfahr.networking.BackendUtil;
import de.tum.mitfahr.networking.api.UserAPIService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by abhijith on 12/05/14.
 */
public class UserRESTClient extends AbstractRESTClient {

    protected UserRESTClient(String mBaseBackendURL) {
        super(mBaseBackendURL);
    }

    public void geUser(String username) {
        UserAPIService userAPIService = mRestAdapter.create(UserAPIService.class);
        userAPIService.getUser(BackendUtil.getAPIKey(), username, new Callback<JSONObject>() {
            @Override
            public void success(JSONObject jsonObject, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    public void updateUser() {

    }

}
