package com.example.a.mediaplayer2.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a.mediaplayer2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 12917 on 2017/5/8.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();
    private Context context;
    private ItemListener itemListener;
    public MyAdapter(ArrayList<HashMap<String,Object>> list,Context context) {
        super();
        this.context=context;
        this.list=list;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(list.get(position).get("title").toString());
        holder.imageView.setImageBitmap((Bitmap) list.get(position).get("bitmap"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder=new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycle_item,parent,false),itemListener);
        return viewHolder;
    }
    public void setItemListener(ItemListener itemListener){
        this.itemListener=itemListener;
    }

    public interface ItemListener{
         void onItemListener(View view,int postion);
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        ItemListener listener;
        public ViewHolder(View itemView,ItemListener listeners) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.title);
            imageView=(ImageView)itemView.findViewById(R.id.image);
            listener=listeners;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemListener(v,getPosition());
                }
            });
        }


    }
}
