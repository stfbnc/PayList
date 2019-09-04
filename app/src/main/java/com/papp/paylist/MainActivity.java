package com.papp.paylist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private TextView sep;
    private ArrayList<String> paylist;
    private PayListAdapter adapter;
    public static Hashtable<String, Double> dataMap = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText search_et = findViewById(R.id.search_box);
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPayList(s);
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
        adapter = new PayListAdapter(paylist);
        rec_paylist.setAdapter(adapter);
        //rec_paylist.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
        //    @Override public void onItemClick(View view, int position) {
        //        openDetailsFragment(position, qList, true);
        //    }
        //}));

        setDataMap();
    }

    public void setDataMap(){
        dataMap.put("pippo", 2.30);
        dataMap.put("buiin", 4.32);
        dataMap.put("diwehu", 5887.0);
        dataMap.put("oduy", 58.25);
        dataMap.put("oduyiuu", 57.25);
    }

    public void getPayList(CharSequence typed){
        String str = typed.toString().trim();
        if (str.length() == 0) {
            paylist.clear();
            sep.setVisibility(View.GONE);
        } else {
            if (paylist.size() > 0)
                paylist.clear();
            Enumeration<String> enu = dataMap.keys();
            if(enu != null) {
                while (enu.hasMoreElements()) {
                    String nm = enu.nextElement().trim();
                    if(nm.matches("^"+str+".*"))
                        paylist.add(nm);
                }
            }
            if(paylist.size() > 0)
                sep.setVisibility(View.VISIBLE);
            else
                sep.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }
}
