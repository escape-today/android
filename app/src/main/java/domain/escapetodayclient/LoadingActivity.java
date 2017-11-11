package domain.escapetodayclient;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class LoadingActivity extends AppCompatActivity {

    enum ReadyState {LOADING, READY, ERROR};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        new MainLoadTask(this).execute();
    }

    private static class MainLoadTask extends AsyncTask<Void, Void, ReadyState> {

        WeakReference<Context> con;

        MainLoadTask(Context requiredCon){
            con = new WeakReference<>(requiredCon);
        }

        @Override
        protected ReadyState doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
                //Toast.makeText(this,"things okay", Toast.LENGTH_SHORT).show();
                return ReadyState.READY;

            } catch (InterruptedException e) {
                return ReadyState.ERROR;
                //Toast.makeText(activity,"things have broken", Toast.LENGTH_SHORT).show();
                //Thread.currentThread().interrupt();
            }
        }

        @Override
        protected void onPostExecute(ReadyState result){
            if (con.get() != null && result == ReadyState.READY) {// Main Activity
                con.get().startActivity(new Intent(con.get(), MainActivity.class));
            } else if (con.get() != null){ // Error Activity
                System.exit(1);
            } else { // die
                System.exit(1);
            }
        }
    }
}
