package cse110.grocerysaver;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    EditText expDateField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // set the default date to be the one previously entered
        expDateField = (EditText) getActivity().findViewById(R.id.expDateField);
        Calendar c = Calendar.getInstance();

        String v = expDateField.getText().toString();
        if (!v.isEmpty()) {
            DateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
            Date date = null;
            try {
                date = df.parse(v);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.setTimeInMillis(date.getTime());
        }

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        DatePicker picker = dialog.getDatePicker();
        
        c = Calendar.getInstance();

        // accepted range of dates from next day to one year later
        c.add(Calendar.DATE, 1);
        picker.setMinDate(c.getTimeInMillis());

        c.add(Calendar.YEAR, 1);
        picker.setMaxDate(c.getTimeInMillis());

        // return dialog box
        return dialog;


    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        expDateField.setText(df.format(c.getTime()));
    }
}