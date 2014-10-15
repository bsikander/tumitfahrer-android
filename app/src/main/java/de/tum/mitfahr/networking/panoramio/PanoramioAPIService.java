package de.tum.mitfahr.networking.panoramio;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by abhijith on 09/10/14.
 */
public interface PanoramioAPIService {

    @GET("")
    public PanoramioResponse getPhotos(
            @Query("minx") int minx,
            @Query("miny") int miny,
            @Query("maxx") int maxx,
            @Query("maxy") int maxy
    );
}
