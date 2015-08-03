package com.moosader.happythings.com.moosader.happythings.services;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.moosader.happythings.R;

import java.io.IOException;

/**
 * Created by eatskolnikov on 4/8/15.
 */

public class HappyCastConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = HappyCastConnectionCallbacks.class.getSimpleName();
    private Context mContext;
    private GoogleApiClient mApiClient;
    private String mSessionId;
    private boolean mWaitingForReconnect;
    private HappyChannel mHappyChannel;
    private HappyCastMessageSender mMessageSender;

    public HappyCastConnectionCallbacks(Context context, GoogleApiClient apiClient, HappyChannel happyChannel, HappyCastMessageSender messageSender, String sessionId){
        this.mApiClient = apiClient;
        this.mHappyChannel = happyChannel;
        this.mSessionId = sessionId;
        this.mContext = context;
        this.mMessageSender = messageSender;
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        if (mApiClient == null) { return; }

        try {
            if (mWaitingForReconnect) {
                mWaitingForReconnect = false;
                // Check if the receiver app is still running
                if ((connectionHint != null)
                        && connectionHint
                        .getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                    Log.d(TAG, "App  is no longer running");
                    //teardown();
                } else {
                    // Re-create the custom message channel
                    try {
                        Cast.CastApi.setMessageReceivedCallbacks(
                                mApiClient,
                                mHappyChannel.getNamespace(),
                                mHappyChannel);
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while creating channel", e);
                    }
                }
            } else {
                // Launch the receiver app
                Cast.CastApi
                .launchApplication(mApiClient, mContext.getString(R.string.app_id), false)
                .setResultCallback(new ResultCallback<Cast.ApplicationConnectionResult>(){
                    @Override
                    public void onResult(Cast.ApplicationConnectionResult result)
                    {
                        Status status = result.getStatus();
                        Log.d(TAG,
                                "ApplicationConnectionResultCallback.onResult: statusCode"
                                        + status.getStatusCode());
                        if (status.isSuccess()) {
                            ApplicationMetadata applicationMetadata = result.getApplicationMetadata();
                            mSessionId = result.getSessionId();
                            String applicationStatus = result.getApplicationStatus();
                            boolean wasLaunched = result.getWasLaunched();
                            Log.d(TAG, "application name: " + applicationMetadata.getName()
                            + ", status: " + applicationStatus
                            + ", sessionId: " + mSessionId
                            + ", wasLaunched: " + wasLaunched);
                            mHappyChannel = new HappyChannel(mContext);
                            try {
                                Cast.CastApi
                                        .setMessageReceivedCallbacks(
                                                mApiClient,
                                                mHappyChannel.getNamespace(),
                                                mHappyChannel);
                            } catch (IOException e) {
                                Log.e(TAG,
                                        "Exception while creating channel",
                                        e);
                            }
                            mMessageSender.sendMessage(mContext.getString(R.string.title_activity_splash));
                        } else {
                            Log.e(TAG,
                                    "application could not launch");
                            //teardown();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch application", e);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended");
        mWaitingForReconnect = true;
    }
}
