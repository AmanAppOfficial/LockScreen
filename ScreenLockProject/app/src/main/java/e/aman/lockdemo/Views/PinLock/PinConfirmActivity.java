package e.aman.lockdemo.Views.PinLock;

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
import android.widget.Button;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Services.LockScreenService;

import e.aman.lockdemo.Utils.Constants;
import io.paperdb.Paper;

public class PinConfirmActivity extends AppCompatActivity {


    String final_pin = "";
    private String save_pin_key = Constants.pin_code;
    String previous_pin ="";
    Button button_1 ,button_2 ,button_3 ,
            button_4 ,button_5 ,button_6 ,button_7 ,button_8 ,button_9 ,button_0;
    ImageView button_back;
    TextView editText , time ;
    int count=0 ;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_confirm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        SharedPreferences sf = getApplication().getSharedPreferences(Constants.default_is_first_time_pin , Context.MODE_PRIVATE);
        String isFirstTime = sf.getString(Constants.default_is_first_time_pin_status , "yes");


        TextView status_view = findViewById(R.id.statusviewKeyboard);
        status_view.setText("Re-enter New Pin");


        Paper.init(this);
        String from_reset_pin = Paper.book().read(Constants.from_pin_reset_button);

        previous_pin = getIntent().getExtras().getString("pin_lock_key");

        button_0 = (Button) findViewById(R.id.button0);
        button_1 = (Button) findViewById(R.id.button1);
        button_2 = (Button) findViewById(R.id.button2);
        button_3 = (Button) findViewById(R.id.button3);
        button_4 = (Button) findViewById(R.id.button4);
        button_5 = (Button) findViewById(R.id.button5);
        button_6 = (Button) findViewById(R.id.button6);
        button_7 = (Button) findViewById(R.id.button7);
        button_8 = (Button) findViewById(R.id.button8);
        button_9 = (Button) findViewById(R.id.button9);
        button_back = (ImageView) findViewById(R.id.button_back);
        editText = findViewById(R.id.editText);
        time = findViewById(R.id.time_text);




        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        time.setText(currentTime);



          if(isFirstTime.equals("yes"))
        {
            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.putString(Constants.enable_pin_lock_status, Constants.default_lock_status_not_defined);
            editor1.commit();


        }


        SharedPreferences sharedPref_gallery = getApplicationContext().getSharedPreferences("my_background", Context.MODE_PRIVATE);
        String type = sharedPref_gallery.getString("type" , "");
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.pin_lock_layout);
        if(type.equals("app"))
        {
            int bg = sharedPref_gallery.getInt("background_resource", android.R.color.white);
            relativeLayout.setBackgroundResource(bg);
        }


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


        button_0.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "0";
               editText.setText(final_pin);
               if(count==4)
               {
//                   startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));

                   if(final_pin.equals(previous_pin))
                   {
                       startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));
                       Paper.book().write(save_pin_key,final_pin);
                       Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                       if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                       {
                           Log.e("a","a");
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();

                       }
                       else
                       {
                           Log.e("a","b");
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();


                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           String status_pattern_lock = preferences.getString(Constants.default_lock_status, Constants.default_lock_status_not_defined);


                           if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                           {
                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                       }


                       if(isFirstTime.equals("no"))
                       {


                           Log.e("a","c");
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();

                       }
                       else
                       {


                           Log.e("a","D");

                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                           if(status_pattern_lock.equals(Constants.default_lock_pattern))
                           {

                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();


                           String status = preferences.getString(Constants.default_lock_status, null);
                           if(status.equals(Constants.default_lock_pin))
                           {

                               LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                               Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                               startService(mServiceIntent);
                           }
                       }


                       SharedPreferences.Editor editor_time = sf.edit();
                       editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                       editor_time.commit();

                   }
                   else
                   {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
              if(count==4)            {

//                  startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));

                  if(final_pin.equals(previous_pin))
                  {
                      startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));
                      Paper.book().write(save_pin_key,final_pin);
                      Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                      if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                      {
                          Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                          startActivity(Pattern_MainActivity_Intent);
                          finish();

                      }
                      else
                      {
                          Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                          startActivity(Pattern_MainActivity_Intent);
                          finish();

                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                          String status_pattern_lock = sharedpreferences.getString(Constants.default_lock_status , Constants.default_lock_status_not_defined);


                          if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                          {
                              SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                              SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                              edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                              edit_pattern.commit();

                          }


                      }


                      if(isFirstTime.equals("no"))
                      {


                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor1 = sharedpreferences.edit();
                          editor1.putString(Constants.enable_pin_lock_status, "enable");
                          editor1.commit();

                          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                          SharedPreferences.Editor editor = preferences.edit();
                          editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                          editor.commit();

                      }
                      else
                      {



                          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                          String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                          if(status_pattern_lock.equals(Constants.default_lock_pattern))
                          {

                              SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                              SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                              edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                              edit_pattern.commit();

                          }


                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor1 = sharedpreferences.edit();
                          editor1.putString(Constants.enable_pin_lock_status, "enable");
                          editor1.commit();

                          SharedPreferences.Editor editor = preferences.edit();
                          editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                          editor.commit();


                          String status = preferences.getString(Constants.default_lock_status, null);
                          if(status.equals(Constants.default_lock_pin))
                          {

                              LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                              Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                              startService(mServiceIntent);
                          }
                      }


                      SharedPreferences.Editor editor_time = sf.edit();
                      editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                      editor_time.commit();

                  }
                  else
                  {
                      count=0;
                      editText.setText("");
                      final_pin="";
                      Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
             if(count==4)            {

//                 startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));

                 if(final_pin.equals(previous_pin))
                 {
                     startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));
                     Paper.book().write(save_pin_key,final_pin);
                     Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                     if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                     {
                         Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                         startActivity(Pattern_MainActivity_Intent);
                         finish();

                     }
                     else
                     {
                         Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                         startActivity(Pattern_MainActivity_Intent);
                         finish();

                         SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                         String status_pattern_lock = sharedpreferences.getString(Constants.default_lock_status , Constants.default_lock_status_not_defined);


                         if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                         {
                             SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                             SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                             edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                             edit_pattern.commit();

                         }


                     }


                     if(isFirstTime.equals("no"))
                     {


                         SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                         SharedPreferences.Editor editor1 = sharedpreferences.edit();
                         editor1.putString(Constants.enable_pin_lock_status, "enable");
                         editor1.commit();

                         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                         SharedPreferences.Editor editor = preferences.edit();
                         editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                         editor.commit();

                     }
                     else
                     {



                         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                         String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                         if(status_pattern_lock.equals(Constants.default_lock_pattern))
                         {

                             SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                             SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                             edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                             edit_pattern.commit();

                         }


                         SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                         SharedPreferences.Editor editor1 = sharedpreferences.edit();
                         editor1.putString(Constants.enable_pin_lock_status, "enable");
                         editor1.commit();

                         SharedPreferences.Editor editor = preferences.edit();
                         editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                         editor.commit();


                         String status = preferences.getString(Constants.default_lock_status, null);
                         if(status.equals(Constants.default_lock_pin))
                         {

                             LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                             Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                             startService(mServiceIntent);
                         }
                     }


                     SharedPreferences.Editor editor_time = sf.edit();
                     editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                     editor_time.commit();

                 }
                 else
                 {
                     Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
               if(count==4)            {

//                   startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));

                   if(final_pin.equals(previous_pin))
                   {
                       startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));
                       Paper.book().write(save_pin_key,final_pin);
                       Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                       if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                       {
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();

                       }
                       else
                       {
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();

                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_pattern_lock = sharedpreferences.getString(Constants.default_lock_status , Constants.default_lock_status_not_defined);


                           if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                           {
                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                       }


                       if(isFirstTime.equals("no"))
                       {


                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();

                       }
                       else
                       {



                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                           if(status_pattern_lock.equals(Constants.default_lock_pattern))
                           {

                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();


                           String status = preferences.getString(Constants.default_lock_status, null);
                           if(status.equals(Constants.default_lock_pin))
                           {

                               LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                               Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                               startService(mServiceIntent);
                           }
                       }


                       SharedPreferences.Editor editor_time = sf.edit();
                       editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                       editor_time.commit();

                   }
                   else
                   {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
                startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));
                if(count==4)            {

//                    startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));


                    if(final_pin.equals(previous_pin))
                    {
                        Paper.book().write(save_pin_key,final_pin);
                        Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                        if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                        {
                            Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                            startActivity(Pattern_MainActivity_Intent);
                            finish();

                        }
                        else
                        {
                            Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                            startActivity(Pattern_MainActivity_Intent);
                            finish();

                            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                            String status_pattern_lock = sharedpreferences.getString(Constants.default_lock_status , Constants.default_lock_status_not_defined);


                            if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                            {
                                SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                                edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                                edit_pattern.commit();

                            }


                        }


                        if(isFirstTime.equals("no"))
                        {


                            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedpreferences.edit();
                            editor1.putString(Constants.enable_pin_lock_status, "enable");
                            editor1.commit();

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                            editor.commit();

                        }
                        else
                        {



                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                            if(status_pattern_lock.equals(Constants.default_lock_pattern))
                            {

                                SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                                edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                                edit_pattern.commit();

                            }


                            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedpreferences.edit();
                            editor1.putString(Constants.enable_pin_lock_status, "enable");
                            editor1.commit();

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                            editor.commit();


                            String status = preferences.getString(Constants.default_lock_status, null);
                            if(status.equals(Constants.default_lock_pin))
                            {

                                LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                                Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                                startService(mServiceIntent);
                            }
                        }


                        SharedPreferences.Editor editor_time = sf.edit();
                        editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                        editor_time.commit();

                    }
                    else
                    {
                        count=0;
                        editText.setText("");
                        final_pin="";
                        Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
              if(count==4)            {
                  if(final_pin.equals(previous_pin))
                  {
//                      startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));
                      Paper.book().write(save_pin_key,final_pin);
                      Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                      if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                      {
                          Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                          startActivity(Pattern_MainActivity_Intent);
                          finish();

                      }
                      else
                      {
                          Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                          startActivity(Pattern_MainActivity_Intent);
                          finish();

                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                          String status_pattern_lock = sharedpreferences.getString(Constants.default_lock_status , Constants.default_lock_status_not_defined);


                          if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                          {
                              SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                              SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                              edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                              edit_pattern.commit();

                          }


                      }


                      if(isFirstTime.equals("no"))
                      {


                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor1 = sharedpreferences.edit();
                          editor1.putString(Constants.enable_pin_lock_status, "enable");
                          editor1.commit();

                          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                          SharedPreferences.Editor editor = preferences.edit();
                          editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                          editor.commit();

                      }
                      else
                      {



                          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                          String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                          if(status_pattern_lock.equals(Constants.default_lock_pattern))
                          {

                              SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                              SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                              edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                              edit_pattern.commit();

                          }


                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor1 = sharedpreferences.edit();
                          editor1.putString(Constants.enable_pin_lock_status, "enable");
                          editor1.commit();

                          SharedPreferences.Editor editor = preferences.edit();
                          editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                          editor.commit();


                          String status = preferences.getString(Constants.default_lock_status, null);
                          if(status.equals(Constants.default_lock_pin))
                          {

                              LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                              Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                              startService(mServiceIntent);
                          }
                      }


                      SharedPreferences.Editor editor_time = sf.edit();
                      editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                      editor_time.commit();

                  }
                  else
                  {
                      count=0;
                      editText.setText("");
                      final_pin="";
                      Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
               if(count==4)            {
                   if(final_pin.equals(previous_pin))
                   {
//                       startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));

                       Paper.book().write(save_pin_key,final_pin);
                       Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                       if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                       {
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();

                       }
                       else
                       {
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();

                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_pattern_lock = sharedpreferences.getString(Constants.default_lock_status , Constants.default_lock_status_not_defined);


                           if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                           {
                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                       }


                       if(isFirstTime.equals("no"))
                       {


                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();

                       }
                       else
                       {



                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                           if(status_pattern_lock.equals(Constants.default_lock_pattern))
                           {

                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();


                           String status = preferences.getString(Constants.default_lock_status, null);
                           if(status.equals(Constants.default_lock_pin))
                           {

                               LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                               Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                               startService(mServiceIntent);
                           }
                       }


                       SharedPreferences.Editor editor_time = sf.edit();
                       editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                       editor_time.commit();

                   }
                   else
                   {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
               if(count==4)            {
                   if(final_pin.equals(previous_pin))
                   {
//                       startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));

                       Paper.book().write(save_pin_key,final_pin);
                       Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                       if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                       {
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();

                       }
                       else
                       {
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();

                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_pattern_lock = sharedpreferences.getString(Constants.default_lock_status , Constants.default_lock_status_not_defined);


                           if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                           {
                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                       }


                       if(isFirstTime.equals("no"))
                       {


                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();

                       }
                       else
                       {



                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                           if(status_pattern_lock.equals(Constants.default_lock_pattern))
                           {

                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();


                           String status = preferences.getString(Constants.default_lock_status, null);
                           if(status.equals(Constants.default_lock_pin))
                           {

                               LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                               Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                               startService(mServiceIntent);
                           }
                       }


                       SharedPreferences.Editor editor_time = sf.edit();
                       editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                       editor_time.commit();

                   }
                   else
                   {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
               if(count==4)            {
                   if(final_pin.equals(previous_pin))
                   {
//                       startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));

                       Paper.book().write(save_pin_key,final_pin);
                       Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                       if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                       {
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();

                       }
                       else
                       {
                           Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                           startActivity(Pattern_MainActivity_Intent);
                           finish();

                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                           String status_pattern_lock = sharedpreferences.getString(Constants.default_lock_status , Constants.default_lock_status_not_defined);


                           if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                           {
                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                       }


                       if(isFirstTime.equals("no"))
                       {


                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();

                       }
                       else
                       {



                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                           String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                           if(status_pattern_lock.equals(Constants.default_lock_pattern))
                           {

                               SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                               edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                               edit_pattern.commit();

                           }


                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "enable");
                           editor1.commit();

                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                           editor.commit();


                           String status = preferences.getString(Constants.default_lock_status, null);
                           if(status.equals(Constants.default_lock_pin))
                           {

                               LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                               Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                               startService(mServiceIntent);
                           }
                       }


                       SharedPreferences.Editor editor_time = sf.edit();
                       editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                       editor_time.commit();

                   }
                   else
                   {
                       count=0;
                       editText.setText("");
                       final_pin="";
                       Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
              if(count==4)            {
                  if(final_pin.equals(previous_pin))
                  {
//                      startForegroundService(new Intent(getApplicationContext(), LockScreenService.class));

                      Paper.book().write(save_pin_key,final_pin);
                      Toast.makeText(getApplicationContext() , Constants.pin_changed_successfully , Toast.LENGTH_SHORT).show();


                      if(!from_reset_pin.equals("yes") && isFirstTime.equals("no"))
                      {
                          Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , PinLockScreenActivity.class);
                          startActivity(Pattern_MainActivity_Intent);
                          finish();

                      }
                      else
                      {
                          Intent Pattern_MainActivity_Intent = new Intent(PinConfirmActivity.this , MainActivityPin.class);
                          startActivity(Pattern_MainActivity_Intent);
                          finish();

                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                          String status_pattern_lock = sharedpreferences.getString(Constants.default_lock_status , Constants.default_lock_status_not_defined);


                          if(status_pattern_lock.equals(Constants.default_lock_pattern) && !status_pattern_lock.equals(Constants.default_lock_status_not_defined))
                          {
                              SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                              SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                              edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                              edit_pattern.commit();

                          }


                      }


                      if(isFirstTime.equals("no"))
                      {


                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor1 = sharedpreferences.edit();
                          editor1.putString(Constants.enable_pin_lock_status, "enable");
                          editor1.commit();

                          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                          SharedPreferences.Editor editor = preferences.edit();
                          editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                          editor.commit();

                      }
                      else
                      {



                          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                          String status_pattern_lock = preferences.getString(Constants.default_lock_status , "null");

                          if(status_pattern_lock.equals(Constants.default_lock_pattern))
                          {

                              SharedPreferences pattern_pref = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                              SharedPreferences.Editor edit_pattern = pattern_pref.edit();
                              edit_pattern.putString(Constants.enable_pattern_lock_status, "disable");
                              edit_pattern.commit();

                          }


                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor1 = sharedpreferences.edit();
                          editor1.putString(Constants.enable_pin_lock_status, "enable");
                          editor1.commit();

                          SharedPreferences.Editor editor = preferences.edit();
                          editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                          editor.commit();


                          String status = preferences.getString(Constants.default_lock_status, null);
                          if(status.equals(Constants.default_lock_pin))
                          {

                              LockScreenService mSensorService = new LockScreenService(getApplicationContext());
                              Intent mServiceIntent = new Intent(PinConfirmActivity.this, mSensorService.getClass());
                              startService(mServiceIntent);
                          }
                      }


                      SharedPreferences.Editor editor_time = sf.edit();
                      editor_time.putString(Constants.default_is_first_time_pin_status , "no");
                      editor_time.commit();

                  }
                  else
                  {
                      count=0;
                      editText.setText("");
                      final_pin="";
                      Toast.makeText(getApplicationContext() , "PIN didn't match" , Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();

            if(final_pin.isEmpty())
            {

                SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(Constants.enable_pin_lock_status, "not defined");
                editor1.commit();
                Paper.book().write(save_pin_key , "empty");
                Paper.book().write(Constants.from_pin_reset_button , "yes");

                SharedPreferences sf = getApplication().getSharedPreferences(Constants.default_is_first_time_pin , Context.MODE_PRIVATE);
                SharedPreferences.Editor ss = sf.edit();
                ss.putString(Constants.default_is_first_time_pin_status,"not_defined");
                ss.commit();

                SharedPreferences pref_in_app = getSharedPreferences("from_switch_button" , Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_in_app = pref_in_app.edit();
                editor_in_app.putString("switch_status" , "not defined");
                editor_in_app.commit();



            }

    }



}
