package com.moosader.happythings.com.moosader.happythings.services;

import android.content.Context;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;

import java.io.IOException;

/**
 * Created by eatskolnikov on 4/8/15.
 */
public class HappyMediaRouterCallback extends MediaRouter.Callback {

    private static final String TAG = HappyChannel.class.getSimpleName();
    private CastDevice mSelectedDevice;
    private Cast.Listener mCastListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private GoogleApiClient mApiClient;
    private HappyChannel mHappyChannel;
    private HappyCastMessageSender mMessagesSender;
    private boolean mApplicationStarted;
    private String mSessionId;
    private Context mContext;

    public HappyMediaRouterCallback(Context context, CastDevice selectedDevice, GoogleApiClient apiClient, HappyChannel happyChannel, GoogleApiClient.ConnectionCallbacks connectionCallbacks){
        this.mSelectedDevice = selectedDevice;
        this.mApiClient = apiClient;
        this.mHappyChannel = happyChannel;
        this.mConnectionCallbacks = connectionCallbacks;
        this.mContext = context;
    }
    @Override
    public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
        Log.d(TAG, "onRouteSelected");
        mSelectedDevice = CastDevice.getFromBundle(info.getExtras());
        launchReceiver();
    }

    @Override
    public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
        Log.d(TAG, "onRouteUnselected: info=" + info);
        teardown();
        mSelectedDevice = null;
    }


    private void launchReceiver() {
        try {
            mCastListener = new Cast.Listener() {

                @Override
                public void onApplicationDisconnected(int errorCode) {
                    Log.d(TAG, "application has stopped");
                    teardown();
                }

            };
            // Connect to Google Play services
            mMessagesSender = new HappyCastMessageSender(mApiClient, mHappyChannel );
            mConnectionCallbacks = new HappyCastConnectionCallbacks(this.mContext, mApiClient, mHappyChannel, mMessagesSender, mSessionId);
            mConnectionFailedListener = new ConnectionFailedListener();
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mSelectedDevice, mCastListener);
            mApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mConnectionFailedListener)
                    .build();

            mApiClient.connect();
        } catch (Exception e) {
            Log.e(TAG, "Failed launchReceiver", e);
        }
    }

    private class ConnectionFailedListener implements
            GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.e(TAG, "onConnectionFailed ");
            teardown();
        }
    }
    private void teardown() {
        Log.d(TAG, "teardown");
        if (mApiClient != null) {
            if (mApplicationStarted) {
                if (mApiClient.isConnected()  || mApiClient.isConnecting()) {
                    try {
                        Cast.CastApi.stopApplication(mApiClient, mSessionId);
                        if (mHappyChannel != null) {
                            Cast.CastApi.removeMessageReceivedCallbacks(
                                    mApiClient,
                                    mHappyChannel.getNamespace());
                            mHappyChannel = null;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while removing channel", e);
                    }
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
            }
            mApiClient = null;
        }
        mSelectedDevice = null;
        mSessionId = null;
    }
}
