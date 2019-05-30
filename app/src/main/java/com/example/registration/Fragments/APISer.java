package com.example.registration.Fragments;

import com.example.registration.Notifications.MyResponse;
import com.example.registration.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APISer {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAATISn7Ds:APA91bGN2Y28oYeCzeysGqbiyRMY3f60hA7JqK7CNLW44qWIvXBRqMhkzE1Dk9ahd6tTtt1rjZbYCgScTXxooVRYoXFASl0RApqTM3fnm-25alsbo-9JPY2UUR9dD6KAhgWLOkzO8PIX"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifications(@Body Sender body);
}
