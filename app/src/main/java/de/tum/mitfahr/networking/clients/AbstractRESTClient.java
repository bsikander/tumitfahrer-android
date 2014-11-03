package de.tum.mitfahr.networking.clients;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;

import de.tum.mitfahr.BusProvider;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

/**
 * Created by abhijith on 09/05/14.
 */
public abstract class AbstractRESTClient implements ErrorHandler {

    RestAdapter mRestAdapter;
    String mBaseBackendURL;
    Bus mBus;

    protected AbstractRESTClient(String mBaseBackendURL) {
        this.mBaseBackendURL = mBaseBackendURL;
        this.mBus = BusProvider.getInstance();
        mBus.register(this);
        configureRestAdapter();
    }

    protected void configureRestAdapter() {

        this.mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(mBaseBackendURL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(this)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Content-Type", "application/json;charset=utf-8");
                        request.addHeader("Accept", "application/json;charset=utf-8");
                        request.addHeader("Accept-Language", "en");
                    }
                })
                .setConverter(getGsonConverter())
                .build();
    }

    protected GsonConverter getGsonConverter() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        return new GsonConverter(gson);
    }

    @Override
    public Throwable handleError(RetrofitError retrofitError) {
        // Do some checking with the response here!
        // Response response = retrofitError.getResponse();
        return retrofitError;
    }


}
