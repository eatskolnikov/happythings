package com.moosader.happythings;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.moosader.happythings.com.moosader.happythings.services.Connectivity;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(Connectivity.isConnected(this)){
            if(Connectivity.isConnectedMobile(this)){
                Toast toast = Toast.makeText(this, R.string.message_warning_mobile_data,Toast.LENGTH_LONG);
                toast.show();
            }
            if(!Connectivity.isConnectedFast(this)){
                Toast toast = Toast.makeText(this, R.string.message_warning_slow_connection,Toast.LENGTH_LONG);
                toast.show();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);

        }else{
            final int theDuration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(this, R.string.message_need_internet,Toast.LENGTH_LONG);
            toast.show();
            Thread t = new Thread( new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(theDuration == Toast.LENGTH_SHORT ? 2500 : 4000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    SplashActivity.this.finish();
                }

            });
            t.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
