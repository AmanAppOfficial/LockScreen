package e.aman.lockdemo.Views.PinLock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Views.PatternLock.MainActivityPattern;
import e.aman.lockdemo.Views.PatternLock.PatternLockScreenActivity;
import e.aman.lockdemo.Views.PatternLock.ResetPatternActivity;
import io.paperdb.Paper;

public class MainActivityPin extends AppCompatActivity {

    Button redirect_reset_pin_button;
    Switch switch_to_pattern_button , enable_pinlock_button;
    TextView enable_pinlock_text;
    private String save_pin_key = "pin_code";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_activity_main);

        switch_to_pattern_button = (Switch)findViewById(R.id.switch_to_pattern_button);
        enable_pinlock_button = (Switch)findViewById(R.id.enable_pinlock_button);
        redirect_reset_pin_button = (Button)findViewById(R.id.redirect_reset_pin_button);
        enable_pinlock_text = (TextView)findViewById(R.id.enable_pinlock_text);


//        SharedPreferences sharedpreferences_pattern = getSharedPreferences("enable_lock_status", Context.MODE_PRIVATE);
        SharedPreferences sharedpreferences = getSharedPreferences("enable_pinlock_status", Context.MODE_PRIVATE);
        String status_enable_pinlock =  sharedpreferences.getString("pinlock_status_is", "not defined");

        if(!status_enable_pinlock.isEmpty() || !status_enable_pinlock.equals("null") || !status_enable_pinlock.equals(null) || !status_enable_pinlock.equals("not defined"))
        {
            if(status_enable_pinlock.equals("enable"))
            {
                enable_pinlock_text.setText("Enable Lock");
                enable_pinlock_button.setChecked(true);
            }
            else
            {
                enable_pinlock_text.setText("Enable Lock");
                enable_pinlock_button.setChecked(false);
            }
        }


        enable_pinlock_button.setOnClickListener(view->
        {
            if(status_enable_pinlock.equals("not defined"))
            {
                enable_pinlock_button.setChecked(false);
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString("pinlock_status_is", "disable");
                editor1.commit();
                Paper.book().write(save_pin_key , "empty");
                Toast.makeText(getApplicationContext() , "First Register Pin" , Toast.LENGTH_SHORT).show();
                Intent Register_Pattern_Intent = new Intent(MainActivityPin.this , ResetPinActivity.class);
                startActivity(Register_Pattern_Intent);
                finish();

            }
            else
            {
                if(status_enable_pinlock.equals("disable"))
                {

                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.putString("pinlock_status_is", "enable");
                    editor1.commit();


//                    SharedPreferences.Editor editor_pattern = sharedpreferences_pattern.edit();
//                    editor_pattern.putString("lock_status_is", "disable");
//                    editor_pattern.commit();

                    enable_pinlock_text.setText("Enable lock");

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("status", "lock_pin");
                    editor.commit();

                    Intent Start_lock_Intent = new Intent(MainActivityPin.this , PinLockScreenActivity.class);
                    startActivity(Start_lock_Intent);
                    finish();
                }
                else
                {

                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.putString("pinlock_status_is", "disable");
                    editor1.commit();
                    enable_pinlock_text.setText("Enable lock");

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("status", "unlock_pin");
                    editor.commit();
                    Intent Start_lock_Intent = new Intent(MainActivityPin.this , PinLockScreenActivity.class);
                    startActivity(Start_lock_Intent);
                    finish();

                }
            }

        });

        switch_to_pattern_button.setOnClickListener(view ->
        {

            Intent switch_intent =  new Intent(MainActivityPin.this , MainActivityPattern.class);
            startActivity(switch_intent);
            finish();
        });

        redirect_reset_pin_button.setOnClickListener(v -> {
            Intent Change_Pattern_Intent = new Intent(MainActivityPin.this, ResetPinActivity.class);
            startActivity(Change_Pattern_Intent);
        });



    }
}
