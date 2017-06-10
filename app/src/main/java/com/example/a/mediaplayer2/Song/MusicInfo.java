package com.example.a.mediaplayer2.Song;

import android.graphics.Bitmap;

import com.example.a.mediaplayer2.Lrc.LrcManager;

/**
 * Created by 12917 on 2017/3/27.
 */

public class MusicInfo
{
    private Long id;
    private String uri;
    private String title;
    private Long time;
    private String artist;
    private Bitmap album;
    private String albumstr;
    private String lrcurl1="";
    private String lrcurl2="";
    private String lrcurl3="";
    private String albumurl;
    private Long albumid;
    private LrcManager lrcManager=null;
    public MusicInfo(long id,String url,String title,Long time,String artist,String albumstr,Long albumid) {
        this.id=id;
        this.uri=url;
        this.title=title;
        this.time=time;
        this.artist=artist;
        this.albumstr=albumstr;
        this.lrcurl1=url.replace(".mp3",".lrc");
        this.lrcurl2=url.replace("Music","Musiclrc").replace(".mp3",".lrc");
        this.lrcurl3=url.replaceAll("Music\\/.*\\.mp3", "Download/Lyric/")+""+id;
        this.albumurl="content://media/external/audio/media/"+id+"/"+albumid;
        this.albumid=albumid;
    }

    public void setLrcManager(LrcManager lrcManager) {
        this.lrcManager = lrcManager;
    }

    public LrcManager getLrcManager() {
        return lrcManager;
    }

    public String getLrcurl1() {
        return lrcurl1;
    }

    public String getLrcurl2() {
        return lrcurl2;
    }

    public String getLrcurl3() {
        return lrcurl3;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getAlbumurl() {
        return albumurl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(Bitmap album) {
        this.album = album;
    }

    public Long getId() {
        return id;
    }

    public void setAlbumstr(String albumstr) {
        this.albumstr = albumstr;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public Long getTime() {
        return time;
    }

    public String getArtist() {
        return artist;
    }

    public Bitmap getAlbum() {
        return album;
    }

    public String getAlbumstr() {
        return albumstr;
    }
}
