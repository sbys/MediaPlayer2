package com.example.a.mediaplayer2.Activity;
import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Messenger;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a.mediaplayer2.MyApplications.MyApplication;
import com.example.a.mediaplayer2.R;
import com.example.a.mediaplayer2.SendAndGet.SendAndGet;
import com.example.a.mediaplayer2.Service.MusicService;
import com.example.a.mediaplayer2.Song.MusciList;
import com.example.a.mediaplayer2.Song.MusicInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.cache.DiskLruCache;

public class Main extends AppCompatActivity {
    private SearchView searchView;
    private ImageView imageView;
    private TextView  title;
    private ImageButton play;
    private ListView listView;
    private LinearLayout linearLayout;
    private ArrayList<HashMap<String,Object>> list;
    private Map<String,Object> netresult;
    private SimpleAdapter simpleAdapter;
    private static final int REQUEST_PERMMISON=1;
    //请求手机权限
    private static String[] PERMISSIONS={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TextView video;
    private MusciList musciList=new MusciList();
    //取出歌曲id，标题，时间，作者，专辑
    private String[] projections={  MediaStore.Audio.Media._ID,
                                    MediaStore.Audio.Media.DATA,
                                    MediaStore.Audio.Media.TITLE,
                                    MediaStore.Audio.Media.DURATION,
                                    MediaStore.Audio.Media.ARTIST,
                                    MediaStore.Audio.Media.ALBUM,
                                    MediaStore.Audio.Media.ALBUM_ID};
    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SendAndGet.getInstance().setmServiceMessenger(new Messenger(service));
            SendAndGet.getInstance().initMessenger();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getPermissions();
        getList();
        initViews();
        initEvents();
        Intent intent=new Intent(Main.this,MusicService.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private void initViews(){
        imageView=(ImageView)findViewById(R.id.content_image);
        title=(TextView)findViewById(R.id.content_title);
        play=(ImageButton)findViewById(R.id.content_play);
        play.setBackgroundResource(R.drawable.play);
        searchView=(SearchView)findViewById(R.id.search);
        linearLayout=(LinearLayout)findViewById(R.id.line1) ;
        listView=new ListView(this);
        video=(TextView)findViewById(R.id.video);
        update();
    }
    private void initEvents(){

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main.this,Play.class);
                startActivity(intent);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SendAndGet.getInstance().isPause()) {
                    SendAndGet.getInstance().play();
                    SendAndGet.getInstance().changeStatePause();
                    //设置图标为暂停
                    play.setBackgroundResource(R.drawable.pause);
                }
                else
                {
                    //暂停，设置图标为播放
                    play.setBackgroundResource(R.drawable.play);
                    SendAndGet.getInstance().changeStatePause();
                    SendAndGet.getInstance().pause();

                }
            }
        });
        SendAndGet.getInstance().setOnNextMainListener(new SendAndGet.nextMain() {
            @Override
            public void nextListener() {
                /*下一首*/
                update();
            }
        });
        SendAndGet.getInstance().setOnPreMainListener(new SendAndGet.preMain() {
            @Override
            public void preListener() {
                /*上一首*/
                update();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                post(newText);
                return false;
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main.this,Video.class);
                startActivity(intent);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                linearLayout.removeView(listView);
                return false;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(play!=null)
            if(SendAndGet.getInstance().isPause())
                play.setBackgroundResource(R.drawable.play);
            else
                play.setBackgroundResource(R.drawable.pause);
    }

    public  void getPermissions(){
        ActivityCompat.requestPermissions(Main.this,PERMISSIONS,REQUEST_PERMMISON);
    }
    private void getList(){
        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projections, MediaStore.Audio.Media.IS_MUSIC+"=?",new String[]{"1"},MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        String[] s=cursor.getColumnNames();
        for(cursor.moveToFirst();!cursor.isLast();cursor.moveToNext()){
            MusicInfo musicInfo=new MusicInfo(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getLong(6));
            musciList.add(musicInfo);
        }
        cursor.close();
        ((MyApplication)getApplication()).setMusciList(musciList);

    }
    private void update(){
        /*图片，title更新*/
        title.setText(((MyApplication)getApplication()).getMusciList().getNow().getTitle());
        imageView.setBackgroundResource(SendAndGet.getInstance().getRandom());
    }
    private void post(String string){

        FormBody formBody=new FormBody.Builder().add("s",string).add("offest","10").add("limit","10").add("type","1").build();
        Request request=new Request.Builder().url("http://music.163.com/api/search/pc")
                .post(formBody).build();
        SendAndGet.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s=response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject js=jsonObject.getJSONObject("result");
                    JSONArray jsonArray=js.getJSONArray("songs");
                    list=new ArrayList<HashMap<String, Object>>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        HashMap<String,Object> map=new HashMap<String, Object>();
                        map.put("name",jsonObject1.getString("name"));
                        int id=(int)jsonObject1.getInt("id");
                        map.put("artist",jsonObject1.getJSONArray("artists").getJSONObject(0).getString("name"));
                        map.put("mp3url",jsonObject1.getString("mp3Url"));
                        map.put("picurl",jsonObject1.getJSONArray("artists").getJSONObject(0).getString("picUrl"));
                        map.put("lrcurl","http://music.163.com/api/song/lyric?os=pc&id="+id+"&lv=-1&kv=-1&tv=-1");
                        list.add(map);
                    }
                }

                catch (JSONException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            linearLayout.removeView(listView);
                        }
                        catch (Exception e){}
                        simpleAdapter=new SimpleAdapter(Main.this, list,R.layout.item ,new String[]{"name","artist"},new int[]{R.id.name,R.id.artist});
                        listView.setCacheColorHint(Color.argb(255,1,1,11));
                        listView.setAdapter(simpleAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                /*根据选定的item通知service播放同时更新图片*/
                                netresult=list.get(position);
                                title.setText(netresult.get("name").toString());
                                Glide.with(Main.this).load(netresult.get("picurl").toString()).centerCrop().into(imageView);
                                updateFromNet();
                                linearLayout.removeView(listView);
                            }
                        });
                        Toast.makeText(Main.this, "", Toast.LENGTH_SHORT).show();
                        linearLayout.addView(listView,1);


                    }
                });


            }
        });
    }
    /*根绝选定的item通知service播放，同时更新图片，一个是歌手，一个是歌曲歌词*/
    private void updateFromNet(){
        SendAndGet.getInstance().flush_net(netresult);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(this, "aaaa", Toast.LENGTH_SHORT).show();
        return super.onTouchEvent(event);
    }
}
