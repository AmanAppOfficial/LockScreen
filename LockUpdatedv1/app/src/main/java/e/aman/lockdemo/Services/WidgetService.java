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
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import e.aman.lockdemo.Utils.Constants;
import e.aman.lockdemo.R;

import e.aman.lockdemo.Views.EmptyActivity;
import e.aman.lockdemo.Views.PatternLock.MainActivityPattern;
import e.aman.lockdemo.Views.PatternLock.ResetPatternActivity;
import e.aman.lockdemo.Views.PinLock.MainActivityPin;
import e.aman.lockdemo.Views.PinLock.ResetPinActivity;
import io.paperdb.Paper;


public class WidgetService extends Service
{
    private WindowManager mWindowManager;
    private View overlayView;
    int count=0 ;


    private String final_pattern = "";
    private String final_pin = "";
    private String save_pattern_key = Constants.pattern_code;
    private String save_pin_key = Constants.pin_code;
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
        String status = preferences.getString(Constants.default_lock_status, null);


        if(status.equals(Constants.default_lock_pattern))
        {

            Intent i = new Intent(WidgetService.this , EmptyActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);


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


            SharedPreferences sharedPref_gallery = getApplicationContext().getSharedPreferences("my_background", Context.MODE_PRIVATE);
            int bg = sharedPref_gallery.getInt("background_resource", android.R.color.white);
            String type = sharedPref_gallery.getString("type" , "");
            RelativeLayout relativeLayout = (RelativeLayout) overlayView.findViewById(R.id.root_container_pattern);
            if(type.equals("app"))
                relativeLayout.setBackgroundResource(bg);
            else if(type.equals("gallery"))
            {
                String real_path = sharedPref_gallery.getString("resource","");
                try {
                    InputStream inputStream = getContentResolver().openInputStream(Uri.parse(real_path));
                    Drawable yourDrawable = Drawable.createFromStream(inputStream, real_path);
                    relativeLayout.setBackground(yourDrawable);

                } catch (FileNotFoundException e) {

                }
            }


          //  Add the view to the window.
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            |WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            |WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
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
                    {
                        Intent intent1 = new Intent("close");
                        sendBroadcast(intent1);

                        String status_from_switch_pattern = Paper.book().read(Constants.from_switch_pattern_button);
                        if(status_from_switch_pattern.equals("yes"))
                        {
                            Paper.book().write(Constants.from_pin_reset_button , "no");
                            String save_pin_key = Constants.pin_code;
                            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                            String status_enable_pinlock =  sharedpreferences.getString(Constants.enable_pin_lock_status, Constants.default_lock_status_not_defined);
                            if(status_enable_pinlock.equals("disable"))
                            {
                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.putString(Constants.enable_pin_lock_status, "enable");
                                editor1.commit();

                                SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = preferences1.edit();
                                editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                                editor.commit();

                                Paper.book().write(save_pin_key , "empty");
                                Paper.book().write(Constants.from_pin_reset_button , "yes");
                                Intent Start_lock_Intent = new Intent(WidgetService.this , ResetPinActivity.class);
                                Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Start_lock_Intent);
                            }
                            else if(status_enable_pinlock.equals(Constants.default_lock_status_not_defined))
                            {
                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.putString(Constants.enable_pin_lock_status, "disable");
                                editor1.commit();
                                Paper.book().write(save_pin_key , "empty");
                                Toast.makeText(getApplicationContext() , "First Register Pin" , Toast.LENGTH_SHORT).show();
                                Intent Register_Pattern_Intent = new Intent(WidgetService.this , ResetPinActivity.class);
                                Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Register_Pattern_Intent);
                            }
                            else
                            {
                                Intent intent = new Intent(WidgetService.this , MainActivityPin.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }


                        }


                        stopSelf();
                    }

                    else
                        Toast.makeText(getApplicationContext() , "PATTERN doesn't match" , Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCleared() {
                }
            });

        }


        else if(status.equals(Constants.default_lock_pin))
        {

            Intent i = new Intent(WidgetService.this , EmptyActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

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


            SharedPreferences sharedPref_gallery = getApplicationContext().getSharedPreferences("my_background", Context.MODE_PRIVATE);
            int bg = sharedPref_gallery.getInt("background_resource", android.R.color.white);
            String type = sharedPref_gallery.getString("type" , "");
            RelativeLayout relativeLayout = (RelativeLayout) overlayView.findViewById(R.id.pin_lock_layout);
            if(type.equals("app"))
                relativeLayout.setBackgroundResource(bg);
            else if(type.equals("gallery"))
            {
                String real_path = sharedPref_gallery.getString("resource","");
                try {
                    InputStream inputStream = getContentResolver().openInputStream(Uri.parse(real_path));
                    Drawable yourDrawable = Drawable.createFromStream(inputStream, real_path);
                    relativeLayout.setBackground(yourDrawable);

                } catch (FileNotFoundException e) {

                }
            }


       //     Add the view to the window.
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            |WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            |WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
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


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String status = preferences.getString(Constants.default_lock_status, null);

        if(status.equals(Constants.default_lock_pattern) || status.equals(Constants.default_lock_pin))
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

        return START_STICKY;



    }


    public void keyboard_handler(View overlayView , String save_pin)
    {
        Button button_1 ,button_2 ,button_3 ,
                button_4 ,button_5 ,button_6 ,button_7 ,button_8 ,button_9 ,button_0  ;
        ImageView button_back ;
        TextView editText , time ;

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());


        button_0 = overlayView.findViewById(R.id.button0);
        button_1 = overlayView.findViewById(R.id.button1);
        button_2 = overlayView.findViewById(R.id.button2);
        button_3 = overlayView.findViewById(R.id.button3);
        button_4 = overlayView.findViewById(R.id.button4);
        button_5 = overlayView.findViewById(R.id.button5);
        button_6 = overlayView.findViewById(R.id.button6);
        button_7 = overlayView.findViewById(R.id.button7);
        button_8 = overlayView.findViewById(R.id.button8);
        button_9 = overlayView.findViewById(R.id.button9);
        button_back = overlayView.findViewById(R.id.button_back);
        editText = overlayView.findViewById(R.id.editText);
        time = overlayView.findViewById(R.id.time_text);

        time.setText(currentTime);


        button_0.setOnClickListener(view ->
        {
         if (count<4)
         {
             count++;
             final_pin = final_pin +  "0";
             editText.setText(final_pin);
             if(count==4)
             {
                 if (final_pin.equals(save_pin))
                 {
                 count=1 ;
                     Intent intent1 = new Intent("close");
                     sendBroadcast(intent1);

                     String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                 if(status_from_switch_pin.equals("yes"))
                 {

                     Paper.book().write(Constants.from_pattern_reset_button , "no");
                     String save_pattern_key = Constants.pattern_code;
                      SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                     String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                     if(status_enable_lock.equals("disable"))
                     {

                         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                         SharedPreferences.Editor editor1 = sharedpreferences.edit();
                         editor1.putString(Constants.enable_pattern_lock_status, "enable");
                         editor1.commit();

                         SharedPreferences.Editor editor = preferences.edit();
                         editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                         editor.commit();

                         Paper.book().write(Constants.from_pattern_reset_button , "yes");
                         Paper.book().write(save_pattern_key , "empty");
                         Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                         Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         startActivity(Start_lock_Intent);
                     }
                     else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                     {

                         SharedPreferences.Editor editor1 = sharedpreferences.edit();
                         editor1.putString(Constants.enable_pattern_lock_status, "disable");
                         editor1.commit();
                         Paper.book().write(save_pattern_key , "empty");
                         Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                         Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         startActivity(Register_Pattern_Intent);
                     }
                     else
                     {
                         Intent intent = new Intent(this , MainActivityPattern.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         startActivity(intent);
                     }

                 }

                 stopSelf();
             } else {
                 count=0;
                 editText.setText("");
                 final_pin="";
                 Toast.makeText(this,"PIN doesn't match", Toast.LENGTH_SHORT).show();
             }
             }
         }
        });

        button_1.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "1";
               editText.setText(final_pin);
               if(count==4)
               {
                   if (final_pin.equals(save_pin)) {
                       Intent intent1 = new Intent("close");
                       sendBroadcast(intent1);

                       count=1 ;
                       String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                       if(status_from_switch_pin.equals("yes"))
                       {

                           Paper.book().write(Constants.from_pattern_reset_button , "no");
                           String save_pattern_key = Constants.pattern_code;
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                           if(status_enable_lock.equals("disable"))
                           {

                               SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "enable");
                               editor1.commit();

                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                               editor.commit();

                               Paper.book().write(Constants.from_pattern_reset_button , "yes");
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                               Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Start_lock_Intent);
                           }
                           else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                           {

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "disable");
                               editor1.commit();
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                               Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Register_Pattern_Intent);
                           }
                           else
                           {
                               Intent intent = new Intent(this , MainActivityPattern.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                           }

                       }
                       stopSelf();
                   } else {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
                   }
               }
           }
        });

        button_2.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "2";
               editText.setText(final_pin);
               if(count==4)
               {
                   if (final_pin.equals(save_pin)) {
                       Intent intent1 = new Intent("close");
                       sendBroadcast(intent1);

                       count=1 ;
                       String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                       if(status_from_switch_pin.equals("yes"))
                       {

                           Paper.book().write(Constants.from_pattern_reset_button , "no");
                           String save_pattern_key = Constants.pattern_code;
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                           if(status_enable_lock.equals("disable"))
                           {

                               SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "enable");
                               editor1.commit();

                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                               editor.commit();

                               Paper.book().write(Constants.from_pattern_reset_button , "yes");
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                               Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Start_lock_Intent);
                           }
                           else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                           {

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "disable");
                               editor1.commit();
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                               Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Register_Pattern_Intent);
                           }
                           else
                           {
                               Intent intent = new Intent(this , MainActivityPattern.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                           }

                       }
                       stopSelf();
                   } else {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
                   }
               }
           }
        });

        button_3.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "3";
               editText.setText(final_pin);
               if(count==4)
               {
                   if (final_pin.equals(save_pin)) {
                       Intent intent1 = new Intent("close");
                       sendBroadcast(intent1);

                       count=1 ;
                       String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                       if(status_from_switch_pin.equals("yes"))
                       {

                           Paper.book().write(Constants.from_pattern_reset_button , "no");
                           String save_pattern_key = Constants.pattern_code;
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                           if(status_enable_lock.equals("disable"))
                           {

                               SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "enable");
                               editor1.commit();

                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                               editor.commit();
                               Paper.book().write(Constants.from_pattern_reset_button , "yes");
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                               Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Start_lock_Intent);
                           }
                           else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                           {

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "disable");
                               editor1.commit();
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                               Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Register_Pattern_Intent);
                           }
                           else
                           {
                               Intent intent = new Intent(this , MainActivityPattern.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                           }

                       }
                       stopSelf();
                   } else {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
                   }
               }
           }

        });

        button_4.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "4";
               editText.setText(final_pin);
               if(count==4)
               {
                   if (final_pin.equals(save_pin)) {
                       Intent intent1 = new Intent("close");
                       sendBroadcast(intent1);

                       count=1 ;
                       String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                       if(status_from_switch_pin.equals("yes"))
                       {

                           Paper.book().write(Constants.from_pattern_reset_button , "no");
                           String save_pattern_key = Constants.pattern_code;
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                           if(status_enable_lock.equals("disable"))
                           {

                               SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "enable");
                               editor1.commit();

                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                               editor.commit();

                               Paper.book().write(Constants.from_pattern_reset_button , "yes");
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                               Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Start_lock_Intent);
                           }
                           else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                           {

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "disable");
                               editor1.commit();
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                               Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Register_Pattern_Intent);
                           }
                           else
                           {
                               Intent intent = new Intent(this , MainActivityPattern.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                           }

                       }
                       stopSelf();
                   } else {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
                   }
               }
           }
        });

        button_5.setOnClickListener(view ->
        {
            if(count<4)
            {
                count++;
                final_pin = final_pin +  "5";
                editText.setText(final_pin);
                if(count==4)
                {
                    if (final_pin.equals(save_pin)) {
                        Intent intent1 = new Intent("close");
                        sendBroadcast(intent1);

                        count=1 ;
                        String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                        if(status_from_switch_pin.equals("yes"))
                        {

                            Paper.book().write(Constants.from_pattern_reset_button , "no");
                            String save_pattern_key = Constants.pattern_code;
                            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                            String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                            if(status_enable_lock.equals("disable"))
                            {

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.putString(Constants.enable_pattern_lock_status, "enable");
                                editor1.commit();

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                                editor.commit();

                                Paper.book().write(Constants.from_pattern_reset_button , "yes");
                                Paper.book().write(save_pattern_key , "empty");
                                Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                                Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Start_lock_Intent);
                            }
                            else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                            {

                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.putString(Constants.enable_pattern_lock_status, "disable");
                                editor1.commit();
                                Paper.book().write(save_pattern_key , "empty");
                                Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                                Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Register_Pattern_Intent);
                            }
                            else
                            {
                                Intent intent = new Intent(this , MainActivityPattern.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        }
                        stopSelf();
                    } else {
                        count=0;
                        editText.setText("");
                        final_pin="";
                        Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        button_6.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin + "6";
               editText.setText(final_pin);
               if(count==4)
               {
                   if (final_pin.equals(save_pin)) {
                       Intent intent1 = new Intent("close");
                       sendBroadcast(intent1);

                       count=1 ;
                       String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                       if(status_from_switch_pin.equals("yes"))
                       {

                           Paper.book().write(Constants.from_pattern_reset_button , "no");
                           String save_pattern_key = Constants.pattern_code;
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                           if(status_enable_lock.equals("disable"))
                           {

                               SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "enable");
                               editor1.commit();

                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                               editor.commit();

                               Paper.book().write(Constants.from_pattern_reset_button , "yes");
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                               Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Start_lock_Intent);
                           }
                           else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                           {

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "disable");
                               editor1.commit();
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                               Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Register_Pattern_Intent);
                           }
                           else
                           {
                               Intent intent = new Intent(this , MainActivityPattern.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                           }

                       }
                       stopSelf();
                   } else {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
                   }
               }
           }
        });

        button_7.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "7";
               editText.setText(final_pin);
               if(count==4)
               {
                   if (final_pin.equals(save_pin)) {
                       Intent intent1 = new Intent("close");
                       sendBroadcast(intent1);

                       count=1 ;
                       String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                       if(status_from_switch_pin.equals("yes"))
                       {

                           Paper.book().write(Constants.from_pattern_reset_button , "no");
                           String save_pattern_key = Constants.pattern_code;
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                           if(status_enable_lock.equals("disable"))
                           {

                               SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "enable");
                               editor1.commit();

                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                               editor.commit();

                               Paper.book().write(Constants.from_pattern_reset_button , "yes");
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                               Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Start_lock_Intent);
                           }
                           else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                           {

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "disable");
                               editor1.commit();
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                               Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Register_Pattern_Intent);
                           }
                           else
                           {
                               Intent intent = new Intent(this , MainActivityPattern.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                           }

                       }
                       stopSelf();
                   } else {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
                   }
               }
           }
        });

        button_8.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "8";
               editText.setText(final_pin);
               if(count==4)
               {
                   if (final_pin.equals(save_pin)) {
                       Intent intent1 = new Intent("close");
                       sendBroadcast(intent1);

                       count=1 ;
                       String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                       if(status_from_switch_pin.equals("yes"))
                       {

                           Paper.book().write(Constants.from_pattern_reset_button , "no");
                           String save_pattern_key = Constants.pattern_code;
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                           if(status_enable_lock.equals("disable"))
                           {

                               SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "enable");
                               editor1.commit();

                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                               editor.commit();

                               Paper.book().write(Constants.from_pattern_reset_button , "yes");
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                               Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Start_lock_Intent);
                           }
                           else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                           {

                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pattern_lock_status, "disable");
                               editor1.commit();
                               Paper.book().write(save_pattern_key , "empty");
                               Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                               Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(Register_Pattern_Intent);
                           }
                           else
                           {
                               Intent intent = new Intent(this , MainActivityPattern.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                           }

                       }
                       stopSelf();
                   } else {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
                   }
               }
           }
        });

        button_9.setOnClickListener(view ->
        {
            if(count<4)
            {

                count++;
                final_pin = final_pin + "9";
                editText.setText(final_pin);
                if(count==4)
                {
                    if (final_pin.equals(save_pin)) {
                        Intent intent1 = new Intent("close");
                        sendBroadcast(intent1);

                        count=1 ;
                        String status_from_switch_pin = Paper.book().read(Constants.from_switch_pin_button);
                        if(status_from_switch_pin.equals("yes"))
                        {

                            Paper.book().write(Constants.from_pattern_reset_button , "no");
                            String save_pattern_key = Constants.pattern_code;
                            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                            String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                            if(status_enable_lock.equals("disable"))
                            {

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.putString(Constants.enable_pattern_lock_status, "enable");
                                editor1.commit();

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                                editor.commit();

                                Paper.book().write(Constants.from_pattern_reset_button , "yes");
                                Paper.book().write(save_pattern_key , "empty");
                                Intent Start_lock_Intent = new Intent(this , ResetPatternActivity.class);
                                Start_lock_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Start_lock_Intent);
                            }
                            else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                            {

                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.putString(Constants.enable_pattern_lock_status, "disable");
                                editor1.commit();
                                Paper.book().write(save_pattern_key , "empty");
                                Intent Register_Pattern_Intent = new Intent(this , ResetPatternActivity.class);
                                Register_Pattern_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Register_Pattern_Intent);
                            }
                            else
                            {
                                Intent intent = new Intent(this , MainActivityPattern.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        }
                        stopSelf();
                    } else {
                        count=0;
                        editText.setText("");
                        final_pin="";
                        Toast.makeText(this, "PIN doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        button_back.setOnClickListener(view ->
        {
            if(count>=1)
            {
                count-- ;
                editText.setText(final_pin.substring(0 , count));
                final_pin = final_pin.substring(0 , count);

            }

        });



    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Paper.book().write(Constants.from_switch_pin_button, "no");
        Paper.book().write(Constants.from_switch_pattern_button , "no");
        if (overlayView != null) mWindowManager.removeView(overlayView);
        stopSelf();

    }




}