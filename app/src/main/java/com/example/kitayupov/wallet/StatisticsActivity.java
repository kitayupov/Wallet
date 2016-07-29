package com.example.kitayupov.wallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kitayupov.wallet.dto.TransDbHelper;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {

    private Map<String, Float> map;
    private TextView statistics;
    private boolean isProfit;
    private Calendar calendar;
    private long startDate = 0;
    private static final long dateToday = System.currentTimeMillis();

    private SelectionTime selectionTime = SelectionTime.TOTAL;
    private SelectionType selectionType = SelectionType.BY_TYPE;

    private enum SelectionTime {TOTAL, YEAR, MONTH, WEEK}

    private enum SelectionType {BY_TYPE, BY_TIME}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initialize();
        readDatabase();
        setRadioButtonsListeners();
    }

    private void initialize() {
        isProfit = getIntent().getBooleanExtra(MainActivity.IS_PROFIT, true);
        calendar = Calendar.getInstance();
    }

    private void setRadioButtonsListeners() {
        findViewById(R.id.total_radio_button).setOnClickListener(this);
        findViewById(R.id.year_radio_button).setOnClickListener(this);
        findViewById(R.id.month_radio_button).setOnClickListener(this);

        findViewById(R.id.type_radio_button).setOnClickListener(this);
        findViewById(R.id.time_radio_button).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        readDatabase();
        return true;
    }

    private void readDatabase() {
        float total = 0f;
        map = new HashMap<>();
        TransDbHelper dbHelper = new TransDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {MainActivity.AMOUNT, MainActivity.TYPE, MainActivity.DATE};
        String selection = MainActivity.IS_PROFIT + " = ? AND " + MainActivity.DATE + " >= ? AND " + MainActivity.DATE + " <= ?";
        String[] selectionArgs = {String.valueOf(isProfit ? 1 : 0), String.valueOf(startDate), String.valueOf(dateToday)};
        Cursor cursor = db.query(TransDbHelper.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int amountIndex = cursor.getColumnIndex(MainActivity.AMOUNT);
            int typeIndex = cursor.getColumnIndex(MainActivity.TYPE);
            int dateIndex = cursor.getColumnIndex(MainActivity.DATE);
            do {
                float amount = cursor.getFloat(amountIndex);
                String type = cursor.getString(typeIndex);
                long date = cursor.getLong(dateIndex);
                if (selectionType.equals(SelectionType.BY_TYPE)) {
                    Constants.addTypeAmount(map, type, amount);
                } else {
                    addTimeAmount(date, amount);
                }
                total += amount;
            } while (cursor.moveToNext());
            cursor.close();
            setResult(total);
        }
    }

    private void addTimeAmount(long date, float amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        switch (selectionTime) {
            case TOTAL:
                Constants.addTypeAmount(map,
                        new SimpleDateFormat("dd MMMM yyyy", Locale.ROOT).format(date), amount);
                break;
            case YEAR:
                Constants.addTypeAmount(map,
                        new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)], amount);
                break;
            case MONTH:
                Constants.addTypeAmount(map,
                        String.format(Locale.ROOT, "%02d %s", calendar.get(Calendar.DAY_OF_MONTH),
                                new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)]), amount);
                break;
            case WEEK:
                Constants.addTypeAmount(map,
                        new DateFormatSymbols().getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)], amount);
                break;
        }
    }

    private void setResult(float total) {
        ArrayList<String> records = new ArrayList<>();
        Map<String, Float> sortedMap = sortByValue(map);
        for (String type : sortedMap.keySet()) {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format(Locale.ROOT, "%10s", type))
                    .append("\t")
                    .append(String.format(Locale.ROOT, "%10s", MainActivity.stringFormat(sortedMap.get(type))))
                    .append("\t")
                    .append(String.format(Locale.ROOT, "%10.2f%%", sortedMap.get(type) / total * 100));

            records.add(builder.toString());
        }
        ((ListView) findViewById(R.id.list_view)).setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, records));
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

    @Override
    public void onClick(View view) {
        calendar.setTimeInMillis(dateToday);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        switch (view.getId()) {
            case R.id.total_radio_button:
                selectionTime = SelectionTime.TOTAL;
                startDate = 0;
                break;
            case R.id.year_radio_button:
                selectionTime = SelectionTime.YEAR;
                calendar.set(calendar.get(Calendar.YEAR), 0, 0);
                startDate = calendar.getTimeInMillis();
                break;
            case R.id.month_radio_button:
                selectionTime = SelectionTime.MONTH;
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 0);
                startDate = calendar.getTimeInMillis();
                break;
            case R.id.time_radio_button:
                selectionType = SelectionType.BY_TIME;
                break;
            case R.id.type_radio_button:
                selectionType = SelectionType.BY_TYPE;
                break;
        }
        readDatabase();
    }
}
