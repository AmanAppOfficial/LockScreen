package e.aman.lockdemo.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Services.FloatingWidgetService;
import e.aman.lockdemo.Services.LockScreenService;
import io.paperdb.Paper;

public class PatternLockScreenActivity extends AppCompatActivity {

    Intent mServiceIntent;
    private LockScreenService mSensorService;
    Context ctx ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        startService(new Intent(this, FloatingWidgetService.class));
        ctx = this ;
        mSensorService = new LockScreenService(ctx);
        mServiceIntent = new Intent(ctx, mSensorService.getClass());
        startService(mServiceIntent);
        setContentView(R.layout.activity_pattern_lockscreen);

        finish();


    }

    private void makeFullScreen()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_FULLSCREEN|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        int visibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        this.getWindow().getDecorView().setSystemUiVisibility(visibility);

    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onDestroy() {

        stopService(mServiceIntent);
        super.onDestroy();
    }

}
