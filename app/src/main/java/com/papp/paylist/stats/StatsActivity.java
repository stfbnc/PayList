package com.papp.paylist.stats;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;
import com.papp.paylist.db.DataManager;

import java.util.ArrayList;
import java.util.Hashtable;

public class StatsActivity extends BaseActivity {

    private ArrayList<Integer> urnos;
    private String dbUrnosFormat;
    //private LineChart inoutChart;
    private BarChart inoutBar;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.stats_layout);

        Intent intent = getIntent();
        urnos = intent.getIntegerArrayListExtra(URNO_LIST);
        dbUrnosFormat = urnosListToDbFormat(urnos);
        TextView period = findViewById(R.id.date_range);
        String str = getResources().getString(R.string.date_period)+" "+
                intent.getStringExtra(START_DATE)+" "+
                getResources().getString(R.string.dates_sep)+" "+
                intent.getStringExtra(END_DATE);
        period.setText(str);
        TextView tot_in = findViewById(R.id.incomes);
        double in = getTotalIncomes();
        str = getResources().getString(R.string.total_incomes)+" "+String.valueOf(in)+" "+getResources().getString(R.string.euro_symbol);
        tot_in.setText(str);
        TextView tot_out = findViewById(R.id.outflows);
        double out = getTotalOutflows();
        str = getResources().getString(R.string.total_outflows)+" "+String.valueOf(out)+" "+getResources().getString(R.string.euro_symbol);
        tot_out.setText(str);
        TextView tot_diff = findViewById(R.id.difference);
        str = getResources().getString(R.string.total_difference)+" "+String.valueOf(in-out)+" "+getResources().getString(R.string.euro_symbol);
        tot_diff.setText(str);
        TextView max_in = findViewById(R.id.max_income);
        String[] euro_date = getMaxIncome();
        str = getResources().getString(R.string.greater_income)+" "+euro_date[0]+" "+getResources().getString(R.string.euro_symbol);
        if(!euro_date[0].equals("0.0"))
            str += " ("+getAppFormatDate(euro_date[1])+")";
        max_in.setText(str);
        TextView max_out = findViewById(R.id.max_outflow);
        euro_date = getMaxOutflow();
        str = getResources().getString(R.string.greater_outflow)+" "+euro_date[0]+" "+getResources().getString(R.string.euro_symbol);
        if(!euro_date[0].equals("0.0"))
            str += " ("+getAppFormatDate(euro_date[1])+")";
        max_out.setText(str);

        /*inoutChart = findViewById(R.id.inout_line);
        inoutChart.setDragEnabled(true);
        inoutChart.setScaleEnabled(true);

        ArrayList<Entry> incomes = getIncomes();
        LineDataSet inSet = new LineDataSet(incomes, "Incomes");
        inSet.setColor(getResources().getColor(R.color.green));
        inSet.setLineWidth(2f);

        ArrayList<Entry> outflows = getOutflows();
        LineDataSet outSet = new LineDataSet(outflows, "Outflows");
        outSet.setColor(getResources().getColor(R.color.red));
        outSet.setLineWidth(2f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(inSet);
        dataSets.add(outSet);
        LineData lineData = new LineData(dataSets);
        inoutChart.setData(lineData);
        inoutChart.getDescription().setEnabled(false);*/

        inoutBar = findViewById(R.id.inout_bars);
        inoutBar.setDragEnabled(true);
        inoutBar.setScaleEnabled(true);
        inoutBar.getAxisRight().setEnabled(false);
        inoutBar.getAxisLeft().setEnabled(true);

        ArrayList<BarEntry> barData = getInOutData();
        BarDataSet set = new BarDataSet(barData, "");
        set.setDrawIcons(false);
        set.setStackLabels(new String[]{"Outflows", "Incomes"});
        int[] colors = new int[]{getResources().getColor(R.color.red), getResources().getColor(R.color.green)};
        set.setColors(colors);
        BarData bars = new BarData(set);
        inoutBar.setData(bars);
        inoutBar.setFitBars(true);
        inoutBar.invalidate();
        inoutBar.getDescription().setText("Daily report");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String urnosListToDbFormat(ArrayList<Integer> list){
        String str = "";
        if(list.size() > 0){
            str += String.valueOf(list.get(0));
            for(int i = 1; i < list.size(); i++)
                str += ","+String.valueOf(list.get(i));
        }
        return str;
    }

    private double getTotalIncomes(){
        DataManager dm = new DataManager(this);
        return dm.paytabSelectTotal(dbUrnosFormat, INCOME);
    }

    private double getTotalOutflows(){
        DataManager dm = new DataManager(this);
        return dm.paytabSelectTotal(dbUrnosFormat, OUTFLOW);
    }

    private String[] getMaxIncome(){
        DataManager dm = new DataManager(this);
        return dm.paytabSelectMax(dbUrnosFormat, INCOME);
    }

    private String[] getMaxOutflow(){
        DataManager dm = new DataManager(this);
        return dm.paytabSelectMax(dbUrnosFormat, OUTFLOW);
    }

    /*private void getInOutByDate(){
        DataManager dm = new DataManager(this);
        Cursor c = dm.paytabSelectEuroGroupByDate(dbUrnosFormat);
        Hashtable<String, float[]> hashtable;
        while(c.moveToNext()){

        }
    }*/

    private ArrayList<BarEntry> getInOutData(){
        ArrayList<BarEntry> data = new ArrayList<>();
        data.add(new BarEntry(0, new float[]{12.0f, 1.0f}));
        data.add(new BarEntry(1, new float[]{16.0f, 6.0f}));
        data.add(new BarEntry(2, new float[]{1.5f, 0.5f}));
        data.add(new BarEntry(3, new float[]{11.5f, 1.5f}));
        data.add(new BarEntry(4, new float[]{1.6f, 16f}));
        data.add(new BarEntry(5, new float[]{125.7f, 12.7f}));
        data.add(new BarEntry(6, new float[]{6.3f, 9.0f}));
        data.add(new BarEntry(7, new float[]{0.28f, 0.3f}));
        data.add(new BarEntry(8, new float[]{9.4f, 0.2f}));
        data.add(new BarEntry(9, new float[]{5.1f, 3.0f}));
        return data;
    }

}
