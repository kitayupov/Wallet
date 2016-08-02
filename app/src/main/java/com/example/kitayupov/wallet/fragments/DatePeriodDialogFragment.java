package com.example.kitayupov.wallet.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.kitayupov.wallet.R;
import com.example.kitayupov.wallet.StatisticsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePeriodDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText startDateText;
    private EditText finishDateText;
    private Calendar startCalendar;
    private Calendar finishCalendar;

    private OnDateChangeListener dateChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_select_period, null);
        builder.setView(view);
        builder.setTitle(R.string.select_dates);

        startDateText = (EditText) view.findViewById(R.id.start_edit_text);
        finishDateText = (EditText) view.findViewById(R.id.finish_edit_text);

        startDateText.setOnClickListener(this);
        finishDateText.setOnClickListener(this);

        startCalendar = Calendar.getInstance();
        finishCalendar = Calendar.getInstance();

        long startDate = getArguments().getLong(StatisticsActivity.START_DATE);
        long finishDate = getArguments().getLong(StatisticsActivity.FINISH_DATE);

        if (startDate == 0) {
            startCalendar.setTimeInMillis(finishDate);
            startCalendar.set(Calendar.MONTH, startCalendar.get(Calendar.MONTH) - 1);
        } else {
            startCalendar.setTimeInMillis(getArguments().getLong(StatisticsActivity.START_DATE));
        }
        finishCalendar.setTimeInMillis(finishDate);

        setDate();

        // Добавляет кнопку сохранения изображения
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dateChangeListener.onChange(startCalendar, finishCalendar);
            }
        });

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_edit_text:
                new DatePickerDialog(getActivity(), d1,
                        startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
            case R.id.finish_edit_text:
                new DatePickerDialog(getActivity(), d2,
                        finishCalendar.get(Calendar.YEAR),
                        finishCalendar.get(Calendar.MONTH),
                        finishCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
        }
    }


    // Обрабатывает выбор даты
    private DatePickerDialog.OnDateSetListener d1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, monthOfYear);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDate();
        }
    };

    // Обрабатывает выбор даты
    private DatePickerDialog.OnDateSetListener d2 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            finishCalendar.set(Calendar.YEAR, year);
            finishCalendar.set(Calendar.MONTH, monthOfYear);
            finishCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDate();
        }
    };

    private void setDate() {
        startDateText.setText(new SimpleDateFormat("dd MMMM yyyy").format(startCalendar.getTimeInMillis()));
        finishDateText.setText(new SimpleDateFormat("dd MMMM yyyy").format(finishCalendar.getTimeInMillis()));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dateChangeListener = (OnDateChangeListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCategoryListener");
        }
    }
}
