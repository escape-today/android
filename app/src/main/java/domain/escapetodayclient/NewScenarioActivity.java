package domain.escapetodayclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.logging.Logger;

public class NewScenarioActivity extends AppCompatActivity implements LocationListener{

    JSONArray jsonData = new JSONArray();

    LocationManager mLocationManager;

    //String locationString = getLocation();
    ScenarioItemAdapter mAdapter = new ScenarioItemAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        setContentView(R.layout.activity_new_scenario);
        RecyclerView rv = findViewById(R.id.recycler_view_new_scenario);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);

        // sort our recycler view
        rv.setLayoutManager(lm);
        rv.setAdapter(mAdapter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    NewScenarioActivity.Codes.LOCATION_REQUEST);
        } else {
            Location l = mLocationManager.getLastKnownLocation(Context.LOCATION_SERVICE);
            if (l != null) {
                Toast.makeText(this, "L " + l.getLatitude() + ", " + l.getLongitude() , Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "location 1 not found", Toast.LENGTH_LONG).show();
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }

    public void onLocationChanged(Location location) {
        Log.i("LOCATION", "CHANGED LOCATION");
        if (location != null) {
            Toast.makeText(this, "L " + location.getLatitude() + ", " + location.getLongitude() , Toast.LENGTH_LONG).show();
            mLocationManager.removeUpdates(this);

            FetchAddressIntentService.startActionFetch(this, new AddressResultReciever(new android.os.Handler()), location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderEnabled(String s) {}
    @Override
    public void onProviderDisabled(String s) {}

    private class ScenarioItemAdapter extends RecyclerView.Adapter<ScenarioItemAdapter.ScenarioItemViewHolder> {

        @Override
        public ScenarioItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scenario_item, parent, false);

            // create a view holder
            return new ScenarioItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ScenarioItemViewHolder holder, int position) {
            // use position to fill the view holder with data
            // i.e. holder.name.setText(nameList.get(position))
            try {
                JSONObject obj = jsonData.getJSONObject(position);
                holder.price.setText(Integer.valueOf(obj.getInt("MinPrice")).toString());
                holder.direct.setText(Boolean.valueOf(obj.getBoolean("Direct")).toString());
                holder.origin.setText(obj.getJSONObject("OutboundLeg").getString("Origin"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Toast.makeText(getApplicationContext(), "P: " + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public int getItemCount() {
            // data.size()
            return jsonData.length(); //jsonData.length();
        }

        protected class ScenarioItemViewHolder extends RecyclerView.ViewHolder {

            // things our adaptor can access
            public TextView price, direct, origin;

            protected ScenarioItemViewHolder(View itemView) {
                super(itemView);
                price = itemView.findViewById(R.id.scenario_item_price);
                direct = itemView.findViewById(R.id.scenario_item_direct);
                origin = itemView.findViewById(R.id.scenario_item_origin);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case Codes.LOCATION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // task you need to do.
                    Toast.makeText(this, mLocationManager.getLastKnownLocation(Context.LOCATION_SERVICE).toString(), Toast.LENGTH_LONG).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                break;
        }
    }

    class AddressResultReciever extends ResultReceiver{

        public AddressResultReciever(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int code, Bundle bundle){
            Log.i("YESSSSSsss", "stuff");
            switch (code){
                case FetchAddressIntentService.Codes.SUCCESS:
                    String bundleString = bundle.getString("city");
                    Log.i("BUN", bundle.toString());

                    // send request to API
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    String url = String.format(getString(R.string.url_get_flights), bundleString);
                    Log.i("NET GET", url +  " / " +getString(R.string.url_get_flights));
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject res) {
                                    //Toast.makeText(getApplicationContext(), "done request", Toast.LENGTH_SHORT).show();
                                    try {
                                        jsonData = res.getJSONArray("msg");
                                        Logger.getLogger("hello").info(jsonData.toString(2));
                                        mAdapter.notifyDataSetChanged();
                                    } catch (Exception e){

                                    }
                                    System.out.println(jsonData);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Logger.getLogger("hello").warning(error.getMessage());
                        }
                    });

                    queue.add(request);


                    mAdapter.notifyDataSetChanged();
                    break;
                case FetchAddressIntentService.Codes.FAILURE:
                    break;
                default:
                    break;
            }
        }
    }

    public class Codes {
        public static final int LOCATION_REQUEST = 0;
    }
}
