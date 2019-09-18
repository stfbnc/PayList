package com.papp.paylist.insert;

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
import android.widget.RadioButton;
import android.widget.Spinner;

import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;
import com.papp.paylist.db.DataManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class InsertActivity extends BaseActivity {

    private int clickedRadio = OUTFLOW;
    private RadioButton income_radio, outflow_radio;
    private Spinner type_spinner;
    private ArrayList<String> spinner_list = new ArrayList<>();
    private ArrayAdapter<String> type_adapter;
    private SimpleDateFormat dateFormatter;
    private EditText insertDate, note, euro;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.insert_layout);

        Button cancel_btn = findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button ok_button = findViewById(R.id.ok_btn);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDb();
                finish();
            }
        });

        Button modify_button = findViewById(R.id.modify_btn);
        modify_button.setVisibility(View.GONE);

        Button delete_button = findViewById(R.id.delete_btn);
        delete_button.setVisibility(View.GONE);

        type_spinner = findViewById(R.id.type_spinner);
        type_adapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, spinner_list);
        type_adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        type_spinner.setAdapter(type_adapter);
        setSpinnerItems();

        FloatingActionButton fab_new_type = findViewById(R.id.fab_new_type);
        fab_new_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewType newType = new NewType(context, type_spinner, spinner_list, type_adapter);
                newType.showDetails();
            }
        });

        euro = findViewById(R.id.money);

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

        note = findViewById(R.id.descr);
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

    private void saveDataToDb(){
        Bundle bndl = new Bundle();
        String et_date = insertDate.getText().toString();
        bndl.putString(DataManager.PAYTAB_DATE, getDbFormatDate(et_date));
        bndl.putString(DataManager.PAYTAB_DSCR, note.getText().toString());
        bndl.putDouble(DataManager.PAYTAB_EURO, Double.valueOf(euro.getText().toString()));
        bndl.putString(DataManager.PAYTAB_TYPE, type_spinner.getSelectedItem().toString());
        bndl.putInt(DataManager.PAYTAB_IORO, clickedRadio);
        DataManager dm = new DataManager(context);
        dm.paytabInsert(bndl);
    }

}
