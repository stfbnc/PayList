package com.papp.paylist.stats;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.papp.paylist.R;
import com.papp.paylist.base.BaseActivity;

public class StatsActivity extends BaseActivity {

    private TextView period, tot_in, tot_out, tot_diff;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.stats_layout);

        //final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        period = findViewById(R.id.date_range);
        period.setText(getResources().getString(R.string.date_period));

        tot_in = findViewById(R.id.incomes);
        tot_in.setText("100");

        tot_out = findViewById(R.id.outflows);
        tot_out.setText("200");

        tot_diff = findViewById(R.id.difference);
        tot_diff.setText("-100");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
