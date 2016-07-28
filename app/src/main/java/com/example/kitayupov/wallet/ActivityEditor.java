package com.example.kitayupov.wallet;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.kitayupov.wallet.dto.Transaction;
import com.example.kitayupov.wallet.fragments.CategoryListFragment;
import com.example.kitayupov.wallet.fragments.OnCompleteListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class ActivityEditor extends AppCompatActivity implements OnCompleteListener,
        CompoundButton.OnCheckedChangeListener, TextWatcher {

    public static final int LAYOUT = R.layout.activity_editor;

    private EditText amountEditText;
    private AutoCompleteTextView typeEditText;
    private AutoCompleteTextView descEditText;
    private EditText dateEditText;
    private DatePicker datePicker;
    private RadioButton profitRadio;
    private RadioButton spendRadio;

    private View button;

    private Transaction transaction;
    private int position;
    private int colorProfit;
    private int colorSpend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        colorProfit = getResources().getColor(R.color.colorProfit);
        colorSpend = getResources().getColor(R.color.colorSpend);
        initialize();
    }

    private void initialize() {
        amountEditText = (EditText) findViewById(R.id.amount_edit_text);
        typeEditText = (AutoCompleteTextView) findViewById(R.id.type_edit_text);
        descEditText = (AutoCompleteTextView) findViewById(R.id.desc_edit_text);
        dateEditText = (EditText) findViewById(R.id.date_edit_text);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        profitRadio = (RadioButton) findViewById(R.id.profit_radio_button);
        spendRadio = (RadioButton) findViewById(R.id.spend_radio_button);

        button = findViewById(R.id.button);

        Intent intent = getIntent();
        position = intent.getIntExtra(MainActivity.POSITION, Integer.MIN_VALUE);
        transaction = intent.getParcelableExtra(Transaction.class.getCanonicalName());

        if (transaction == null) {
            transaction = new Transaction();
        }

        float amount = transaction.getAmount();
        amountEditText.setText(amount != 0f ? stringFormat(amount) : null);
        typeEditText.setText(transaction.getType());
        descEditText.setText(transaction.getDescription());
        dateEditText.setText(DateFormat.format("dd MMM yyyy", transaction.getDate()));
        setDate(transaction.getDate());
        profitRadio.setChecked(transaction.isProfit());
        spendRadio.setChecked(!transaction.isProfit());
        amountEditText.setTextColor(transaction.isProfit() ? colorProfit : colorSpend);

        setButtonListeners();

        profitRadio.setOnCheckedChangeListener(this);
        spendRadio.setOnCheckedChangeListener(this);

        setTypeAutoCompleteArray(profitRadio.isChecked());

        descEditText.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                Constants.descriptions));
        descEditText.addTextChangedListener(this);

    }

    private void setButtonListeners() {
        profitRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setTextColor(colorProfit);
            }
        });
        spendRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setTextColor(colorSpend);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(MainActivity.IS_PROFIT, profitRadio.isChecked());
                DialogFragment dialogFragment = new CategoryListFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getFragmentManager(), "Categories");
            }
        });
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
            transaction.setDate(getDate(datePicker));
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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePicker.updateDate(year, month, day);
    }

    private long getDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime().getTime();
    }

    private void sendResult(Transaction transaction) {
        Intent intent = new Intent();
        intent.putExtra(Transaction.class.getCanonicalName(), transaction);
        intent.putExtra(MainActivity.POSITION, position);
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
        outState.putInt(MainActivity.POSITION, position);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onComplete(String category) {
        typeEditText.setText(category);
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
    }

    private void setTypeAutoCompleteArray(boolean isProfit) {
        Map<String, Integer> map = isProfit ? Constants.profitMap : Constants.spendMap;
        typeEditText.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(map.keySet())));
        typeEditText.addTextChangedListener(this);
    }

    private String stringFormat(float f) {
        if (f == (long) f) {
            return String.format(Locale.ROOT, "%d", (long) f);
        } else {
            return String.format("%s", f);
        }
    }
}
