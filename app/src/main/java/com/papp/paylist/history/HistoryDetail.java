package com.papp.paylist.history;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.papp.paylist.R;
import com.papp.paylist.db.DataManager;

import java.util.ArrayList;

public class HistoryDetail {

    private Context ctx;
    private int urno;

    public HistoryDetail(Context ctx, int urno) {
        this.ctx = ctx;
        this.urno = urno;
    }

    public void showHistory() {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View popupView = inflater.inflate(R.layout.history_layout, null);

        final PopupWindow mpopup = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        WindowManager wm = (WindowManager) popupView.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int height = displaymetrics.heightPixels;
        mpopup.setWidth(width * 8 / 10);
        mpopup.setHeight(height * 8 / 10);
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
        mpopup.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        RecyclerView rec_hist = popupView.findViewById(R.id.recycle_history);
        rec_hist.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);
        rec_hist.setLayoutManager(layoutManager);
        HistoryAdapter adapter = new HistoryAdapter(getHistory());
        rec_hist.setAdapter(adapter);
    }

    private ArrayList<Integer> getHistory(){
        ArrayList<Integer> urnos = new ArrayList<>();
        DataManager dm = new DataManager(ctx);
        Cursor c = dm.updtabSelectIds(urno);
        while(c.moveToNext())
            urnos.add(c.getInt(0));
        c.close();
        return urnos;
    }

}
