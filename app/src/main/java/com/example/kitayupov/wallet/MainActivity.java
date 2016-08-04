package com.example.kitayupov.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kitayupov.wallet.dto.Transaction;
import com.example.kitayupov.wallet.fragments.OnDateChangedListener;
import com.example.kitayupov.wallet.fragments.TabsFragmentAdapter;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnDateChangedListener {

    public static final int LAYOUT = R.layout.activity_main;

    public static final int REQUEST_CODE = 0;

    public static final String POSITION = "position";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "date";
    public static final String IS_PROFIT = "is_profit";
    public static final String LOG_TAG = "MainActivity";

    private Context context;

    private ViewPager viewPager;
    private TabsFragmentAdapter adapter;

    private static TextView totalTabTitle;
    private static TextView profitTabTitle;
    private static TextView spendTabTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        initFloatingActionButton();
        initialize();
        initTabLayout();
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EditorActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }
    }

    private void initialize() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new TabsFragmentAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        getTypeMaps();
    }

    private void initTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        View totalTabView = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        View profitTabView = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        View spendTabView = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);

        totalTabTitle = (TextView) totalTabView.findViewById(R.id.tab_text);
        profitTabTitle = (TextView) profitTabView.findViewById(R.id.tab_text);
        spendTabTitle = (TextView) spendTabView.findViewById(R.id.tab_text);

        totalTabTitle.setTextColor(context.getResources().getColor(R.color.colorTextLight));
        profitTabTitle.setTextColor(context.getResources().getColor(R.color.colorProfit));
        spendTabTitle.setTextColor(context.getResources().getColor(R.color.colorSpend));

        tabLayout.getTabAt(0).setCustomView(totalTabView);
        tabLayout.getTabAt(1).setCustomView(profitTabView);
        tabLayout.getTabAt(2).setCustomView(spendTabView);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    doneTransaction(data);
                    Log.e(LOG_TAG, "done transaction");
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
            Transaction item = data.getParcelableExtra(Transaction.class.getCanonicalName());
            Log.e(LOG_TAG, "save transaction " + position + " " + item.toString());
            adapter.saveTransaction(position, item);
        }
    }

    public static void setTotal(float profit, float spend) {
        totalTabTitle.setText(String.valueOf(profit - spend));
        profitTabTitle.setText(String.valueOf(profit));
        spendTabTitle.setText(String.valueOf(spend));
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
                adapter.clearDatabase();
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
    public void onDateChanged(long date1, long date2) {
        adapter.setDates(date1, date2);
    }
}
