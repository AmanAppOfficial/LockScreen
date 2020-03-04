package com.example.videoeditordemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;

public class TrimActivity extends AppCompatActivity {

    Uri uri;
    ImageView imageView;
    VideoView videoView;
    TextView textViewLeft , textViewRight;
    RangeSeekBar rangeSeekBar;
    boolean isPlaying = false;

    int duration;
    String filePrefix;
    String command[];
    File dest;
    String original_path;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);

        alert = new AlertDialog.Builder(this);

        imageView = findViewById(R.id.pause_image);
        videoView = findViewById(R.id.videoView);
        textViewLeft = findViewById(R.id.tvvleft);
        textViewRight = findViewById(R.id.tvvright);
        rangeSeekBar = findViewById(R.id.seekbar);

        Intent i = getIntent();
        if(i!=null)
        {
            String img_path = i.getStringExtra("uri");
            uri = Uri.parse(img_path);
            isPlaying = true;
            videoView.setVideoURI(uri);
            videoView.start();
        }

        setListeners();
    }

    private void setListeners()
    {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying)
                {
                    imageView.setImageResource(R.drawable.ic_play);
                    videoView.pause();
                    isPlaying = false;
                }
                else
                {
                    videoView.start();
                    imageView.setImageResource(R.drawable.pause);
                    isPlaying = true;
                }
            }
        });





        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                duration = mp.getDuration()/1000;

                textViewLeft.setText("00.00.00");
                textViewRight.setText(getTime(mp.getDuration()/1000));
                mp.setLooping(true);
                rangeSeekBar.setRangeValues(0,duration);
                rangeSeekBar.setSelectedMaxValue(duration);
                rangeSeekBar.setSelectedMinValue(0);
                rangeSeekBar.setEnabled(true);
                rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                        videoView.seekTo((int)minValue*1000);
                        textViewLeft.setText(getTime((int)bar.getSelectedMinValue()));
                        textViewRight.setText(getTime((int)bar.getSelectedMaxValue()));
                    }
                });

            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(videoView.getCurrentPosition() >= rangeSeekBar.getSelectedMaxValue().intValue() * 1000)
                    videoView.seekTo(rangeSeekBar.getSelectedMinValue().intValue() * 1000);

            }
        },1000);



    }

    private String getTime(int seconds)
    {
        int hr = seconds/3600;
        int rem = seconds % 3600;
        int mn = rem/60;
        int sec = rem%60;
        return  String.format("%02d",hr) + ":" + String.format("%02d",mn) + ":" + String.format("%02d",sec);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.trim)
        {

            LinearLayout linearLayout = new LinearLayout(TrimActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(50 , 0 , 50 , 100);

            final EditText input = new EditText(TrimActivity.this);
            input.setLayoutParams(params);
            input.setGravity(Gravity.TOP | Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            linearLayout.addView(input , params);

            alert.setMessage("Set Video Name");
            alert.setTitle("Change Video Name");
            alert.setView(linearLayout);
            alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
            });

            alert.setPositiveButton("submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filePrefix = input.getText().toString().trim();

                    trimVideo(rangeSeekBar.getSelectedMinValue().intValue() * 1000 ,
                            rangeSeekBar.getSelectedMaxValue().intValue() * 1000,
                            filePrefix);

                    Intent intent = new Intent(TrimActivity.this , ProgressBarActivity.class);
                    intent.putExtra("duration" , duration);
                    intent.putExtra("command" , command);
                    intent.putExtra("destination" , dest.getAbsolutePath());
                    startActivity(intent);
                    finish();

                    dialog.dismiss();



                }
            });

            alert.show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void trimVideo(int startMs, int endMs, String fileName)
    {

//        File folder = new File(Environment.getExternalStorageDirectory() + "/TrimVideos");
//        if(!folder.exists())
//        {
//            folder.mkdir();
//        }

        String root = Environment.getExternalStorageDirectory().toString();

        File folder = new File(root + "/TrimVideos");

        folder.mkdirs();

        filePrefix = fileName;

        String fileExtension = ".mp4";
        dest = new File(folder , filePrefix + fileExtension);
        original_path = getRealPathFromUri(getApplicationContext() , uri);

        Log.e("original path from link" , original_path);
        duration = (endMs - startMs)/1000 ;

        command = new String[]{"-ss", "" + startMs / 1000, "-y", "-i", original_path, "-t", "" + (endMs - startMs) / 1000,"-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", dest.getAbsolutePath()};

    }

    private String getRealPathFromUri(Context context, Uri contentUri)
    {
        Cursor cursor = null;

        try {

            String proj[] = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri , proj , null , null , null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu , menu);
        return true;
    }
}

