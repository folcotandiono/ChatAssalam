package com.assalam.chatassalam.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by folcotandiono on 2/1/2019.
 */

public class ApiClient {
    public static final String BASE_URL = "https://www.assalam.id/";
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        return retrofit;
    }
}
