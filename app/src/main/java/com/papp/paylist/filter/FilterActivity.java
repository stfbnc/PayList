package com.papp.paylist.filter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;
import com.papp.paylist.db.DataManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FilterActivity extends BaseActivity {

    public static final int TAG = 12;
    private int clickedRadio = BOTH;
    private RadioButton income_radio, outflow_radio, both_radio;
    private Spinner type_spinner;
    private ArrayList<String> spinner_list = new ArrayList<>();
    private ArrayAdapter<String> type_adapter;
    private EditText startDate, endDate, descr;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.filter_layout);

        Button cancel_btn = findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button filter_button = findViewById(R.id.filter_btn);
        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultData();
                finish();
            }
        });

        type_spinner = findViewById(R.id.type_spinner);
        type_adapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, spinner_list);
        type_adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        type_spinner.setAdapter(type_adapter);
        setSpinnerItems();

        income_radio = findViewById(R.id.radio_income);
        income_radio.setChecked(false);
        income_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedRadio = INCOME;
            }
        });

        outflow_radio = findViewById(R.id.radio_outflow);
        outflow_radio.setChecked(false);
        outflow_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedRadio = OUTFLOW;
            }
        });

        both_radio = findViewById(R.id.radio_both);
        both_radio.setChecked(true);
        both_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedRadio = BOTH;
            }
        });

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        startDate = findViewById(R.id.data_start);
        startDate.setInputType(InputType.TYPE_NULL);
        startDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!startDate.getText().toString().isEmpty())
                    endDate.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        endDate = findViewById(R.id.data_end);
        endDate.setInputType(InputType.TYPE_NULL);
        endDate.setEnabled(false);

        Calendar startCalendar = Calendar.getInstance();
        final Calendar newDate = Calendar.getInstance();
        final DatePickerDialog startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate.set(year, monthOfYear, dayOfMonth);
                startDate.setTextColor(getResources().getColor(R.color.black));
                startDate.setText(dateFormatter.format(newDate.getTime()));
            }
        },startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH));
        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    startDatePickerDialog.show();
                v.clearFocus();
            }
        });

        Calendar endCalendar = Calendar.getInstance();
        final DatePickerDialog endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate2 = Calendar.getInstance();
                newDate2.set(year, monthOfYear, dayOfMonth);
                endDate.setTextColor(getResources().getColor(R.color.black));
                endDate.setText(dateFormatter.format(newDate2.getTime()));
            }
        },endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH));
        endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    newDate.add(Calendar.DAY_OF_MONTH, 1);
                    endDatePickerDialog.getDatePicker().setMinDate(newDate.getTimeInMillis());
                    endDatePickerDialog.show();
                }
                v.clearFocus();
            }
        });

        descr = findViewById(R.id.descr);
    }

    @Override
    public void onResume() {
        super.onResume();
        income_radio.setChecked(clickedRadio == INCOME);
        outflow_radio.setChecked(clickedRadio == OUTFLOW);
        both_radio.setChecked(clickedRadio == BOTH);
    }

    private void setSpinnerItems(){
        spinner_list.add(getResources().getString(R.string.empty_type));
        DataManager dm = new DataManager(context);
        Cursor c = dm.typtabSelectTypes();
        while(c.moveToNext())
            spinner_list.add(c.getString(0));
        type_adapter.notifyDataSetChanged();
    }

    private void setResultData(){
        Intent returnIntent = new Intent();
        if(startDate.getText().toString().equals(getResources().getString(R.string.start_date)))
            returnIntent.putExtra(START_DATE, "");
        else
            returnIntent.putExtra(START_DATE, getDbFormatDate(startDate.getText().toString()));
        if(endDate.getText().toString().equals(getResources().getString(R.string.end_date)))
            returnIntent.putExtra(END_DATE, "");
        else
            returnIntent.putExtra(END_DATE, getDbFormatDate(endDate.getText().toString()));
        returnIntent.putExtra(FILTER_TYPE, type_spinner.getSelectedItem().toString());
        returnIntent.putExtra(FILTER_IORO, clickedRadio);
        returnIntent.putExtra(FILTER_DSCR, descr.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
    }

}
