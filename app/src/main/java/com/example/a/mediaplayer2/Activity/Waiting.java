package com.example.a.mediaplayer2.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.a.mediaplayer2.R;

import java.util.Timer;

public class Waiting extends AppCompatActivity {

    private Button waiting;
    private Timer timer;
    private int j;                                                      //倒数时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting);
        waiting=(Button)findViewById(R.id.waiting);
        timer=new Timer();
        waiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Waiting.this,Main.class);
                startActivity(intent);
            }
        });
        setTime(waiting,5);


    }
    /*
    * 在按钮上设置倒数
    * */
    private void setTime(final Button button , final int i){
        CountDownTimer countDownTimer=new CountDownTimer(i*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                button.setText("跳过"+millisUntilFinished/1000+"s");
            }

            @Override
            public void onFinish() {
                Intent intent=new Intent(Waiting.this,Main.class);
                startActivity(intent);
                finish();
            }
        };
        countDownTimer.start();
    }

}
