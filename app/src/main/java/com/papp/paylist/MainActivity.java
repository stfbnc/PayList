package com.papp.paylist;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.papp.paylist.db.DataManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView sep;
    private ArrayList<Integer> paylist;
    private PayListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DataManager dm = new DataManager(getApplication());

        EditText search_et = findViewById(R.id.search_box);
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPayList(s, dm);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        sep = findViewById(R.id.div);
        sep.setVisibility(View.GONE);

        RecyclerView rec_paylist = findViewById(R.id.recycle_paylist);
        rec_paylist.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rec_paylist.setLayoutManager(layoutManager);
        paylist = new ArrayList<>();
        adapter = new PayListAdapter(dm, paylist);
        rec_paylist.setAdapter(adapter);
        //rec_paylist.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
        //    @Override public void onItemClick(View view, int position) {
        //        openDetailsFragment(position, qList, true);
        //    }
        //}));

        FloatingActionButton fab_new = findViewById(R.id.fab_new);
        fab_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab_filter = findViewById(R.id.fab_filter);
        fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab_stats = findViewById(R.id.fab_stats);
        fab_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void openInsertActivity() {
        Intent intent = new Intent(getApplication(), InsertActivity.class);
        startActivityForResult(intent, InsertActivity.TAG);
    }


    public void getPayList(CharSequence typed, DataManager dataManager){
        String str = typed.toString().trim();
        if (str.length() == 0) {
            paylist.clear();
            sep.setVisibility(View.GONE);
        } else {
            if (paylist.size() > 0)
                paylist.clear();
            Cursor c = dataManager.dbSearchNames(str);
            while(c.moveToNext()) {
                paylist.add(c.getInt(0));
                Log.d("urno", "urno = "+c.getInt(0));
            }
            if(paylist.size() > 0)
                sep.setVisibility(View.VISIBLE);
            else
                sep.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }
}
