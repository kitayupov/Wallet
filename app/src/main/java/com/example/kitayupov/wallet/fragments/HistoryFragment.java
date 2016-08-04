package com.example.kitayupov.wallet.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kitayupov.wallet.Constants;
import com.example.kitayupov.wallet.EditorActivity;
import com.example.kitayupov.wallet.MainActivity;
import com.example.kitayupov.wallet.R;
import com.example.kitayupov.wallet.dto.TransAdapter;
import com.example.kitayupov.wallet.dto.TransDbHelper;
import com.example.kitayupov.wallet.dto.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.content_main;

    private List<Transaction> mArrayList;
    private TransAdapter mAdapter;
    private ListView mListView;
    private TransDbHelper dbHelper;

    private float totalProfit;
    private float totalSpend;

    public static HistoryFragment getInstance(Context context) {
        Bundle args = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(new TransDbHelper(context));
        fragment.setTitle("Total");

        return fragment;
    }

    private void setData(TransDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        mListView = (ListView) view.findViewById(R.id.transaction_list_view);

        mArrayList = new ArrayList<>();
        dbHelper = new TransDbHelper(context);
        mAdapter = new TransAdapter(context, mArrayList);

        mListView.setAdapter(mAdapter);

        registerContextualActionBar();
        readDatabase();

        return view;
    }

    private void registerContextualActionBar() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, EditorActivity.class);
                intent.putExtra(MainActivity.POSITION, position);
                intent.putExtra(Transaction.class.getCanonicalName(), mAdapter.getItem(position));
                startActivityForResult(intent, MainActivity.REQUEST_CODE);
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

    private void readDatabase() {
        totalProfit = 0;
        totalSpend = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TransDbHelper.TABLE_NAME, null, null, null, null, null, MainActivity.DATE);
        if (cursor.moveToFirst()) {
            int amountIndex = cursor.getColumnIndex(MainActivity.AMOUNT);
            int typeIndex = cursor.getColumnIndex(MainActivity.TYPE);
            int descIndex = cursor.getColumnIndex(MainActivity.DESCRIPTION);
            int dateIndex = cursor.getColumnIndex(MainActivity.DATE);
            int isProfitIndex = cursor.getColumnIndex(MainActivity.IS_PROFIT);
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
                Log.d(MainActivity.LOG_TAG, "read " + item.toString());
            } while (cursor.moveToNext());
            cursor.close();
            Log.d(MainActivity.LOG_TAG, "all read " + mArrayList.size());
            MainActivity.setTotal(totalProfit, totalSpend);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case MainActivity.REQUEST_CODE:
                    if (data != null) {
                        int position = data.getIntExtra(MainActivity.POSITION, Integer.MIN_VALUE);
                        Transaction item = data.getParcelableExtra(Transaction.class.getCanonicalName());
                        saveTransaction(position, item);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveTransaction(int position, Transaction item) {
        position = (position >= 0 && position < mArrayList.size()) ? position : mArrayList.size();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MainActivity.AMOUNT, item.getAmount());
        values.put(MainActivity.TYPE, item.getType());
        values.put(MainActivity.DESCRIPTION, item.getDescription());
        values.put(MainActivity.DATE, item.getDate());
        values.put(MainActivity.IS_PROFIT, item.isProfit() ? 1 : 0);

        if (position == mArrayList.size()) {
            db.insert(TransDbHelper.TABLE_NAME, null, values);
            Log.d(MainActivity.LOG_TAG, "insert #" + position + item.toString());
        } else {
            updateRow(mArrayList.get(position), values);
            Log.d(MainActivity.LOG_TAG, "update #" + position + item.toString());
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
        MainActivity.setTotal(totalProfit, totalSpend);
    }

    private void updateRow(Transaction item, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause =
                MainActivity.AMOUNT + "=? and " + MainActivity.TYPE + "=? and " +
                        MainActivity.DATE + "=? and " + MainActivity.IS_PROFIT + "=?";
        String[] whereArgs = new String[]{
                String.valueOf(item.getAmount()), item.getType(),
                String.valueOf(item.getDate()), String.valueOf(item.isProfit() ? 1 : 0)};
        db.update(TransDbHelper.TABLE_NAME, values, whereClause, whereArgs);
        mArrayList.remove(item);
        totalProfit -= item.isProfit() ? item.getAmount() : 0;
        totalSpend -= item.isProfit() ? 0 : item.getAmount();
    }

    public void deleteTransaction(ArrayList<Transaction> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Transaction item : list) {
            String whereClause =
                    MainActivity.AMOUNT + "=? and " + MainActivity.TYPE + "=? and " +
                            MainActivity.DESCRIPTION + "=? and " + MainActivity.DATE + "=? and " + MainActivity.IS_PROFIT + "=?";
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
        MainActivity.setTotal(totalProfit, totalSpend);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void clearDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from " + TransDbHelper.TABLE_NAME);
        mArrayList.clear();
        mAdapter.notifyDataSetChanged();
        totalProfit = totalSpend = 0;
        MainActivity.setTotal(totalProfit, totalSpend);
    }
}