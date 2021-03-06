package com.venoty.laura.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;

public class MyDatePickerFragment extends DialogFragment {

    private View v;

    public void setView(View v) { this.v = v; }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year         = c.get(Calendar.YEAR);
        int month        = c.get(Calendar.MONTH);
        int day          = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    ((EditText)v).setText(view.getDayOfMonth() + "/" + (view.getMonth()+1) +"/" + view.getYear());
                }
            };
}