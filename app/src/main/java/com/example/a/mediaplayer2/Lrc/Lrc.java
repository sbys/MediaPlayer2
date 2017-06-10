package com.example.a.mediaplayer2.Lrc;

/**
 * Created by 12917 on 2017/3/29.
 */

public class Lrc {private int time;
    private String content;
    public Lrc(String time, String content){
        setTime(time);
        setContent(content);
    }
    public int getTime() {
        return time;
    }
    public String getContent() {
        return content;
    }
    public void setTime(String string) {
        String [] strings=string.split(":");
        int mtime=Integer.parseInt(strings[0])*60*1000 ;
        mtime+=(int)(Double.parseDouble(strings[1])*1000);
        this.time=mtime;


    }
    public void setContent(String content) {
        this.content = content;
    }
}