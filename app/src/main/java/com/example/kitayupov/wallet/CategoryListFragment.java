package com.example.kitayupov.wallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CategoryListFragment extends DialogFragment {

    private ListView listView;
    private OnCompleteListener completeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        getContentView();
        builder.setView(listView);
        builder.setTitle(R.string.label_select_category);

        return builder.create();
    }

    private void getContentView() {
        listView = new ListView(getActivity());
        final ArrayList<String> types = new ArrayList<>(Constants.categories.keySet());
        Collections.sort(types, new Comparator<String>() {
            @Override
            public int compare(String ls, String rs) {
                int i = Constants.categories.get(rs) - Constants.categories.get(ls);
                return i;
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
