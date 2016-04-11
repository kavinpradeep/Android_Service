package com.example.android.service_now;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter();
        intentFilter.addAction("FILE_DOWNLOADED_ACTION");
        registerReceiver(intentReceiver, intentFilter);

    }

    public void downnow(View v) {

        Intent i = new Intent(getBaseContext(), DownService.class);

        try {

            URL[] urls = new URL[]{
                    new URL("http://www.cisco.com/c/dam/en_us/about/ac79/docs/innov/IoE.pdf"),
                    new URL("http://www.cisco.com/web/about/ac79/docs/innov/IoE_Economy.pdf")};
            i.putExtra("URLs",urls);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        startService(i);

        Log.v("Now : ", "vanthachu");
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getBaseContext(), "File downloaded!",
                    Toast.LENGTH_LONG).show();
        }
    };
}
