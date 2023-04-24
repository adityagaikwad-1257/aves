package com.adi.aves.retrofit;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static final String BASE_URL = "https://api.platerecognizer.com/v1/";
    public static final String TOKEN = "Token f91b5568e40a877f26990ec0a2430c82188643f8";

    public static Retrofit getRetrofit(Context context){
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }
}
