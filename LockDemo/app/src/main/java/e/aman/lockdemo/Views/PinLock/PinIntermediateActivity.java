package e.aman.lockdemo.Views.PinLock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.andrognito.patternlockview.PatternLockView;
import com.goodiebag.pinview.Pinview;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Views.PatternLock.PatternConfirmActivity;
import e.aman.lockdemo.Views.PatternLock.PatternIntermediateActivity;
import io.paperdb.Paper;

public class PinIntermediateActivity extends AppCompatActivity {

    private Button intermediate_pin_button ;
    private Pinview intermediate_pin_view ;
    String final_pin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_intermediate);

        Paper.init(this);


        intermediate_pin_button = findViewById(R.id.intermediate_pin_button);
        intermediate_pin_view = findViewById(R.id.intermediate_pin_view);
        intermediate_pin_view.requestFocus();

        intermediate_pin_view.setPinViewEventListener((pinview, fromUser) -> final_pin = pinview.getValue());

        intermediate_pin_button.setOnClickListener(view ->
                {
                    Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                    Confirm_intent.putExtra("pin_lock_key" , final_pin);
                    startActivity(Confirm_intent);
                    closeKeyBoard();
                    finish();

                }
        );

    }
    private void closeKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
