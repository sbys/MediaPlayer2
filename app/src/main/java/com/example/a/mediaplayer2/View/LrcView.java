package com.example.a.mediaplayer2.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.example.a.mediaplayer2.Lrc.Lrc;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by 12917 on 2017/4/16.
 */

public class LrcView extends TextView {
    private float width ;                                                                                   //歌词视图宽度
    private float height;                                                                                   //歌词视图高度
    private Paint currentPaint;                                                                             //当前画笔对象
    private Paint oldPaint;                                                                                 //其它歌词
    private float textHeight=100;                                                                            //文本高度
    private float testsize=24;
    private int currentid=0;
    private List<Lrc> mLrcList=new ArrayList<Lrc>();

    public void setmLrcList(List<Lrc> mLrcList) {
        this.mLrcList = mLrcList;
        init();
    }

    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        setFocusable(true);
        currentPaint=new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setTextAlign(Paint.Align.CENTER);
        oldPaint=new Paint();
        oldPaint.setAntiAlias(true);
        oldPaint.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas==null)
            return ;
        currentPaint.setColor(Color.RED);
        oldPaint.setColor(Color.GRAY);
        currentPaint.setTextSize(50);
        currentPaint.setTypeface(Typeface.SERIF);
        oldPaint.setTextSize(50);
        oldPaint.setTypeface(Typeface.DEFAULT);
        try{
            //setText("");
            canvas.drawText(mLrcList.get(currentid).getContent(),width/2,height/2,currentPaint);
            Log.e("draw","sss");
            float tempY=height/2;
            for(int i=currentid-1;i>=0;i--){
            tempY=tempY-textHeight;
            canvas.drawText(mLrcList.get(i).getContent(),width/2,tempY,oldPaint);
        }
        tempY=height/2;
        for(int i=currentid+1;i<mLrcList.size();i++){
            tempY=tempY+textHeight;
            canvas.drawText(mLrcList.get(i).getContent(),width/2,tempY,oldPaint);
        }
    }
    catch (Exception e){
        setText("...木有歌词文件，赶紧去下载...");
    }
        Log.e("drwa","sss");
}



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width=w;
        this.height=h;
    }

    public void setCurrentid(int currentid) {
        this.currentid = currentid;
        invalidate();
        Log.e("setCurrent",""+currentid);
    }
}


























