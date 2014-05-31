package de.tum.mitfahr.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.SearchEvent;
import de.tum.mitfahr.networking.clients.RidesRESTClient;
import de.tum.mitfahr.networking.clients.SearchRESTClient;
import de.tum.mitfahr.networking.models.response.SearchResponse;

/**
 * Created by amr on 31/05/14.
 */
public class SearchService {

    private SharedPreferences mSharedPreferences;
    private SearchRESTClient mSearchRESTClient;
    private Bus mBus;

    public SearchService(final Context context) {
        String baseBackendURL = TUMitfahrApplication.getApplication(context).getBaseURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mSearchRESTClient = new SearchRESTClient(baseBackendURL);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void search(String from, String to, String dateTime) {
        String userAPIKey = mSharedPreferences.getString("api_key", null);
        mSearchRESTClient.search(from, to, dateTime, userAPIKey);
    }

    @Subscribe
    public void onSearchResult(SearchEvent result) {
        if(result.getType() == SearchEvent.Type.RESULT) {
            SearchResponse response = result.getResponse();
            if (null == response.getRides()) {
                mBus.post(new SearchEvent(SearchEvent.Type.SEARCH_FAILED, response));
            } else {
                mBus.post(new SearchEvent(SearchEvent.Type.SEARCH_SUCCESSFUL, response));
            }
        }
    }
}
