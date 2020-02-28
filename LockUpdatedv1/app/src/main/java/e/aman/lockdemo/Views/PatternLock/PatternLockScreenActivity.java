package e.aman.lockdemo.Views.PatternLock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Services.WidgetService;
import e.aman.lockdemo.Services.LockScreenService;
import e.aman.lockdemo.Utils.Constants;


public class PatternLockScreenActivity extends AppCompatActivity {

    Intent mServiceIntent;
    private LockScreenService mSensorService;
    Context ctx ;
    String status ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        makeFullScreen();
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
//        }
//
//

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);




        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        status = preferences.getString(Constants.default_lock_status, Constants.default_lock_status_not_defined);
        if(status.equals(Constants.default_lock_pattern))
        {
            ctx = this ;
            startService(new Intent(this, WidgetService.class));
            mSensorService = new LockScreenService(ctx);
            mServiceIntent = new Intent(ctx, mSensorService.getClass());
            startService(mServiceIntent);



            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
            String get_status = sharedpreferences.getString(Constants.enable_pin_lock_status , Constants.default_lock_status_not_defined);
            if(!get_status.equals(Constants.default_lock_status_not_defined))
            {

                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(Constants.enable_pin_lock_status, "disable");
                editor1.commit();

            }


        }

        setContentView(R.layout.activity_pattern_lockscreen);


        finish();

    }

//    private void makeFullScreen()
//    {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_FULLSCREEN|
//                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//
//        int visibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        if (Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
//            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
//        }
//        this.getWindow().getDecorView().setSystemUiVisibility(visibility);
//
//    }
//


    @Override
    protected void onDestroy() {

           if(status.equals(Constants.default_lock_pattern))
            stopService(mServiceIntent);

        super.onDestroy();
    }

}
