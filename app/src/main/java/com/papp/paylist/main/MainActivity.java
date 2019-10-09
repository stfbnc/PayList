package com.papp.paylist.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.papp.paylist.base.BaseActivity;
import com.papp.paylist.filter.FilterActivity;
import com.papp.paylist.history.HistoryDetail;
import com.papp.paylist.insert.InsertActivity;
import com.papp.paylist.R;
import com.papp.paylist.db.DataManager;
import com.papp.paylist.stats.StatsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private TextView date_range;
    private ArrayList<Integer> paylist;
    private PayListAdapter adapter;
    private Bundle filtered_bundle = new Bundle();
    private Context context;
    private String date1 = "", date2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        date_range = findViewById(R.id.date_range);

        final RecyclerView rec_paylist = findViewById(R.id.recycle_paylist);
        rec_paylist.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rec_paylist.setLayoutManager(layoutManager);
        paylist = new ArrayList<>();
        adapter = new PayListAdapter(paylist);
        rec_paylist.setAdapter(adapter);
        adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetails(rec_paylist.getChildAdapterPosition(v));
            }
        });
        adapter.setHistoryButtonListener(new PayListAdapter.OnHistoryButtonClickListener() {
            @Override
            public void onHistoryClick(View button, int position) {
                HistoryDetail h_det = new HistoryDetail(context, paylist.get(position));
                h_det.showHistory();
            }
        });
        //getPayList(false);
        getListAtStart();

        FloatingActionButton fab_all = findViewById(R.id.fab_all);
        fab_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtered_bundle.clear();
                getPayList(false);
            }
        });

        FloatingActionButton fab_new = findViewById(R.id.fab_new);
        fab_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInsertActivity();
            }
        });

        FloatingActionButton fab_filter = findViewById(R.id.fab_filter);
        fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilter();
            }
        });

        FloatingActionButton fab_stats = findViewById(R.id.fab_stats);
        fab_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!date1.equals("") && !date2.equals(""))
                    openStats();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPayList(!filtered_bundle.isEmpty());
    }

    private void openInsertActivity() {
        Intent intent = new Intent(getApplication(), InsertActivity.class);
        startActivity(intent, null);
    }

    private void openDetails(Integer urno){
        Intent intent = new Intent(getApplication(), PayListDetail.class);
        intent.putExtra(DataManager.PAYTAB_ID, paylist.get(urno));
        startActivity(intent, null);
    }

    private void openFilter(){
        Intent intent = new Intent(getApplication(), FilterActivity.class);
        startActivityForResult(intent, FilterActivity.TAG);
    }

    private void openStats(){
        Intent intent = new Intent(getApplication(), StatsActivity.class);
        intent.putExtra(START_DATE, date1);
        intent.putExtra(END_DATE, date2);
        intent.putIntegerArrayListExtra(URNO_LIST, paylist);
        startActivity(intent, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FilterActivity.TAG) {
            if (resultCode == RESULT_OK) {
                filtered_bundle.putString(START_DATE, data.getStringExtra(START_DATE));
                filtered_bundle.putString(END_DATE, data.getStringExtra(END_DATE));
                filtered_bundle.putString(FILTER_TYPE, data.getStringExtra(FILTER_TYPE));
                filtered_bundle.putInt(FILTER_IORO, data.getIntExtra(FILTER_IORO, BOTH));
                filtered_bundle.putString(FILTER_DSCR, data.getStringExtra(FILTER_DSCR));
                getPayList(true);
            } else {
                Log.d("filterResult", "Filter Result: "+resultCode);
            }
        }
    }

    private void getListAtStart(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.ITALY);
        String today = formatter.format(calendar.getTime());
        String firstOfMonth = "";
        if(!today.substring(6).equals("01")) {
            firstOfMonth = today.substring(0, 6)+"01";
            filtered_bundle.putString(START_DATE, firstOfMonth);
            filtered_bundle.putString(END_DATE, today);
        }else{
            filtered_bundle.putString(START_DATE, today);
            filtered_bundle.putString(END_DATE, firstOfMonth);
        }
        filtered_bundle.putString(FILTER_TYPE, getResources().getString(R.string.empty_type));
        filtered_bundle.putInt(FILTER_IORO, BOTH);
        filtered_bundle.putString(FILTER_DSCR, "");
        getPayList(true);
    }

    private void getPayList(boolean filterd){
        paylist.clear();
        DataManager dataManager = new DataManager(this);
        Cursor c;
        if(filterd)
            c = dataManager.paytabSelect(filtered_bundle);
        else
            c = dataManager.paytabSelectAll();
        date1 = "";
        date2 = "";
        int idx = 1;
        while(c.moveToNext()){
            paylist.add(c.getInt(DataManager.PAYTAB_ID_IDX));
            if(idx == 1)
                date2 = getAppFormatDate(c.getString(DataManager.PAYTAB_DATE_IDX));
            if(idx == c.getCount())
                date1 = getAppFormatDate(c.getString(DataManager.PAYTAB_DATE_IDX));
            idx++;
        }
        c.close();
        String txt = getResources().getString(R.string.date_period)+" "+date1+" "+getResources().getString(R.string.dates_sep)+" "+date2;
        date_range.setText(txt);
        adapter.notifyDataSetChanged();
    }

}
