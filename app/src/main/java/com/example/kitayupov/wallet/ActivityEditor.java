package com.example.kitayupov.wallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class ActivityEditor extends AppCompatActivity {

    private EditText amountEditText;
    private EditText typeEditText;
    private DatePicker datePicker;
    private RadioButton profitRadio;
    private RadioButton spendRadio;

    private static final String LOG_TAG = "ActivityEditor";

    private Transaction transaction;
    private int position;
    private int colorProfit;
    private int colorSpend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        colorProfit = getResources().getColor(R.color.colorProfit);
        colorSpend = getResources().getColor(R.color.colorSpend);
        initialize();
    }

    private void initialize() {
        amountEditText = (EditText) findViewById(R.id.amount_edit_text);
        typeEditText = (EditText) findViewById(R.id.type_edit_text);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        profitRadio = (RadioButton) findViewById(R.id.profit_radio_button);
        spendRadio = (RadioButton) findViewById(R.id.spend_radio_button);

        Intent intent = getIntent();
        position = intent.getIntExtra(MainActivity.POSITION, Integer.MIN_VALUE);
        transaction = intent.getParcelableExtra(Transaction.class.getCanonicalName());

        if (transaction != null) {
            amountEditText.setText(String.valueOf(transaction.getAmount()));
            typeEditText.setText(transaction.getType());
            datePicker.getCalendarView().setDate(transaction.getDate());
            profitRadio.setChecked(transaction.isProfit());
            spendRadio.setChecked(!transaction.isProfit());
            amountEditText.setTextColor(transaction.isProfit() ? colorProfit : colorSpend);
        }

        setButtonListeners();
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
            transaction = new Transaction();
            transaction.setAmount(Float.parseFloat(string));
            transaction.setType(typeEditText.getText().toString().trim());
            transaction.setDate(datePicker.getCalendarView().getDate());
            transaction.setProfit(profitRadio.isChecked());
            sendResult(transaction);
        } else {
            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
        }
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
}
