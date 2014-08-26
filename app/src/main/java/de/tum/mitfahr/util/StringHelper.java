package de.tum.mitfahr.util;

/**
 * Created by abhijith on 19/08/14.
 */
public class StringHelper {

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        if (str.trim().length() == 0) {
            return true;
        }
        if (str.equals("")) {
            return true;
        }
        if (str.equals("null") || str.equals("NULL")) {
            return true;
        }
        return false;
    }

}
