package com.papp.paylist.main;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;
import com.papp.paylist.db.DataManager;
import com.papp.paylist.insert.InsertActivity;
import com.papp.paylist.insert.NewType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PayListDetail extends BaseActivity {

    private int clickedRadio = OUTFLOW;
    private LinearLayout radio_lin;
    private RadioButton income_radio, outflow_radio;
    private Spinner type_spinner;
    private ArrayList<String> spinner_list = new ArrayList<>();
    private ArrayAdapter<String> type_adapter;
    private FloatingActionButton fab_new_type;
    private SimpleDateFormat dateFormatter;
    private EditText insertDate, note, euro;
    private Context context;
    private int urno, oldIoro;
    private String oldDate, oldDscr, oldType;
    private double oldEuro;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.insert_layout);

        Bundle bndl = getIntent().getExtras();
        if(bndl != null)
            urno = bndl.getInt(DataManager.PAYTAB_ID);

        Button cancel_btn = findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button delete_button = findViewById(R.id.delete_btn);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord();
                finish();
            }
        });

        Button ok_button = findViewById(R.id.ok_btn);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupData();
                updateData();
                finish();
            }
        });

        Button modify_button = findViewById(R.id.modify_btn);
        modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableWidgets();
            }
        });

        type_spinner = findViewById(R.id.type_spinner);
        type_adapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, spinner_list);
        type_adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        type_spinner.setAdapter(type_adapter);
        setSpinnerItems();
        type_spinner.setEnabled(false);

        fab_new_type = findViewById(R.id.fab_new_type);
        fab_new_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewType newType = new NewType(context, type_spinner, spinner_list, type_adapter);
                newType.showDetails();
            }
        });
        fab_new_type.hide();

        euro = findViewById(R.id.money);
        euro.setEnabled(false);

        radio_lin = findViewById(R.id.row_2_1);
        radio_lin.setVisibility(View.GONE);

        income_radio = findViewById(R.id.radio_income);
        income_radio.setChecked(false);
        income_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedRadio = INCOME;
            }
        });

        outflow_radio = findViewById(R.id.radio_outflow);
        outflow_radio.setChecked(true);
        outflow_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedRadio = OUTFLOW;
            }
        });

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        insertDate = findViewById(R.id.data);
        insertDate.setInputType(InputType.TYPE_NULL);
        Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                insertDate.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        insertDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    datePickerDialog.show();
                v.clearFocus();
            }
        });
        insertDate.setEnabled(false);

        note = findViewById(R.id.descr);
        note.setEnabled(false);

        DataManager dm = new DataManager(this);
        Cursor c = dm.paytabSelectById(urno);
        while(c.moveToNext()){
            oldType = c.getString(DataManager.PAYTAB_TYPE_IDX);
            type_spinner.setSelection(getSpinnerListIdx(oldType));
            oldEuro = c.getDouble(DataManager.PAYTAB_EURO_IDX);
            euro.setText(String.valueOf(oldEuro));
            oldIoro = c.getInt(DataManager.PAYTAB_IORO_IDX);
            if(oldIoro == INCOME)
                euro.setTextColor(getResources().getColor(R.color.green));
            else if(oldIoro == OUTFLOW)
                euro.setTextColor(getResources().getColor(R.color.red));
            oldDate = c.getString(DataManager.PAYTAB_DATE_IDX);
            insertDate.setText(getAppFormatDate(oldDate));
            oldDscr = c.getString(DataManager.PAYTAB_DSCR_IDX);
            note.setText(oldDscr);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        income_radio.setChecked(clickedRadio == INCOME);
        outflow_radio.setChecked(clickedRadio == OUTFLOW);
    }

    private void setSpinnerItems(){
        DataManager dm = new DataManager(context);
        Cursor c = dm.typtabSelectTypes();
        while(c.moveToNext())
            spinner_list.add(c.getString(0));
        type_adapter.notifyDataSetChanged();
    }

    private int getSpinnerListIdx(String str){
        for(int i = 0; i < spinner_list.size(); i++){
            if(spinner_list.get(i).equals(str)){
                return i;
            }
        }
        return -1;
    }

    private void deleteRecord(){
        DataManager dm = new DataManager(context);
        dm.paytabDelete(urno);
    }

    private void enableWidgets(){
        type_spinner.setEnabled(true);
        fab_new_type.show();
        euro.setEnabled(true);
        euro.setTextColor(getResources().getColor(R.color.black));
        insertDate.setEnabled(true);
        note.setEnabled(true);
        radio_lin.setVisibility(View.VISIBLE);
    }

    private void updateData(){
        Bundle bndl = new Bundle();
        String et_date = insertDate.getText().toString();
        bndl.putString(DataManager.PAYTAB_DATE, getDbFormatDate(et_date));
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.ITALY);
        String date = df.format(Calendar.getInstance().getTime());
        bndl.putString(DataManager.PAYTAB_LSTU, date);
        bndl.putString(DataManager.PAYTAB_DSCR, note.getText().toString());
        bndl.putDouble(DataManager.PAYTAB_EURO, Double.valueOf(euro.getText().toString()));
        bndl.putString(DataManager.PAYTAB_TYPE, type_spinner.getSelectedItem().toString());
        bndl.putInt(DataManager.PAYTAB_IORO, clickedRadio);
        DataManager dm = new DataManager(context);
        dm.paytabUpdate(urno, bndl);
    }

    private void backupData(){
        Bundle bndl = new Bundle();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.ITALY);
        String date = df.format(Calendar.getInstance().getTime());
        bndl.putString(DataManager.UPDTAB_CDAT, date);
        bndl.putString(DataManager.UPDTAB_DATE, oldDate);
        bndl.putString(DataManager.UPDTAB_DSCR, oldDscr);
        bndl.putDouble(DataManager.UPDTAB_EURO, oldEuro);
        bndl.putString(DataManager.UPDTAB_TYPE, oldType);
        bndl.putInt(DataManager.UPDTAB_IORO, oldIoro);
        bndl.putInt(DataManager.UPDTAB_UPAY, urno);
        DataManager dm = new DataManager(context);
        dm.updtabInsert(bndl);
    }

}
