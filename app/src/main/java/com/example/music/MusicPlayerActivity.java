package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    RelativeLayout relativeLayout;
    AnimationDrawable animationDrawable;
    TextView titleTv,currentTime,totalTime;
    SeekBar seekBar;
    ImageView pauseplay,nextbtn,preply,musicIcon;
    ArrayList<AudioModel>songsList;
    AudioModel currentsongs;
    MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();
    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        relativeLayout= (RelativeLayout) findViewById(R.id.mainroot);

        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2500);

        animationDrawable.setExitFadeDuration(4500);

        animationDrawable.start();
        titleTv=findViewById(R.id.songstitle);
        currentTime=findViewById(R.id.curranttime);
        totalTime=findViewById(R.id.totaltime);
        seekBar=findViewById(R.id.seekbar);
        pauseplay=findViewById(R.id.pause);
        nextbtn=findViewById(R.id.next);
        preply=findViewById(R.id.previous);
        musicIcon=findViewById(R.id.musiciconbig);
        titleTv.setSelected(true);
        songsList= (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourcesWithMusic();


    }

    void setResourcesWithMusic() {
        currentsongs=songsList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentsongs.getTitle());
        totalTime.setText(convertToMMSS(currentsongs.getDuration()));

        pauseplay.setOnClickListener(view -> pauseplay());
        nextbtn.setOnClickListener(view -> Playnextsong());
        preply.setOnClickListener(view -> Playpresong());

        playMusic();

    }private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentsongs.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void Playnextsong() {
        if (MyMediaPlayer.currentIndex == songsList.size()-1)
            return;
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTime.setText(convertToMMSS(mediaPlayer.getCurrentPosition() +""));
                    if (mediaPlayer.isPlaying()){

                        pauseplay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    }
                    else {
                        pauseplay.setImageResource(R.drawable.ic_baseline_smart_display_24);
                        musicIcon.setRotation(0);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer!=null && b){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void Playpresong() {
        if (MyMediaPlayer.currentIndex== 0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }
    private void pauseplay() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
        musicIcon.setRotation(x++);
    }



    public static String convertToMMSS(String duration){
        Long millis=Long.parseLong(duration);
     return  String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

    }

}