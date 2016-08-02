package com.example.kitayupov.wallet.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;

import com.example.kitayupov.wallet.R;
import com.example.kitayupov.wallet.StatisticsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePeriodDialogFragment extends DialogFragment implements View.OnClickListener {

    private Calendar startCalendar;
    private Calendar finishCalendar;

    private DatePicker startDatePicker;
    private DatePicker finishDatePicker;

    private OnDateChangeListener dateChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_select_period, null);
        builder.setView(view);

        view.findViewById(R.id.start_date_button).setOnClickListener(this);
        view.findViewById(R.id.finish_date_button).setOnClickListener(this);

        startDatePicker = (DatePicker) view.findViewById(R.id.start_date_picker);
        finishDatePicker = (DatePicker) view.findViewById(R.id.finish_date_picker);

        startDatePicker.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                startCalendar.set(i, i1, i2);
            }
        });
        finishDatePicker.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                finishCalendar.set(i, i1, i2);
            }
        });

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

        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.clear(Calendar.MINUTE);
        startCalendar.clear(Calendar.SECOND);
        startCalendar.clear(Calendar.MILLISECOND);

        finishCalendar.setTimeInMillis(finishDate);
        finishCalendar.set(Calendar.HOUR_OF_DAY, 23);
        finishCalendar.set(Calendar.MINUTE, 59);
        finishCalendar.set(Calendar.SECOND, 59);
        finishCalendar.set(Calendar.MILLISECOND, 999);

        setDates();

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
            case R.id.start_date_button:
                new DatePickerDialog(getActivity(), startDateListener,
                        startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
            case R.id.finish_date_button:
                new DatePickerDialog(getActivity(), finishDateListener,
                        finishCalendar.get(Calendar.YEAR),
                        finishCalendar.get(Calendar.MONTH),
                        finishCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
        }
    }

    // Обрабатывает выбор даты
    private DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, monthOfYear);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDates();
        }
    };

    // Обрабатывает выбор даты
    private DatePickerDialog.OnDateSetListener finishDateListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            finishCalendar.set(Calendar.YEAR, year);
            finishCalendar.set(Calendar.MONTH, monthOfYear);
            finishCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDates();
        }
    };

    private void setDates() {
        startDatePicker.updateDate(startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH));
        finishDatePicker.updateDate(finishCalendar.get(Calendar.YEAR),
                finishCalendar.get(Calendar.MONTH),
                finishCalendar.get(Calendar.DAY_OF_MONTH));
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
