package com.example.kitayupov.wallet.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kitayupov.wallet.dto.Transaction;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabsFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    private List<Transaction> data;

    private HistoryFragment historyFragment;
    private StatisticsFragment statisticsProfitFragment;
    private StatisticsFragment statisticsSpendFragment;

    public TabsFragmentAdapter(Context context, FragmentManager fm, List<Transaction> data) {
        super(fm);
        this.data = data;
        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();
        historyFragment = HistoryFragment.getInstance(context, data);
        statisticsProfitFragment = StatisticsFragment.getInstance(context, true);
        statisticsSpendFragment = StatisticsFragment.getInstance(context, false);
        tabs.put(0, historyFragment);
        tabs.put(1, statisticsProfitFragment);
        tabs.put(2, statisticsSpendFragment);
    }

    public void setData(List<Transaction> data) {
        this.data = data;
        historyFragment.refreshList(data);
    }

    public void setTitles(float total, float totalProfit, float totalSpend) {
        historyFragment.setTitle(String.valueOf(total));
        statisticsProfitFragment.setTitle(String.valueOf(totalProfit));
        statisticsSpendFragment.setTitle(String.valueOf(totalSpend));
    }

    public void setDates(long date1, long date2) {
        statisticsProfitFragment.setDatePeriod(date1, date2);
    }
}