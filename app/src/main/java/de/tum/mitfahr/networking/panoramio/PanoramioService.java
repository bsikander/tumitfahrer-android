package de.tum.mitfahr.networking.panoramio;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.clients.PanoramioRESTClient;

/**
 * Created by abhijith on 09/10/14.
 */
public class PanoramioService {

    private SharedPreferences mSharedPreferences;
    private PanoramioRESTClient mPanoramioRESTClient;
    private Bus mBus;

    public PanoramioService(Context context) {
        String baseURL = TUMitfahrApplication.getApplication(context).getPanoramioURLBackend();
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mPanoramioRESTClient = new PanoramioRESTClient(baseURL);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public PanoramioPhoto getPhoto(int minx, int miny, int maxx, int maxy) {
        return mPanoramioRESTClient.getPhoto(minx, miny, maxx, maxy);
    }

}
