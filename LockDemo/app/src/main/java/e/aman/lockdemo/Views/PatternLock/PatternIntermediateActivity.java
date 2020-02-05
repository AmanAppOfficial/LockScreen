package e.aman.lockdemo.Views.PatternLock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import e.aman.lockdemo.R;
import io.paperdb.Paper;


public class PatternIntermediateActivity extends AppCompatActivity {

    private Button intermediate_pattern_button ;
    private PatternLockView intermediate_pattern_view ;
    String final_pattern = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_intermediate);

        Paper.init(this);


        intermediate_pattern_button = findViewById(R.id.intermediate_pattern_button);
        intermediate_pattern_view = findViewById(R.id.intermediate_pattern_view);

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
            }

            @Override
            public void onCleared() {

            }
        });

        intermediate_pattern_button.setOnClickListener(view ->
        {
            Intent Confirm_intent = new Intent(PatternIntermediateActivity.this , PatternConfirmActivity.class);
            Confirm_intent.putExtra("lock_key" , final_pattern);
            startActivity(Confirm_intent);
            finish();

        }
        );



    }
}
