package e.aman.lockdemo.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import java.io.FileNotFoundException;
import java.io.InputStream;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Utils.Constants;
import io.paperdb.Paper;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        String pattern_saved =  Paper.book().read(Constants.pattern_code , "not");
        String pin_saved =  Paper.book().read(Constants.pin_code , "not");
        if(pattern_saved.equals("empty") || pin_saved.equals("empty"))
        {
            finish();
        }


          Paper.init(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        registerReceiver(mReceiver  , new IntentFilter("close"));

        SharedPreferences sharedPref = getSharedPreferences(Constants.default_background, Context.MODE_PRIVATE);
        int bg = sharedPref.getInt(Constants.default_background_status, android.R.color.white);
        String type = sharedPref.getString(Constants.default_background_type , "");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.empty_container);

        if(type.equals("app"))
            linearLayout.setBackgroundResource(bg);
        else if(type.equals("gallery")) {
            String real_path = sharedPref.getString("resource", "");
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.parse(real_path));
                Drawable yourDrawable = Drawable.createFromStream(inputStream, real_path);
                linearLayout.setBackground(yourDrawable);

            } catch (FileNotFoundException e) {

            }
        }


    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

           String key =  Paper.book().read("from_status" , "no");


             if(key.equals("no"))
             {
                 Paper.book().write("from_status" , "yes");
                 finish();
             }
             else
             {
                 Paper.book().write("from_status" , "yes");
                 finishAndRemoveTask();

             }


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }
}
