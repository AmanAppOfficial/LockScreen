package e.aman.lockdemo.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import e.aman.lockdemo.Utils.Constants;
import e.aman.lockdemo.Receivers.LockScreenReceiver;
import e.aman.lockdemo.Receivers.RestartWhenAppDiesReceiver;

public class LockScreenService extends Service
{
    IntentFilter filter;
    BroadcastReceiver receiver;

    public LockScreenService(Context applicationContext) {
        super();
    }

    public LockScreenService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate() {
        super.onCreate();



        //Start listening for the Screen On, Screen Off, and Boot completed actions
        filter  = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);

        //Set up a receiver to listen for the Intents in this Service
        receiver = new LockScreenReceiver();
        registerReceiver(receiver, filter);



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String get_status = prefs.getString(Constants.default_lock_status, "abc");

        if(get_status.equals(Constants.default_lock_pin) || get_status.equals(Constants.default_lock_pattern))
        {

            if (Build.VERSION.SDK_INT >= 26) {
                String CHANNEL_ID = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("")
                        .setContentText("").build();

                startForeground(1, notification);
            }

        }



    }

    @Override
    public void onDestroy() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String get_status = prefs.getString(Constants.default_lock_status , "abc");

        if(get_status.equals(Constants.default_lock_pin) || get_status.equals(Constants.default_lock_pattern))
        {

            unregisterReceiver(receiver);
            Intent broadcastIntent = new Intent(this, RestartWhenAppDiesReceiver.class);
            sendBroadcast(broadcastIntent);
            super.onDestroy();

        }



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String get_status = prefs.getString(Constants.default_lock_status , "abc");

        if(get_status.equals(Constants.default_lock_pattern) || get_status.equals(Constants.default_lock_pin)) {

            if (Build.VERSION.SDK_INT >= 26) {
                String CHANNEL_ID = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("")
                        .setContentText("").build();

                startForeground(1, notification);
            }


        }


        return START_STICKY ;
    }
}
