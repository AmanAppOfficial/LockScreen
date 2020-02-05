package e.aman.lockdemo.Views.Wallpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;

import e.aman.lockdemo.R;

public class ChangeBackgroundActivity extends AppCompatActivity {

    ImageView sample_0 , sample_1 , sample_2 , sample_3 , sample_4 , sample_5;
    FloatingActionButton floatingActionButton;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    public boolean set_external_storage_granted = false ;



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
        floatingActionButton = findViewById(R.id.floating_action_button);
        addExternalStoragePermission();

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

         floatingActionButton.setOnClickListener(view -> setImageFromGallery());





    }
    public void setImageFromGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageUri = data.getData();
            File file = new File(getRealPathFromURI(imageUri));
            editor.putString("resource", file.toString());
            editor.putString("type" ,"gallery");
            editor.apply();

            Toast.makeText(getApplicationContext() , "Background changed Successfully" , Toast.LENGTH_SHORT).show();
            finish();

        }

    }
    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public void addExternalStoragePermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        set_external_storage_granted = true ;
                    }

                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {
                        Log.e("permission" , "denied");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();
    }


}
