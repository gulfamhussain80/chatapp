package com.example.chatproj2.Models;

import com.example.chatproj2.Notifications.MyResponse;
import com.example.chatproj2.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key-AAAAfotyUY4:APA91bHUBLW8Ths8782EPdbxxY8kq7x5Mc0Tf1_2dLLaOJXNFOanldoWUVacbY7crsKt4RAiV1F3S_f6c2sszH_AVTwHrYdyPmCUDLHtAqZhjC_-r_ncaATNokljzhMynXhf1m_IqDbo"

    }
    )
    @POST("fcm/send")
    Call<MyResponse> setNotification(@Body Sender body);
}
