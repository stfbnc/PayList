package com.papp.paylist.main;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;
import com.papp.paylist.db.DataManager;

import java.util.ArrayList;

public class PayListAdapter extends RecyclerView.Adapter<PayListAdapter.ViewHolder> {

    private ArrayList<Integer> mList;
    private Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView type, money, data;
        public ViewHolder(View v) {
            super(v);
            type = v.findViewById(R.id.type);
            money = v.findViewById(R.id.money);
            data = v.findViewById(R.id.data);
        }
    }

    public PayListAdapter(ArrayList<Integer> mList) {
        this.mList = mList;
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
            DataManager dataManager = new DataManager(ctx);
            Cursor c = dataManager.paytabSelectById(tab_id);
            if(c.getCount() > 0) {
                c.moveToNext();
                holder.type.setText(c.getString(DataManager.PAYTAB_TYPE_IDX));
                holder.money.setText(String.valueOf(c.getDouble(DataManager.PAYTAB_EURO_IDX)));
                int ioro = c.getInt(DataManager.PAYTAB_IORO_IDX);
                if(ioro == BaseActivity.INCOME)
                    holder.money.setTextColor(ctx.getResources().getColor(R.color.green));
                else if(ioro == BaseActivity.OUTFLOW)
                    holder.money.setTextColor(ctx.getResources().getColor(R.color.red));
                BaseActivity base = new BaseActivity();
                holder.data.setText(base.getAppFormatDate(c.getString(DataManager.PAYTAB_DATE_IDX)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
