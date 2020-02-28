package e.aman.lockdemo.Views.PatternLock;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Views.PinLock.MainActivityPin;
import e.aman.lockdemo.Views.Wallpaper.ChangeBackgroundActivity;
import io.paperdb.Paper;


public class MainActivityPattern extends AppCompatActivity
{

    Button redirect_reset_pattern_button , change_background_button;
    Switch switch_to_pin_button , enable_lock_switch;
    TextView enable_lock_text ;

    public final static int REQUEST_CODE = 10101;
    private String save_pattern_key = "pattern_code";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_activity_main);

        Paper.init(this);
         redirect_reset_pattern_button = (Button)findViewById(R.id.redirect_reset_pattern_button);

        switch_to_pin_button = (Switch)findViewById(R.id.switch_to_pin_button);
        enable_lock_switch = (Switch)findViewById(R.id.enable_lock_button);
        change_background_button = (Button)findViewById(R.id.change_background_button);
        enable_lock_text = (TextView)findViewById(R.id.enable_lock_text);
        SharedPreferences sharedpreferences = getSharedPreferences("enable_lock_status", Context.MODE_PRIVATE);
//        SharedPreferences sharedpreferences_pin = getSharedPreferences("enable_pinlock_status", Context.MODE_PRIVATE);

        checkDrawOverlayPermission();

        String status_enable_lock =  sharedpreferences.getString("lock_status_is", "not defined");

       if(!status_enable_lock.isEmpty() || !status_enable_lock.equals("null") || !status_enable_lock.equals(null) || !status_enable_lock.equals("not defined"))
       {
           if(status_enable_lock.equals("enable"))
           {
               enable_lock_text.setText("Enable Lock");
               enable_lock_switch.setChecked(true);
           }
           else
           {
               enable_lock_text.setText("Enable Lock");
               enable_lock_switch.setChecked(false);
           }
       }



       enable_lock_switch.setOnClickListener(view->
       {
           if(status_enable_lock.equals("not defined"))
           {
               enable_lock_switch.setChecked(false);
               SharedPreferences.Editor editor1 = sharedpreferences.edit();
               editor1.putString("lock_status_is", "disable");
               editor1.commit();
               Paper.book().write(save_pattern_key , "empty");
               Toast.makeText(getApplicationContext() , "First Register Pattern" , Toast.LENGTH_SHORT).show();
               Intent Register_Pattern_Intent = new Intent(MainActivityPattern.this , ResetPatternActivity.class);
               startActivity(Register_Pattern_Intent);
               finish();

           }
           else
           {
               if(status_enable_lock.equals("disable"))
               {

                   SharedPreferences.Editor editor1 = sharedpreferences.edit();
                   editor1.putString("lock_status_is", "enable");
                   editor1.commit();

//                   SharedPreferences.Editor editor1_pin_lock = sharedpreferences_pin.edit();
//                   editor1_pin_lock.putString("pinlock_status_is", "disable");
//                   editor1_pin_lock.commit();

                   enable_lock_text.setText("Enable lock");



                   SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                   SharedPreferences.Editor editor = preferences.edit();
                   editor.putString("status", "lock_pattern");
                   editor.commit();

                   Intent Start_lock_Intent = new Intent(MainActivityPattern.this , PatternLockScreenActivity.class);
                   startActivity(Start_lock_Intent);
                   finish();
               }
               else
               {

                   SharedPreferences.Editor editor1 = sharedpreferences.edit();
                   editor1.putString("lock_status_is", "disable");
                   editor1.commit();
                   enable_lock_text.setText("Enable lock");

                   SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                   SharedPreferences.Editor editor = preferences.edit();
                   editor.putString("status", "unlock_pattern");
                   editor.commit();
                   Intent Start_lock_Intent = new Intent(MainActivityPattern.this , PatternLockScreenActivity.class);
                   startActivity(Start_lock_Intent);
                   finish();

               }
           }

       });

        switch_to_pin_button.setOnClickListener(view ->
        {
            Intent switch_intent = new Intent(MainActivityPattern.this , MainActivityPin.class);
            startActivity(switch_intent);
            finish();
        });



       redirect_reset_pattern_button.setOnClickListener(v -> {
            Intent Change_Pattern_Intent = new Intent(MainActivityPattern.this, ResetPatternActivity.class);
            startActivity(Change_Pattern_Intent);
        });

        change_background_button.setOnClickListener(view ->
        {
            Intent change_background_intent = new Intent(MainActivityPattern.this , ChangeBackgroundActivity.class);
            startActivity(change_background_intent);
        });

    }


    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }



}
