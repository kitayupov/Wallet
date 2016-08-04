package com.example.kitayupov.wallet;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;

import com.example.kitayupov.wallet.dialog.OnCategorySelectListener;
import com.example.kitayupov.wallet.dto.Transaction;
import com.example.kitayupov.wallet.dialog.CalculateDialogFragment;
import com.example.kitayupov.wallet.dialog.CategoryListDialogFragment;
import com.example.kitayupov.wallet.dialog.OnCalculateListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class EditorActivity extends AppCompatActivity implements OnCategorySelectListener, OnCalculateListener,
        CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener {

    private static final int LAYOUT = R.layout.activity_editor;

    private EditText amountEditText;
    private AutoCompleteTextView typeEditText;
    private AutoCompleteTextView descEditText;
    private EditText dateEditText;
    private RadioButton profitRadio;
    private RadioButton spendRadio;

    private Transaction transaction;
    private int position;
    private int colorProfit;
    private int colorSpend;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        setNotificationBarColor();
        initialize();
    }

    private void setNotificationBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void initialize() {
        calendar = Calendar.getInstance();
        colorProfit = getResources().getColor(R.color.colorProfit);
        colorSpend = getResources().getColor(R.color.colorSpend);

        amountEditText = (EditText) findViewById(R.id.amount_edit_text);
        typeEditText = (AutoCompleteTextView) findViewById(R.id.type_edit_text);
        descEditText = (AutoCompleteTextView) findViewById(R.id.desc_edit_text);
        dateEditText = (EditText) findViewById(R.id.date_edit_text);
        profitRadio = (RadioButton) findViewById(R.id.profit_radio_button);
        spendRadio = (RadioButton) findViewById(R.id.spend_radio_button);

        Intent intent = getIntent();
        position = intent.getIntExtra(Constants.POSITION, Integer.MIN_VALUE);
        transaction = intent.getParcelableExtra(Transaction.class.getCanonicalName());

        if (transaction == null) {
            transaction = new Transaction();
        }

        float amount = transaction.getAmount();
        amountEditText.setText(amount != 0f ? MainActivity.stringFormat(amount) : null);
        typeEditText.setText(transaction.getType());
        descEditText.setText(transaction.getDescription());
        setDate(transaction.getDate());
        boolean isProfit = transaction.isProfit();
        profitRadio.setChecked(isProfit);
        spendRadio.setChecked(!isProfit);
        amountEditText.setTextColor(isProfit ? colorProfit : colorSpend);

        if (isProfit) {
            profitRadio.setTextColor(getResources().getColor(R.color.colorTextLight));
        } else {
            spendRadio.setTextColor(getResources().getColor(R.color.colorTextLight));
        }

        setButtonListeners();

        profitRadio.setOnCheckedChangeListener(this);
        spendRadio.setOnCheckedChangeListener(this);

        setTypeAutoCompleteArray(profitRadio.isChecked());

        descEditText.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(Constants.descriptionMap.keySet())));
        descEditText.addTextChangedListener(this);

    }

    private void setButtonListeners() {
        findViewById(R.id.calculate_button).setOnClickListener(this);
        findViewById(R.id.select_type_button).setOnClickListener(this);
        findViewById(R.id.select_desc_button).setOnClickListener(this);
        findViewById(R.id.set_date_button).setOnClickListener(this);
        dateEditText.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                doneTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doneTransaction() {
        String string = amountEditText.getText().toString().trim();
        if (!"".equals(string)) {
            if (string.contains(".") && string.indexOf(".") + 2 < string.length() - 1) {
                string = string.substring(0, string.indexOf(".") + 3);
            }
            transaction.setAmount(Float.parseFloat(string));
            String type = typeEditText.getText().toString().trim();
            transaction.setType(type);
            String desc = descEditText.getText().toString().trim();
            transaction.setDescription(desc);
            transaction.setDate(calendar.getTimeInMillis());
            boolean isProfit = profitRadio.isChecked();
            transaction.setProfit(isProfit);
            Constants.addType(isProfit, type);
            Constants.addDescription(desc);
            sendResult(transaction);
        } else {
            amountEditText.setError(getString(R.string.message_empty_field));
        }
    }

    private void setDate(long date) {
        calendar.setTimeInMillis(date);
        dateEditText.setText(DateFormat.format("dd MMMM yyyy HH:mm", calendar.getTimeInMillis()));
    }

    private void sendResult(Transaction transaction) {
        Intent intent = new Intent();
        intent.putExtra(Transaction.class.getCanonicalName(), transaction);
        intent.putExtra(Constants.POSITION, position);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.POSITION, position);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCategorySelect(String category) {
        typeEditText.setText(category);
    }

    @Override
    public void onCalculateComplete(float amount) {
        amountEditText.setText(String.valueOf(amount));
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        setTypeAutoCompleteArray(profitRadio.isChecked());
        profitRadio.setTextColor(getResources().getColor(profitRadio.isChecked() ? R.color.colorTextLight : R.color.colorTextDark));
        spendRadio.setTextColor(getResources().getColor(profitRadio.isChecked() ? R.color.colorTextDark : R.color.colorTextLight));
        amountEditText.setTextColor(profitRadio.isChecked() ? colorProfit : colorSpend);
    }

    private void setTypeAutoCompleteArray(boolean isProfit) {
        Map<String, Integer> map = isProfit ? Constants.profitMap : Constants.spendMap;
        typeEditText.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(map.keySet())));
        typeEditText.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        DialogFragment categoryDialog = new CategoryListDialogFragment();
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.calculate_button:
                DialogFragment calculateDialog = new CalculateDialogFragment();
                String amount = amountEditText.getText().toString();
                if (!"".equals(amount)) {
                    bundle.putFloat(Constants.AMOUNT, Float.parseFloat(amount));
                    calculateDialog.setArguments(bundle);
                }
                calculateDialog.show(getFragmentManager(), "Calculator");
                break;
            case R.id.select_type_button:
                bundle.putBoolean(Constants.IS_PROFIT, profitRadio.isChecked());
                categoryDialog.setArguments(bundle);
                categoryDialog.show(getFragmentManager(), "Categories");
                break;
            case R.id.select_desc_button:
                categoryDialog.show(getFragmentManager(), "Descriptions");
                break;
            case R.id.set_date_button:
            case R.id.date_edit_text:
                setDate();
                break;
        }
    }

    private void setDate() {
        new DatePickerDialog(EditorActivity.this, d,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void setTime() {
        new TimePickerDialog(EditorActivity.this, t,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true)
                .show();
    }

    // Обрабатывает выбор даты
    private DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setTime();
        }
    };

    // Обрабатывает выбор времени
    private TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            setDate(calendar.getTimeInMillis());
        }
    };
}
