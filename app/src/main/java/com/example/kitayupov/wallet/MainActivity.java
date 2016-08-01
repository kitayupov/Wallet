package com.example.kitayupov.wallet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kitayupov.wallet.dto.TransAdapter;
import com.example.kitayupov.wallet.dto.TransDbHelper;
import com.example.kitayupov.wallet.dto.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int LAYOUT = R.layout.activity_main;

    public static final int REQUEST_CODE = 0;

    public static final String POSITION = "position";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "date";
    public static final String IS_PROFIT = "is_profit";
    private static final String LOG_TAG = "MainActivity";

    private ListView mListView;
    private ArrayList<Transaction> mArrayList;
    private TransAdapter mAdapter;
    private TransDbHelper dbHelper;
    private Context context;

    private TextView totalTextView;
    private TextView profitTextView;
    private TextView spendTextView;

    private float totalProfit;
    private float totalSpend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ActivityEditor.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }
        initialize();
    }

    private void initialize() {
        profitTextView = (TextView) findViewById(R.id.profit_text_view);
        spendTextView = (TextView) findViewById(R.id.spend_text_view);
        totalTextView = (TextView) findViewById(R.id.total_text_view);

        profitTextView.setOnClickListener(this);
        spendTextView.setOnClickListener(this);

        mArrayList = new ArrayList<>();
        dbHelper = new TransDbHelper(context);
        mAdapter = new TransAdapter(context, mArrayList);
        mListView = (ListView) findViewById(R.id.transaction_list_view);
        mListView.setAdapter(mAdapter);

        getTypeMaps();
        readDatabase();
        registerContextualActionBar();
    }

    private void getTypeMaps() {
        Constants.profitMap = new HashMap<>();
        Constants.spendMap = new HashMap<>();
        Constants.descriptionMap = new HashMap<>();
        for (String type : Settings.profitArray) {
            Constants.addType(true, type);
        }
        for (String type : Settings.spendArray) {
            Constants.addType(false, type);
        }
        for (String type : Settings.descriptions) {
            Constants.addDescription(type);
        }
    }

    private void readDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TransDbHelper.TABLE_NAME, null, null, null, null, null, DATE);
        if (cursor.moveToFirst()) {
            int amountIndex = cursor.getColumnIndex(AMOUNT);
            int typeIndex = cursor.getColumnIndex(TYPE);
            int descIndex = cursor.getColumnIndex(DESCRIPTION);
            int dateIndex = cursor.getColumnIndex(DATE);
            int isProfitIndex = cursor.getColumnIndex(IS_PROFIT);
            do {
                float amount = cursor.getFloat(amountIndex);
                String type = cursor.getString(typeIndex);
                String desc = cursor.getString(descIndex);
                Constants.addDescription(desc);
                long date = cursor.getLong(dateIndex);
                boolean isProfit = cursor.getInt(isProfitIndex) == 1;
                Constants.addType(isProfit, type);
                if (isProfit) {
                    totalProfit += amount;
                } else {
                    totalSpend += amount;
                }
                Transaction item = new Transaction(amount, type, desc, date, isProfit);
                mArrayList.add(item);
                Log.d(LOG_TAG, "read " + item.toString());
            } while (cursor.moveToNext());
            cursor.close();
            Log.d(LOG_TAG, "total read " + mArrayList.size());
            setTotal();
        }
    }

    private void registerContextualActionBar() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ActivityEditor.class);
                intent.putExtra(POSITION, position);
                intent.putExtra(Transaction.class.getCanonicalName(), mAdapter.getItem(position));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            ArrayList<Transaction> list = new ArrayList<>();

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                mode.setTitle(String.valueOf(mListView.getCheckedItemCount()));
                Transaction item = mAdapter.getItem(position);
                if (checked) {
                    list.add(item);
                } else {
                    list.remove(item);
                }
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.context_menu_delete:
                        deleteTransaction(list);
                        list = new ArrayList<>();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_contextual, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Здесь можно обновить явление, если CAB был удален. По умолчанию с выбранных элементов снимается выбор.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Здесь можно обновлять CAB при запросе invalidate()
                return false;
            }
        });
    }

    private void deleteTransaction(ArrayList<Transaction> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Transaction item : list) {
            String whereClause =
                    AMOUNT + "=? and " + TYPE + "=? and " +
                            DESCRIPTION + "=? and " + DATE + "=? and " + IS_PROFIT + "=?";
            String[] whereArgs = new String[]{
                    String.valueOf(item.getAmount()), item.getType(), item.getDescription(),
                    String.valueOf(item.getDate()), String.valueOf(item.isProfit() ? 1 : 0)};
            db.delete(TransDbHelper.TABLE_NAME, whereClause, whereArgs);
            mArrayList.remove(item);
            if (item.isProfit()) {
                totalProfit -= item.getAmount();
            } else {
                totalSpend -= item.getAmount();
            }
        }
        mAdapter.notifyDataSetChanged();
        setTotal();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    doneTransaction(data);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doneTransaction(Intent data) {
        if (data != null) {
            int position = data.getIntExtra(POSITION, Integer.MIN_VALUE);
            position = (position >= 0 && position < mArrayList.size()) ? position : mArrayList.size();
            Transaction item = data.getParcelableExtra(Transaction.class.getCanonicalName());

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AMOUNT, item.getAmount());
            values.put(TYPE, item.getType());
            values.put(DESCRIPTION, item.getDescription());
            values.put(DATE, item.getDate());
            values.put(IS_PROFIT, item.isProfit() ? 1 : 0);

            if (position == mArrayList.size()) {
                db.insert(TransDbHelper.TABLE_NAME, null, values);
                Log.d(LOG_TAG, "insert #" + position + item.toString());
            } else {
                updateRow(mArrayList.get(position), values);
                Log.d(LOG_TAG, "update #" + position + item.toString());
            }

            totalProfit += item.isProfit() ? item.getAmount() : 0;
            totalSpend += item.isProfit() ? 0 : item.getAmount();
            mArrayList.add(position, item);
            Collections.sort(mArrayList, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return (int) (t1.getDate() - t2.getDate());
                }
            });
            mAdapter.notifyDataSetChanged();
            setTotal();
        }
    }

    private void updateRow(Transaction item, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause =
                AMOUNT + "=? and " + TYPE + "=? and " +
                        DATE + "=? and " + IS_PROFIT + "=?";
        String[] whereArgs = new String[]{
                String.valueOf(item.getAmount()), item.getType(),
                String.valueOf(item.getDate()), String.valueOf(item.isProfit() ? 1 : 0)};
        db.update(TransDbHelper.TABLE_NAME, values, whereClause, whereArgs);
        mArrayList.remove(item);
        totalProfit -= item.isProfit() ? item.getAmount() : 0;
        totalSpend -= item.isProfit() ? 0 : item.getAmount();
    }

    private void setTotal() {
        profitTextView.setText(stringFormat(totalProfit));
        spendTextView.setText(stringFormat(totalSpend));
        totalTextView.setText(stringFormat(totalProfit - totalSpend));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("delete from " + TransDbHelper.TABLE_NAME);
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();
                totalProfit = totalSpend = 0;
                setTotal();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String stringFormat(float f) {
        if (f == (long) f) {
            return String.format(Locale.ROOT, "%d", (long) f);
        } else {
            return String.format("%s", f);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        switch (view.getId()) {
            case R.id.profit_text_view:
                intent.putExtra(IS_PROFIT, true);
                startActivity(intent);
                break;
            case R.id.spend_text_view:
                intent.putExtra(IS_PROFIT, false);
                startActivity(intent);
                break;
        }
    }
}
