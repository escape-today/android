package domain.escapetodayclient;

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

import java.util.List;
import java.util.Locale;

public class NewScenarioActivity extends AppCompatActivity {

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

        // redundant
        mAdapter.notifyDataSetChanged();
    }

    private class ScenarioItemAdapter extends RecyclerView.Adapter<ScenarioItemAdapter.ScenarioItemViewHolder>{

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
            return 1000000;
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
}
