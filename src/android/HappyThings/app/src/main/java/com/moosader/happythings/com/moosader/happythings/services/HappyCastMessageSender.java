package com.moosader.happythings.com.moosader.happythings.services;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.moosader.happythings.MainActivity;

/**
 * Created by eatskolnikov on 4/8/15.
 */
public class HappyCastMessageSender {

    private HappyChannel mHappyChannel;
    private GoogleApiClient mApiClient;

    public HappyCastMessageSender(GoogleApiClient apiClient, HappyChannel happyChannel){
        this.mApiClient = apiClient;
        this.mHappyChannel = happyChannel;
    }
    public void sendMessage(String message) {
        if (mApiClient != null && mHappyChannel != null) {
            try {
                Cast.CastApi.sendMessage(mApiClient,
                        mHappyChannel.getNamespace(), message)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                    Log.e(HappyCastMessageSender.class.getSimpleName(), "Sending message failed");
                                }
                            }
                        });
            } catch (Exception e) {
                Log.e(HappyCastMessageSender.class.getSimpleName(), "Exception while sending message", e);
            }
        }
    }
}
