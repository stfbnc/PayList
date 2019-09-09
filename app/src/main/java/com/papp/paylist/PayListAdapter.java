package com.papp.paylist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.papp.paylist.db.DataManager;

import java.util.ArrayList;

public class PayListAdapter extends RecyclerView.Adapter<PayListAdapter.ViewHolder> {

    private ArrayList<Integer> mList;
    private DataManager dataManager;
    private Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, money;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            money = v.findViewById(R.id.money);
        }
    }

    public PayListAdapter(DataManager dataManager, ArrayList<Integer> mList) {
        this.mList = mList;
        this.dataManager = dataManager;
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
            Integer tab_id = mList.get(position);
            Cursor c = dataManager.dbSelectByUrno(tab_id);
            if(c.getCount() > 0) {
                c.moveToNext();
                holder.name.setText(c.getString(0));
                holder.money.setText(String.valueOf(c.getDouble(1)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
