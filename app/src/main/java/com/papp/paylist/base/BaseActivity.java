package com.papp.paylist.base;

import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public static final int INCOME = 0;
    public static final int OUTFLOW = 1;
    public static final int BOTH = 2;
    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";
    public static final String FILTER_TYPE = "FILTER_TYPE";
    public static final String FILTER_IORO = "FILTER_IORO";
    public static final String FILTER_DSCR = "FILTER_DSCR";
    public static final String URNO_LIST = "URNO_LIST";

    public String getAppFormatDate(String date){
        return date.substring(6)+"/"+date.substring(4, 6)+"/"+date.substring(0, 4);
    }

    public String getDbFormatDate(String date){
        return date.substring(6)+date.substring(3, 5)+date.substring(0, 2);
    }

}
