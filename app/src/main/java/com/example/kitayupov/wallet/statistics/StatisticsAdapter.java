package com.example.kitayupov.wallet.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kitayupov.wallet.MainActivity;
import com.example.kitayupov.wallet.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatisticsAdapter extends BaseAdapter {

    private Context context;
    private List<StatisticsItem> mArrayList;

    public StatisticsAdapter(Context context, List<StatisticsItem> mArrayList) {
        this.context = context;
        this.mArrayList = mArrayList;
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public StatisticsItem getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_stat_layout, null);
        }
        fillView(view, position);
        return view;
    }

    private void fillView(View view, int position) {
        StatisticsItem item = getItem(position);

        TextView typeText = (TextView) view.findViewById(R.id.type_text);
        TextView amountText = (TextView) view.findViewById(R.id.amount_text);
        TextView percentText = (TextView) view.findViewById(R.id.percent_text);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        typeText.setText(item.getType());
        amountText.setText(MainActivity.stringFormat(item.getAmount()));
        percentText.setText(String.format(Locale.ROOT, "%.2f", item.getPercent()));

        progressBar.setProgress((int) item.getPercent());
    }

    public void setData(List<StatisticsItem> data) {
        mArrayList = data;
    }
}
