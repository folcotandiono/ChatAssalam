package com.assalam.chatassalam.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by folcotandiono on 2/1/2019.
 */

public class ApiClient {
    public static final String BASE_URL = "https://www.assalam.id/";
    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;
    public static Retrofit getClient() {
            client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60 / 2, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .cache(null)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

        return retrofit;
    }
}
