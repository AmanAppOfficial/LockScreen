package e.aman.lockdemo.Views.PatternLock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Views.PinLock.MainActivityPin;
import io.paperdb.Paper;

public class PatternConfirmActivity extends AppCompatActivity {

    private PatternLockView confirm_pattern_view ;
    private Button confirm_pattern_button ;
    String final_pattern = "";
    private String save_pattern_key = "pattern_code";
    String previous_pattern ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_confirm);

        Paper.init(this);
        previous_pattern = getIntent().getExtras().getString("lock_key");

        confirm_pattern_button = findViewById(R.id.confirm_pattern_button);
        confirm_pattern_view = findViewById(R.id.confirm_pattern_view);


        confirm_pattern_view.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern)
            {
                final_pattern = PatternLockUtils.patternToString(confirm_pattern_view , pattern);
                Paper.book().write(save_pattern_key , final_pattern);
            }

            @Override
            public void onCleared() {

            }
        });


        confirm_pattern_button.setOnClickListener(view ->
        {
            if(final_pattern.equals(previous_pattern))
            {
                Paper.book().write(save_pattern_key,final_pattern);
                Toast.makeText(getApplicationContext() , "Pattern changed Successfully" , Toast.LENGTH_SHORT).show();
                Intent Pattern_MainActivity_Intent = new Intent(this , MainActivityPattern.class);
                startActivity(Pattern_MainActivity_Intent);
                finish();

            }
            else
            {
               Toast.makeText(getApplicationContext() , "Password different" , Toast.LENGTH_SHORT).show();
            }
        });


    }
}
