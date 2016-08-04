package com.example.kitayupov.wallet.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kitayupov.wallet.MainActivity;
import com.example.kitayupov.wallet.R;
import com.example.kitayupov.wallet.dto.Transaction;

import java.util.List;

public class TransAdapter extends BaseAdapter {

    private Context context;
    private List<Transaction> mArrayList;

    public TransAdapter(Context context, List<Transaction> mArrayList) {
        this.context = context;
        this.mArrayList = mArrayList;
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Transaction getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_history_layout, null);
        }
        fillView(view, position);
        return view;
    }

    private void fillView(View view, int position) {
        Transaction item = getItem(position);

        TextView amountText = (TextView) view.findViewById(R.id.amount_text_view);
        TextView typeText = (TextView) view.findViewById(R.id.type_text_view);
        TextView dateText = (TextView) view.findViewById(R.id.date_text_view);

        amountText.setText(MainActivity.stringFormat(item.getAmount()));
        typeText.setText(item.getType());
        dateText.setText(DateFormat.format("dd.MM.yyyy", item.getDate()));

        if (item.isProfit()) {
            amountText.setTextColor(context.getResources().getColor(R.color.colorProfit));
        } else {
            amountText.setTextColor(context.getResources().getColor(R.color.colorSpend));
        }
    }

    public void setData(List<Transaction> data) {
        mArrayList = data;
    }
}
