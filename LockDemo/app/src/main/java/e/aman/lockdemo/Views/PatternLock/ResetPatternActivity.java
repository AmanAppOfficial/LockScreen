package e.aman.lockdemo.Views.PatternLock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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
    private String save_pattern_key = "pattern_code";
    private PatternLockView register_patternview;
    private TextView register_pattern_text ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pattern);

        Paper.init(this);


        String old_pattern = Paper.book().read(save_pattern_key);

        reset_pattern_button = findViewById(R.id.reset_pattern_button);
        register_patternview= findViewById(R.id.reset_pattern_view);
        register_pattern_text = findViewById(R.id.register_pattern_text);

        if(old_pattern.equals("empty"))
        {
            reset_pattern_button.setText("Set new pattern");
            register_pattern_text.setText("Set New Pattern");

        }
        else
        {
            reset_pattern_button.setText("Input Old Pattern");
            register_pattern_text.setText("Set Old Pattern");
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
            }

            @Override
            public void onCleared() {

            }
        });


        reset_pattern_button.setOnClickListener(view ->
        {

            if(old_pattern.equals("empty"))
            {

                Intent Confirm_Pattern_Intent = new Intent(ResetPatternActivity.this , PatternConfirmActivity.class);
                Confirm_Pattern_Intent.putExtra("lock_key" , final_pattern);
                startActivity(Confirm_Pattern_Intent);
                finish();
            }
            else
            {
                if(old_pattern.equals(final_pattern))
                {
                    Intent Intermediate_Pattern_Intent = new Intent(ResetPatternActivity.this , PatternIntermediateActivity.class);
                    startActivity(Intermediate_Pattern_Intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext() , "Old password not matched" , Toast.LENGTH_SHORT).show();
                }
            }


        }
        );
    }
}
