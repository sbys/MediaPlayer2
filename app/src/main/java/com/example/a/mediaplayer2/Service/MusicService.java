package com.example.a.mediaplayer2.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.a.mediaplayer2.MyApplications.MyApplication;
import com.example.a.mediaplayer2.Song.MusciList;
import com.example.a.mediaplayer2.Song.MusicInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    Timer timer;
    private Messenger mActivityMessenger;
    private Messenger mServiceMessaenger;
    private final int PREPARE=-1;
    private final int  PLAY=0;
    private final int  PAUSE=1;
    private final int NEXT=2;
    private final int PRE=3;
    private final int FLUSH_LRC=4;
    private final int FLUSH_BAR=5;
    private final int SET_DUR=6;
    private final int SET_PROGRESS=7;
    private final int NEXT_FROMSERVICE=8;
    private final int PRE_FROMSERVICE=9;
    private final int FLUSH_NET=10;
    private MediaPlayer mediaPlayer;
    private boolean isPause=false;
    private Handler handler;
    int index=0;
    int s=0;
    private int currentid;
    private MusicInfo musicInfo;
    public MusicService() {
    }
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {


        return mServiceMessaenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
                //提醒activity
            }
        });

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case PREPARE:
                        break;
                    case PLAY:
                        playFirst();
                        break;
                    case PAUSE:
                        pause();
                        break;
                    case NEXT:
                        next();
                        break;
                    case PRE:
                        pre();
                        break;
                    case FLUSH_LRC:
                        flush();
                        break;
                    case SET_PROGRESS:
                        setProgress((int)msg.obj);
                        break;
                    case FLUSH_NET:
                        playFromNet((HashMap<String,Object>)msg.obj);
                    default:

                }
                if(mActivityMessenger==null)
                    mActivityMessenger=msg.replyTo;


            }
        };
        mServiceMessaenger=new Messenger(handler);
    }


    //这是一个测试方法

    private void playFirst(){


        if(isPause)
        {
            mediaPlayer.start();
            isPause=false;
        }
        else{
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(((MyApplication)getApplication()).getMusciList().getNow().getUri());
            mediaPlayer.prepare();
            Message message=Message.obtain();
            message.what=SET_DUR;
            message.obj=mediaPlayer.getDuration();
            try {
                mActivityMessenger.send(message);
            }
            catch (RemoteException e){}

            mediaPlayer.start();
            update();

        }
        catch (IOException e){
            e.printStackTrace();
        }}

    }
    private void pause(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause=true;
        }


    }
    private void next(){
        play(mediaPlayer,((MyApplication)getApplication()).getMusciList().getNext().getUri());
        Message message=Message.obtain();
        message.what=NEXT_FROMSERVICE;
        try {
            mActivityMessenger.send(message);

        }
        catch (RemoteException r){}
        ((MyApplication)getApplication()).setMusciList(((MyApplication)getApplication()).getMusciList());

    }
    private void pre(){
        play(mediaPlayer,((MyApplication)getApplication()).getMusciList().getPre().getUri());
        Message message=Message.obtain();
        message.what=PRE_FROMSERVICE;
        try {
            mActivityMessenger.send(message);

        }
        catch (RemoteException r){}

    }

    private void flush(){}

    //让mediaplayer播放的方法
    private void play(MediaPlayer mediaPlayer,String path){
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            Message message=Message.obtain();
            message.what=SET_DUR;
            message.obj=mediaPlayer.getDuration();
            try {
                mActivityMessenger.send(message);
            }
            catch (RemoteException e){}
            mediaPlayer.start();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        update();

    }
    /*与activiy交互更新进度条*/
    private void  update(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while(mediaPlayer.isPlaying()){
                    Message message=Message.obtain();
                    message.what=FLUSH_BAR;
                    message.obj=mediaPlayer.getCurrentPosition();
                    try {
                        mActivityMessenger.send(message);
                         }
                    catch (RemoteException e){
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1);
                    }
                    catch (InterruptedException e){}
            }}
        }).start();
    }

    private void setProgress(int i){
        mediaPlayer.seekTo(i);
    }
    private void playFromNet(HashMap<String,Object> map){
        play(mediaPlayer,map.get("mp3url").toString());

    }

}

