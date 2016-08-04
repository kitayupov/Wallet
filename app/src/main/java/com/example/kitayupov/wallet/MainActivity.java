package com.example.kitayupov.wallet;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.example.kitayupov.wallet.dialog.OnDatePeriodSelectListener;
import com.example.kitayupov.wallet.adapter.TabsFragmentAdapter;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnDatePeriodSelectListener, TabLayout.OnTabSelectedListener {

    private static final int LAYOUT = R.layout.activity_main;

    private Context context;

    private FloatingActionButton fab;

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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.createTransaction();
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
        tabLayout.setOnTabSelectedListener(this);

        View totalTabView = LayoutInflater.from(context).inflate(R.layout.tab_title_layout, null);
        View profitTabView = LayoutInflater.from(context).inflate(R.layout.tab_title_layout, null);
        View spendTabView = LayoutInflater.from(context).inflate(R.layout.tab_title_layout, null);

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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        animateFab(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    protected void animateFab(final int position) {
        fab.clearAnimation();
        // Scale down animation
        if (position == 0) {
            ScaleAnimation expand = new ScaleAnimation(0.2f, 1.2f, 0.2f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            expand.setDuration(200);
            expand.setInterpolator(new AccelerateInterpolator());
            expand.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ScaleAnimation shrink =  new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    shrink.setDuration(100);
                    shrink.setInterpolator(new AccelerateInterpolator());
                    fab.startAnimation(shrink);
                    fab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fab.startAnimation(expand);
        } else if (fab.getVisibility() == View.VISIBLE) {

            ScaleAnimation expand =  new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            expand.setDuration(100);
            expand.setInterpolator(new AccelerateInterpolator());
            fab.startAnimation(expand);

            ScaleAnimation shrink = new ScaleAnimation(1.2f, 0.2f, 1.2f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            shrink.setDuration(200);
            shrink.setInterpolator(new DecelerateInterpolator());
            shrink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fab.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fab.startAnimation(shrink);
        }
    }
}
