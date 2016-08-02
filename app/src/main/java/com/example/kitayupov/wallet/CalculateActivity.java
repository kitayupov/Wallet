package com.example.kitayupov.wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CalculateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private TextView textView;
    private float result;
    private MathAction action;

    private enum MathAction {ADD, SUBTRACT, MULTIPLY, DIVIDE, EQUAL}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calculate);

        editText = (EditText) findViewById(R.id.edit);
        textView = (TextView) findViewById(R.id.text);

        findViewById(R.id.button_add).setOnClickListener(this);
        findViewById(R.id.button_subtract).setOnClickListener(this);
        findViewById(R.id.button_multiply).setOnClickListener(this);
        findViewById(R.id.button_divide).setOnClickListener(this);
        findViewById(R.id.button_equal).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String text = editText.getText().toString();
        if ("".equals(text)) {
            if (!MathAction.EQUAL.equals(action)) {
                return;
            } else {
                text = "0";
            }
        }
        if (!"".equals(textView.getText().toString())) {
            textView.append("\n");
        }
        switch (view.getId()) {
            case R.id.button_add:
                takeResult(Float.parseFloat(text), MathAction.ADD);
                textView.append("\n+");
                break;
            case R.id.button_subtract:
                takeResult(Float.parseFloat(text), MathAction.SUBTRACT);
                textView.append("\n-");
                break;
            case R.id.button_multiply:
                takeResult(Float.parseFloat(text), MathAction.MULTIPLY);
                textView.append("\n*");
                break;
            case R.id.button_divide:
                takeResult(Float.parseFloat(text), MathAction.DIVIDE);
                textView.append("\n/");
                break;
            case R.id.button_equal:
                takeResult(Float.parseFloat(text), MathAction.EQUAL);
                textView.append("\n");
                break;
        }
        editText.setText(null);
    }

    private void takeResult(float v, MathAction a) {
        if (result == 0) {
            result = v;
        } else {
            switch (action) {
                case ADD:
                    result += v;
                    break;
                case SUBTRACT:
                    result -= v;
                    break;
                case MULTIPLY:
                    result *= v;
                    break;
                case DIVIDE:
                    result /= v;
                    break;
                case EQUAL:
                    break;
            }
        }
        textView.setText(String.valueOf(result));
        action = a;
    }
}
