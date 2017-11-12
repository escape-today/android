package domain.escapetodayclient;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
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
    private static final String ACTION_FETCH_ADDRESS= "domain.escapetodayclient.action.FETCH_ADDRESS";

    private static final String LOCATION_X = "0";
    private static final String LOCATION_Y = "0";

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetch(Context context, String x, String y) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.setAction(ACTION_FETCH_ADDRESS);
        intent.putExtra(LOCATION_X, x);
        intent.putExtra(LOCATION_Y, y);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                String city = geocoder.getFromLocation(0, 0, 1).get(0).getAddressLine(0);
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
