package e.aman.lockdemo.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Views.MainActivity;
import e.aman.lockdemo.Views.PatternLockScreenActivity;
import io.paperdb.Paper;

public class FloatingWidgetService extends Service
{
    private WindowManager mWindowManager;
    private View overlayView;


    private String final_pattern = "";
    private String save_pattern_key = "pattern_code";
    PatternLockView LockScreenPattern ;
    public FloatingWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();



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

        //Inflate the chat head layout we created
        overlayView = LayoutInflater.from(this).inflate(R.layout.activity_pattern_lockscreen, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.width = Resources.getSystem().getDisplayMetrics().widthPixels;


        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(overlayView, params);

        ImageView closeButton = (ImageView) overlayView.findViewById(R.id.close_floating_view);
        closeButton.setOnClickListener(v -> stopSelf());

        Paper.init(this);
        Log.e("this" , this.toString());


       final String save_pattern = Paper.book().read(save_pattern_key);
        LockScreenPattern = (PatternLockView)overlayView.findViewById(R.id.app_logo);

        LockScreenPattern.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }
            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }
            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                final_pattern = PatternLockUtils.patternToString(LockScreenPattern , pattern);

                if(final_pattern.equals(save_pattern))
                {
                    Log.e("pattern" , "Successs");
                    stopSelf();
                }
                else
                    Log.e("pattern" , "Not Successs");

            }

            @Override
            public void onCleared() {
            }
        });



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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) mWindowManager.removeView(overlayView);
        stopSelf();
    }
}