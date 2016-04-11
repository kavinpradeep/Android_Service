package com.example.android.service_now;

import java.io.File;
import java.io.FileWriter;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kavin on 3/13/2016.
 */
public class DownService extends Service {

    int counter=0;
    public URL[] urls;
    private Timer timer = new Timer();

    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        DownService getService() {
            return DownService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        Log.v("Service :","Available now");

        Object[] objUrls = (Object[]) intent.getExtras().get("URLs");
        URL[] urls = new URL[objUrls.length];
        for (int i=0; i<objUrls.length-1; i++) {
            urls[i] = (URL) objUrls[i];
            Toast.makeText(this,urls[i].toString(),Toast.LENGTH_LONG).show();
        }
        new DoBackgroundTask().execute(urls);

        return START_STICKY;
    }

    private void doSomethingRepeatedly() {
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {
                Log.d("MyService", String.valueOf(++counter));
                try {
                    Thread.sleep(4000);
                    Log.d("MyService", counter + " Finished");

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.cancel();
        }
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    private int DownloadFile(URL url) {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        String filePath = getBaseContext().getFilesDir().getPath().toString() + "/mypdf.pdf";
        File file = new File(filePath);
        Log.v("Location :",filePath);

        try {

            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            Log.v("Response Code ",connection.getResponseCode()+"");
            int fileLength = connection.getContentLength();
            Log.v("File Length ", fileLength + "");


            //File Download to local
/*
            file.createNewFile();
            FileWriter writer;
            writer = new FileWriter(file);
            writer.write("This\n is\n an\n example\n");
            writer.flush();
            Log.v("File Write ", "Completed Successfully");*/

            input = connection.getInputStream();
            output = new FileOutputStream(filePath);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }



            //---simulate taking some time to download a file---
            Thread.sleep(5000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 100;
    }

    private class DoBackgroundTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalBytesDownloaded = 0;
            for (int i = 0; i < count; i++) {
                totalBytesDownloaded += DownloadFile(urls[i]);
                //---calculate precentage downloaded and
                // report its progress---
                publishProgress((int) (((i+1) / (float) count) * 100));
            }
            return totalBytesDownloaded;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d("Downloading files",
                    String.valueOf(progress[0]) + "% downloaded");
            Toast.makeText(getBaseContext(),
                    String.valueOf(progress[0]) + "% downloaded",
                    Toast.LENGTH_LONG).show();
        }

        protected void onPostExecute(Long result) {
            Toast.makeText(getBaseContext(),
                    "Downloaded " + result + " bytes",
                    Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

}
