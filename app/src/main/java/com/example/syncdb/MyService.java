package com.example.syncdb;

import android.app.job.JobService;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nishant on 7/19/2017.
 */

public class MyService extends com.firebase.jobdispatcher.JobService{
    @Override
    public boolean onStartJob(JobParameters job) {
        final DbHelper dbHelper=new DbHelper(getApplicationContext());
        final SQLiteDatabase database=dbHelper.getWritableDatabase();
        Cursor cursor=dbHelper.readFromDatabase(database);
        Log.i("hellopq", "onStartJob: ");
        while(cursor.moveToNext()){
            int sync_status=cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));
            if(sync_status==DbContract.SYNC_STATUS_FAILED){
                final String name=cursor.getString(cursor.getColumnIndex(DbContract.NAME));
                ApiInterface apiInterface=ApiClient.getApiInterface();
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                Call<MyJson> temp = apiInterface.getDatabase(map);
                temp.enqueue(new Callback<MyJson>() {
                    @Override
                    public void onResponse(Call<MyJson> call, Response<MyJson> response) {
                        if(response.body().getResponse().equals("ok")){
                            dbHelper.updateLocalDatabase(name,DbContract.SYNC_STATUS_OK,database);
                        }
                    }

                    @Override
                    public void onFailure(Call<MyJson> call, Throwable t) {

                    }
                });
            }
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
