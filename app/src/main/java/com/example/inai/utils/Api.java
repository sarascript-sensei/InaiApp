package com.example.inai.utils;

import com.example.inai.models.ApiModel;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    public volatile ApiModel apiModel;
    static volatile Api api;

    private Api() {
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://secure-scrubland-16082.herokuapp.com/")   // live
                .baseUrl("http://10.0.2.2:5000/")                         // development
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiModel = retrofit.create(ApiModel.class);
    }

    public static Api getInstance() {
        if (api == null)
        {
            // To make thread safe
            synchronized (Api.class)
            {

                if (api==null)
                    api = new Api();
            }
        }
        return api;
    }
}
