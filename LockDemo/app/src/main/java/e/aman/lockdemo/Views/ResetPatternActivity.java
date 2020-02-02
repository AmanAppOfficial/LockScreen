package e.aman.lockdemo.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import e.aman.lockdemo.R;
import io.paperdb.Paper;

public class ResetPatternActivity extends AppCompatActivity {

    Button reset_pattern_button;
    String final_pattern = "";
    private PatternLockView register_patternview;
    private String save_pattern_key = "pattern_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pattern);

        Paper.init(this);

        reset_pattern_button = findViewById(R.id.reset_pattern_button);
        register_patternview= findViewById(R.id.reset_pattern_view);

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
            }

            @Override
            public void onCleared() {

            }
        });


        reset_pattern_button.setOnClickListener(view ->
        {
            Paper.book().write(save_pattern_key,final_pattern);

        }
        );}
}
