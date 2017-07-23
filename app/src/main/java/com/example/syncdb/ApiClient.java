package com.example.syncdb;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nishant on 7/18/2017.
 */

public class ApiClient {
    static ApiInterface apiInterface;
    public static ApiInterface getApiInterface(){
        if(apiInterface==null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(DbContract.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiInterface = retrofit.create(ApiInterface.class);
        }
        return apiInterface;

    }
}

