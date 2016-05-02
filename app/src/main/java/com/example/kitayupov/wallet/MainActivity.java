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
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 0;
    public static final String POSITION = "position";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
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
    private float total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        mArrayList = new ArrayList<>();
        dbHelper = new TransDbHelper(context);
        mAdapter = new TransAdapter(context, mArrayList);
        mListView = (ListView) findViewById(R.id.transaction_list_view);
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
            readDatabase();
            registerContextualActionBar();
        }
    }

    private void readDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TransDbHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
//            int id = cursor.getColumnIndex(BaseColumns._ID);
            int amountIndex = cursor.getColumnIndex(AMOUNT);
            int typeIndex = cursor.getColumnIndex(TYPE);
            int dateIndex = cursor.getColumnIndex(DATE);
            int isProfitIndex = cursor.getColumnIndex(IS_PROFIT);
            do {
                float amount = cursor.getFloat(amountIndex);
                String type = cursor.getString(typeIndex);
                long date = cursor.getLong(dateIndex);
                boolean isProfit = cursor.getInt(isProfitIndex) == 1;
                if (isProfit) {
                    totalProfit += amount;
                } else {
                    totalSpend += amount;
                }
                Transaction item = new Transaction(amount, type, date, isProfit);
                mArrayList.add(item);
                Log.d(LOG_TAG, "readed " + item.toString());
            } while (cursor.moveToNext());
            cursor.close();
            Log.d(LOG_TAG, "total readed " + mArrayList.size());
            setTotal();
        }
    }

    private void registerContextualActionBar() {
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            ArrayList<Transaction> list = new ArrayList<Transaction>();

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
                Log.d(LOG_TAG, (checked ? "added " : "removed ") + item.toString());
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.context_menu_delete:
                        deleteTransaction(list);
                        list = new ArrayList<Transaction>();
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
        Log.d(LOG_TAG, "" + Arrays.asList(list).toString());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Transaction item : list) {
            //Transaction item = (Transaction) mListView.getItemAtPosition(i);/* mArrayList.get(i);*/
//            db.delete(TransDbHelper.TABLE_NAME, AMOUNT + "=?", new String[]{String.valueOf(item.getAmount())});
//            db.delete(TransDbHelper.TABLE_NAME, BaseColumns._ID + "=?", new String[]{String.valueOf(i)});
            String whereClause =
                    AMOUNT + "=? and " + TYPE + "=? and " + DATE + "=? and " + IS_PROFIT + "=?";
            String[] whereArgs = new String[]{
                    String.valueOf(item.getAmount()), item.getType(),
                    String.valueOf(item.getDate()), String.valueOf(item.isProfit() ? 1 : 0)};
            db.delete(TransDbHelper.TABLE_NAME, whereClause, whereArgs);
            mArrayList.remove(item);
            if (item.isProfit()) {
                totalProfit -= item.getAmount();
            } else {
                totalSpend -= item.getAmount();
            }
            Log.d(LOG_TAG, "deleted " + item.toString());
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
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doneTransaction(Intent data) {
        if (data != null) {
            int position = data.getIntExtra(POSITION, Integer.MIN_VALUE);
            position = (position >= 0 && position < mArrayList.size()) ? position : mArrayList.size();
            Transaction item = data.getParcelableExtra(Transaction.class.getCanonicalName());
            Log.d(LOG_TAG, item.toString());
            totalProfit += item.isProfit() ? item.getAmount() : 0;
            totalSpend += item.isProfit() ? 0 : item.getAmount();
            mArrayList.add(position, item);
            mAdapter.notifyDataSetChanged();
            setTotal();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AMOUNT, item.getAmount());
            values.put(TYPE, item.getType());
            values.put(DATE, item.getDate());
            values.put(IS_PROFIT, item.isProfit());
            if (position == mArrayList.size() - 1) {
                db.insert(TransDbHelper.TABLE_NAME, null, values);
                Log.d(LOG_TAG, "insert " + item.toString());
            } else {
                Log.d(LOG_TAG, "update " + item.toString());
            }
            //fixme example
//            } else {
////                db.update(NoteDBHelper.TABLE_NAME, values, BaseColumns._ID + "= ?", new String[] );
//                db.insert(NoteDBHelper.TABLE_NAME, null, values);
//                Log.d(LOG_TAG, "edited: " + item.toString());
//                /*String whereClause = BODY + "=? and " + TYPE + "=? and " + DATE + "=? and " +
//                        RATING + "=? and " + IS_DONE + "=?";
//                String[] whereArgs = new String[]{
//                        item.getBody(), item.getType(), String.valueOf(item.getDate()),
//                        String.valueOf(item.getRating()), String.valueOf(item.getIsDone())};
//                db.delete(NoteDBHelper.TABLE_NAME, whereClause, whereArgs);*/
//            }

        }
    }

    private void setTotal() {
        total = totalProfit - totalSpend;
        profitTextView.setText(String.valueOf(totalProfit));
        spendTextView.setText(String.valueOf(totalSpend));
        totalTextView.setText(String.valueOf(total));
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
//                deleteTransaction(mArrayList);
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

//    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
//
//        // Вызывается при создании контекстного режима
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//
//            Log.d(LOG_TAG, "CAB!!!");
//
//            // Заполняем меню
//            MenuInflater inflater = mode.getMenuInflater();
//            inflater.inflate(R.menu.menu_contextual, menu);
//            return true;
//        }
//
//        // Вызывается при каждом отображении контекстного режима. Всегда вызывается после onCreateActionMode, но
//        // может быть вызван несколько раз
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return false; // возвращаем false, если ничего не сделано
//        }
//
//        // Вызывается при выборе действия контекстной панели
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.context_menu_delete:
//                    Log.d(LOG_TAG, "context delete");
//                    mode.finish(); // Action picked, so close the CAB
//                    return true;
//                default:
//                    return false;
//            }
//        }
//
//        // Вызывается при выходе из контекстного режима
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
////            mActionMode = null;
//        }
//    };
}
