package com.cognitio.goti.Adapters;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cognitio.goti.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshu on 19/03/17.
 */

public class AvailableWifiAdapter extends RecyclerView.Adapter <AvailableWifiAdapter.myviewholder> {
    Context context;
    LayoutInflater inflater;
    List<ScanResult> list = new ArrayList<>();
    final String[] modes = {"WPA", "EAP","WEP" };

    public AvailableWifiAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public myviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.available_wifi_row,parent,false);
        AvailableWifiAdapter.myviewholder viewholder = new AvailableWifiAdapter.myviewholder(v);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(myviewholder holder, int position) {
        ScanResult curr = list.get(position);
        holder.name.setText(curr.SSID);

        final String cap = curr.capabilities;
        for (int i = modes.length - 1; i >= 0; i--) {
            if (cap.contains(modes[i])) {
                holder.icon.setVisibility(View.VISIBLE);
            }
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<ScanResult> list) {
        Log.e("sentlist",list.toString());
        this.list=list;
        Log.e("thislist",this.list.toString());
        notifyDataSetChanged();
    }

    static class myviewholder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView name;
        ImageView icon;
        public myviewholder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.wifi_card);
            name = (TextView)itemView.findViewById(R.id.wifi_name_tv);
            icon = (ImageView)itemView.findViewById(R.id.wifi_icon);
        }
    }
}
