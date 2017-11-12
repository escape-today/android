package domain.escapetodayclient;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView mDraw = (NavigationView) findViewById(R.id.navigation);
        mDraw.setNavigationItemSelectedListener(new NavListener(this));
    }

    private class NavListener implements NavigationView.OnNavigationItemSelectedListener {

        Context context;

        NavListener(Context con){
            context = con;
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Toast.makeText(context,"clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            switch (item.getItemId()){
                case R.id.menu_main_about:
                    //showAbout();
                    return true;
                case R.id.menu_main_new_scenario:
                    showNewScenario();
                    return true;
                case R.id.menu_main_help:
                    //showHelp();
                    return true;
                case R.id.menu_main_customise_scenario:
                    //showCustomise();
                    return true;
                default: return false;
            }
        }

        private void showNewScenario(){
            Intent i = new Intent(context, NewScenarioActivity.class);
            startActivity(i);
        }
    }
}
