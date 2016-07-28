package com.example.kitayupov.wallet.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kitayupov.wallet.Constants;
import com.example.kitayupov.wallet.MainActivity;
import com.example.kitayupov.wallet.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class CategoryListFragment extends DialogFragment {

    private ListView listView;
    private OnCompleteListener completeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (getArguments() != null && getArguments().containsKey(MainActivity.IS_PROFIT)) {
            boolean isProfit = getArguments().getBoolean(MainActivity.IS_PROFIT);
            final Map<String, Integer> map = isProfit ? Constants.profitMap : Constants.spendMap;
            getContentView(map);
            builder.setTitle(R.string.label_select_category);
        } else {
            getContentView(Constants.descriptionMap);
            builder.setTitle(R.string.label_select_description);
        }
        builder.setView(listView);

        return builder.create();
    }

    private void getContentView(final Map<String, Integer> map) {
        listView = new ListView(getActivity());
        final ArrayList<String> types = new ArrayList<>(map.keySet());
        Collections.sort(types, new Comparator<String>() {
            @Override
            public int compare(String ls, String rs) {
                return map.get(rs) - map.get(ls);
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, types);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                completeListener.onComplete(types.get(i));
                dismiss();
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            completeListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}
