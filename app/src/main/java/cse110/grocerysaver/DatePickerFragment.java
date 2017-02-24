package cse110.grocerysaver;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;


import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    EditText expDateField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // set the default date to be the one previously entered
        expDateField = (EditText) getActivity().findViewById(R.id.expDateField);
        int month, day, year;
        final Calendar currDate = Calendar.getInstance();

        if (!(TextUtils.isEmpty(expDateField.getText().toString()))) {
            String[] inputDate = expDateField.getText().toString().split(" / ", -1);

            month = Integer.parseInt(inputDate[0]) - 1;
            day = Integer.parseInt(inputDate[1]);
            year = Integer.parseInt(inputDate[2]);
        }
        else {
            month = currDate.get(Calendar.MONTH);
            day = currDate.get(Calendar.DAY_OF_MONTH);
            year = currDate.get(Calendar.YEAR);
        }

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        DatePicker picker = dialog.getDatePicker();

        // accepted range of dates from next day to one year later
        currDate.add(Calendar.DATE, 1);
        picker.setMinDate(currDate.getTimeInMillis());

        currDate.add(Calendar.YEAR, 1);
        picker.setMaxDate(currDate.getTimeInMillis());

        // return dialog box
        return dialog;


    }

    public void onDateSet(DatePicker view, int year, int month, int day){
        expDateField.setText ((month+1) + " / " + day + " / " + year);
    }
}