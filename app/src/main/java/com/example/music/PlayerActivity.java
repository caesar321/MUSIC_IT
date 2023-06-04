package com.example.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    private Button btnplay,btnnext,btnprev,btnff,btnrewind;
    private TextView txtsname,txtstart,txtstop;
    private ImageView imageView;
    SeekBar seekBar;
    String sname;
    public  static  final String EXTRA_NAME= "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File>mysongs;
    Thread updateseekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
       getSupportActionBar().setTitle("Now playing");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnnext= findViewById(R.id.btnnext);
        btnff=findViewById(R.id.btnfastfor);
        btnplay=findViewById(R.id.playbtn);
        btnprev=findViewById(R.id.btnprev);
        btnrewind=findViewById(R.id.btnrewind);
        txtsname= findViewById(R.id.txtsn);
        txtstart= findViewById(R.id.txtstart);
        txtstop=findViewById(R.id.txtstop);
        imageView=findViewById(R.id.imgview);
        seekBar=findViewById(R.id.seekbar);
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        mysongs=(ArrayList) bundle.getParcelableArrayList("songs");
        String songName= intent.getStringExtra("songname");
        position= bundle.getInt("pos",0);
        txtsname.setSelected(true);
      Uri uri=Uri.parse(mysongs.get(position).toString());
      sname= mysongs.get(position).getName();
      txtsname.setText(sname);

      mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
      mediaPlayer.start();
      updateseekbar= new Thread()
        {
            @Override
            public void run() {
                int totalDuration= mediaPlayer.getDuration();
                int currentposition=0;
                while (currentposition<totalDuration)
                {
                    try {
                        sleep(500);
                        currentposition= mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                    }
                    catch (InterruptedException|IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
      seekBar.setMax(mediaPlayer.getDuration());
      updateseekbar.start();
     // seekBar.setProgressDrawable().setColorFilter(getResources().getColor(R.color.black_shade_2), PorterDuff.Mode.MULTIPLY);
      seekBar.getThumb().setColorFilter(getResources().getColor(R.color.black_shade_2),PorterDuff.Mode.SRC_IN);

      seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {

          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
          }
      });
String endTime= createTime(mediaPlayer.getDuration());
txtstop.setText(endTime);

final Handler handler= new Handler();
final int delay =1000;

handler.postDelayed(new Runnable() {
    @Override
    public void run() {
        String currentTime= createTime(mediaPlayer.getCurrentPosition());
        txtstart.setText(currentTime);
        handler.postDelayed(this,delay);
    }
},delay);



      btnplay.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(mediaPlayer.isPlaying())
              {
                  btnplay.setBackgroundResource(R.drawable.ic_play);
                  mediaPlayer.pause();
              }
              else{
                  btnplay.setBackgroundResource(R.drawable.ic_pause);
                  mediaPlayer.start();
              }
          }
      });

      //next listener
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });
      btnnext.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              mediaPlayer.stop();
              mediaPlayer.release();
              position=((position+1)%mysongs.size());
              Uri u= Uri.parse(mysongs.get(position).toString());
              mediaPlayer= MediaPlayer.create(getApplicationContext(),u);
              sname= mysongs.get(position).getName();
              txtsname.setText(sname);
              mediaPlayer.start();
              btnplay.setBackgroundResource(R.drawable.ic_pause);
              startAnimation(imageView);
          }
      });
      btnprev.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              mediaPlayer.stop();
              mediaPlayer.release();
              position= ((position-1)<0)?(mysongs.size()-1):(position-1);
              Uri u= Uri.parse(mysongs.get(position).toString());
              mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
              sname= mysongs.get(position).getName();
              txtsname.setText(sname);
              mediaPlayer.start();
              btnplay.setBackgroundResource(R.drawable.ic_pause);
              startAnimation(imageView);
          }
      });

      btnff.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(mediaPlayer.isPlaying())
              {
                  mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+1000);
              }
          }
      });
      btnrewind.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(mediaPlayer.isPlaying())
              {
                  mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-1000);
              }
          }
      });
    }
    public void startAnimation(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
                animator.setDuration(1000);
        AnimatorSet animatorSet= new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public  String createTime(int duration)
    {
        String time= "";
        int min = duration/1000/60;
        int sec= duration/1000%60;

                time+= min+":";
        if(sec<10)
        {
            time+="0;";
        }
        time+=sec;
        return time;
    }
}