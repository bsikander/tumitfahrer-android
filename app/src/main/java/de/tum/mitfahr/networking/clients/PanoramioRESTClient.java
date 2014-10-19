package de.tum.mitfahr.networking.clients;

import de.tum.mitfahr.networking.panoramio.PanoramioAPIService;
import de.tum.mitfahr.networking.panoramio.PanoramioPhoto;
import de.tum.mitfahr.networking.panoramio.PanoramioResponse;

/**
 * Created by abhijith on 09/10/14.
 */
public class PanoramioRESTClient extends AbstractRESTClient {

    private PanoramioAPIService panoramioAPIService;

    public PanoramioRESTClient(String mBaseBackendURL) {
        super(mBaseBackendURL);
        panoramioAPIService = mRestAdapter.create(PanoramioAPIService.class);
    }

    public PanoramioPhoto getPhoto(int minx, int miny, int maxx, int maxy) {
        PanoramioResponse response = panoramioAPIService.getPhotos
                ("public", 0, 20, minx, miny, maxx, maxy, "medium", true);
        if (response != null) {
            if (response.getCount() > 0) {
                return response.getPhotos()[0];
            } else {
                return null;
            }
        }
        return null;
    }

}
