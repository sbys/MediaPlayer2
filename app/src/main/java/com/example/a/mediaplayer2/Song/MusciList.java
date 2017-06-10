package com.example.a.mediaplayer2.Song;

import com.example.a.mediaplayer2.Lrc.LrcManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 12917 on 2017/3/27.
 */

public class MusciList {
    private List<MusicInfo> musicInfos;
    private int currentid=0;                                                        //当前播放音乐id

    public MusciList() {
        musicInfos=new ArrayList<MusicInfo>();
    }
    public void add(MusicInfo musicInfo){
        musicInfos.add(musicInfo);
    }

    public MusicInfo getFirst(){
        currentid=1;
        return musicInfos.get(currentid);
    }
    public MusicInfo getNext(){
        if(++currentid>musicInfos.size())
            return null;
        return musicInfos.get(currentid);
    }
    public MusicInfo getPre(){
        if(--currentid<0)
            return null;
        return musicInfos.get(currentid);
    }
    public MusicInfo getRandomMusic(){
        currentid=(int)Math.random()*musicInfos.size();
        return musicInfos.get(currentid);
    }
    public void setLrcManager(int location,LrcManager lrcManager){
        musicInfos.get(location).setLrcManager(lrcManager);
    }

    public void setCurrentid(int currentid) {
        this.currentid = currentid;
    }

    public int getCurrentid() {
        return currentid;
    }
    public MusicInfo getNow(){
        return musicInfos.get(currentid);
    }
}
