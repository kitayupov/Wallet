package com.example.kitayupov.wallet.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.example.kitayupov.wallet.EditorActivity;
import com.example.kitayupov.wallet.MainActivity;
import com.example.kitayupov.wallet.R;
import com.example.kitayupov.wallet.dto.TransAdapter;
import com.example.kitayupov.wallet.dto.Transaction;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.content_main;

    private List<Transaction> data;
    private TransAdapter mAdapter;
    private ListView mListView;

    public static HistoryFragment getInstance(Context context, List<Transaction> data) {
        Bundle args = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setData(data);
        fragment.setTitle("Total");

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        mListView = (ListView) view.findViewById(R.id.transaction_list_view);
        mAdapter = new TransAdapter(context, data);
        mListView.setAdapter(mAdapter);

        registerContextualActionBar();

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
                        MainActivity.deleteTransaction(list);
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

    public void refreshList(List<Transaction> data) {
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<Transaction> data) {
        this.data = data;
    }
}