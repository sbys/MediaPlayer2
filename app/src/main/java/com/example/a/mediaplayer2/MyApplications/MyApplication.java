package com.example.a.mediaplayer2.MyApplications;

import android.app.Application;
import android.view.View;

import com.example.a.mediaplayer2.Song.MusciList;

/**
 * Created by 12917 on 2017/3/29.
 */


/*
* 这里放一个播放列表,Acitivity中确定播放列表,然后service播放音乐*/
public class MyApplication extends Application {
    private MusciList musciList;
    public void setMusciList(MusciList musciList) {
        this.musciList = musciList;
    }
    public MusciList getMusciList() {
        return musciList;
    }
    public int getCurrentId(){
        return musciList.getCurrentid();
    }
    public String getTitle(){
        return musciList.getNow().getTitle();
    }
    public String getSinger(){
        return musciList.getNow().getArtist();
    }
}
