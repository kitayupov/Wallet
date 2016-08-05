package com.example.kitayupov.wallet.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.kitayupov.wallet.Constants;
import com.example.kitayupov.wallet.R;

public class CalculateDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextView editTextView;
    private TextView resultTextView;
    private MathAction action;
    private StringBuilder builder;
    private float result;

    private OnCalculateListener listener;

    private enum MathAction {ADD, SUBTRACT, MULTIPLY, DIVIDE, EQUAL}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(getContentView());
        builder.setTitle(R.string.calculator);

        builder.setPositiveButton(R.string.save, null);
        builder.setNeutralButton(R.string.clean, null);

        final AlertDialog dialog = builder.create();

        // Overrides button listeners
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface di) {
                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onSaveButtonClicked();
                            }
                        });
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OnClearButtonClicked();
                            }
                        });
            }
        });
        return dialog;
    }

    // Clears result
    private void OnClearButtonClicked() {
        resultTextView.setText(null);
        editTextView.setText(null);
        builder = new StringBuilder();
        result = 0;
    }

    // Saves result
    private void onSaveButtonClicked() {
        listener.onCalculateComplete(result);
        dismiss();
    }

    // Returns inflated view
    @NonNull
    private View getContentView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_calculate, null);

        resultTextView = (TextView) view.findViewById(R.id.text);
        editTextView = (TextView) view.findViewById(R.id.edit);
        editTextView.requestFocus();

        builder = new StringBuilder();

        if (getArguments() != null && getArguments().containsKey(Constants.AMOUNT)) {
            float decimal = getArguments().getFloat(Constants.AMOUNT);
            builder.append(formatFloat(decimal));
            editTextView.setText(builder.toString());
        }

        view.findViewById(R.id.button_add).setOnClickListener(this);
        view.findViewById(R.id.button_subtract).setOnClickListener(this);
        view.findViewById(R.id.button_multiply).setOnClickListener(this);
        view.findViewById(R.id.button_divide).setOnClickListener(this);
        view.findViewById(R.id.button_equal).setOnClickListener(this);

        view.findViewById(R.id.button_clear).setOnClickListener(this);
        view.findViewById(R.id.button_dot).setOnClickListener(this);

        view.findViewById(R.id.button_numeric_0).setOnClickListener(this);
        view.findViewById(R.id.button_numeric_1).setOnClickListener(this);
        view.findViewById(R.id.button_numeric_2).setOnClickListener(this);
        view.findViewById(R.id.button_numeric_3).setOnClickListener(this);
        view.findViewById(R.id.button_numeric_4).setOnClickListener(this);
        view.findViewById(R.id.button_numeric_5).setOnClickListener(this);
        view.findViewById(R.id.button_numeric_6).setOnClickListener(this);
        view.findViewById(R.id.button_numeric_7).setOnClickListener(this);
        view.findViewById(R.id.button_numeric_8).setOnClickListener(this);
        view.findViewById(R.id.button_numeric_9).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add:
                takeResult(MathAction.ADD);
                resultTextView.append("\n+");
                break;
            case R.id.button_subtract:
                takeResult(MathAction.SUBTRACT);
                resultTextView.append("\n-");
                break;
            case R.id.button_multiply:
                takeResult(MathAction.MULTIPLY);
                resultTextView.append("\n*");
                break;
            case R.id.button_divide:
                takeResult(MathAction.DIVIDE);
                resultTextView.append("\n/");
                break;
            case R.id.button_equal:
                takeResult(MathAction.EQUAL);
                resultTextView.append("\n");
                break;
            case R.id.button_numeric_0:
                appendNumber(0);
                break;
            case R.id.button_numeric_1:
                appendNumber(1);
                break;
            case R.id.button_numeric_2:
                appendNumber(2);
                break;
            case R.id.button_numeric_3:
                appendNumber(3);
                break;
            case R.id.button_numeric_4:
                appendNumber(4);
                break;
            case R.id.button_numeric_5:
                appendNumber(5);
                break;
            case R.id.button_numeric_6:
                appendNumber(6);
                break;
            case R.id.button_numeric_7:
                appendNumber(7);
                break;
            case R.id.button_numeric_8:
                appendNumber(8);
                break;
            case R.id.button_numeric_9:
                appendNumber(9);
                break;
            case R.id.button_dot:
                setFloatingDot();
                break;
            case R.id.button_clear:
                clearLast();
                break;
        }
        editTextView.setText(builder);
    }

    private void clearLast() {
        String text = builder.toString();
        if (!"".equals(text)) {
            text = text.substring(0, text.length() - 1);
            builder = new StringBuilder().append(text);
        }
    }

    private void setFloatingDot() {
        if (!builder.toString().contains(".")) {
            builder.append(".");
        }
    }

    private void appendNumber(int i) {
        if (!"".equals(builder.toString()) || i != 0) {
            builder.append(String.valueOf(i));
        }
    }

    private String formatFloat(float f) {
        String text;
        if (f == (long) f) {
            text = String.valueOf((long) f);
        } else {
            text = String.valueOf(f);
        }
        return text;
    }

    // Takes arithmetic operations
    private void takeResult(MathAction a) {
        if ("".equals(resultTextView.getText().toString())) {
            if ("".equals(builder.toString())) {
                return;
            }
        } else {
            resultTextView.append("\n");
        }

        float decimal;
        if (!"".equals(builder.toString())) {
            decimal = Float.parseFloat(builder.toString());
        } else {
            decimal = result;
        }

        if (result == 0) {
            result = decimal;
        } else {
            switch (action) {
                case ADD:
                    result += decimal;
                    break;
                case SUBTRACT:
                    result -= decimal;
                    break;
                case MULTIPLY:
                    result *= decimal;
                    break;
                case DIVIDE:
                    result /= decimal;
                    break;
                case EQUAL:
                    result = decimal;
                    break;
            }
        }
        resultTextView.setText(String.valueOf(result));
        action = a;
        builder = new StringBuilder();
    }

    // Passes data to activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnCalculateListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCalculateListener");
        }
    }

    // Sets soft keyboard automatically visible
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
