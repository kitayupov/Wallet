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
import android.widget.EditText;
import android.widget.TextView;

import com.example.kitayupov.wallet.MainActivity;
import com.example.kitayupov.wallet.R;

public class CalculateDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText editText;
    private TextView textView;
    private MathAction action;
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
        textView.setText(null);
        editText.setText(null);
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

        textView = (TextView) view.findViewById(R.id.text);
        editText = (EditText) view.findViewById(R.id.edit);
        editText.requestFocus();

        if (getArguments() != null && getArguments().containsKey(MainActivity.AMOUNT)) {
            editText.setText(String.valueOf(getArguments().getFloat(MainActivity.AMOUNT)));
        }

        view.findViewById(R.id.button_add).setOnClickListener(this);
        view.findViewById(R.id.button_subtract).setOnClickListener(this);
        view.findViewById(R.id.button_multiply).setOnClickListener(this);
        view.findViewById(R.id.button_divide).setOnClickListener(this);
        view.findViewById(R.id.button_equal).setOnClickListener(this);

        return view;
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

    // Takes arithmetic operations
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
