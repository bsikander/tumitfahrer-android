package de.tum.mitfahr.networking.panoramio;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by abhijith on 09/10/14.
 */
public interface PanoramioAPIService {

    @GET("/get_panoramas.php")
    public PanoramioResponse getPhotos(
            @Query("set") String set,
            @Query("from") int from,
            @Query("to") int to,
            @Query("minx") int minx,
            @Query("miny") int miny,
            @Query("maxx") int maxx,
            @Query("maxy") int maxy,
            @Query("size") String medium,
            @Query("mapfilter") boolean mapfilter
    );
}
