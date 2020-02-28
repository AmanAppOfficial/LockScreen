package e.aman.lockdemo.Views.PinLock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.goodiebag.pinview.Pinview;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Views.PatternLock.MainActivityPattern;
import io.paperdb.Paper;

public class PinConfirmActivity extends AppCompatActivity {

    private Pinview confirm_pin_view ;
    private Button confirm_pin_button ;
    String final_pin = "";
    private String save_pin_key = "pin_code";
    String previous_pin ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_confirm);

        Paper.init(this);
        previous_pin = getIntent().getExtras().getString("pin_lock_key");

        confirm_pin_button = findViewById(R.id.confirm_pin_button);
        confirm_pin_view = findViewById(R.id.confirm_pin_view);
        confirm_pin_view.requestFocus();

        confirm_pin_view.setPinViewEventListener((pinview, fromUser) ->
        {
            final_pin = pinview.getValue();
            Paper.book().write(save_pin_key , final_pin);
        });

        confirm_pin_button.setOnClickListener(view ->
        {
            if(final_pin.equals(previous_pin))
            {
                Paper.book().write(save_pin_key,final_pin);
                Toast.makeText(getApplicationContext() , "Pin changed Successfully" , Toast.LENGTH_SHORT).show();
                Intent Pin_MainActivity_Intent = new Intent(this , MainActivityPin.class);
                startActivity(Pin_MainActivity_Intent);
                closeKeyBoard();
                finish();

            }
            else
            {
                Toast.makeText(getApplicationContext() , "Pin different" , Toast.LENGTH_SHORT).show();
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
