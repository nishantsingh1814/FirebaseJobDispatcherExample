package com.example.syncdb;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Nishant on 7/18/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<Contact> arrayList=new ArrayList<>();

    public RecyclerAdapter(ArrayList<Contact> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.nameTv.setText(arrayList.get(position).getName());
        int sync_status=arrayList.get(position).getSync_status();
        if(sync_status==DbContract.SYNC_STATUS_OK){
            holder.sync_status.setImageResource(R.drawable.ic_ok_appproval_acceptance);
        }else{
            holder.sync_status.setImageResource(R.drawable.ic_synchronization_arrows);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView sync_status;
        TextView nameTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            nameTv=(TextView)itemView.findViewById(R.id.textName);
            sync_status=(ImageView)itemView.findViewById(R.id.imgSync);
        }
    }
}
