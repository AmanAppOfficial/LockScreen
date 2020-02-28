package e.aman.lockdemo.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.goodiebag.pinview.Pinview;

import java.io.File;
import java.util.List;

import e.aman.lockdemo.R;
import io.paperdb.Paper;

public class WidgetService extends Service
{
    private WindowManager mWindowManager;
    private View overlayView;
    int count=1 ;


    String status="";
    private String final_pattern = "";
    private String final_pin = "";
    private String save_pattern_key = "pattern_code";
    private String save_pin_key = "pin_code";
    PatternLockView LockScreenPattern ;
    public WidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String status = preferences.getString("status", null);
        if(status.equals("lock_pattern"))
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

        //Inflate the chat head layout we created
            overlayView = LayoutInflater.from(this).inflate(R.layout.activity_pattern_lockscreen, null);

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_background", Context.MODE_PRIVATE);
            int bg = sharedPref.getInt("background_resource", android.R.color.white);
            String type = sharedPref.getString("type" , "");
            RelativeLayout relativeLayout = (RelativeLayout) overlayView.findViewById(R.id.root_container_pattern);

           if(type.equals("app"))
           {
               relativeLayout.setBackgroundResource(bg);
           }
           else if(type.equals("gallery"))
           {
               String real_path = sharedPref.getString("resource","");
               File file = new File(real_path);
               Drawable d = Drawable.createFromPath(file.getAbsolutePath());
               relativeLayout.setBackground(d);
           }




            //Add the view to the window.
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            params.width = Resources.getSystem().getDisplayMetrics().widthPixels;
            params.height = Resources.getSystem().getDisplayMetrics().heightPixels;


            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(overlayView, params);

            Paper.init(this);
            final String save_pattern = Paper.book().read(save_pattern_key);
            LockScreenPattern = (PatternLockView)overlayView.findViewById(R.id.pattern_lock);


            LockScreenPattern.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }
                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {
                }
                @Override
                public void onComplete(List<PatternLockView.Dot> pattern)
                {
                    final_pattern = PatternLockUtils.patternToString(LockScreenPattern , pattern);

                    if(final_pattern.equals(save_pattern))
                        stopSelf();
                }

                @Override
                public void onCleared() {
                }
            });



        }
        else if(status.equals("lock_pin"))
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

            //Inflate the chat head layout we created
            overlayView = LayoutInflater.from(this).inflate(R.layout.activity_pin_lockscreen, null);

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_background", Context.MODE_PRIVATE);
            int bg = sharedPref.getInt("background_resource", android.R.color.white);
            String type = sharedPref.getString("type" , "");
            RelativeLayout relativeLayout = (RelativeLayout) overlayView.findViewById(R.id.root_container_pin);
           if(type.equals("app"))
            relativeLayout.setBackgroundResource(bg);
           else if(type.equals("gallery"))
           {
               String real_path = sharedPref.getString("resource","");
               File file = new File(real_path);
               Drawable d = Drawable.createFromPath(file.getAbsolutePath());
               relativeLayout.setBackground(d);
           }




            //Add the view to the window.
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            params.width = Resources.getSystem().getDisplayMetrics().widthPixels;
            params.height = Resources.getSystem().getDisplayMetrics().heightPixels;


            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(overlayView, params);

            Paper.init(this);
            final String save_pin = Paper.book().read(save_pin_key);
            keyboard_handler(overlayView , save_pin);

        }

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


    public void keyboard_handler(View overlayView , String save_pin)
    {
        Button button_1 ,button_2 ,button_3 ,
                button_4 ,button_5 ,button_6 ,button_7 ,button_8 ,button_9 ,button_0 ,button_back ,button_done ;

        EditText e1 , e2 , e3 , e4 ;



        button_0 = (Button)overlayView.findViewById(R.id.button_0);
        button_1 = (Button)overlayView.findViewById(R.id.button_1);
        button_2 = (Button)overlayView.findViewById(R.id.button_2);
        button_3 = (Button)overlayView.findViewById(R.id.button_3);
        button_4 = (Button)overlayView.findViewById(R.id.button_4);
        button_5 = (Button)overlayView.findViewById(R.id.button_5);
        button_6 = (Button)overlayView.findViewById(R.id.button_6);
        button_7 = (Button)overlayView.findViewById(R.id.button_7);
        button_8 = (Button)overlayView.findViewById(R.id.button_8);
        button_9 = (Button)overlayView.findViewById(R.id.button_9);
        button_back = (Button)overlayView.findViewById(R.id.button_delete);
        button_done = (Button)overlayView.findViewById(R.id.button_enter);

        e1 = (EditText)overlayView.findViewById(R.id.e1);
        e2 = (EditText)overlayView.findViewById(R.id.e2);
        e3 = (EditText)overlayView.findViewById(R.id.e3);
        e4 = (EditText)overlayView.findViewById(R.id.e4);

        button_0.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("0");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("0");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("0");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("0");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();
                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });

        button_1.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("1");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("1");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("1");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("1");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();
                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });
        button_2.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("2");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("2");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("2");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("2");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();
                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });

        button_3.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("3");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("3");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("3");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("3");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();
                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });

        button_4.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("4");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("4");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("4");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("4");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();
                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });

        button_5.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("5");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("5");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("5");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("5");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();
                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });
        button_6.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("6");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("6");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("6");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("6");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();
                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });
        button_7.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("7");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("7");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("7");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("7");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();
                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });
        button_8.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("8");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("8");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("8");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("8");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                Log.e("final_string", final_pin);
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();

                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });
        button_9.setOnClickListener(view ->
        {
            if(count == 1)
            {
                e1.setText("9");
                count++ ;
            }
            else if(count == 2)
            {
                e2.setText("9");
                count++ ;
            }
            else if(count == 3)
            {
                e3.setText("9");
                count++ ;
            }
            else if(count == 4)
            {
                e4.setText("9");
                count++ ;
            }
            if(count == 5 ) {
                final_pin = e1.getText().toString() + "" + e2.getText().toString() + "" + e3.getText().toString() + "" + e4.getText().toString();
                if (final_pin.equals(save_pin)) {
                    count=1 ;
                    stopSelf();

                } else {
                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }}

        });
        button_back.setOnClickListener(view ->
        {
            if(count == 2)
            {
                e1.setText("");
            }
            else if(count == 3)
            {
                e2.setText("");
            }
            else if(count == 4)
            {
                e3.setText("");
            }
            else if(count == 5)
            {
                e4.setText("");
            }
            if(count>=2&& count<=5)
                count-- ;
        });



    }


    @Override
    public void onDestroy() {
        super.onDestroy();

            if (overlayView != null) mWindowManager.removeView(overlayView);
            stopSelf();


        }



}