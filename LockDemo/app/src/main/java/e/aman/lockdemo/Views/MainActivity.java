package e.aman.lockdemo.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import e.aman.lockdemo.R;

public class MainActivity extends AppCompatActivity {

    Button redirect_reset_pattern_button , start_lock_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redirect_reset_pattern_button = (Button)findViewById(R.id.redirect_reset_pattern_button);
        start_lock_button = (Button)findViewById(R.id.start_lock_button);

        redirect_reset_pattern_button.setOnClickListener(v -> {
            Intent Change_Pattern_Intent = new Intent(MainActivity.this, ResetPatternActivity.class);
            startActivity(Change_Pattern_Intent);
        });

        start_lock_button.setOnClickListener(view ->
        {
            Intent Start_lock_Intent = new Intent(MainActivity.this , PatternLockScreenActivity.class);
            startActivity(Start_lock_Intent);
        });
    }
}
