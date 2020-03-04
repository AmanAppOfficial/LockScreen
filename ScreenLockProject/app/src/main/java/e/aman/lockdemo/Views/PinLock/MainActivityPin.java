package e.aman.lockdemo.Views.PinLock;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;



import java.util.List;
import e.aman.lockdemo.R;
import e.aman.lockdemo.Services.LockScreenService;
import e.aman.lockdemo.Utils.Constants;
import e.aman.lockdemo.Views.PatternLock.MainActivityPattern;
import e.aman.lockdemo.Views.PatternLock.ResetPatternActivity;
import e.aman.lockdemo.Views.Wallpaper.ChangeBackgroundActivity;
import io.paperdb.Paper;

public class MainActivityPin extends AppCompatActivity {

    LinearLayout redirect_reset_pin_button ,  switch_to_pattern_button , change_background_button , change_background_gallery_button ;
    Switch enable_pinlock_button;
    private String save_pin_key = Constants.pin_code;
    private static final int PICK_IMAGE = 100;
    public final static int REQUEST_CODE = 10101;
    private Uri imageUri;
    public boolean set_external_storage_granted = false ;

    SharedPreferences sharedPref_gallery ;
    SharedPreferences.Editor editor_gallery ;

    String status_enable_pinlock = null;
    SharedPreferences sharedpreferences;
    SharedPreferences pref_in_app;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_activity_main);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
//        }



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        }

        switch_to_pattern_button = (LinearLayout) findViewById(R.id.switch_to_pattern_button);
        enable_pinlock_button = (Switch)findViewById(R.id.enable_pinlock_button);
        redirect_reset_pin_button = (LinearLayout) findViewById(R.id.redirect_reset_pin_button);
        change_background_button = (LinearLayout) findViewById(R.id.change_wallpaper_button);
        change_background_gallery_button = (LinearLayout)findViewById(R.id.change_wallpaper_gallery_button);
        LinearLayout change_activity_pin = (LinearLayout)findViewById(R.id.change_pin_layout);


        SharedPreferences sharedpreferences2 = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
        String status_enable_lock2 =  sharedpreferences2.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);


        pref_in_app = getSharedPreferences("from_switch_button" , Context.MODE_PRIVATE);
        String InApp = pref_in_app.getString("switch_status" , Constants.default_lock_status_not_defined);

        Log.e("In app" , InApp);
        Log.e("status pattern", status_enable_lock2);

        if(status_enable_lock2.equals("enable") && !InApp.equals("yes"))
        {
            Intent i = new Intent(this , MainActivityPattern.class);
            startActivity(i);
            finish();


        }



        Paper.init(this);
        Paper.book().write(Constants.from_change_image_pin , "no");
        Paper.book().write(Constants.from_pin_reset_button , "no");
        Paper.book().write(Constants.from_switch_pin_button,"no");
        sharedPref_gallery = getApplication().getSharedPreferences("my_background", Context.MODE_PRIVATE);
        editor_gallery = sharedPref_gallery.edit();


        SharedPreferences sharedPreferences_time = getSharedPreferences(Constants.default_is_first_time_pin , Context.MODE_PRIVATE);
        String is_first_time = sharedPreferences_time.getString(Constants.default_is_first_time_pin_status , "yes");


        if(is_first_time.equals("yes"))
        {
            change_activity_pin.setVisibility(View.GONE);
        }
        else
        {
            change_activity_pin.setVisibility(View.VISIBLE);
        }


        change_background_gallery_button.setOnClickListener(view ->
                changeImageFromGallery()
        );

        change_background_button.setOnClickListener(view ->
        {
            Intent change_background_intent = new Intent(MainActivityPin.this , ChangeBackgroundActivity.class);
            startActivity(change_background_intent);
        });

        checkDrawOverlayPermission();
        addExternalStoragePermission();

        callPref();

        enable_pinlock_button.setOnClickListener(view->
        {
            Paper.book().write(Constants.from_switch_pin_button , "no");
            callPref();

            Log.e("pin main status" , status_enable_pinlock);
            if(status_enable_pinlock.equals(Constants.default_lock_status_not_defined))
            {

                enable_pinlock_button.setChecked(false);
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(Constants.enable_pin_lock_status, "disable");
                editor1.commit();
                Paper.book().write(save_pin_key , "empty");
                Toast.makeText(getApplicationContext() , "First Register Pin" , Toast.LENGTH_SHORT).show();
                Intent Register_Pattern_Intent = new Intent(MainActivityPin.this , ResetPinActivity.class);
                startActivity(Register_Pattern_Intent);
            }
            else
            {
                if(status_enable_pinlock.equals("disable"))
                {

                    Paper.book().write("from_status" , "no");

                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.putString(Constants.enable_pin_lock_status, "enable");
                    editor1.commit();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                    editor.commit();

                    Intent Start_lock_Intent = new Intent(MainActivityPin.this , PinLockScreenActivity.class);
                    startActivity(Start_lock_Intent);


                }
                else if(status_enable_pinlock.equals("enable"))
                {
                    stopService(new Intent(this, LockScreenService.class));


                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.putString(Constants.enable_pin_lock_status, "disable");
                    editor1.commit();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constants.default_lock_status, Constants.default_unlock_pin_status);
                    editor.commit();
                    Intent Start_lock_Intent = new Intent(MainActivityPin.this , PinLockScreenActivity.class);
                    startActivity(Start_lock_Intent);


                }
            }

        });

        switch_to_pattern_button.setOnClickListener(view ->
        {

            Paper.book().write("from_status" , "no");


            sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
            status_enable_pinlock =  sharedpreferences.getString(Constants.enable_pin_lock_status, Constants.default_lock_status_not_defined);

            if(status_enable_pinlock.equals("enable"))
            {

                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putString(Constants.default_lock_status , Constants.default_lock_pin);
                edit.commit();


                Intent i = new Intent(MainActivityPin.this , PinLockScreenActivity.class);
                Paper.book().write(Constants.from_switch_pin_button , "yes");
                startActivity(i);
                finish();

            }
            else
            {


                Paper.book().write(Constants.from_pattern_reset_button , "no");
                String save_pattern_key = Constants.pattern_code;
                sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                 String status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

                if(status_enable_lock.equals("disable") || status_enable_lock.equals("enable"))
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
                    Intent Start_lock_Intent = new Intent(MainActivityPin.this , ResetPatternActivity.class);
                    startActivity(Start_lock_Intent);
                    finish();

                }
                else if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
                {

                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.putString(Constants.enable_pattern_lock_status, "disable");
                    editor1.commit();

                    Paper.book().write(save_pattern_key , "empty");
                    Intent Register_Pattern_Intent = new Intent(MainActivityPin.this , ResetPatternActivity.class);
                    startActivity(Register_Pattern_Intent);
                    finish();

                }

            }

        });

        redirect_reset_pin_button.setOnClickListener(v -> {

            Paper.book().write(Constants.from_pin_reset_button , "yes");
            if(is_first_time.equals("yes"))
            {
                enable_pinlock_button.setChecked(false);
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(Constants.enable_pin_lock_status, "disable");
                editor1.commit();
                Paper.book().write(save_pin_key , "empty");
                Toast.makeText(getApplicationContext() , "First Register Pin" , Toast.LENGTH_SHORT).show();
                Intent Register_Pattern_Intent = new Intent(MainActivityPin.this , ResetPinActivity.class);
                startActivity(Register_Pattern_Intent);

            }
            else
            {
                Intent Change_Pin_Intent = new Intent(MainActivityPin.this, ResetPinActivity.class);
                startActivity(Change_Pin_Intent);


            }

        });

    }

    private void callPref() {
        sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
        status_enable_pinlock =  sharedpreferences.getString(Constants.enable_pin_lock_status, Constants.default_lock_status_not_defined);

        if(!status_enable_pinlock.isEmpty() || !status_enable_pinlock.equals("null") || !status_enable_pinlock.equals(null) || !status_enable_pinlock.equals(Constants.default_lock_status_not_defined))
        {
            if(status_enable_pinlock.equals("enable"))
            {
                enable_pinlock_button.setChecked(true);
            }
            else
            {
                enable_pinlock_button.setChecked(false);
            }
        }

    }

    private void changeImageFromGallery()
    {
        setImageFromGallery();
    }

    public void setImageFromGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
        Paper.book().write(Constants.from_change_image_pin , "yes");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {

            if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
            {
                imageUri = data.getData();
                editor_gallery.putString("resource", imageUri.toString());
                editor_gallery.putString("type" ,"gallery");
                editor_gallery.apply();
                Toast.makeText(getApplicationContext() , "Background changed Successfully" , Toast.LENGTH_SHORT).show();
            }
        }



    }
    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
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
    public void addExternalStoragePermission()
    {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            set_external_storage_granted = true ;
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onPause() {

        if(Paper.book().read(Constants.from_change_image_pin).equals("no"))
        {

            if (isApplicationSentToBackground(this)){
                finish();
            }

        }
        Paper.book().write(Constants.from_change_image_pin , "no");
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        callPref();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor_in_app = pref_in_app.edit();
        editor_in_app.putString("in_app_status" , "not defined");
        editor_in_app.commit();
        Paper.book().write(Constants.from_change_image_pin , "no");

    }
}
