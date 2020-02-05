package e.aman.lockdemo.Services;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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

      KeyguardManager.KeyguardLock key;
        KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);

        //This is deprecated, but it is a simple way to disable the lockscreen in code
        key = km.newKeyguardLock("IN");

        key.disableKeyguard();

        //Start listening for the Screen On, Screen Off, and Boot completed actions
        filter  = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);

        //Set up a receiver to listen for the Intents in this Service
        receiver = new LockScreenReceiver();
        registerReceiver(receiver, filter);


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

        super.onCreate();

    }

    @Override
    public void onDestroy() {
      unregisterReceiver(receiver);

        Intent broadcastIntent = new Intent(this, RestartWhenAppDiesReceiver.class);
        sendBroadcast(broadcastIntent);
       super.onDestroy();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
        return super.onStartCommand(intent, flags, startId);
    }
}
