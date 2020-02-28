package e.aman.lockdemo.Views.PinLock;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import e.aman.lockdemo.R;
import e.aman.lockdemo.Utils.Constants;
import io.paperdb.Paper;

public class ResetPinActivity extends AppCompatActivity {

    private String save_pin_key = Constants.pin_code;
    Button button_1 ,button_2 ,button_3 ,
            button_4 ,button_5 ,button_6 ,button_7 ,button_8 ,button_9 ,button_0;
    ImageView button_back;
    TextView editText , time , status_view;

    int count =0 ;
    String final_pin = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Paper.init(this);
        String old_pin = Paper.book().read(save_pin_key);

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


        status_view = findViewById(R.id.statusviewKeyboard);


        if (old_pin.equals("empty")) {
            status_view.setText("Enter New Pin");

        } else {
            status_view.setText("Enter Old Pin");
        }

        SharedPreferences sharedPreferences_time = getSharedPreferences(Constants.default_is_first_time_pin , Context.MODE_PRIVATE);
        String is_first_time = sharedPreferences_time.getString(Constants.default_is_first_time_pin_status , "yes");


        if(is_first_time.equals("yes"))
        {
            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.putString(Constants.enable_pin_lock_status, Constants.default_lock_status_not_defined);
            editor1.commit();


        }

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
                    if (old_pin.equals("empty")) {

                        if(is_first_time.equals("yes"))
                        {
                            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedpreferences.edit();
                            editor1.putString(Constants.enable_pin_lock_status, "disable");
                            editor1.commit();

                        }

                        Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                        Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                        startActivity(Confirm_Pin_Intent);
                        finish();
                    } else
                        {
                            if (old_pin.equals(final_pin)) {

                                if(is_first_time.equals("yes"))
                                {
                                    SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                    editor1.putString(Constants.enable_pin_lock_status, "disable");
                                    editor1.commit();

                                }
                            Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                            startActivity(Intermediate_Pin_Intent);
                            finish();
                            }
                            else {
                                count=0;
                                editText.setText("");
                                final_pin="";
                            Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                            }
                    }
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
                   if (old_pin.equals("empty")) {
                       if(is_first_time.equals("yes"))
                       {
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "disable");
                           editor1.commit();

                       }
                       Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                       Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                       startActivity(Confirm_Pin_Intent);
                       finish();
                   } else {
                       if (old_pin.equals(final_pin)) {
                           if(is_first_time.equals("yes"))
                           {
                               SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pin_lock_status, "disable");
                               editor1.commit();

                           }
                           Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                           startActivity(Intermediate_Pin_Intent);
                           finish();
                       } else {
                           count=0;
                           editText.setText("");
                           final_pin="";
                           Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                       }
                   }
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
                   if (old_pin.equals("empty")) {
                       if(is_first_time.equals("yes"))
                       {
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "disable");
                           editor1.commit();

                       }

                       Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                       Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                       startActivity(Confirm_Pin_Intent);
                       finish();
                   } else {
                       if (old_pin.equals(final_pin)) {

                           if(is_first_time.equals("yes"))
                           {
                               SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pin_lock_status, "disable");
                               editor1.commit();

                           }

                           Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                           startActivity(Intermediate_Pin_Intent);
                           finish();
                       } else {
                           count=0;
                           editText.setText("");
                           final_pin="";
                           Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                       }
                   }            }
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
                 if (old_pin.equals("empty")) {
                     if(is_first_time.equals("yes"))
                     {
                         SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                         SharedPreferences.Editor editor1 = sharedpreferences.edit();
                         editor1.putString(Constants.enable_pin_lock_status, "disable");
                         editor1.commit();

                     }

                     Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                     Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                     startActivity(Confirm_Pin_Intent);
                     finish();
                 } else {
                     if (old_pin.equals(final_pin)) {
                         if(is_first_time.equals("yes"))
                         {
                             SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                             SharedPreferences.Editor editor1 = sharedpreferences.edit();
                             editor1.putString(Constants.enable_pin_lock_status, "disable");
                             editor1.commit();

                         }

                         Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                         startActivity(Intermediate_Pin_Intent);
                         finish();
                     } else {
                         count=0;
                         editText.setText("");
                         final_pin="";
                         Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                     }
                 }            }
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
                   if (old_pin.equals("empty")) {
                       if(is_first_time.equals("yes"))
                       {
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "disable");
                           editor1.commit();

                       }

                       Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                       Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                       startActivity(Confirm_Pin_Intent);
                       finish();
                   } else {
                       if (old_pin.equals(final_pin)) {
                           if(is_first_time.equals("yes"))
                           {
                               SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pin_lock_status, "disable");
                               editor1.commit();

                           }

                           Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                           startActivity(Intermediate_Pin_Intent);
                           finish();
                       } else {
                           count=0;
                           editText.setText("");
                           final_pin="";
                           Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                       }
                   }            }
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
                    if (old_pin.equals("empty")) {
                        if(is_first_time.equals("yes"))
                        {
                            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedpreferences.edit();
                            editor1.putString(Constants.enable_pin_lock_status, "disable");
                            editor1.commit();

                        }

                        Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                        Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                        startActivity(Confirm_Pin_Intent);
                        finish();
                    } else {
                        if (old_pin.equals(final_pin)) {
                            if(is_first_time.equals("yes"))
                            {
                                SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.putString(Constants.enable_pin_lock_status, "disable");
                                editor1.commit();

                            }

                            Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                            startActivity(Intermediate_Pin_Intent);
                            finish();
                        } else {
                            count=0;
                            editText.setText("");
                            final_pin="";
                            Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                        }
                    }            }
            }
        });

        button_6.setOnClickListener(view ->
        {
            if(count<4)
            {
                count++;
                final_pin = final_pin + "6";
                editText.setText(final_pin);
                if(count==4)            {
                    if (old_pin.equals("empty")) {
                        if(is_first_time.equals("yes"))
                        {
                            SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedpreferences.edit();
                            editor1.putString(Constants.enable_pin_lock_status, "disable");
                            editor1.commit();

                        }

                        Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                        Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                        startActivity(Confirm_Pin_Intent);
                        finish();
                    } else {
                        if (old_pin.equals(final_pin)) {
                            if(is_first_time.equals("yes"))
                            {
                                SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                                editor1.putString(Constants.enable_pin_lock_status, "disable");
                                editor1.commit();

                            }

                            Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                            startActivity(Intermediate_Pin_Intent);
                            finish();
                        } else {
                            count=0;
                            editText.setText("");
                            final_pin="";
                            Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                        }
                    }            }
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
                   if (old_pin.equals("empty")) {
                       if(is_first_time.equals("yes"))
                       {
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "disable");
                           editor1.commit();

                       }

                       Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                       Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                       startActivity(Confirm_Pin_Intent);
                       finish();
                   } else {
                       if (old_pin.equals(final_pin)) {
                           if(is_first_time.equals("yes"))
                           {
                               SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pin_lock_status, "disable");
                               editor1.commit();

                           }

                           Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                           startActivity(Intermediate_Pin_Intent);
                           finish();
                       } else {
                           count=0;
                           editText.setText("");
                           final_pin="";
                           Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                       }
                   }            }
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
                   if (old_pin.equals("empty")) {
                       if(is_first_time.equals("yes"))
                       {
                           SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor1 = sharedpreferences.edit();
                           editor1.putString(Constants.enable_pin_lock_status, "disable");
                           editor1.commit();

                       }

                       Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                       Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                       startActivity(Confirm_Pin_Intent);
                       finish();
                   } else {
                       if (old_pin.equals(final_pin)) {
                           if(is_first_time.equals("yes"))
                           {
                               SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor1 = sharedpreferences.edit();
                               editor1.putString(Constants.enable_pin_lock_status, "disable");
                               editor1.commit();

                           }

                           Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                           startActivity(Intermediate_Pin_Intent);
                           finish();
                       } else {
                           count=0;
                           editText.setText("");
                           final_pin="";
                           Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                       }
                   }            }
           }
        });

        button_9.setOnClickListener(view ->
        {
          if(count<4)
          {
              count++;
              final_pin = final_pin + "9";
              editText.setText(final_pin);
              if(count==4)
              {
                  if (old_pin.equals("empty"))
                  {
                      if(is_first_time.equals("yes"))
                      {
                          SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor1 = sharedpreferences.edit();
                          editor1.putString(Constants.enable_pin_lock_status, "disable");
                          editor1.commit();

                      }

                      Intent Confirm_Pin_Intent = new Intent(ResetPinActivity.this, PinConfirmActivity.class);
                      Confirm_Pin_Intent.putExtra("pin_lock_key", final_pin);
                      startActivity(Confirm_Pin_Intent);
                      finish();
                  } else
                      {
                      if (old_pin.equals(final_pin))
                      {
                          if(is_first_time.equals("yes"))
                          {
                              SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                              SharedPreferences.Editor editor1 = sharedpreferences.edit();
                              editor1.putString(Constants.enable_pin_lock_status, "disable");
                              editor1.commit();

                          }

                          Intent Intermediate_Pin_Intent = new Intent(ResetPinActivity.this, PinIntermediateActivity.class);
                          startActivity(Intermediate_Pin_Intent);
                          finish();
                      } else
                          {
                              count=0;
                              editText.setText("");
                              final_pin="";
                          Toast.makeText(getApplicationContext(), "Old PIN didn't match", Toast.LENGTH_SHORT).show();
                      }
                  }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        String old_pin = Paper.book().read(save_pin_key);

        if(old_pin.equals("empty"))
        {
            if(final_pin.isEmpty())
            {

                SharedPreferences sharedpreferences = getSharedPreferences(Constants.enable_pin_lock, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString(Constants.enable_pin_lock_status, "not defined");
                editor1.commit();
                Paper.book().write(save_pin_key , "empty");
                Paper.book().write(Constants.from_pin_reset_button , "yes");

                SharedPreferences sf = getApplication().getSharedPreferences(Constants.default_is_first_time_pin , Context.MODE_PRIVATE);
                SharedPreferences.Editor ss = sf.edit();
                ss.putString(Constants.default_is_first_time_pin_status,"not_defined");
                ss.commit();




            }

        }

      }


    @Override
    public void onBackPressed() {
        super.onBackPressed();








    }
}

