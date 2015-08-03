package com.moosader.happythings.com.moosader.happythings.services;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.moosader.happythings.R;

/**
 * Created by eatskolnikov on 4/8/15.
 */
public class HappyChannel implements Cast.MessageReceivedCallback {

    private static final String TAG = HappyChannel.class.getSimpleName();
    Context context;
    public HappyChannel(Context context){
        this.context = context;
    }
    /**
     * @return custom namespace
     */
    public String getNamespace() {
        return context.getString(R.string.namespace);
    }

    /*
     * Receive message from the receiver app
     */
    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace,
                                  String message) {
        Log.d(TAG, "onMessageReceived: " + message);
    }

}