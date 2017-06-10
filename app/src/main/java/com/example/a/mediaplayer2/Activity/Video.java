package com.example.a.mediaplayer2.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.a.mediaplayer2.Adapter.MyAdapter;
import com.example.a.mediaplayer2.R;
import com.example.a.mediaplayer2.VideoControllerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Video extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,VideoControllerView.MediaPlayerControl {
    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;

    private static String[] PERMISSIONS={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMMISON=1;
    String string=null ;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
    private boolean isFinish=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initViews();
        initEvent();
        get();
        while (!isFinish);
        MyAdapter adapter=new MyAdapter(list,Video.this);
//        MediaController mediaController=new MediaController(Video.this,false);
//
//        mediaController.setAnchorView(video);
//        video.setMediaController(mediaController);
//
//        get();
//        while (!isFinish);
//        video.setVideoPath(list.get(0).get("path").toString());
//        MyAdapter adapter=new MyAdapter(list,Video.this);
//        adapter.setItemListener(new MyAdapter.ItemListener() {
//            @Override
//            public void onItemListener(View view, int postion) {
//                video.setVideoPath(list.get(postion).get("path").toString());
//                video.start();
//                drawerLayout.closeDrawer(GravityCompat.START);
//            }
//        });
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
            player.setOnPreparedListener(this);


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(Video.this));
        recyclerView.setAdapter(adapter);




    }
    private void initViews(){
        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);
        player = new MediaPlayer();
        controller = new VideoControllerView(this);
        controller.setMediaPlayer(this);
        drawerLayout=(DrawerLayout)findViewById(R.id.activity_main);
        recyclerView=(RecyclerView)findViewById(R.id.recycleview);

    }
    private void initEvent(){
        videoSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.show();
            }
        });
    }

    private void get(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                    Uri originalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    getPermissions();
                    ContentResolver cr = Video.this.getApplicationContext().getContentResolver();
                    Cursor cursor = cr.query(originalUri, null, null, null, null);
                    if (cursor == null) {
                        return;
                    }
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        HashMap<String,Object> map=new HashMap<String, Object>();
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                        map.put("title",title);
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        map.put("path",path);
                        Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(path,MediaStore.Images.Thumbnails.MINI_KIND);
                        bitmap=ThumbnailUtils.extractThumbnail(bitmap,375,250,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                        map.put("bitmap",bitmap);
                        list.add(map);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                isFinish=true;

            }
        }).start();

    }
    public  void getPermissions(){
        ActivityCompat.requestPermissions(Video.this,PERMISSIONS,REQUEST_PERMMISON);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        Toast.makeText(this, "touch", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }



    //MediaController 接口
    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        player.start();
    }
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }
}

