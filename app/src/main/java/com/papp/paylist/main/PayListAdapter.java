package com.papp.paylist.main;

import android.content.Context;
import android.database.Cursor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;
import com.papp.paylist.db.DataManager;

import java.util.ArrayList;

public class PayListAdapter extends RecyclerView.Adapter<PayListAdapter.ViewHolder> {

    public interface OnHistoryButtonClickListener {
        void onHistoryClick(View button, int position);
    }

    private ArrayList<Integer> mList;
    private Context ctx;
    View.OnClickListener clickListener;
    OnHistoryButtonClickListener historyButtonListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView type, money, data;
        public FloatingActionButton fab_hist;
        public ViewHolder(View v) {
            super(v);
            type = v.findViewById(R.id.type);
            money = v.findViewById(R.id.money);
            data = v.findViewById(R.id.data);
            fab_hist = v.findViewById(R.id.fab_history);
        }
    }

    public PayListAdapter(ArrayList<Integer> mList) {
        this.mList = mList;
    }

    public void setOnItemClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setHistoryButtonListener(OnHistoryButtonClickListener historyButtonListener) {
        this.historyButtonListener = historyButtonListener;
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
    public void onBindViewHolder(final PayListAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(clickListener);
        if(mList.size() > 0) {
            final Integer tab_id = mList.get(position);
            DataManager dataManager = new DataManager(ctx);
            Cursor c = dataManager.paytabSelectById(tab_id);
            if(c.getCount() > 0) {
                c.moveToNext();
                holder.type.setText(c.getString(DataManager.PAYTAB_TYPE_IDX));
                holder.money.setText(String.valueOf(c.getDouble(DataManager.PAYTAB_EURO_IDX)));
                int ioro = c.getInt(DataManager.PAYTAB_IORO_IDX);
                if(ioro == BaseActivity.INCOME)
                    holder.money.setTextColor(ctx.getResources().getColor(R.color.green));
                else if(ioro == BaseActivity.EXPENSE)
                    holder.money.setTextColor(ctx.getResources().getColor(R.color.red));
                BaseActivity base = new BaseActivity();
                holder.data.setText(base.getAppFormatDate(c.getString(DataManager.PAYTAB_DATE_IDX)));
                holder.fab_hist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        historyButtonListener.onHistoryClick(view, holder.getAdapterPosition());
                    }
                });
            }
            c.close();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
