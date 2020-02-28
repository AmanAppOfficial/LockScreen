package e.aman.lockdemo.Views.PatternLock;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import e.aman.lockdemo.Utils.Constants;
import e.aman.lockdemo.R;

import e.aman.lockdemo.Services.LockScreenService;
import io.paperdb.Paper;

public class PatternConfirmActivity extends AppCompatActivity {

    private PatternLockView confirm_pattern_view ;
    String final_pattern = "";
    private String save_pattern_key = Constants.pattern_code;
    String previous_pattern ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_confirm);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }



        SharedPreferences sf_pattern_time = getApplication().getSharedPreferences(Constants.default_is_first_time_pattern , Context.MODE_PRIVATE);
        String isFirstTime = sf_pattern_time.getString(Constants.default_is_first_time_pattern_status , "yes");

        Paper.init(this);

        String from_reset_pattern = Paper.book().read(Constants.from_pattern_reset_button);
        previous_pattern = getIntent().getExtras().getString("lock_key");
        confirm_pattern_view = findViewById(R.id.confirm_pattern_view);


        SharedPreferences sharedPref_gallery = getSharedPreferences("my_background", Context.MODE_PRIVATE);
        int bg = sharedPref_gallery.getInt("background_resource", android.R.color.white);
        String type = sharedPref_gallery.getString("type" , "");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.confirm_pattern_layout);

        if(type.equals("app"))
            linearLayout.setBackgroundResource(bg);
        else if(type.equals("gallery"))
        {
            String real_path = sharedPref_gallery.getString("resource","");
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.parse(real_path));
                Drawable yourDrawable = Drawable.createFromStream(inputStream, real_path);
                linearLayout.setBackground(yourDrawable);

            } catch (FileNotFoundException e) {

            }
        }

        if(isFirstTime.equals("yes"))
        {
            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.putString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);
            editor1.commit();
        }



        confirm_pattern_view.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(List<PatternLockView.Dot> pattern)
            {
                startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));
                final_pattern = PatternLockUtils.patternToString(confirm_pattern_view , pattern);

                if(final_pattern.equals(previous_pattern))
                {
                    Paper.book().write(save_pattern_key,final_pattern);
                    Toast.makeText(getApplicationContext() , Constants.pattern_changed_successfully , Toast.LENGTH_SHORT).show();

                    if(!from_reset_pattern.equals("yes") && isFirstTime.equals("no"))
                    {
                        Intent Pattern_Lock_Intent = new Intent(PatternConfirmActivity.this , PatternLockScreenActivity.class);
                        startActivity(Pattern_Lock_Intent);
                        finish();
                    }
                    else
                    {
                        Intent Pattern_MainActivity_Intent = new Intent(PatternConfirmActivity.this , MainActivityPattern.class);
                        startActivity(Pattern_MainActivity_Intent);
                        finish();

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String status_pin_lock = preferences.getString(Constants.default_lock_status, Constants.default_lock_status_not_defined);


                        if(status_pin_lock.equals(Constants.default_lock_pin) && !status_pin_lock.equals(Constants.default_lock_status_not_defined))
                        {
                            SharedPreferences pin_pref = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit_pin = pin_pref.edit();
                            edit_pin.putString(Constants.enable_pin_lock_status, "disable");
                            edit_pin.commit();
                        }

                    }


                    if(isFirstTime.equals("no"))
                    {
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                            editor1.putString(Constants.enable_pattern_lock_status, "enable");
                            editor1.commit();

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                            editor.commit();




                    }
                    else
                    {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String status_pin_lock = preferences.getString(Constants.default_lock_status , "null");

                        if(status_pin_lock.equals(Constants.default_lock_pin))
                        {


                            SharedPreferences pin_pref = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit_pin = pin_pref.edit();
                            edit_pin.putString(Constants.enable_pin_lock_status, "disable");
                            edit_pin.commit();

                        }


                        SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor1 = sharedpreferences.edit();
                            editor1.putString(Constants.enable_pattern_lock_status, "enable");
                            editor1.commit();

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                            editor.commit();

                            String status = preferences.getString(Constants.default_lock_status, null);
                            if(status.equals(Constants.default_lock_pattern))
                            {
                                LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                                Intent mServiceIntent = new Intent(PatternConfirmActivity.this, mSensorService.getClass());
                                startService(mServiceIntent);

                            }



                    }



                    SharedPreferences.Editor editor_time_pattern = sf_pattern_time.edit();
                    editor_time_pattern.putString(Constants.default_is_first_time_pattern_status , "no");
                    editor_time_pattern.commit();

                }
                else
                {

                    Toast.makeText(getApplicationContext() , "PATTERN didn't match" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCleared() {

            }
        });




    }
}
