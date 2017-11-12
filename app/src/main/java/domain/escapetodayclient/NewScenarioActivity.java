package domain.escapetodayclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class NewScenarioActivity extends AppCompatActivity {

    JSONArray jsonData = new JSONArray();
    //String locationString = getLocation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scenario);
        RecyclerView rv = findViewById(R.id.recycler_view_new_scenario);
        ScenarioItemAdapter mAdapter = new ScenarioItemAdapter();
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);

        // sort our recycler view
        rv.setLayoutManager(lm);
        rv.setAdapter(mAdapter);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getString(R.string.url_get_flights), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray res) {
                        Toast.makeText(getApplicationContext(), "done request", Toast.LENGTH_SHORT).show();
                        jsonData = res;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.getLogger("hello").warning(error.getMessage());
            }
        });

        queue.add(request);

        // redundant
        mAdapter.notifyDataSetChanged();
    }

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
            holder.title.setText(String.format(Locale.ENGLISH, "POS: %d", position));
            Toast.makeText(getApplicationContext(), "P: " + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public int getItemCount() {
            // data.size()
            return 100000; //jsonData.length();
        }

        protected class ScenarioItemViewHolder extends RecyclerView.ViewHolder {

            // things our adaptor can access
            public TextView title;

            protected ScenarioItemViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.scenario_item_title);
            }
        }
    }

    private String getLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "NULL";
        }

        final Context context = this;

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {


            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Intent i = new Intent(context, FetchAddressIntentService.class);
                    //i.putExtra(location.getLongitude());
                    startActivity(i);
                }
            }
        });

        return "";
    }
}
