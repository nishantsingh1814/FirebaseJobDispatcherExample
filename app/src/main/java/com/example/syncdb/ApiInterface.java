package com.example.syncdb;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Nishant on 7/18/2017.
 *
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST("SyncDb/syncInfo.php")
    Call<MyJson> getDatabase(@FieldMap Map<String,String> name);
}
