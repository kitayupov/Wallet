package com.example.kitayupov.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 0;
    public static final String POSITION = "position";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String DATE = "date";
    private static final String LOG_TAG = "MainActivity";

    private ListView mListView;
    private ArrayList<Transaction> mArrayList;
    private TransAdapter mAdapter;
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
        mArrayList = new ArrayList<>();
        mAdapter = new TransAdapter(context, mArrayList);
        mListView = (ListView) findViewById(R.id.transaction_list_view);
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
        }
        profitTextView = (TextView) findViewById(R.id.profit_text_view);
        spendTextView = (TextView) findViewById(R.id.spend_text_view);
        totalTextView = (TextView) findViewById(R.id.total_text_view);
        registerContextualActionBar();
    }

    private void registerContextualActionBar() {
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                mode.setTitle(String.valueOf(mListView.getCheckedItemCount()));
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.context_delete:
                        deleteTransaction();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_context, menu);
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
        }
    }

    private void setTotal() {
        total = totalProfit - totalSpend;
        profitTextView.setText(String.valueOf(totalProfit));
        spendTextView.setText(String.valueOf(totalSpend));
        totalTextView.setText(String.valueOf(total));
    }

    private void deleteTransaction() {
        Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
