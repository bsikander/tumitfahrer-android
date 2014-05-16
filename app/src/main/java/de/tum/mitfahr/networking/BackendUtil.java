package de.tum.mitfahr.networking;

import android.util.Base64;

import java.security.Security;

import de.tum.mitfahr.util.Crypto;

/**
 * Created by abhijith on 09/05/14.
 */
public class BackendUtil {

    public static String getLoginHeader(String username, String password) {
        String credentials = username.trim() + ":" + Crypto.sha512(password.trim());
        return "Basic "+ Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }


    public static String getAPIKey() {
        return null;
    }
}
