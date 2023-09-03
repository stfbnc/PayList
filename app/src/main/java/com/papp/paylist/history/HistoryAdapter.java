package com.papp.paylist.history;

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;
import com.papp.paylist.db.DataManager;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<Integer> mList;
    private Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView upd, type, money, data, descr, div;
        public ViewHolder(View v) {
            super(v);
            upd = v.findViewById(R.id.update);
            type = v.findViewById(R.id.type);
            money = v.findViewById(R.id.money);
            data = v.findViewById(R.id.data);
            descr = v.findViewById(R.id.descr);
            div = v.findViewById(R.id.div);
        }
    }

    public HistoryAdapter(ArrayList<Integer> mList) {
        this.mList = mList;
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View row = inflater.inflate(R.layout.history_row, parent, false);
        HistoryAdapter.ViewHolder viewHolder = new HistoryAdapter.ViewHolder(row);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        if(mList.size() > 0) {
            Integer tab_id = mList.get(position);
            DataManager dataManager = new DataManager(ctx);
            Cursor c = dataManager.updtabSelectById(tab_id);
            if(c.getCount() > 0) {
                c.moveToNext();
                holder.type.setText(c.getString(DataManager.UPDTAB_TYPE_IDX));
                holder.money.setText(String.valueOf(c.getDouble(DataManager.UPDTAB_EURO_IDX)));
                int ioro = c.getInt(DataManager.UPDTAB_IORO_IDX);
                if(ioro == BaseActivity.INCOME)
                    holder.money.setTextColor(ctx.getResources().getColor(R.color.green));
                else if(ioro == BaseActivity.EXPENSE)
                    holder.money.setTextColor(ctx.getResources().getColor(R.color.red));
                BaseActivity base = new BaseActivity();
                holder.data.setText(base.getAppFormatDate(c.getString(DataManager.UPDTAB_DATE_IDX)));
                String txt = ctx.getResources().getString(R.string.record_update)+" "+base.getAppFormatDate(c.getString(DataManager.UPDTAB_CDAT_IDX));
                holder.upd.setText(txt);
                holder.descr.setText(c.getString(DataManager.UPDTAB_DSCR_IDX));
            }
            c.close();
            if(position == mList.size()-1)
                holder.div.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
