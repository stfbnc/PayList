package com.papp.paylist.stats;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;
import com.papp.paylist.db.DataManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;

public class StatsActivity extends BaseActivity {

    private String dbUrnosFormat;
    private ArrayList<String> dayList = new ArrayList<>();
    private ArrayList<String> monthList;
    private ArrayList<String> yearList;
    private ArrayList<Entry> incomes;
    private ArrayList<Entry> expenses;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.stats_layout);

        Intent intent = getIntent();
        ArrayList<Integer> urnos = intent.getIntegerArrayListExtra(URNO_LIST);
        dbUrnosFormat = urnosListToDbFormat(urnos);
        TextView period = findViewById(R.id.date_range);
        String date1 = intent.getStringExtra(START_DATE);
        String date2 = intent.getStringExtra(END_DATE);
        String str = getResources().getString(R.string.date_period)+" "+date1+" "+getResources().getString(R.string.dates_sep)+" "+date2;
        period.setText(str);
        TextView tot_in = findViewById(R.id.incomes);
        double in = getTotalIncomes();
        str = getResources().getString(R.string.total_incomes)+" "+String.valueOf(in)+" "+getResources().getString(R.string.euro_symbol);
        tot_in.setText(str);
        TextView tot_out = findViewById(R.id.expenses);
        double out = getTotalExpenses();
        str = getResources().getString(R.string.total_expenses)+" "+String.valueOf(out)+" "+getResources().getString(R.string.euro_symbol);
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
        TextView max_out = findViewById(R.id.max_expense);
        euro_date = getMaxExpense();
        str = getResources().getString(R.string.greater_expense)+" "+euro_date[0]+" "+getResources().getString(R.string.euro_symbol);
        if(!euro_date[0].equals("0.0"))
            str += " ("+getAppFormatDate(euro_date[1])+")";
        max_out.setText(str);

        if(getDatesList(date1, date2)) {
            getDailyInExp();
            getLineChart(R.id.daily_chart, dayList);
            if(monthList.size() > 1) {
                //getMonthlyInExp();
                getLineChart(R.id.monthly_chart, monthList);
            }else{
                findViewById(R.id.monthly_chart).setVisibility(View.GONE);
            }
            if(yearList.size() > 1) {
                //getYearlyInExp();
                getLineChart(R.id.yearly_chart, yearList);
            }else{
                findViewById(R.id.yearly_chart).setVisibility(View.GONE);
            }
        }
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

    private boolean getDatesList(String d1, String d2){
        try {
            SimpleDateFormat sdf_day = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
            SimpleDateFormat sdf_month = new SimpleDateFormat("MM/yyyy", Locale.ITALY);
            SimpleDateFormat sdf_year = new SimpleDateFormat("yyyy", Locale.ITALY);
            Date date1 = sdf_day.parse(d1);
            Date date2 = sdf_day.parse(d2);
            Calendar start = Calendar.getInstance();
            start.setTime(date1);
            Calendar end = Calendar.getInstance();
            end.setTime(date2);
            end.add(Calendar.DAY_OF_YEAR, 1);
            ArrayList<String> m = new ArrayList<>();
            ArrayList<String> y = new ArrayList<>();
            while (start.before(end)) {
                dayList.add(sdf_day.format(start.getTime()));
                m.add(sdf_month.format(start.getTime()));
                y.add(sdf_year.format(start.getTime()));
                start.add(Calendar.DAY_OF_YEAR, 1);
            }
            monthList = new ArrayList<>(new HashSet<>(m));
            yearList = new ArrayList<>(new HashSet<>(y));
            return true;
        }catch (ParseException pe){
            return false;
        }
    }

    private double getTotalIncomes(){
        DataManager dm = new DataManager(this);
        return dm.paytabSelectTotal(dbUrnosFormat, INCOME);
    }

    private double getTotalExpenses(){
        DataManager dm = new DataManager(this);
        return dm.paytabSelectTotal(dbUrnosFormat, EXPENSE);
    }

    private String[] getMaxIncome(){
        DataManager dm = new DataManager(this);
        return dm.paytabSelectMax(dbUrnosFormat, INCOME);
    }

    private String[] getMaxExpense(){
        DataManager dm = new DataManager(this);
        return dm.paytabSelectMax(dbUrnosFormat, EXPENSE);
    }

    private void getDailyInExp(){
        Hashtable<String, Entry> in = new Hashtable<>();
        Hashtable<String, Entry> out = new Hashtable<>();
        for(int i = 0; i < dayList.size(); i++){
            in.put(dayList.get(i), new Entry(i, 0.0f));
            out.put(dayList.get(i), new Entry(i, 0.0f));
        }
        DataManager dm = new DataManager(this);
        Cursor c = dm.paytabSelectEuroGroupByDate(dbUrnosFormat);
        while(c.moveToNext()){
            if (c.getInt(1) == INCOME) {
                Entry entry = in.get(getAppFormatDate(c.getString(0)));
                entry.setY((float)c.getDouble(2));
            } else if (c.getInt(1) == EXPENSE) {
                Entry entry = out.get(getAppFormatDate(c.getString(0)));
                entry.setY((float)c.getDouble(2));
            }
        }
        c.close();
        incomes = getSortedEntries(in.values());
        expenses = getSortedEntries(out.values());
    }

    private ArrayList<Entry> getSortedEntries(Collection<Entry> entries){
        ArrayList<Entry> arr = new ArrayList<>();
        for(int i = 0; i < entries.size(); i++) {
            for (Entry entry : entries) {
                if ((int) entry.getX() == i)
                    arr.add(entry);
            }
        }
        return arr;
    }

    private ArrayList<BarEntry> getDiffEntries(ArrayList<Entry> in, ArrayList<Entry> out){
        ArrayList<BarEntry> arr = new ArrayList<>();
        for(int i = 0; i < in.size(); i++)
            arr.add(new BarEntry(i, in.get(i).getY()-out.get(i).getY()));
        return arr;
    }

    private ArrayList<Entry> getMonthlyInExp(int in_out){
        ArrayList<Entry> arr = new ArrayList<>();
        ArrayList<Entry> mainArr;
        if(in_out == INCOME)
            mainArr = incomes;
        else if(in_out == EXPENSE)
            mainArr = expenses;

        return arr;
    }

    private void getLineChart(int chartView, ArrayList<String> list){
        LinearLayout ll = findViewById(chartView);
        CombinedChart combChart = ll.findViewById(R.id.inout_comb);
        combChart.getDescription().setEnabled(false);
        combChart.setDragEnabled(true);
        combChart.setScaleEnabled(true);
        combChart.getAxisRight().setEnabled(false);
        combChart.getAxisLeft().setEnabled(true);
        XAxis x = combChart.getXAxis();
        x.setGranularity(1f);
        x.setLabelRotationAngle(-90);
        x.setValueFormatter(new IndexAxisValueFormatter(list));
        x.setPosition(XAxis.XAxisPosition.BOTTOM);

        //legend
        Legend l = combChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        //Line Data
        LineDataSet inSet = new LineDataSet(incomes, getResources().getString(R.string.incomes)+" ("+getResources().getString(R.string.euro_symbol)+")");
        inSet.setColor(getResources().getColor(R.color.green));
        inSet.setLineWidth(2f);
        inSet.setDrawCircles(true);
        inSet.setCircleColor(getResources().getColor(R.color.green));
        inSet.setDrawValues(false);
        LineDataSet outSet = new LineDataSet(expenses, getResources().getString(R.string.expenses)+" ("+getResources().getString(R.string.euro_symbol)+")");
        outSet.setColor(getResources().getColor(R.color.red));
        outSet.setLineWidth(2f);
        outSet.setDrawCircles(true);
        outSet.setCircleColor(getResources().getColor(R.color.red));
        outSet.setDrawValues(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(inSet);
        dataSets.add(outSet);
        LineData lineData = new LineData(dataSets);

        //bar data
        BarDataSet barSet = new BarDataSet(getDiffEntries(incomes, expenses), getResources().getString(R.string.net)+" ("+getResources().getString(R.string.euro_symbol)+")");
        barSet.setDrawIcons(false);
        barSet.setColor(getResources().getColor(R.color.fade));
        BarData barData = new BarData(barSet);

        //set data
        CombinedData data = new CombinedData();
        data.setData(lineData);
        data.setData(barData);
        combChart.setData(data);
        combChart.invalidate();
    }

}
