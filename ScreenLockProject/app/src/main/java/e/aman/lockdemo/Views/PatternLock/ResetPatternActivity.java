package e.aman.lockdemo.Views.PatternLock;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Utils.Constants;
import io.paperdb.Paper;

public class ResetPatternActivity extends AppCompatActivity {

    String final_pattern = "";
    private String save_pattern_key = Constants.pattern_code;
    private PatternLockView register_patternview;
    private TextView register_pattern_text ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pattern);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }



        Paper.init(this);
               String old_pattern = Paper.book().read(save_pattern_key);


            register_patternview= findViewById(R.id.reset_pattern_view);
            register_pattern_text = findViewById(R.id.register_pattern_text);

            if(old_pattern.equals("empty"))
            {

                register_pattern_text.setText("Enter New Pattern");

            }
            else
            {

                register_pattern_text.setText("Enter Old Pattern");
            }

        SharedPreferences sharedPreferences_time = getSharedPreferences(Constants.default_is_first_time_pattern , Context.MODE_PRIVATE);
        String is_first_time = sharedPreferences_time.getString(Constants.default_is_first_time_pattern_status , "yes");

        if(is_first_time.equals("yes"))
        {
            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.putString(Constants.enable_pattern_lock_status, Constants.default_lock_status_not_defined);
            editor1.commit();
        }


        SharedPreferences sharedPref_gallery = getSharedPreferences("my_background", Context.MODE_PRIVATE);
        int bg = sharedPref_gallery.getInt("background_resource", android.R.color.white);
        String type = sharedPref_gallery.getString("type" , "");
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.reset_pattern_container);

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




        register_patternview.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern)
                {




                    final_pattern = PatternLockUtils.patternToString(register_patternview , pattern);

                        if(old_pattern.equals("empty"))
                        {

                            SharedPreferences sharedPreferences_time = getSharedPreferences(Constants.default_is_first_time_pattern , Context.MODE_PRIVATE);
                            String is_first_time = sharedPreferences_time.getString(Constants.default_is_first_time_pattern_status , "yes");

                            if(is_first_time.equals("yes"))
                            {
                                SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.putString(Constants.enable_pattern_lock_status, "disable");
                                editor1.commit();
                            }

                            Intent Confirm_Pattern_Intent = new Intent(ResetPatternActivity.this , PatternConfirmActivity.class);
                            Confirm_Pattern_Intent.putExtra("lock_key" , final_pattern);
                            startActivity(Confirm_Pattern_Intent);
                            finish();
                        }
                        else
                        {
                            if(old_pattern.equals(final_pattern))
                            {

                                SharedPreferences sharedPreferences_time = getSharedPreferences(Constants.default_is_first_time_pattern , Context.MODE_PRIVATE);
                                String is_first_time = sharedPreferences_time.getString(Constants.default_is_first_time_pattern_status , "yes");

                                if(is_first_time.equals("yes"))
                                {
                                    SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                    editor1.putString(Constants.enable_pattern_lock_status, "disable");
                                    editor1.commit();
                                }

                                Intent Intermediate_Pattern_Intent = new Intent(ResetPatternActivity.this , PatternIntermediateActivity.class);
                                startActivity(Intermediate_Pattern_Intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext() , "Old PATTERN didn't match" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                @Override
                public void onCleared() {

                }
            });


        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(final_pattern.isEmpty())
        {
            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pattern_lock, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.putString(Constants.enable_pattern_lock_status, "not defined");
            editor1.commit();
            Paper.book().write(save_pattern_key , "empty");
            Paper.book().write(Constants.from_pattern_reset_button , "yes");

            SharedPreferences sf = getApplication().getSharedPreferences(Constants.default_is_first_time_pattern , Context.MODE_PRIVATE);
            SharedPreferences.Editor ss = sf.edit();
            ss.putString(Constants.default_is_first_time_pattern_status,"not_defined");
            ss.commit();
        }
    }
}
