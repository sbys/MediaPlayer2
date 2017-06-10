package com.example.a.mediaplayer2.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a.mediaplayer2.Lrc.Lrc;
import com.example.a.mediaplayer2.Lrc.LrcManager;
import com.example.a.mediaplayer2.MyApplications.MyApplication;
import com.example.a.mediaplayer2.R;
import com.example.a.mediaplayer2.SendAndGet.SendAndGet;
import com.example.a.mediaplayer2.View.LrcView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Play extends AppCompatActivity {

    private ImageButton pre;
    private ImageButton play;
    private ImageButton next;
    private ImageButton back;
    private LrcView  lrcView;
    private ImageView imageView;

    private TextView title;
    private TextView singer;
    private TextView lrc;
    private int currentId;
    public  static  SeekBar play_bar;
    private LinearLayout linearLayout;
    private int curprogress;                                                                       //播放进度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏 
        setContentView(R.layout.activity_play);

        initViews();
        initEvents();
    }
    private void initViews(){
        pre=(ImageButton)findViewById(R.id.play_pre);
        play=(ImageButton)findViewById(R.id.play_play);
        next=(ImageButton)findViewById(R.id.play_next);
        back=(ImageButton)findViewById(R.id.play_back);
        title=(TextView) findViewById(R.id.play_title);
        singer=(TextView)findViewById(R.id.play_singer);
        lrcView=(LrcView)findViewById(R.id.lrcview);
        lrc=(TextView)findViewById(R.id.lrcview);
        imageView=(ImageView)findViewById(R.id.album);
        linearLayout=(LinearLayout)findViewById(R.id.activity_main);
        play_bar=(SeekBar)findViewById(R.id.play_bar);
        currentId=((MyApplication)getApplication()).getCurrentId();


        if(SendAndGet.getInstance().isPause()) {
            play.setBackgroundResource(R.drawable.play2);
        }
        else
        {
            //暂停

            play.setBackgroundResource(R.drawable.pause2);

        }

    }
    private void initEvents(){
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendAndGet.getInstance().pre();

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {

                if(SendAndGet.getInstance().isPause()) {
                    SendAndGet.getInstance().play();
                    SendAndGet.getInstance().changeStatePause();
                    play.setBackgroundResource(R.drawable.pause2);
                }
                else
                {
                    //暂停

                    SendAndGet.getInstance().changeStatePause();
                    SendAndGet.getInstance().pause();
                    play.setBackgroundResource(R.drawable.play2);

                }

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendAndGet.getInstance().next();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        play_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                curprogress=progress;

                //更新歌词
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                SendAndGet.getInstance().setPlayIsSetDur(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SendAndGet.getInstance().setProgress(curprogress);
                SendAndGet.getInstance().setPlayIsSetDur(true);
                SendAndGet.getInstance().flush_lrc(curprogress);
            }
        });
        //lrcView.setmLrcList();
        // 设置歌词
        lrcView.setAnimation(AnimationUtils.loadAnimation(Play.this,R.anim.a));
        lrcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //lrcView.setVisibility(View.INVISIBLE);
            }
        });
        play_bar.setMax(SendAndGet.getInstance().getLength());
        SendAndGet.getInstance().setPlayIsSetDur(true);
        update();
        /*设置切换歌曲回调*/
        SendAndGet.getInstance().setOnPrePlayListener(new SendAndGet.prePlay() {
            @Override
            public void preListener() {
                update();
            }
        });
        SendAndGet.getInstance().setOnNextPlayListener(new SendAndGet.nextPlay() {
            @Override
            public void nextListener() {
                update();
            }
        });
        //SendAndGet.getInstance().setLrcView(lrcView);


    }
    /*
    * 刷新title，singer 和 lrc*/
    private void update(){
        if(!SendAndGet.getInstance().isPlayFromNet()) {
            title.setText(((MyApplication) getApplication()).getMusciList().getNow().getTitle());
            singer.setText(((MyApplication) getApplication()).getMusciList().getNow().getArtist());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    readLrc();
                }
            }).start();
        }
        else
        {
            SendAndGet.getInstance().setLrcView(lrcView);
            title.setText(SendAndGet.getInstance().getMap().get("name").toString());
            singer.setText(SendAndGet.getInstance().getMap().get("artist").toString());
            Glide.with(Play.this).load(SendAndGet.getInstance().getMap().get("picurl").toString()).centerCrop().into(imageView);

        }

        /*刷新歌词*/
    }


    public void readLrc() {

        File file;
        FileReader fileReader=null;
        boolean ifSuccess = false;
        if (!ifSuccess) {
            try {
                file = new File(((MyApplication) getApplication()).getMusciList().getNow().getLrcurl1());
                fileReader = new FileReader(file);
                ifSuccess = true;
            } catch (FileNotFoundException e) {

            }
            ;
        }
        if (!ifSuccess) {
            try {
                file = new File(((MyApplication) getApplication()).getMusciList().getNow().getLrcurl2());
                fileReader = new FileReader(file);
                ifSuccess = true;
            } catch (FileNotFoundException e) {

            }
            ;
        }
        if (!ifSuccess) {
            try {
                file = new File(((MyApplication) getApplication()).getMusciList().getNow().getLrcurl3());
                fileReader = new FileReader(file);
                ifSuccess = true;
            } catch (FileNotFoundException e) {

            }

        }
        if (ifSuccess) {

            //这里寻找路径分两种第一种是该后缀，另一种参考网易云路径、解析也是分两种，一种普通一种参考网易云的歌词
            try {


                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuilder.append(str);

                }
                String regex = "\\[";
                String[] strings = stringBuilder.toString().split(regex);
                int s = strings.length;
                LrcManager lrcManager = new LrcManager();
                String regex3 = "\\[*(.*)\\](.*)";
                for (int i = 0; i < s; i++) {
                    System.out.println(strings[i]);
                    Pattern pattern2 = Pattern.compile(regex3);
                    Matcher matcher2 = pattern2.matcher(strings[i]);
                    if (matcher2.find()) {
                        Lrc lrc = new Lrc(matcher2.group(1), matcher2.group(2));
                        lrcManager.add(lrc);
                    }

                }
                SendAndGet.getInstance().setmLrcmanager(lrcManager);
                SendAndGet.getInstance().setLrcView(lrcView);

            /*
            * 两套解析
            * 如果是网易云的话，用json
            * 如果是普通的歌词，直接正则*/

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            //读取歌词失败
            SendAndGet.getInstance().setErrorLrc();
        }
    }


}
