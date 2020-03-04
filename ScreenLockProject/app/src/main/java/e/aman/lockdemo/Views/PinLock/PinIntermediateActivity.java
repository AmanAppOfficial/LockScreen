package e.aman.lockdemo.Views.PinLock;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import e.aman.lockdemo.R;
import io.paperdb.Paper;

public class PinIntermediateActivity extends AppCompatActivity {

     String final_pin = "";
    Button button_1 ,button_2 ,button_3 ,
            button_4 ,button_5 ,button_6 ,button_7 ,button_8 ,button_9 ,button_0;
    ImageView button_back;
    TextView editText , time ;
    int count=0 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_intermediate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        Paper.init(this);


        button_0 = (Button) findViewById(R.id.button0);
        button_1 = (Button) findViewById(R.id.button1);
        button_2 = (Button) findViewById(R.id.button2);
        button_3 = (Button) findViewById(R.id.button3);
        button_4 = (Button) findViewById(R.id.button4);
        button_5 = (Button) findViewById(R.id.button5);
        button_6 = (Button) findViewById(R.id.button6);
        button_7 = (Button) findViewById(R.id.button7);
        button_8 = (Button) findViewById(R.id.button8);
        button_9 = (Button) findViewById(R.id.button9);
        button_back = (ImageView) findViewById(R.id.button_back);
        editText = findViewById(R.id.editText);
        time = findViewById(R.id.time_text);

        TextView status_view = findViewById(R.id.statusviewKeyboard);
        status_view.setText("Enter New Pin");



        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        time.setText(currentTime);


        SharedPreferences sharedPref_gallery = getApplicationContext().getSharedPreferences("my_background", Context.MODE_PRIVATE);
        String type = sharedPref_gallery.getString("type" , "");
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.pin_lock_layout);
        if(type.equals("app"))
        {
            int bg = sharedPref_gallery.getInt("background_resource", android.R.color.white);
            relativeLayout.setBackgroundResource(bg);
        }
        else if(type.equals("gallery"))
        {
            String real_path = sharedPref_gallery.getString("resource","");
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.parse(real_path));
                Drawable yourDrawable = Drawable.createFromStream(inputStream, real_path);
                relativeLayout.setBackground(yourDrawable);

            } catch (FileNotFoundException e) {

            }
        }



        button_0.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "0";
               editText.setText(final_pin);
               if(count==4)
               {
                   Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                   Confirm_intent.putExtra("pin_lock_key" , final_pin);
                   startActivity(Confirm_intent);
                   finish();

               }
           }
        });

        button_1.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "1";
               editText.setText(final_pin);
               if(count==4)            {
                   Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                   Confirm_intent.putExtra("pin_lock_key" , final_pin);
                   startActivity(Confirm_intent);
                   finish();
               }
           }
        });

        button_2.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "2";
               editText.setText(final_pin);
               if(count==4)            {
                   Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                   Confirm_intent.putExtra("pin_lock_key" , final_pin);
                   startActivity(Confirm_intent);
                   finish();
               }
           }
        });

        button_3.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "3";
               editText.setText(final_pin);
               if(count==4)            {
                   Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                   Confirm_intent.putExtra("pin_lock_key" , final_pin);
                   startActivity(Confirm_intent);
                   finish();
               }
           }
        });

        button_4.setOnClickListener(view ->
        {
            if(count<4)
            {
                count++;
                final_pin = final_pin +  "4";
                editText.setText(final_pin);
                if(count==4)            {
                    Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                    Confirm_intent.putExtra("pin_lock_key" , final_pin);
                    startActivity(Confirm_intent);
                    finish();
                }
            }
        });

        button_5.setOnClickListener(view ->
        {
            if(count<4)
            {
                count++;
                final_pin = final_pin +  "5";
                editText.setText(final_pin);
                if(count==4)            {
                    Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                    Confirm_intent.putExtra("pin_lock_key" , final_pin);
                    startActivity(Confirm_intent);
                    finish();
                }
            }
        });

        button_6.setOnClickListener(view ->
        {
            count++;
            final_pin = final_pin + "6";
            editText.setText(final_pin);
            if(count==4)            {
                Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                Confirm_intent.putExtra("pin_lock_key" , final_pin);
                startActivity(Confirm_intent);
                finish();
            }
        });

        button_7.setOnClickListener(view ->
        {
          if(count<4)
          {
              count++;
              final_pin = final_pin +  "7";
              editText.setText(final_pin);
              if(count==4)            {
                  Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                  Confirm_intent.putExtra("pin_lock_key" , final_pin);
                  startActivity(Confirm_intent);
                  finish();
              }
          }
        });

        button_8.setOnClickListener(view ->
        {
           if(count<4)
           {
               count++;
               final_pin = final_pin +  "8";
               editText.setText(final_pin);
               if(count==4)            {
                   Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                   Confirm_intent.putExtra("pin_lock_key" , final_pin);
                   startActivity(Confirm_intent);
                   finish();
               }
           }
        });

        button_9.setOnClickListener(view ->
        {
            if(count<4)
            {
                count++;
                final_pin = final_pin + "9";
                editText.setText(final_pin);
                if(count==4)            {
                    Intent Confirm_intent = new Intent(PinIntermediateActivity.this , PinConfirmActivity.class);
                    Confirm_intent.putExtra("pin_lock_key" , final_pin);
                    startActivity(Confirm_intent);
                    finish();
                }
            }
        });


        button_back.setOnClickListener(view ->
        {
            if(count>=1)
            {
                count-- ;
                editText.setText(final_pin.substring(0 , count));
                final_pin = final_pin.substring(0 , count);

            }

        });

    }

}
