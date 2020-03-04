package e.aman.lockdemo.Views.Wallpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;


import e.aman.lockdemo.R;


public class ChangeBackgroundActivity extends AppCompatActivity {

    ImageView sample_0 , sample_1 , sample_2 , sample_3 , sample_4 , sample_5;

    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor ;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_background);

        sample_0 = findViewById(R.id.image_sample_0);
        sample_1 = findViewById(R.id.image_sample_1);
        sample_2 = findViewById(R.id.image_sample_2);
        sample_3 = findViewById(R.id.image_sample_3);
        sample_4 = findViewById(R.id.image_sample_4);
        sample_5 = findViewById(R.id.image_sample_5);


        sharedPref = getApplication().getSharedPreferences("my_background", Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        sample_0.setOnClickListener(view ->
        {
            editor.putInt("background_resource", R.drawable.sample_0);
            editor.putString("type" ,"app");
            editor.apply();
            Toast.makeText(getApplicationContext() , "Background changed Successfully" , Toast.LENGTH_SHORT).show();
            finish();
        });
        sample_1.setOnClickListener(view ->
        {
            editor.putInt("background_resource", R.drawable.sample_1);
            editor.putString("type" ,"app");
            editor.apply();
            Toast.makeText(getApplicationContext() , "Background changed Successfully" , Toast.LENGTH_SHORT).show();
            finish();
        });
        sample_2.setOnClickListener(view ->
        {
            editor.putInt("background_resource", R.drawable.sample_2);
            editor.putString("type" ,"app");
            editor.apply();
            Toast.makeText(getApplicationContext() , "Background changed Successfully" , Toast.LENGTH_SHORT).show();
            finish();
        });
        sample_3.setOnClickListener(view ->
        {
            editor.putInt("background_resource", R.drawable.sample_3);
            editor.putString("type" ,"app");
            editor.apply();
            Toast.makeText(getApplicationContext() , "Background changed Successfully" , Toast.LENGTH_SHORT).show();
            finish();
        });
        sample_4.setOnClickListener(view ->
        {
            editor.putInt("background_resource", R.drawable.sample_4);
            editor.putString("type" ,"app");
            editor.apply();
            Toast.makeText(getApplicationContext() , "Background changed Successfully" , Toast.LENGTH_SHORT).show();
            finish();
        });
        sample_5.setOnClickListener(view ->
        {
            editor.putInt("background_resource", R.drawable.sample_5);
            editor.putString("type" ,"app");
            editor.apply();
            Toast.makeText(getApplicationContext() , "Background changed Successfully" , Toast.LENGTH_SHORT).show();
            finish();
        });



    }



}
