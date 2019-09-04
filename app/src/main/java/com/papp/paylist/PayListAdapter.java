package com.papp.paylist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PayListAdapter extends RecyclerView.Adapter<PayListAdapter.ViewHolder> {

    private ArrayList<String> mList;
    private Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, money;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            money = v.findViewById(R.id.money);
        }
    }

    public PayListAdapter(ArrayList<String> lst) {
        mList = lst;
    }

    @Override
    public PayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View row = inflater.inflate(R.layout.paylist_row, parent, false);
        PayListAdapter.ViewHolder viewHolder = new PayListAdapter.ViewHolder(row);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PayListAdapter.ViewHolder holder, int position) {
        if(mList.size() > 0) {
            String nm = mList.get(position);
            holder.name.setText(nm);
            holder.money.setText(String.valueOf(MainActivity.dataMap.get(nm)));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
