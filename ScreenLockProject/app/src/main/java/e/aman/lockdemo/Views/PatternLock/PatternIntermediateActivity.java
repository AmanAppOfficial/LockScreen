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

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import e.aman.lockdemo.R;
import io.paperdb.Paper;


public class PatternIntermediateActivity extends AppCompatActivity {

    private PatternLockView intermediate_pattern_view ;
    String final_pattern = "";
    TextView status_view ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_intermediate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }



        Paper.init(this);

        status_view = findViewById(R.id.pattern_intermediate_text);
        intermediate_pattern_view = findViewById(R.id.intermediate_pattern_view);

        SharedPreferences sharedPref_gallery = getSharedPreferences("my_background", Context.MODE_PRIVATE);
        int bg = sharedPref_gallery.getInt("background_resource", android.R.color.white);
        String type = sharedPref_gallery.getString("type" , "");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.intermediate_pattern_container);

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



        intermediate_pattern_view.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern)
            {
                final_pattern = PatternLockUtils.patternToString(intermediate_pattern_view , pattern);
                Intent Confirm_intent = new Intent(PatternIntermediateActivity.this , PatternConfirmActivity.class);
                Confirm_intent.putExtra("lock_key" , final_pattern);
                startActivity(Confirm_intent);
                finish();

            }

            @Override
            public void onCleared() {

            }
        });




    }
}
