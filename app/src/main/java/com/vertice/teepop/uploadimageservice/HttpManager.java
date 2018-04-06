package com.vertice.teepop.uploadimageservice;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by topza on 4/6/2018.
 */

public class HttpManager {
    private static final HttpManager ourInstance = new HttpManager();

    public static HttpManager getInstance() {
        return ourInstance;
    }

    private ApiService apiService;

    private HttpManager() {

        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.baseUrl("http://192.168.1.46:65/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

    }

    public ApiService getApiService() {
        return apiService;
    }
}
