package com.example.kitayupov.wallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.kitayupov.wallet.dto.TransDbHelper;

import java.util.Calendar;
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
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initialize();
        readDatabase(0);
    }

    private void initialize() {
        isProfit = getIntent().getBooleanExtra(MainActivity.IS_PROFIT, true);
        statistics = (TextView) findViewById(R.id.statistics);
        calendar = Calendar.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long startDate;
        switch (item.getItemId()) {
            case R.id.menu_total:
                startDate = 0;
                break;
            case R.id.menu_year:
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                startDate = calendar.getTimeInMillis();
                break;
            case R.id.menu_month:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = calendar.getTimeInMillis();
                break;
            case R.id.menu_week:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = calendar.getTimeInMillis();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        readDatabase(startDate);
        return true;
    }

    private void readDatabase(long startDate) {
        float total = 0f;
        map = new HashMap<>();
        TransDbHelper dbHelper = new TransDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TransDbHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int amountIndex = cursor.getColumnIndex(MainActivity.AMOUNT);
            int typeIndex = cursor.getColumnIndex(MainActivity.TYPE);
            int dateIndex = cursor.getColumnIndex(MainActivity.DATE);
            int isProfitIndex = cursor.getColumnIndex(MainActivity.IS_PROFIT);
            do {
                float amount = cursor.getFloat(amountIndex);
                String type = cursor.getString(typeIndex);
                long date = cursor.getLong(dateIndex);
                boolean isProfit = cursor.getInt(isProfitIndex) == 1;
                if (this.isProfit == isProfit && date > startDate) {
                    Constants.addTypeAmount(map, type, amount);
                    total += amount;
                }
            } while (cursor.moveToNext());
            cursor.close();
            setResult(total);
        }
    }

    private void setResult(float total) {
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
