package com.example.kitayupov.wallet.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kitayupov.wallet.Constants;
import com.example.kitayupov.wallet.MainActivity;
import com.example.kitayupov.wallet.R;
import com.example.kitayupov.wallet.dto.TransDbHelper;
import com.example.kitayupov.wallet.statistics.StatisticsAdapter;
import com.example.kitayupov.wallet.statistics.StatisticsItem;

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

public class StatisticsFragment extends AbstractTabFragment implements View.OnClickListener {
    private static final int LAYOUT = R.layout.fragment_statistics;

    private Context context;

    private Map<String, Float> map;
    private boolean isProfit;

    private long startDate;
    private long finishDate;

    private SelectionTime selectionTime;
    private SelectionType selectionType;

    public static final String START_DATE = "Statistics.StartDate";
    public static final String FINISH_DATE = "Statistics.FinishDate";

    private enum SelectionTime {TOTAL, YEAR, MONTH, CUSTOM}

    private enum SelectionType {BY_TYPE, BY_TIME;}

    public static StatisticsFragment getInstance(Context context, boolean isProfit) {
        Bundle args = new Bundle();
        StatisticsFragment fragment = new StatisticsFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(isProfit ? "Profit" : "Spend");
        fragment.setIsProfit(isProfit);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        initialize();
        setRadioButtonsListeners();
        readDatabase();

        return view;
    }

    // Initializes dates
    private void initialize() {

        startDate = 0;

        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR_OF_DAY, 23);
        current.set(Calendar.MINUTE, 59);
        current.set(Calendar.SECOND, 59);
        current.set(Calendar.MILLISECOND, 999);
        finishDate = current.getTimeInMillis();

        selectionTime = SelectionTime.TOTAL;
        selectionType = SelectionType.BY_TYPE;
    }

    // Sets radio button listeners
    private void setRadioButtonsListeners() {
        view.findViewById(R.id.total_radio_button).setOnClickListener(this);
        view.findViewById(R.id.year_radio_button).setOnClickListener(this);
        view.findViewById(R.id.month_radio_button).setOnClickListener(this);
        view.findViewById(R.id.custom_radio_button).setOnClickListener(this);

        view.findViewById(R.id.type_radio_button).setOnClickListener(this);
        view.findViewById(R.id.time_radio_button).setOnClickListener(this);
    }

    // Reads transaction database
    private void readDatabase() {
        float total = 0f;
        map = new HashMap<>();
        TransDbHelper dbHelper = new TransDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {MainActivity.AMOUNT, MainActivity.TYPE, MainActivity.DATE};
        String selection = MainActivity.IS_PROFIT + " = ? AND " + MainActivity.DATE + " >= ? AND " + MainActivity.DATE + " <= ?";
        String[] selectionArgs = {String.valueOf(isProfit ? 1 : 0), String.valueOf(startDate), String.valueOf(finishDate)};
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
        }
        setResult(total);
    }

    // Configures type map by dates
    private void addTimeAmount(long date, float amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        switch (selectionTime) {
            case TOTAL:
            case CUSTOM:
                Constants.addTypeAmount(map,
                        new SimpleDateFormat("dd MMM yyyy").format(date), amount);
                break;
            case YEAR:
                Constants.addTypeAmount(map, "01 - " +
                        calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + " " +
                        new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)], amount);
                break;
            case MONTH:
                Constants.addTypeAmount(map,
                        String.format(Locale.ROOT, "%02d %s", calendar.get(Calendar.DAY_OF_MONTH),
                                new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)]), amount);
                break;
        }
    }

    // Outputs result
    private void setResult(float total) {
        ArrayList<StatisticsItem> stats = new ArrayList<>();
        Map<String, Float> sortedMap = sortByValue(map);
        for (String type : sortedMap.keySet()) {
            stats.add(new StatisticsItem(type, sortedMap.get(type), sortedMap.get(type) / total * 100));
        }
        StatisticsAdapter adapter = new StatisticsAdapter(context, stats);
        ((ListView) view.findViewById(R.id.list_view)).setAdapter(adapter);
    }

    // Sorts type map by amount sum value
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        switch (view.getId()) {
            case R.id.total_radio_button:
                selectionTime = SelectionTime.TOTAL;
                startDate = 0;
                finishDate = System.currentTimeMillis();
                break;
            case R.id.year_radio_button:
                selectionTime = SelectionTime.YEAR;
                calendar.set(calendar.get(Calendar.YEAR), 0, 1);
                startDate = calendar.getTimeInMillis();
                finishDate = System.currentTimeMillis();
                break;
            case R.id.month_radio_button:
                selectionTime = SelectionTime.MONTH;
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
                startDate = calendar.getTimeInMillis();
                finishDate = System.currentTimeMillis();
                break;
            case R.id.custom_radio_button:
                selectionTime = SelectionTime.CUSTOM;
                DatePeriodDialogFragment dialogFragment = new DatePeriodDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(START_DATE, startDate);
                bundle.putLong(FINISH_DATE, finishDate);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getActivity().getFragmentManager(), "Dates");
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

    private void setContext(Context context) {
        this.context = context;
    }

    private void setIsProfit(boolean isProfit) {
        this.isProfit = isProfit;
    }

    public void setDatePeriod(Calendar cal1, Calendar cal2) {
        startDate = cal1.getTimeInMillis();
        finishDate = cal2.getTimeInMillis();
        if (startDate > finishDate) {
            long tmp = startDate;
            startDate = finishDate;
            finishDate = tmp;
        }
        readDatabase();
    }
}

