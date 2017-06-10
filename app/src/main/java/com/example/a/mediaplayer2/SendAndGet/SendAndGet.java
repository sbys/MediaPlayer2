package com.example.a.mediaplayer2.SendAndGet;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.a.mediaplayer2.Activity.Play;
import com.example.a.mediaplayer2.Lrc.Lrc;
import com.example.a.mediaplayer2.Lrc.LrcManager;
import com.example.a.mediaplayer2.R;
import com.example.a.mediaplayer2.View.LrcView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 12917 on 2017/3/31.
 * 创建一个静态类，代表两个activity与service交互
 */

public class SendAndGet {
    private Messenger mActivityMessenger;
    private Messenger mServiceMessenger;
    private LrcManager mLrcmanager=null;
    private Handler handler;
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
    private int[] artists=new int[]{R.drawable.a0,R.drawable.a1,R.drawable.a2,R.drawable.a3,R.drawable.a4,R.drawable.a5,R.drawable.a6};
    private static SendAndGet mSendAndGet;
    private boolean isPause=true;
    private boolean mainIsSetDur=false;
    private boolean playIsSetDur=false;
    private boolean isFirst=true;
    private boolean isMainResNext=false;                                                            //Main是否响应service发来的next消息
    private boolean isPlayResNext=false;                                                            //Play页面是否响应service发来的消息
    private boolean isMainResPre=false;                                                            //Main是否响应service发来的next消息
    private boolean isPlayResPre=false;                                                            //Play页面是否响应service发来的消息
    private int length;                                                                         //当前播放曲目长度
    private Map<String,Object> map;
    private boolean isPlayFromNet=false;
    private nextMain mNext;
    private nextPlay mNextPlay;
    private preMain mPre;
    private prePlay mPrePlay;
    private LrcView lrcView;
    private OkHttpClient okHttpClient=new OkHttpClient();
    private LrcManager tmpLrc=null;
    private SendAndGet() {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case  SET_DUR:
                        setSET_DUR((int)msg.obj);
                        break;
                    case FLUSH_BAR:
                        setNowDur((int)msg.obj);
                        flush_lrc((int)msg.obj);
                        break;
                    case NEXT_FROMSERVICE:
                        servicee_next();
                        break;
                    case PRE_FROMSERVICE:
                        service_pre();
                        break;


                }
                super.handleMessage(msg);
            }
        };
        mActivityMessenger=new Messenger(handler);
    }

    public static SendAndGet getInstance(){
        if(mSendAndGet==null)
            mSendAndGet=new SendAndGet();
        return mSendAndGet;
    }


    public void setmServiceMessenger(Messenger mServiceMessenger) {
        this.mServiceMessenger = mServiceMessenger;


    }
    public void initMessenger(){
        Message message=Message.obtain();
        message.what=PREPARE;
        message.obj=null;
        message.replyTo=mActivityMessenger;
        try{
            mServiceMessenger.send(message);
        }
        catch (RemoteException e){}
    }
    public void play(){
        if(isFirst) {
            initMessenger();
            isFirst=false;
        }

        Message message=new Message();
        message.what=PLAY;
        message.replyTo=mActivityMessenger;
        try {
            mServiceMessenger.send(message);
        }
        catch (RemoteException e)
        {}
    }
    public void pause(){
        Message message=new Message();
        message.what=PAUSE;
        message.replyTo=mActivityMessenger;
        try{
            mServiceMessenger.send(message);
        }
        catch (RemoteException e){}
    }
    public  void changeStatePause(){
        if(isPause)
            isPause=false;
        else
            isPause=true;
    }
    public  boolean isPause(){
        return isPause;
    }
    public void next(){
        Message message=new Message();
        message.what=NEXT;
        message.replyTo=mActivityMessenger;
        try {
            mServiceMessenger.send(message);
        }
        catch (RemoteException e)
        {}
    }
    public void pre(){

        Message message=new Message();
        message.what=PRE;
        message.replyTo=mActivityMessenger;
        try {
            mServiceMessenger.send(message);
        }
        catch (RemoteException e)
        {}
    }
    public void setMainIsSetDur(boolean mainIsSetDur) {
        this.mainIsSetDur = mainIsSetDur;
    }

    public void setPlayIsSetDur(boolean playIsSetDur) {
        this.playIsSetDur = playIsSetDur;
    }

    private void setSET_DUR(int  dur){
        length=dur;
        if(mainIsSetDur){
            /*给Main设置进度跳长度*/

        }
        if(playIsSetDur){
            /*给play设置进度条长度*/
            Play.play_bar.setMax(dur);
        }

    }
    private void setNowDur(int i){
        if(mainIsSetDur){
            /*给Main设置进度跳长度*/

        }
        if(playIsSetDur){
            /*给play设置进度条长度*/
            Play.play_bar.setProgress(i);
        }
    }
    /*直接设置进度的方法*/
    public void setProgress(int i){
        Message message=new Message();
        message.what=SET_PROGRESS;
        message.obj=i;
        message.replyTo=mActivityMessenger;
        try{
            mServiceMessenger.send(message);
        }
        catch (RemoteException e){}
    }

    public int getLength() {

        return length;
    }

    public interface nextMain{
        void nextListener();
    }
    public interface nextPlay{
        void nextListener();
    }
    public interface preMain{
        void preListener();
    }
    public interface prePlay{
        void preListener();
    }
    public void setOnNextMainListener(nextMain nextListener){
        this.mNext=nextListener;
        isMainResNext=true;
    }
    public void setOnNextPlayListener(nextPlay nextPlayListener){
        this.mNextPlay=nextPlayListener;
        isPlayResNext=true;
    }
    public void setOnPreMainListener(preMain preMainListener){
        this.mPre=preMainListener;
        isMainResPre=true;
    }
    public void setOnPrePlayListener(prePlay prePlayListener)
    {
        this.mPrePlay=prePlayListener;
        isPlayResPre=true;
    }
    /*收到service的消息next*/
    private void servicee_next(){
        if(isMainResNext)
            mNext.nextListener();
        if(isPlayResNext)
            mNextPlay.nextListener();
    }
    private void service_pre(){
        if(isMainResPre)
            mPre.preListener();
        if(isPlayResPre)
            mPrePlay.preListener();
    }
    public  void flush_lrc(int i){
        if(lrcView!=null&mLrcmanager!=null)
        {
            int j=mLrcmanager.getIndex(i);
            if(j!=0) {

                lrcView.setCurrentid(j);
            }
        }
    }
    public void flush_net(Map<String,Object> map){
        this.map=map;
        getNetLrc(map.get("lrcurl").toString());
        while(tmpLrc==null);
        mLrcmanager=tmpLrc;
        tmpLrc=null;

        Message message=new Message();
        message.what=FLUSH_NET;
        message.obj=map;
        try {
            mServiceMessenger.send(message);
        }
        catch (RemoteException e){

        }
    }



    public void setLrcView(LrcView lrcView) {
        this.lrcView = lrcView;
        lrcView.setmLrcList(mLrcmanager.mLrcs);
    }
    /*读取歌词失败*/
    public void setErrorLrc(){
        if(lrcView!=null)
        lrcView.setText("读取错误");
    }

    public void setmLrcmanager(LrcManager mLrcmanager) {
        this.mLrcmanager = mLrcmanager;
    }
    /*获取网络歌词*/
    private void getNetLrc(String url){
        get(url);
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
    private void  get(String path){
        isPlayFromNet=true;
        final Request.Builder requestBuilder=new Request.Builder().url(path);
        requestBuilder.method("GET",null);
        Call call=okHttpClient.newCall(requestBuilder.build());
        /*异步调用*/
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody=response.body();
                String str=responseBody.string();
                try{
                    JSONObject jsonObject=new JSONObject(str);
                    String lrc=jsonObject.getJSONObject("lrc").getString("lyric");
                    String regex = "\\[";
                    String[] strings = lrc.toString().split(regex);
                    int s = strings.length;
                    LrcManager lrcManager = new LrcManager();
                    String regex3 = "\\[*(.*)\\](.*)";
                    for (int i = 0; i < s; i++) {
                        System.out.println(strings[i]);
                        Pattern pattern2 = Pattern.compile(regex3);
                        Matcher matcher2 = pattern2.matcher(strings[i]);
                        if (matcher2.find()) {
                            Lrc lrcc = new Lrc(matcher2.group(1), matcher2.group(2));
                            lrcManager.add(lrcc);
                        }

                    }
                    tmpLrc=lrcManager;
                }
                catch (JSONException j){}

            }
        });
        //也支持同步调用call.excute()
    }
    public boolean isPlayFromNet(){
        return  isPlayFromNet;
    }

    public Map<String, Object> getMap() {
        return map;
    }
    public int getRandom(){
        return artists[(int)(Math.random()*6)];
    }
}
