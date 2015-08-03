package com.moosader.happythings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.Cast.ApplicationConnectionResult;
import com.google.android.gms.cast.Cast.MessageReceivedCallback;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.moosader.happythings.com.moosader.happythings.services.FetchHappyThingsTask;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by eatskolnikov on 4/5/15.
 */
public class HappythingsFragment extends Fragment {

    private static final String TAG = HappythingsFragment.class.getSimpleName();

    private ArrayAdapter<String> mHappyThingsAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHappyThingsAdapter = new ArrayAdapter<String>(
                        getActivity(), R.layout.list_item_happything, R.id.list_item_happything_textview,
                        new ArrayList<String>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_happythings);
        listView.setAdapter(mHappyThingsAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView listText = (TextView) view.findViewById(R.id.list_item_happything_textview);
                String text = listText.getText().toString();
                //((MainActivity)getActivity()).sendMessage(text);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text+" find more at "+getString(R.string.happythings_site_url));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                //Toast.makeText(getActivity().getApplication().getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        updateHappyThings();
    }
    private void updateHappyThings() {
        FetchHappyThingsTask weatherTask = new FetchHappyThingsTask(mHappyThingsAdapter);
        weatherTask.execute();
    }

}
