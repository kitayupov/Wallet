package com.example.kitayupov.wallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.kitayupov.wallet.dto.TransDbHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private Map<String, Float> map;
    private TextView statistics;
    private boolean isProfit;
    private float total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        isProfit = getIntent().getBooleanExtra(MainActivity.IS_PROFIT, true);
        statistics = (TextView) findViewById(R.id.statistics);

        map = new HashMap<>();
        readDatabase();
    }

    private void readDatabase() {
        TransDbHelper dbHelper = new TransDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TransDbHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int amountIndex = cursor.getColumnIndex(MainActivity.AMOUNT);
            int typeIndex = cursor.getColumnIndex(MainActivity.TYPE);
            int isProfitIndex = cursor.getColumnIndex(MainActivity.IS_PROFIT);
            do {
                float amount = cursor.getFloat(amountIndex);
                String type = cursor.getString(typeIndex);
                boolean isProfit = cursor.getInt(isProfitIndex) == 1;
                if (this.isProfit == isProfit) {
                    Constants.addTypeAmount(map, type, amount);
                    total += amount;
                }
            } while (cursor.moveToNext());
            cursor.close();
            setTotal();
        }
    }

    private void setTotal() {
        StringBuilder builder = new StringBuilder();
        Map<String, Float> sortedMap = sortByValue(map);
        for (String type : sortedMap.keySet()) {
            builder
                    .append(String.format(Locale.ROOT, "%10s", type))
                    .append("\t")
                    .append(String.format(Locale.ROOT, "%10s", MainActivity.stringFormat(sortedMap.get(type))))
                    .append("\t")
                    .append(String.format(Locale.ROOT, "%10.2f%%", sortedMap.get(type) / total * 100))
                    .append("\n")
            ;
        }
        statistics.setText(builder.toString());
    }

    private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((Comparable<V>) ((Map.Entry<K, V>) (o2)).getValue()).compareTo(((Map.Entry<K, V>) (o1)).getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
