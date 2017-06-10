package com.example.a.mediaplayer2.Lrc;

import java.util.ArrayList;

/**
 * Created by 12917 on 2017/3/29.
 */

public class LrcManager {
    private int index=0;
    public ArrayList<Lrc> mLrcs;
    public LrcManager(){
        mLrcs=new ArrayList<Lrc>();
    }
    public void add(Lrc lrc){
        mLrcs.add(lrc);
    }
    public int  getIndex(int i){
        int tmp=0;
        while(mLrcs.get(tmp).getTime()<i){
            tmp++;
        }
        tmp--;
        if(index==tmp)
            return 0;
        else{
            index=tmp;
            return index;
        }




    }


}