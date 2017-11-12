package domain.escapetodayclient;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FETCH_ADDRESS= "domain.escapetodayclient.action.FETCH_ADDRESS";

    public static final String LOCATION_X = "x";
    public static final String LOCATION_Y = "y";
    public static final String RECEIVER = "stuff";

    public static final class Codes {
        public static final int SUCCESS = 0;
        public static final int FAILURE = 1;
    }

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetch(Context context, ResultReceiver r, double x, double y) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.setAction(ACTION_FETCH_ADDRESS);
        intent.putExtra(LOCATION_X, x);
        intent.putExtra(LOCATION_Y, y);
        intent.putExtra(RECEIVER, r);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mReceiver = (ResultReceiver) intent.getParcelableExtra(RECEIVER);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                double lat = intent.getDoubleExtra(LOCATION_Y, 0);
                double lng = intent.getDoubleExtra(LOCATION_X, 0);
                Log.i("LOCATION", lat + " " + lng);
                List<Address> l = geocoder.getFromLocation(lat, lng, 1);
                        String city = "Manchester";
                Log.i("SIZE", Integer.valueOf(l.size()).toString());
                if(!Geocoder.isPresent())
                  Log.w("!!!!!!!!!!!!", "NOT PRESENT");
                if (l.size() > 0){
                        city = l.get(0)
                        .getAddressLine(1);
                        Log.i("GOOD city: ", city);
                }
                Bundle b = new Bundle();
                b.putString("city", city);
                b.putBoolean("err", false);
                mReceiver.send(0, b);
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getCanonicalName()).warning("failed to get location err: " + e.getMessage());
                Bundle b = new Bundle();
                b.putBoolean("err", true);
                mReceiver.send(1, b);
            }
        }
    }
}
