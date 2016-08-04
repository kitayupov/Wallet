package com.example.kitayupov.wallet.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kitayupov.wallet.dto.Transaction;

import java.util.HashMap;
import java.util.Map;

public class TabsFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;

    private HistoryFragment historyFragment;
    private StatisticsFragment statisticsProfitFragment;
    private StatisticsFragment statisticsSpendFragment;

    public TabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
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
        historyFragment = HistoryFragment.getInstance(context);
        statisticsProfitFragment = StatisticsFragment.getInstance(context, true);
        statisticsSpendFragment = StatisticsFragment.getInstance(context, false);
        tabs.put(0, historyFragment);
        tabs.put(1, statisticsProfitFragment);
        tabs.put(2, statisticsSpendFragment);
    }

    public void setDates(long date1, long date2) {
        statisticsProfitFragment.setDatePeriod(date1, date2);
        statisticsSpendFragment.setDatePeriod(date1, date2);
    }

    public void clearDatabase() {
        historyFragment.clearDatabase();
    }

    public void saveTransaction(int position, Transaction item) {
        historyFragment.saveTransaction(position, item);
    }
}