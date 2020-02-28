package e.aman.lockdemo.Views.PinLock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.goodiebag.pinview.Pinview;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Views.PatternLock.PatternConfirmActivity;
import e.aman.lockdemo.Views.PatternLock.PatternIntermediateActivity;
import e.aman.lockdemo.Views.PatternLock.ResetPatternActivity;
import io.paperdb.Paper;

public class ResetPinActivity extends AppCompatActivity {

    private Pinview register_pinview;
    private Button reset_pin_button;
    String final_pin = "";
    private String save_pin_key = "pin_code";
    private TextView register_pin_text ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pin);

        register_pinview = (Pinview)findViewById(R.id.reset_pin_view);
        register_pin_text = findViewById(R.id.register_pin_text);
        register_pinview.requestFocus();
        reset_pin_button = (Button)findViewById(R.id.reset_pin_button);
        Paper.init(this);
        String old_pin = Paper.book().read(save_pin_key);

        if(old_pin.equals("empty"))
        {
            reset_pin_button.setText("Set new pin");
            register_pin_text.setText("Set New Pin");

        }
        else
        {
            reset_pin_button.setText("Input Old Pin");
            register_pin_text.setText("Set Old Pin");
        }

        register_pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser)
            {
                final_pin = pinview.getValue();
            }
        });

        reset_pin_button.setOnClickListener(view ->
        {

            if(old_pin.equals("empty"))
            {

                Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this , PinConfirmActivity.class);
                Confirm_Pin_Intent.putExtra("pin_lock_key" , final_pin);
                startActivity(Confirm_Pin_Intent);
                closeKeyBoard();
                finish();
            }
            else
            {
                if(old_pin.equals(final_pin))
                {
                    Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this , PinIntermediateActivity.class);
                    startActivity(Intermediate_Pin_Intent);
                    closeKeyBoard();
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext() , "Old password not matched" , Toast.LENGTH_SHORT).show();
                }
            }
        });
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
