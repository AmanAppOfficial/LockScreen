package e.aman.lockdemo.Views.PatternLock;

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
import e.aman.lockdemo.Views.PinLock.ResetPinActivity;
import e.aman.lockdemo.Views.Wallpaper.ChangeBackgroundActivity;
import io.paperdb.Paper;



public class MainActivityPattern extends AppCompatActivity
{



   LinearLayout main_reset_pattern_button , change_background_button , change_background_gallery_button  ,switch_to_pin_button ;
    Switch  enable_lock_switch;
    public final static int REQUEST_CODE = 10101;
    private String save_pattern_key = Constants.pattern_code;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    public boolean set_external_storage_granted = false ;
    String status_enable_lock=null;
    SharedPreferences sharedpreferences;



    SharedPreferences sharedPref_gallery;
    SharedPreferences.Editor editor_gallery;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_activity_main);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        }

        sharedPref_gallery = getApplication().getSharedPreferences("my_background", Context.MODE_PRIVATE);
        editor_gallery = sharedPref_gallery.edit();




        Log.e("Pattern key" , Paper.book().read("pattern_code" , "abc"));





        SharedPreferences pref_in_app = getSharedPreferences("from_switch_button" , Context.MODE_PRIVATE);

        String InApp = pref_in_app.getString("switch_status" , Constants.default_lock_status_not_defined);
        if(InApp.equals("yes"))
        {

            SharedPreferences.Editor editor_in_app = pref_in_app.edit();
            editor_in_app.putString("switch_status" , Constants.default_lock_status_not_defined);
            editor_in_app.commit();

        }




        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Paper.init(this);
        Paper.book().write(Constants.from_change_image_pattern , "no");
        Paper.book().write(Constants.from_pattern_reset_button , "no");

        SharedPreferences sharedPreferences_time = getSharedPreferences(Constants.default_is_first_time_pattern , Context.MODE_PRIVATE);
        String is_first_time = sharedPreferences_time.getString(Constants.default_is_first_time_pattern_status , "yes");

        main_reset_pattern_button = (LinearLayout)findViewById(R.id.change_password_button);
        switch_to_pin_button = (LinearLayout) findViewById(R.id.switch_to_pin_button);
        enable_lock_switch = (Switch)findViewById(R.id.enable_lock_button);
        change_background_button = (LinearLayout) findViewById(R.id.change_wallpaper_button);
        change_background_gallery_button = (LinearLayout)findViewById(R.id.change_wallpaper_gallery_button);
        LinearLayout change_pattern_layout = (LinearLayout)findViewById(R.id.change_pattern_layout);



        if(is_first_time.equals("yes"))
        {
            change_pattern_layout.setVisibility(View.GONE);
        }
        else
        {
            change_pattern_layout.setVisibility(View.VISIBLE);
        }



        change_background_gallery_button.setOnClickListener(view ->
                changeImageFromGallery()
        );
       checkDrawOverlayPermission();
        addExternalStoragePermission();


      CallPref();

       enable_lock_switch.setOnClickListener(view->
       {
           Paper.book().write("from_status" , "no");
           Paper.book().write(Constants.from_switch_pattern_button , "no");
           CallPref();
           if(status_enable_lock.equals(Constants.default_lock_status_not_defined))
           {

               enable_lock_switch.setChecked(false);
               SharedPreferences.Editor editor1 = sharedpreferences.edit();
               editor1.putString(Constants.enable_pattern_lock_status, "disable");
               editor1.commit();
               Paper.book().write(save_pattern_key , "empty");
               Intent Register_Pattern_Intent = new Intent(MainActivityPattern.this , ResetPatternActivity.class);
               startActivity(Register_Pattern_Intent);

           }
           else
           {
               if(status_enable_lock.equals("disable"))
               {

                   Paper.book().write("from_status" , "no");
                   SharedPreferences.Editor editor1 = sharedpreferences.edit();
                   editor1.putString(Constants.enable_pattern_lock_status, "enable");
                   editor1.commit();

                   SharedPreferences.Editor editor = preferences.edit();
                   editor.putString(Constants.default_lock_status, Constants.default_lock_pattern);
                   editor.commit();

                   Intent Start_lock_Intent = new Intent(MainActivityPattern.this , PatternLockScreenActivity.class);
                   startActivity(Start_lock_Intent);



               }
               else if(status_enable_lock.equals("enable"))
               {

                   stopService(new Intent(this, LockScreenService.class));

                   SharedPreferences.Editor editor1 = sharedpreferences.edit();
                   editor1.putString(Constants.enable_pattern_lock_status, "disable");
                   editor1.commit();

                   SharedPreferences.Editor editor = preferences.edit();
                   editor.putString(Constants.default_lock_status, Constants.default_unlock_pattern_status);
                   editor.commit();
                   Intent Start_lock_Intent = new Intent(MainActivityPattern.this , PatternLockScreenActivity.class);
                   startActivity(Start_lock_Intent);

               }
           }

       });


        switch_to_pin_button.setOnClickListener(view ->
        {

            Paper.book().write("from_status" , "no");

            SharedPreferences.Editor editor_in_app = pref_in_app.edit();
            editor_in_app.putString("switch_status" , "yes");
            editor_in_app.commit();

            sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
            status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

            if(status_enable_lock.equals("enable"))
            {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putString(Constants.default_lock_status , Constants.default_lock_pattern);
                edit.commit();


                Intent i = new Intent(this , PatternLockScreenActivity.class);
                Paper.book().write(Constants.from_switch_pattern_button , "yes");
                startActivity(i);
                finish();

            }
            else
            {
                Paper.book().write(Constants.from_pin_reset_button , "no");
                String save_pin_key = Constants.pin_code;
                sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                String status_enable_pinlock =  sharedpreferences.getString(Constants.enable_pin_lock_status, Constants.default_lock_status_not_defined);

                if(status_enable_pinlock.equals("disable") || status_enable_pinlock.equals("enable"))
                {
                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.putString(Constants.enable_pin_lock_status, "enable");
                    editor1.commit();

                    SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences1.edit();
                    editor.putString(Constants.default_lock_status, Constants.default_lock_pin);
                    editor.commit();

                    Paper.book().write(Constants.from_pin_reset_button , "yes");
                    Paper.book().write(save_pin_key , "empty");
                    Intent Start_lock_Intent = new Intent(MainActivityPattern.this , ResetPinActivity.class);
                    startActivity(Start_lock_Intent);
                    finish();


                }
                else if(status_enable_pinlock.equals(Constants.default_lock_status_not_defined))
                {
                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.putString(Constants.enable_pin_lock_status, "disable");
                    editor1.commit();
                    Paper.book().write(save_pin_key , "empty");
                    Toast.makeText(getApplicationContext() , "First Register Pin" , Toast.LENGTH_SHORT).show();
                    Intent Register_Pattern_Intent = new Intent(MainActivityPattern.this , ResetPinActivity.class);
                    startActivity(Register_Pattern_Intent);
                    finish();

                }


            }


        });



       main_reset_pattern_button.setOnClickListener(v -> {

           Paper.book().write(Constants.from_pattern_reset_button , "yes");
           if(is_first_time.equals("yes"))
           {
               enable_lock_switch.setChecked(false);
               SharedPreferences.Editor editor1 = sharedpreferences.edit();
               editor1.putString(Constants.enable_pattern_lock_status, "disable");
               editor1.commit();

               Paper.book().write(save_pattern_key , "empty");
               Toast.makeText(getApplicationContext() , "First Register Pattern" , Toast.LENGTH_SHORT).show();
               Intent Register_Pattern_Intent = new Intent(MainActivityPattern.this , ResetPatternActivity.class);
               startActivity(Register_Pattern_Intent);
           }
           else
           {
               Intent Change_Pattern_Intent = new Intent(MainActivityPattern.this, ResetPatternActivity.class);
               startActivity(Change_Pattern_Intent);
           }
       });

        change_background_button.setOnClickListener(view ->
        {
            Intent change_background_intent = new Intent(MainActivityPattern.this , ChangeBackgroundActivity.class);
            startActivity(change_background_intent);
        });

    }   //onCreate closer

    @Override
    protected void onResume() {
        super.onResume();
        CallPref();

    }



    private void CallPref() {
        sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
        status_enable_lock =  sharedpreferences.getString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);

        if(!status_enable_lock.isEmpty() || !status_enable_lock.equals("null") || !status_enable_lock.equals(null) || !status_enable_lock.equals(Constants.default_lock_status_not_defined))
        {
            if(status_enable_lock.equals("enable"))
            {
                enable_lock_switch.setChecked(true);
            }
            else
            {
                enable_lock_switch.setChecked(false);
            }
        }
    }

    private void changeImageFromGallery()
    {

        setImageFromGallery();
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

    public void setImageFromGallery()
    {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
        Paper.book().write(Constants.from_change_image_pattern , "yes");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageUri = data.getData();
            editor_gallery.putString("resource", imageUri.toString());
            editor_gallery.putString("type" ,"gallery");
            editor_gallery.commit();


            Toast.makeText(getApplicationContext() , "Background changed Successfully" , Toast.LENGTH_SHORT).show();
        }
    }
    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(id);
        }
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
        if(Paper.book().read(Constants.from_change_image_pattern).equals("no"))
        {

            if (isApplicationSentToBackground(this)){
                finish();
            }

        }
        Paper.book().write(Constants.from_change_image_pattern , "no");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Paper.book().write(Constants.from_change_image_pattern , "no");

    }
}
