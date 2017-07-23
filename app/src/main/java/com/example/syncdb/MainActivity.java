package com.example.syncdb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    EditText Name;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    ArrayList<Contact> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Name = (EditText) findViewById(R.id.name);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(MainActivity.this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyService.class)
                .setTag("my-unique-tag")
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(0, 20))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(
                        Constraint.ON_UNMETERED_NETWORK
                )
                .build();

        dispatcher.mustSchedule(myJob);

        readFromLocalDatabase();
    }

    public void submitName(View view) {
        String name = Name.getText().toString();
        saveToAppServer(name);
        Name.setText("");
    }

    private void readFromLocalDatabase() {
        arrayList.clear();
        DbHelper helper = new DbHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.readFromDatabase(database);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DbContract.NAME));
            int sync_status = cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));
            Contact contact = new Contact(name, sync_status);
            arrayList.add(contact);
        }
        adapter.notifyDataSetChanged();

        cursor.close();
        helper.close();
    }

    private void saveToAppServer(final String name) {

        if (checkNetworkConnection()) {
            Log.i("hello", "saveToAppServer: "+ name);
            ApiInterface api = ApiClient.getApiInterface();
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            Call<MyJson> temp = api.getDatabase(map);
            temp.enqueue(new Callback<MyJson>() {
                @Override
                public void onResponse(Call<MyJson> call, Response<MyJson> response) {
                    String resStr = response.body().getResponse();
                    Log.i("hello", "saveToAppServer: "+resStr);

                    if (resStr.equals("ok")) {
                        saveToLocalDb(name, DbContract.SYNC_STATUS_OK);
                    } else {
                        saveToLocalDb(name, DbContract.SYNC_STATUS_FAILED);
                    }
                }

                @Override
                public void onFailure(Call<MyJson> call, Throwable t) {
                    Log.i("hello", "saveToAppServer: "+ t.getMessage());

                    saveToLocalDb(name, DbContract.SYNC_STATUS_FAILED);
                }
            });
        } else {
            saveToLocalDb(name, DbContract.SYNC_STATUS_FAILED);
        }

    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void saveToLocalDb(String name, int sync_status) {
        DbHelper helper = new DbHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        helper.saveToLocalDatabase(name, sync_status, database);
        readFromLocalDatabase();
        helper.close();
    }
}
