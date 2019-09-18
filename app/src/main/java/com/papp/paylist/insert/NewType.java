package com.papp.paylist.insert;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.papp.paylist.R;
import com.papp.paylist.db.DataManager;

import java.util.ArrayList;

public class NewType {

    private Context ctx;
    private Spinner spinner;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    public NewType(Context ctx, Spinner spinner, ArrayList<String> list, ArrayAdapter<String> adapter) {
        this.ctx = ctx;
        this.spinner = spinner;
        this.list = list;
        this.adapter = adapter;
    }

    public void showDetails() {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View popupView = inflater.inflate(R.layout.new_type_layout, null);

        final PopupWindow mpopup = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        WindowManager wm = (WindowManager) popupView.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        mpopup.setWidth(width * 7 / 10);
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
        mpopup.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        final EditText type_et = popupView.findViewById(R.id.new_type);

        Button cancel_btn = popupView.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopup.dismiss();
            }
        });

        Button ok_btn = popupView.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTypeToDb(type_et.getText().toString());
                mpopup.dismiss();
            }
        });
    }

    private void addTypeToDb(String text){
        DataManager dm = new DataManager(ctx);
        dm.typtabInsert(text);
        list.add(text);
        adapter.notifyDataSetChanged();
        spinner.setSelection(list.size()-1);
    }

}
