package com.adi.aves.retrofit;


import com.adi.aves.models.PRResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PlateRecognizerAPI {

    @Multipart
    @POST("plate-reader/")
    Call<PRResponse> extractCharacter(@Header("Authorization") String token,
                                      @Part MultipartBody.Part upload);

}
