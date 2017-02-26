package cse110.grocerysaver.database;

import android.net.Uri;

/**
 * Created by Philip on 2/24/17.
 *
 * Class ProviderContract defines constants used for registering class DataProvider as a content
 * provider to Android.
 */

public class ProviderContract {
    private ProviderContract() {};

    public final static String AUTHORITY = "com.cse110.GrocerySaver.provider";

    private final static String SCHEME = "content";
    private final static Uri.Builder builder = new Uri.Builder();

    static {
        builder.scheme(SCHEME).authority(AUTHORITY);
    }

    public static Uri uriForTable(String table) {
        Uri uri = builder.build().buildUpon().appendPath(table).build();
        return uri;
    }

}
